package com.greenbluefish.flagmaster

import com.greenbluefish.flagmaster.data.Continent
import com.greenbluefish.flagmaster.data.CountryContinents
import com.greenbluefish.flagmaster.data.CountryData
import org.junit.Assert.assertEquals
import org.junit.Test

class ContinentMappingTest {

    @Test
    fun mapCoversExactly197Keys() {
        assertEquals(197, CountryContinents.byKey.size)
    }

    @Test
    fun mapKeysAreABijectionWithCountryData() {
        val dataKeys = CountryData.entries.map { it.key }.toSet()
        assertEquals(dataKeys, CountryContinents.byKey.keys)
    }

    @Test
    fun perContinentCountsMatchExpected() {
        val counts = CountryContinents.byKey.values.groupingBy { it }.eachCount()
        assertEquals(54, counts[Continent.AFRICA])
        assertEquals(49, counts[Continent.ASIA])
        assertEquals(45, counts[Continent.EUROPE])
        assertEquals(23, counts[Continent.NORTH_AMERICA])
        assertEquals(12, counts[Continent.SOUTH_AMERICA])
        assertEquals(14, counts[Continent.OCEANIA])
    }

    @Test
    fun knownOverridesAndAnchorsAreCorrect() {
        assertEquals(Continent.ASIA, CountryContinents.byKey["cy"])
        assertEquals(Continent.EUROPE, CountryContinents.byKey["ru"])
        assertEquals(Continent.ASIA, CountryContinents.byKey["tr"])
    }
}
