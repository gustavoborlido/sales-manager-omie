package com.omie.salesmanager.viewmodel

import com.omie.salesmanager.domain.model.SalesItemModel
import com.omie.salesmanager.domain.repository.SalesOrderRepository
import com.omie.salesmanager.presentation.state.SalesDeleteViewState
import com.omie.salesmanager.presentation.state.SalesItemListViewState
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
import com.omie.salesmanager.presentation.viewmodel.SalesItemListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

class SalesItemListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SalesItemListViewModel
    private val mockRepository: SalesOrderRepository = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SalesItemListViewModel(mockRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial item state should be Idle`() = runTest {
        val expectedState = SalesItemListViewState.Idle
        val actualState = viewModel.itemState.first()
        assertEquals(expectedState, actualState)
    }

    @Test
    fun `initial delete state should be Idle`() = runTest {
        val expectedState = SalesDeleteViewState.Idle
        val actualState = viewModel.deleteState.first()
        assertEquals(expectedState, actualState)
    }

    @Test
    fun `getItems should update state to Success when repository succeeds`() = runTest {
        val orderId = "order1"
        val items = listOf(
            SalesItemModel(id = "1", productName = "Item 1", quantity = 1, value = 10.0),
            SalesItemModel(id = "2", productName = "Item 2", quantity = 2, value = 20.0)
        )
        coEvery { mockRepository.getItens(orderId) } returns Result.success(items)

        viewModel.getItems(orderId)

        testScheduler.advanceUntilIdle()

        val expectedState = SalesItemListViewState.Success(items)
        val actualState = viewModel.itemState.first()
        assertEquals(expectedState, actualState)
    }

    @Test
    fun `getItems should update state to Error when repository fails`() = runTest {
        val orderId = "order1"
        val exception = RuntimeException("Failed to fetch items")
        coEvery { mockRepository.getItens(orderId) } returns Result.failure(exception)

        viewModel.getItems(orderId)

        testScheduler.advanceUntilIdle()

        val expectedState = SalesItemListViewState.Error(exception.message ?: "Erro desconhecido")
        val actualState = viewModel.itemState.first()
        assertEquals(expectedState, actualState)
    }

    @Test
    fun `deleteItem should update delete state to Success when repository succeeds`() = runTest {
        val orderId = "order1"
        val itemId = "item1"
        val items = listOf(
            SalesItemModel(id = "1", productName = "Item 1", quantity = 1, value = 10.0),
            SalesItemModel(id = "2", productName = "Item 2", quantity = 2, value = 20.0)
        )

        coEvery { mockRepository.deleteItem(orderId, itemId) } returns Result.success(Unit)

        coEvery { mockRepository.getItens(orderId) } returns Result.success(items)

        viewModel.deleteItem(orderId, itemId)

        testScheduler.advanceUntilIdle()

        val expectedDeleteState = SalesDeleteViewState.Success
        val actualDeleteState = viewModel.deleteState.first()
        assertEquals(expectedDeleteState, actualDeleteState)

        val expectedItemState = SalesItemListViewState.Success(items)
        val actualItemState = viewModel.itemState.first()
        assertEquals(expectedItemState, actualItemState)
    }

    @Test
    fun `deleteItem should update delete state to Error when repository fails`() = runTest {
        val orderId = "order1"
        val itemId = "item1"
        val exception = RuntimeException("Failed to delete item")

        coEvery { mockRepository.deleteItem(orderId, itemId) } returns Result.failure(exception)

        coEvery { mockRepository.getItens(orderId) } returns Result.success(emptyList())

        viewModel.deleteItem(orderId, itemId)

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
