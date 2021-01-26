package io.material.materialthemebuilder.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class CoroutineTestRule : TestWatcher() {

    val testDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope: TestCoroutineScope = TestCoroutineScope()


    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        testDispatcher.cleanupTestCoroutines()
        Dispatchers.resetMain()
        super.finished(description)
    }

    fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) {
        testCoroutineScope.runBlockingTest(block)
    }
}