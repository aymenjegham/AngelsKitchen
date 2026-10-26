package com.angelstudio.angelskitchen.ui.browse

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CountryCatalogTest {
    @Test
    fun everyCountryHasReadableDistinctNameDescriptionAndDecodableFlag() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val names = browseCountries.map { context.getString(it.name) }
        assertEquals(names.size, names.toSet().size)
        assertEquals(supportedCountryAreas, browseCountries.map { it.apiArea }.toSet())
        browseCountries.forEach { country ->
            val name = context.getString(country.name)
            assertTrue(name, name.isNotBlank())
            assertTrue(name, context.getString(country.description).isNotBlank())
            assertTrue(name, country.region != BrowseRegion.All)
            assertNotNull(name, context.getDrawable(countryFlagResource(country.apiArea)))
        }
    }
}
