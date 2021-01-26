package io.material.materialthemebuilder.data

import android.content.SharedPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class PreferenceRepositoryTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    lateinit var repository: PreferenceRepository
    lateinit var sharedPreferences: SharedPreferences

    @Before
    fun initialize() {
        sharedPreferences = ShadowSharedPreference()
        repository = PreferenceRepository(sharedPreferences)
    }

    @Test
    fun `testing changes in preferences`() = coroutineTestRule.runBlockingTest {

        repository.isDarkTheme = true
        delay(5000)
        repository.isDarkThemeLive.collect {
            Assert.assertTrue("$it", it)
        }

        repository.isDarkTheme = false
        delay(5000)
        repository.isDarkThemeLive.collect {
            Assert.assertFalse("$it", it)
        }

    }


}