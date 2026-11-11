package com.greenbluefish.flagmaster.data

/** The three quiz/explore categories a flag can belong to. */
enum class Category { COUNTRY, US, CANADA }

/**
 * A single flag record. [key] is the base filename of the bundled asset:
 * `assets/flags/<key>.png`. [name] is the canonical, exact answer used for
 * scoring. [lat]/[lon] are the capital (or centroid) used to place the map pin.
 */
data class FlagEntry(
    val key: String,
    val name: String,
    val category: Category,
    val region: String,
    val capital: String,
    val lat: Double,
    val lon: Double,
    val funFact: String,
)
