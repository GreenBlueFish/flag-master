package com.greenbluefish.flagmaster.quiz

import com.greenbluefish.flagmaster.data.FlagEntry

/**
 * Dependency-free single-suggestion matcher. Given the user's partial input and
 * the pool of flags for the currently selected categories, it returns the one
 * best candidate (or null for a blank query). The result is display-only: it is
 * never auto-filled into the answer field and never used for scoring.
 *
 * Ranking tiers, best first:
 *   1. prefix match  — name starts with the query (shortest name wins)
 *   2. substring     — name contains the query (shortest name wins)
 *   3. fallback      — smallest Levenshtein distance to the name
 * Ties within a tier break alphabetically for determinism.
 */
object FuzzyMatcher {

    fun closest(query: String, pool: List<FlagEntry>): FlagEntry? {
        val q = query.trim().lowercase()
        if (q.isEmpty() || pool.isEmpty()) return null

        val prefix = ArrayList<FlagEntry>()
        val substring = ArrayList<FlagEntry>()
        pool.forEach { entry ->
            val name = entry.name.lowercase()
            when {
                name.startsWith(q) -> prefix.add(entry)
                name.contains(q) -> substring.add(entry)
            }
        }

        if (prefix.isNotEmpty()) return prefix.minWith(byLengthThenName)
        if (substring.isNotEmpty()) return substring.minWith(byLengthThenName)

        // Fallback: closest by edit distance, ties broken alphabetically.
        return pool.minWith(
            compareBy<FlagEntry> { levenshtein(q, it.name.lowercase()) }
                .thenBy { it.name.lowercase() }
        )
    }

    private val byLengthThenName =
        compareBy<FlagEntry> { it.name.length }.thenBy { it.name.lowercase() }

    /** Classic O(n*m) Levenshtein edit distance over two short strings. */
    private fun levenshtein(a: String, b: String): Int {
        if (a == b) return 0
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length

        var prev = IntArray(b.length + 1) { it }
        var curr = IntArray(b.length + 1)
        for (i in 1..a.length) {
            curr[0] = i
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                curr[j] = minOf(
                    curr[j - 1] + 1,      // insertion
                    prev[j] + 1,          // deletion
                    prev[j - 1] + cost    // substitution
                )
            }
            val tmp = prev
            prev = curr
            curr = tmp
        }
        return prev[b.length]
    }
}
