package com.dicoding.picodiploma.loginwithanimation.view.story

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.viewModel.MainViewModel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StoryActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(StoryActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(MainViewModel.EspressoIdlingResource.idlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(MainViewModel.EspressoIdlingResource.idlingResource)
    }

    @Test
    fun addStory_withValidData_showsSuccess() {
        onView(withId(R.id.ed_add_description))
            .perform(typeText("Cerita baru untuk UI test"), closeSoftKeyboard())

        // Centang checkbox lokasi
        onView(withId(R.id.checkBoxLocation)).perform(click())

        // Tekan tombol tambah
        onView(withId(R.id.button_add)).perform(click())

        // Verifikasi bahwa pesan sukses muncul
        onView(withText("Cerita berhasil diunggah"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun addStory_withoutImage_showsError() {
        // Masukkan deskripsi cerita
        onView(withId(R.id.ed_add_description))
            .perform(typeText("Cerita tanpa gambar"), closeSoftKeyboard())

        // Tekan tombol tambah tanpa memilih gambar
        onView(withId(R.id.button_add)).perform(click())

        // Verifikasi jika muncul pesan peringatan bahwa gambar tidak ada
        onView(withText(R.string.empty_image_warning))
            .check(matches(isDisplayed()))
    }
}
