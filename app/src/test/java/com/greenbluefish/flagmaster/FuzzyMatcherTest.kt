package com.greenbluefish.flagmaster

import com.greenbluefish.flagmaster.data.Category
import com.greenbluefish.flagmaster.data.CountryData
import com.greenbluefish.flagmaster.data.FlagRepository
import com.greenbluefish.flagmaster.data.UsData
import com.greenbluefish.flagmaster.quiz.FuzzyMatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FuzzyMatcherTest {

    private val allCountries = CountryData.entries
    private val usPool = UsData.entries

    @Test
    fun blankQueryReturnsNull() {
        assertNull(FuzzyMatcher.closest("", allCountries))
        assertNull(FuzzyMatcher.closest("   ", allCountries))
    }

    @Test
    fun emptyPoolReturnsNull() {
        assertNull(FuzzyMatcher.closest("united", emptyList()))
    }

    @Test
    fun prefixQueryOverCountriesReturnsAUnitedEntry() {
        val match = FuzzyMatcher.closest("unit", allCountries)
        assertTrue(
            "expected a United* country, got ${match?.name}",
            match?.name?.startsWith("United") == true
        )
    }

    @Test
    fun usOnlyPoolNeverReturnsACountry() {
        // Any query, even one that names a country, must stay inside the US pool.
        val queries = listOf("united states", "france", "canada", "germ", "xyzzy", "cal")
        queries.forEach { q ->
            val match = FuzzyMatcher.closest(q, usPool)
            assertTrue("blank-safe query produced null for '$q'", match != null)
            assertEquals(
                "US-only pool leaked a non-US category for query '$q'",
                Category.US,
                match!!.category
            )
        }
    }

    @Test
    fun typoCalifoniaOverUsPoolReturnsCalifornia() {
        val match = FuzzyMatcher.closest("califonia", usPool)
        assertEquals("California", match?.name)
    }

    @Test
    fun selectedPoolIsolationViaRepository() {
        val usPoolFromRepo = FlagRepository.byCategories(setOf(Category.US))
        val match = FuzzyMatcher.closest("al", usPoolFromRepo)
        assertEquals(Category.US, match?.category)
    }
}
