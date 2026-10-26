package com.angelstudio.angelskitchen.benchmark

import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CountryGridScrollBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollCountryGrid() = benchmarkRule.measureRepeated(
        packageName = "com.angelstudio.angelskitchen",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        setupBlock = {
            startActivityAndWait()
            val worldwideTab = device.wait(Until.findObject(By.text("Worldwide")), 10_000)
                ?: error("Worldwide tab did not appear")
            worldwideTab.click()
            check(device.wait(Until.hasObject(By.text("Europe")), 10_000)) {
                "Country grid did not appear"
            }
        },
        measureBlock = {
            repeat(5) {
                device.swipe(
                    device.displayWidth / 2,
                    device.displayHeight * 4 / 5,
                    device.displayWidth / 2,
                    device.displayHeight / 3,
                    20,
                )
            }
            repeat(5) {
                device.swipe(
                    device.displayWidth / 2,
                    device.displayHeight / 3,
                    device.displayWidth / 2,
                    device.displayHeight * 4 / 5,
                    20,
                )
            }
        },
    )
}
