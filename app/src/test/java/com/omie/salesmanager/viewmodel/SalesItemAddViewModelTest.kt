package com.omie.salesmanager.viewmodel

import com.omie.salesmanager.domain.model.SalesItemModel
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
import com.omie.salesmanager.presentation.viewmodel.SalesItemAddViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

class SalesItemAddViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SalesItemAddViewModel
    private val mockRepository: SalesOrderRepository = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SalesItemAddViewModel(mockRepository)
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
    fun `addItem should update state to Success when repository succeeds`() = runTest {
        val item = SalesItemModel(id = "1", productName = "Item 1", quantity = 1, value = 10.0)
        val orderId = "order1"
        val itemId = "item1"
        coEvery { mockRepository.addItem(item, orderId) } returns Result.success(itemId)

        viewModel.addItem(item, orderId)

        testScheduler.advanceUntilIdle()

        val expectedState = SalesOrderViewState.Success(itemId)
        val actualState = viewModel.orderState.first()
        assertEquals(expectedState, actualState)
    }

    @Test
    fun `addItem should update state to Error when repository fails`() = runTest {
        val item = SalesItemModel(id = "1", productName = "Item 1", quantity = 1, value = 10.0)
        val orderId = "order1"
        val exception = RuntimeException("Failed to add item")
        coEvery { mockRepository.addItem(item, orderId) } returns Result.failure(exception)

    }
}
