package com.greenbluefish.flagmaster.data

import com.greenbluefish.flagmaster.data.Category.CANADA

/**
 * Canada's 10 provinces and 3 territories. Keys match assets/flags/<key>.png,
 * sourced from Wikimedia since flagcdn has no ca-<code> entries.
 */
object CanadaData {
    val entries: List<FlagEntry> = listOf(
        FlagEntry("ca-ab", "Alberta", CANADA, "Province of Canada", "Edmonton", 53.5461, -113.4938, "The Canadian Rockies and Banff, Canada's first national park, lie here."),
        FlagEntry("ca-bc", "British Columbia", CANADA, "Province of Canada", "Victoria", 48.4284, -123.3656, "Its flag shows a setting sun over the Pacific Ocean."),
        FlagEntry("ca-mb", "Manitoba", CANADA, "Province of Canada", "Winnipeg", 49.8951, -97.1384, "Churchill is famed as the polar bear capital of the world."),
        FlagEntry("ca-nb", "New Brunswick", CANADA, "Province of Canada", "Fredericton", 45.9636, -66.6431, "The Bay of Fundy here has the highest tides on Earth."),
        FlagEntry("ca-nl", "Newfoundland and Labrador", CANADA, "Province of Canada", "St. John's", 47.5615, -52.7126, "St. John's is the oldest European-founded city in North America."),
        FlagEntry("ca-ns", "Nova Scotia", CANADA, "Province of Canada", "Halifax", 44.6488, -63.5752, "Its flag reverses the colors of Scotland's Saint Andrew's cross."),
        FlagEntry("ca-on", "Ontario", CANADA, "Province of Canada", "Toronto", 43.6532, -79.3832, "The Canadian side of Niagara Falls lies within Ontario."),
        FlagEntry("ca-pe", "Prince Edward Island", CANADA, "Province of Canada", "Charlottetown", 46.2382, -63.1311, "The smallest Canadian province and the setting of 'Anne of Green Gables'."),
        FlagEntry("ca-qc", "Quebec", CANADA, "Province of Canada", "Quebec City", 46.8139, -71.2080, "The only Canadian province where French is the sole official language."),
        FlagEntry("ca-sk", "Saskatchewan", CANADA, "Province of Canada", "Regina", 50.4452, -104.6189, "Produces a large share of the world's exported lentils and canola."),
        FlagEntry("ca-yt", "Yukon", CANADA, "Territory of Canada", "Whitehorse", 60.7212, -135.0568, "Mount Logan, Canada's highest peak, rises here."),
        FlagEntry("ca-nt", "Northwest Territories", CANADA, "Territory of Canada", "Yellowknife", 62.4540, -114.3718, "Great Bear Lake, the largest lake entirely within Canada, lies here."),
        FlagEntry("ca-nu", "Nunavut", CANADA, "Territory of Canada", "Iqaluit", 63.7467, -68.5170, "Canada's largest and newest territory, created in 1999."),
    )
}
