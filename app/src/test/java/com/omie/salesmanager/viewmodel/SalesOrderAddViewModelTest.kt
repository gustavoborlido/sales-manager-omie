package com.omie.salesmanager.viewmodel

import com.omie.salesmanager.domain.model.SalesOrderModel
import com.omie.salesmanager.domain.repository.SalesOrderRepository
import com.omie.salesmanager.presentation.state.SalesOrderViewState
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
import com.omie.salesmanager.presentation.viewmodel.SalesOrderAddViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

class SalesOrderAddViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SalesOrderAddViewModel
    private val mockRepository: SalesOrderRepository = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SalesOrderAddViewModel(mockRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Idle`() = runTest {
        val expectedState = SalesOrderViewState.Idle
        val actualState = viewModel.orderState.first()
        assertEquals(expectedState, actualState)
    }

    @Test
    fun `addOrder should update state to Success when repository succeeds`() = runTest {
        val order = SalesOrderModel(id = "1", description = "Customer 1", clientName = "client")
        val orderId = "order1"
        coEvery { mockRepository.addOrder(order) } returns Result.success(orderId)

        viewModel.addOrder(order)

        testScheduler.advanceUntilIdle()

        val expectedState = SalesOrderViewState.Success(orderId)
        val actualState = viewModel.orderState.first()
        assertEquals(expectedState, actualState)
    }

    @Test
    fun `addOrder should update state to Error when repository fails`() = runTest {
        val order = SalesOrderModel(id = "1", description = "Customer 1", clientName = "client")
        val exception = RuntimeException("Failed to add order")
        coEvery { mockRepository.addOrder(order) } returns Result.failure(exception)

        viewModel.addOrder(order)

        testScheduler.advanceUntilIdle()

        val expectedState = SalesOrderViewState.Error(exception.message ?: "Erro desconhecido")
        val actualState = viewModel.orderState.first()
        assertEquals(expectedState, actualState)
    }
}
