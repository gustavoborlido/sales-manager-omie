package com.omie.salesmanager.viewmodel

import com.omie.salesmanager.domain.model.SalesOrderModel
import com.omie.salesmanager.domain.repository.SalesOrderRepository
import com.omie.salesmanager.presentation.state.SalesDeleteViewState
import com.omie.salesmanager.presentation.state.SalesOrderListViewState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.omie.salesmanager.presentation.viewmodel.SalesOrderListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

class SalesOrderListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SalesOrderListViewModel
    private val mockRepository: SalesOrderRepository = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SalesOrderListViewModel(mockRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial order state should be Idle`() = runTest {
        val expectedState = SalesOrderListViewState.Idle
        val actualState = viewModel.orderState.first()
        assertEquals(expectedState, actualState)
    }

    @Test
    fun `initial delete state should be Idle`() = runTest {
        val expectedState = SalesDeleteViewState.Idle
        val actualState = viewModel.deleteState.first()
        assertEquals(expectedState, actualState)
    }

    @Test
    fun `getOrders should update state to Success when repository succeeds`() = runTest {
        val orders = listOf(
            SalesOrderModel(id = "1", description = "Customer 1", clientName = "client"),
            SalesOrderModel(id = "2", description = "Customer 1", clientName = "client")
        )
        coEvery { mockRepository.getOrders() } returns Result.success(orders)

        viewModel.getOrders()

        testScheduler.advanceUntilIdle()

        val expectedState = SalesOrderListViewState.Success(orders)
        val actualState = viewModel.orderState.first()
        assertEquals(expectedState, actualState)
    }

    @Test
    fun `getOrders should update state to Error when repository fails`() = runTest {
        val exception = RuntimeException("Failed to fetch orders")
        coEvery { mockRepository.getOrders() } returns Result.failure(exception)

        viewModel.getOrders()

        testScheduler.advanceUntilIdle()

        val expectedState = SalesOrderListViewState.Error(exception.message ?: "Erro desconhecido")
        val actualState = viewModel.orderState.first()
        assertEquals(expectedState, actualState)
    }

    @Test
    fun `deleteOrder should update delete state to Success when repository succeeds`() = runTest {
        val orderId = "order1"
        val orders = listOf(
            SalesOrderModel(id = "2", description = "Customer 2", clientName = "client")
        )

        coEvery { mockRepository.deleteOrder(orderId) } returns Result.success(Unit)

        coEvery { mockRepository.getOrders() } returns Result.success(orders)

        viewModel.deleteOrder(orderId)

        testScheduler.advanceUntilIdle()

        val expectedDeleteState = SalesDeleteViewState.Success
        val actualDeleteState = viewModel.deleteState.first()
        assertEquals(expectedDeleteState, actualDeleteState)

        val expectedOrderState = SalesOrderListViewState.Success(orders)
        val actualOrderState = viewModel.orderState.first()
        assertEquals(expectedOrderState, actualOrderState)
    }

    @Test
    fun `deleteOrder should update delete state to Error when repository fails`() = runTest {
        val orderId = "order1"
        val exception = RuntimeException("Failed to delete order")

        coEvery { mockRepository.deleteOrder(orderId) } returns Result.failure(exception)

        coEvery { mockRepository.getOrders() } returns Result.success(emptyList())

        viewModel.deleteOrder(orderId)

        testScheduler.advanceUntilIdle()

        val expectedDeleteState = SalesDeleteViewState.Error(exception.message ?: "Erro ao excluir o item")
        val actualDeleteState = viewModel.deleteState.first()
        assertEquals(expectedDeleteState, actualDeleteState)
    }

    @Test
    fun `resetDeleteState should update delete state to Idle`() = runTest {
        viewModel.resetDeleteState()

        val expectedState = SalesDeleteViewState.Idle
        val actualState = viewModel.deleteState.first()
        assertEquals(expectedState, actualState)
    }
}