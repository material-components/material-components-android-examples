package io.material.materialthemebuilder.data

import android.content.SharedPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
    fun `Given a preference When it is set to true Then the stateflow changes its value to true`() = coroutineTestRule.runBlockingTest {
        repository.isDarkTheme = true
        Assert.assertTrue(repository.isDarkThemeLive.first())
    }

    @Test
    fun `Given a preference When it is set to false Then the stateflow changes its value to fals`() = coroutineTestRule.runBlockingTest {
        repository.isDarkTheme = false
        Assert.assertFalse(repository.isDarkThemeLive.first())
    }


}