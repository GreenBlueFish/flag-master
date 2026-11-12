package com.greenbluefish.flagmaster.data

/**
 * The six continent buckets used to scope the country quiz pool. Only the
 * COUNTRY category is divided by continent; US and CANADA are never filtered
 * this way.
 */
enum class Continent { AFRICA, ASIA, EUROPE, NORTH_AMERICA, SOUTH_AMERICA, OCEANIA }

/** Human-readable label for a continent, used by the setup dropdown. */
fun Continent.label(): String = when (this) {
    Continent.AFRICA -> "Africa"
    Continent.ASIA -> "Asia"
    Continent.EUROPE -> "Europe"
    Continent.NORTH_AMERICA -> "North America"
    Continent.SOUTH_AMERICA -> "South America"
    Continent.OCEANIA -> "Oceania"
}

/**
 * Explicit per-key continent mapping for every COUNTRY entry in [CountryData].
 *
 * Buckets are derived from each entry's `region` (regions ending in a continent
 * name map to that continent; the Americas sub-regions North/Central America and
 * Caribbean all map to NORTH_AMERICA), with one deliberate override: Cyprus
 * ("cy", region "Southern Europe") is bucketed as ASIA. The map is authored
 * explicitly rather than derived at runtime precisely because of that override.
 *
 * Counts: AFRICA 54, ASIA 49, EUROPE 45, NORTH_AMERICA 23, SOUTH_AMERICA 12,
 * OCEANIA 14 (sum 197).
 */
object CountryContinents {

    val byKey: Map<String, Continent> = mapOf(
        // AFRICA (54)
        "dz" to Continent.AFRICA, "ao" to Continent.AFRICA, "bj" to Continent.AFRICA,
        "bw" to Continent.AFRICA, "bf" to Continent.AFRICA, "bi" to Continent.AFRICA,
        "cv" to Continent.AFRICA, "cm" to Continent.AFRICA, "cf" to Continent.AFRICA,
        "td" to Continent.AFRICA, "km" to Continent.AFRICA, "cg" to Continent.AFRICA,
        "cd" to Continent.AFRICA, "ci" to Continent.AFRICA, "dj" to Continent.AFRICA,
        "eg" to Continent.AFRICA, "gq" to Continent.AFRICA, "er" to Continent.AFRICA,
        "sz" to Continent.AFRICA, "et" to Continent.AFRICA, "ga" to Continent.AFRICA,
        "gm" to Continent.AFRICA, "gh" to Continent.AFRICA, "gn" to Continent.AFRICA,
        "gw" to Continent.AFRICA, "ke" to Continent.AFRICA, "ls" to Continent.AFRICA,
        "lr" to Continent.AFRICA, "ly" to Continent.AFRICA, "mg" to Continent.AFRICA,
        "mw" to Continent.AFRICA, "ml" to Continent.AFRICA, "mr" to Continent.AFRICA,
        "mu" to Continent.AFRICA, "ma" to Continent.AFRICA, "mz" to Continent.AFRICA,
        "na" to Continent.AFRICA, "ne" to Continent.AFRICA, "ng" to Continent.AFRICA,
        "rw" to Continent.AFRICA, "st" to Continent.AFRICA, "sn" to Continent.AFRICA,
        "sc" to Continent.AFRICA, "sl" to Continent.AFRICA, "so" to Continent.AFRICA,
        "za" to Continent.AFRICA, "ss" to Continent.AFRICA, "sd" to Continent.AFRICA,
        "tz" to Continent.AFRICA, "tg" to Continent.AFRICA, "tn" to Continent.AFRICA,
        "ug" to Continent.AFRICA, "zm" to Continent.AFRICA, "zw" to Continent.AFRICA,

        // ASIA (49) — includes Cyprus ("cy") overridden from Southern Europe.
        "af" to Continent.ASIA, "am" to Continent.ASIA, "az" to Continent.ASIA,
        "bh" to Continent.ASIA, "bd" to Continent.ASIA, "bt" to Continent.ASIA,
        "bn" to Continent.ASIA, "kh" to Continent.ASIA, "cn" to Continent.ASIA,
        "cy" to Continent.ASIA, "ge" to Continent.ASIA, "in" to Continent.ASIA,
        "id" to Continent.ASIA, "ir" to Continent.ASIA, "iq" to Continent.ASIA,
        "il" to Continent.ASIA, "jp" to Continent.ASIA, "jo" to Continent.ASIA,
        "kz" to Continent.ASIA, "kp" to Continent.ASIA, "kr" to Continent.ASIA,
        "kw" to Continent.ASIA, "kg" to Continent.ASIA, "la" to Continent.ASIA,
        "lb" to Continent.ASIA, "my" to Continent.ASIA, "mv" to Continent.ASIA,
        "mn" to Continent.ASIA, "mm" to Continent.ASIA, "np" to Continent.ASIA,
        "om" to Continent.ASIA, "pk" to Continent.ASIA, "ph" to Continent.ASIA,
        "qa" to Continent.ASIA, "sa" to Continent.ASIA, "sg" to Continent.ASIA,
        "lk" to Continent.ASIA, "sy" to Continent.ASIA, "tj" to Continent.ASIA,
        "th" to Continent.ASIA, "tl" to Continent.ASIA, "tr" to Continent.ASIA,
        "tm" to Continent.ASIA, "ae" to Continent.ASIA, "uz" to Continent.ASIA,
        "vn" to Continent.ASIA, "ye" to Continent.ASIA, "tw" to Continent.ASIA,
        "ps" to Continent.ASIA,

        // EUROPE (45)
        "al" to Continent.EUROPE, "ad" to Continent.EUROPE, "at" to Continent.EUROPE,
        "by" to Continent.EUROPE, "be" to Continent.EUROPE, "ba" to Continent.EUROPE,
        "bg" to Continent.EUROPE, "hr" to Continent.EUROPE, "cz" to Continent.EUROPE,
        "dk" to Continent.EUROPE, "ee" to Continent.EUROPE, "fi" to Continent.EUROPE,
        "fr" to Continent.EUROPE, "de" to Continent.EUROPE, "gr" to Continent.EUROPE,
        "hu" to Continent.EUROPE, "is" to Continent.EUROPE, "ie" to Continent.EUROPE,
        "it" to Continent.EUROPE, "lv" to Continent.EUROPE, "li" to Continent.EUROPE,
        "lt" to Continent.EUROPE, "lu" to Continent.EUROPE, "mt" to Continent.EUROPE,
        "md" to Continent.EUROPE, "mc" to Continent.EUROPE, "me" to Continent.EUROPE,
        "nl" to Continent.EUROPE, "mk" to Continent.EUROPE, "no" to Continent.EUROPE,
        "pl" to Continent.EUROPE, "pt" to Continent.EUROPE, "ro" to Continent.EUROPE,
        "ru" to Continent.EUROPE, "sm" to Continent.EUROPE, "rs" to Continent.EUROPE,
        "sk" to Continent.EUROPE, "si" to Continent.EUROPE, "es" to Continent.EUROPE,
        "se" to Continent.EUROPE, "ch" to Continent.EUROPE, "ua" to Continent.EUROPE,
        "gb" to Continent.EUROPE, "xk" to Continent.EUROPE, "va" to Continent.EUROPE,

        // NORTH_AMERICA (23) — North America + Central America + Caribbean.
        "ag" to Continent.NORTH_AMERICA, "bs" to Continent.NORTH_AMERICA,
        "bb" to Continent.NORTH_AMERICA, "bz" to Continent.NORTH_AMERICA,
        "ca" to Continent.NORTH_AMERICA, "cr" to Continent.NORTH_AMERICA,
        "cu" to Continent.NORTH_AMERICA, "dm" to Continent.NORTH_AMERICA,
        "do" to Continent.NORTH_AMERICA, "sv" to Continent.NORTH_AMERICA,
        "gd" to Continent.NORTH_AMERICA, "gt" to Continent.NORTH_AMERICA,
        "ht" to Continent.NORTH_AMERICA, "hn" to Continent.NORTH_AMERICA,
        "jm" to Continent.NORTH_AMERICA, "mx" to Continent.NORTH_AMERICA,
        "ni" to Continent.NORTH_AMERICA, "pa" to Continent.NORTH_AMERICA,
        "kn" to Continent.NORTH_AMERICA, "lc" to Continent.NORTH_AMERICA,
        "vc" to Continent.NORTH_AMERICA, "tt" to Continent.NORTH_AMERICA,
        "us" to Continent.NORTH_AMERICA,

        // SOUTH_AMERICA (12)
        "ar" to Continent.SOUTH_AMERICA, "bo" to Continent.SOUTH_AMERICA,
        "br" to Continent.SOUTH_AMERICA, "cl" to Continent.SOUTH_AMERICA,
        "co" to Continent.SOUTH_AMERICA, "ec" to Continent.SOUTH_AMERICA,
        "gy" to Continent.SOUTH_AMERICA, "py" to Continent.SOUTH_AMERICA,
        "pe" to Continent.SOUTH_AMERICA, "sr" to Continent.SOUTH_AMERICA,
        "uy" to Continent.SOUTH_AMERICA, "ve" to Continent.SOUTH_AMERICA,

        // OCEANIA (14)
        "au" to Continent.OCEANIA, "fj" to Continent.OCEANIA, "ki" to Continent.OCEANIA,
        "mh" to Continent.OCEANIA, "fm" to Continent.OCEANIA, "nr" to Continent.OCEANIA,
        "nz" to Continent.OCEANIA, "pw" to Continent.OCEANIA, "pg" to Continent.OCEANIA,
        "ws" to Continent.OCEANIA, "sb" to Continent.OCEANIA, "to" to Continent.OCEANIA,
        "tv" to Continent.OCEANIA, "vu" to Continent.OCEANIA,
    )

    /** The country keys belonging to any continent in [set] (empty -> empty). */
    fun keysFor(set: Set<Continent>): Set<String> =
        byKey.filterValues { it in set }.keys
}
