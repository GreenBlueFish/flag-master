package com.greenbluefish.flagmaster.data

/**
 * Single source of truth over the three hand-authored data sets. All lookups
 * and the quiz pool flow through here so a category is never leaked outside
 * the user's current selection.
 */
object FlagRepository {

    private val everything: List<FlagEntry> =
        CountryData.entries + UsData.entries + CanadaData.entries

    private val byKeyIndex: Map<String, FlagEntry> =
        everything.associateBy { it.key }

    /** Every flag across all categories. */
    fun all(): List<FlagEntry> = everything

    /** The subset of flags whose category is in [set] (empty set -> empty list). */
    fun byCategories(set: Set<Category>): List<FlagEntry> =
        if (set.isEmpty()) emptyList() else everything.filter { it.category in set }

    /** COUNTRY entries whose key falls in one of the selected [continents]. */
    fun countriesByContinents(continents: Set<Continent>): List<FlagEntry> {
        val keys = CountryContinents.keysFor(continents)
        return CountryData.entries.filter { it.key in keys }
    }

    /**
     * The quiz pool for the current selection: COUNTRY entries are scoped to the
     * chosen [continents]; US and CANADA are included whole (never filtered by
     * continent) when their category is selected.
     */
    fun poolFor(categories: Set<Category>, continents: Set<Continent>): List<FlagEntry> {
        val result = ArrayList<FlagEntry>()
        if (Category.COUNTRY in categories) result += countriesByContinents(continents)
        if (Category.US in categories) result += UsData.entries
        if (Category.CANADA in categories) result += CanadaData.entries
        return result
    }

    /** Exact, case-insensitive, trimmed name match used for scoring. */
    fun find(name: String): FlagEntry? {
        val needle = name.trim()
        if (needle.isEmpty()) return null
        return everything.firstOrNull { it.name.equals(needle, ignoreCase = true) }
    }

    /** Direct asset-key lookup, e.g. for the detail/{key} route. */
    fun byKey(key: String): FlagEntry? = byKeyIndex[key]
}
