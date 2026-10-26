package com.angelstudio.angelskitchen.ui.browse

import com.angelstudio.angelskitchen.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BrowseDataTest {
    private val categories = listOf(
        BrowseCategory("Beef", "", "Beef dishes"),
        BrowseCategory("Seafood", "", "Fish and shellfish"),
        BrowseCategory("Vegetarian", "", "Meat-free dishes"),
    )

    @Test fun categorySearchIsTrimmedCaseInsensitiveAndUsesActualNames() {
        assertEquals(listOf(categories[1]), filterCategories(categories, "  SEA  "))
        assertEquals(categories, filterCategories(categories, " "))
        assertTrue(filterCategories(categories, "Fish").isEmpty())
        assertTrue(filterCategories(categories, "Dinner").isEmpty())
    }

    @Test fun legacyCountriesRemainAvailableWithUniqueRouteAndResourceIdentities() {
        val originalAreas = setOf(
            "canadian", "british", "american", "jamaican", "chinese", "dutch",
            "egyptian", "french", "greek", "indian", "irish", "italian", "japanese",
            "kenyan", "malaysian", "mexican", "moroccan", "croatian", "norwegian",
            "portuguese", "russian", "argentinian", "spanish", "slovakian", "thai",
            "vietnamese", "turkish", "syrian", "algerian", "tunisian",
        )
        originalAreas.forEach { assertTrue(it, findBrowseCountry(it) != null) }
        assertEquals(browseCountries.size, browseCountries.map { it.apiArea }.toSet().size)
        assertEquals(browseCountries.size, browseCountries.map { it.name }.toSet().size)
        assertEquals("italian", browseCountries.single { it.name == R.string.browse_italy }.apiArea)
        assertTrue(browseCountries.any { it.name == R.string.browse_argentina })
        assertTrue(browseCountries.any { it.name == R.string.browse_slovakia })
    }

    @Test fun regionsAreDeterministicAndPartitionAllCountries() {
        assertEquals(browseCountries, filterCountries(BrowseRegion.All))
        val regionalCountries = BrowseRegion.entries.filter { it != BrowseRegion.All }
            .flatMap { region ->
                filterCountries(region).also { countries ->
                    assertFalse(countries.isEmpty())
                    assertTrue(countries.all { it.region == region })
                    assertEquals(countries, filterCountries(region))
                }
            }
        assertEquals(browseCountries.toSet(), regionalCountries.toSet())
        assertEquals(browseCountries.size, regionalCountries.size)
        assertTrue(filterCountries(BrowseRegion.Asia).any { it.apiArea == "japanese" })
        assertFalse(filterCountries(BrowseRegion.Europe).any { it.apiArea == "mexican" })
    }

    @Test fun gridAdaptsToNarrowScreensLargeFontsAndTablets() {
        assertEquals(2, browseColumnCount(360f, 1f))
        assertEquals(2, browseColumnCount(540f, 1f))
        assertEquals(1, browseColumnCount(280f, 1f))
        assertEquals(1, browseColumnCount(360f, 2f))
        assertEquals(4, browseColumnCount(1000f, 1f))
        assertEquals(browseColumnCount(1200f, 1f), browseColumnCount(2000f, 1f))
    }

    @Test fun allOfferedCountriesHaveExplicitLocalFlags() {
        assertTrue(browseCountries.all { countryFlagResource(it.apiArea) != R.drawable.unknown })
        assertEquals(browseCountries.size, browseCountries.map { countryFlagResource(it.apiArea) }.toSet().size)
        assertEquals(R.drawable.flag_it, countryFlagResource("italian"))
        assertEquals(R.drawable.unknown, countryFlagResource("unsupported"))
    }

    @Test fun everyCountryHasMatchingIsoFlagEmoji() {
        browseCountries.forEach { country ->
            val expected = country.countryCode.map { letter ->
                String(Character.toChars(0x1F1E6 + letter.code - 'a'.code))
            }.joinToString("")
            assertEquals(country.apiArea, expected, country.flag)
        }
    }

    @Test fun additionalRecipeCuisinesHaveCorrectRegionsAndRouteValues() {
        val additions = mapOf(
            "polish" to BrowseRegion.Europe,
            "ukrainian" to BrowseRegion.Europe,
            "filipino" to BrowseRegion.Asia,
            "australian" to BrowseRegion.Oceania,
            "saudi arabian" to BrowseRegion.Asia,
            "laos" to BrowseRegion.Asia,
        )
        additions.forEach { (area, region) ->
            assertEquals(region, browseCountries.single { it.apiArea == area }.region)
        }
        assertTrue(filterCountries(BrowseRegion.Oceania).any { it.apiArea == "australian" })
    }

    @Test fun legacyAliasesResolveToWorkingCountryFiltersAndUniqueIsoKeys() {
        val corrections = mapOf(
            "french" to "france", "indian" to "india", "american" to "united states",
            "dutch" to "netherlands", "norwegian" to "norway",
            "argentinian" to "argentina", "slovakian" to "slovakia", "laotian" to "laos",
        )
        corrections.forEach { (old, current) ->
            assertEquals(current, findBrowseCountry(" $old ")?.apiArea)
        }
        assertEquals(browseCountries.size, browseCountries.map { it.countryCode }.toSet().size)
        assertEquals(195, browseCountries.size)
        assertEquals(null, findBrowseCountry("not-a-country"))
    }
}
