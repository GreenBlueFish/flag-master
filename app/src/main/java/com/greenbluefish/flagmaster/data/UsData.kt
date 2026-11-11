package com.greenbluefish.flagmaster.data

import com.greenbluefish.flagmaster.data.Category.US

/**
 * The 50 U.S. states, the District of Columbia, and the five major U.S.
 * territories. Territory keys (pr/gu/vi/as/mp) use the flagcdn country-code
 * form but are classified Category.US. Keys match assets/flags/<key>.png.
 */
object UsData {
    val entries: List<FlagEntry> = listOf(
        FlagEntry("us-al", "Alabama", US, "U.S. State", "Montgomery", 32.3770, -86.3000, "Rocket City, Huntsville, helped build the rockets that reached the Moon."),
        FlagEntry("us-ak", "Alaska", US, "U.S. State", "Juneau", 58.3019, -134.4197, "Its flag was designed by a 13-year-old boy in a 1927 contest."),
        FlagEntry("us-az", "Arizona", US, "U.S. State", "Phoenix", 33.4484, -112.0740, "The Grand Canyon is one of its most famous natural wonders."),
        FlagEntry("us-ar", "Arkansas", US, "U.S. State", "Little Rock", 34.7465, -92.2896, "The only U.S. state with an active, publicly accessible diamond mine."),
        FlagEntry("us-ca", "California", US, "U.S. State", "Sacramento", 38.5816, -121.4944, "If it were a country, it would rank among the world's largest economies."),
        FlagEntry("us-co", "Colorado", US, "U.S. State", "Denver", 39.7392, -104.9903, "Denver sits exactly one mile above sea level, hence the 'Mile High City'."),
        FlagEntry("us-ct", "Connecticut", US, "U.S. State", "Hartford", 41.7637, -72.6851, "Home to the first written constitution, the Fundamental Orders of 1639."),
        FlagEntry("us-de", "Delaware", US, "U.S. State", "Dover", 39.1582, -75.5244, "The 'First State' to ratify the U.S. Constitution in 1787."),
        FlagEntry("us-fl", "Florida", US, "U.S. State", "Tallahassee", 30.4383, -84.2807, "Has the longest coastline of any state in the contiguous U.S."),
        FlagEntry("us-ga", "Georgia", US, "U.S. State", "Atlanta", 33.7490, -84.3880, "The world's busiest airport by passenger traffic is in Atlanta."),
        FlagEntry("us-hi", "Hawaii", US, "U.S. State", "Honolulu", 21.3069, -157.8583, "The only U.S. state made up entirely of islands."),
        FlagEntry("us-id", "Idaho", US, "U.S. State", "Boise", 43.6150, -116.2023, "Produces about a third of all potatoes grown in the United States."),
        FlagEntry("us-il", "Illinois", US, "U.S. State", "Springfield", 39.7980, -89.6440, "Chicago is credited with the first modern skyscraper, built in 1885."),
        FlagEntry("us-in", "Indiana", US, "U.S. State", "Indianapolis", 39.7684, -86.1581, "Hosts the Indianapolis 500, one of the oldest auto races in the world."),
        FlagEntry("us-ia", "Iowa", US, "U.S. State", "Des Moines", 41.5868, -93.6250, "Leads the nation in corn production year after year."),
        FlagEntry("us-ks", "Kansas", US, "U.S. State", "Topeka", 39.0473, -95.6752, "The geographic center of the contiguous 48 states lies in Kansas."),
        FlagEntry("us-ky", "Kentucky", US, "U.S. State", "Frankfort", 38.2009, -84.8733, "Home to the Kentucky Derby and most of the world's bourbon."),
        FlagEntry("us-la", "Louisiana", US, "U.S. State", "Baton Rouge", 30.4515, -91.1871, "The only state with parishes instead of counties."),
        FlagEntry("us-me", "Maine", US, "U.S. State", "Augusta", 44.3106, -69.7795, "Supplies the vast majority of America's lobster catch."),
        FlagEntry("us-md", "Maryland", US, "U.S. State", "Annapolis", 38.9784, -76.4922, "Its flag is based on the heraldic arms of the Calvert family."),
        FlagEntry("us-ma", "Massachusetts", US, "U.S. State", "Boston", 42.3601, -71.0589, "Harvard, founded in 1636, is the oldest U.S. university."),
        FlagEntry("us-mi", "Michigan", US, "U.S. State", "Lansing", 42.7325, -84.5555, "Bordered by four of the five Great Lakes."),
        FlagEntry("us-mn", "Minnesota", US, "U.S. State", "Saint Paul", 44.9537, -93.0900, "Nicknamed the 'Land of 10,000 Lakes', it actually has more than 11,000."),
        FlagEntry("us-ms", "Mississippi", US, "U.S. State", "Jackson", 32.2988, -90.1848, "The teddy bear is named after a hunting trip President Roosevelt took here."),
        FlagEntry("us-mo", "Missouri", US, "U.S. State", "Jefferson City", 38.5767, -92.1735, "The Gateway Arch in St. Louis is the tallest arch in the world."),
        FlagEntry("us-mt", "Montana", US, "U.S. State", "Helena", 46.5891, -112.0391, "Nicknamed 'Big Sky Country' for its vast open landscapes."),
        FlagEntry("us-ne", "Nebraska", US, "U.S. State", "Lincoln", 40.8136, -96.7026, "The only U.S. state with a single-house, nonpartisan legislature."),
        FlagEntry("us-nv", "Nevada", US, "U.S. State", "Carson City", 39.1638, -119.7674, "Produces most of the gold mined in the United States."),
        FlagEntry("us-nh", "New Hampshire", US, "U.S. State", "Concord", 43.2081, -71.5376, "Its state motto is the famous phrase 'Live Free or Die'."),
        FlagEntry("us-nj", "New Jersey", US, "U.S. State", "Trenton", 40.2206, -74.7597, "Has more diners than any other U.S. state."),
        FlagEntry("us-nm", "New Mexico", US, "U.S. State", "Santa Fe", 35.6870, -105.9378, "Santa Fe is the oldest state capital in the United States."),
        FlagEntry("us-ny", "New York", US, "U.S. State", "Albany", 42.6526, -73.7562, "The New York City subway is one of the largest transit systems in the world."),
        FlagEntry("us-nc", "North Carolina", US, "U.S. State", "Raleigh", 35.7796, -78.6382, "The Wright brothers made the first powered flight at Kitty Hawk."),
        FlagEntry("us-nd", "North Dakota", US, "U.S. State", "Bismarck", 46.8083, -100.7837, "Leads the nation in production of spring wheat and sunflowers."),
        FlagEntry("us-oh", "Ohio", US, "U.S. State", "Columbus", 39.9612, -82.9988, "The only U.S. state with a non-rectangular flag, a swallowtail pennant."),
        FlagEntry("us-ok", "Oklahoma", US, "U.S. State", "Oklahoma City", 35.4676, -97.5164, "Had the world's first installed parking meter, in 1935."),
        FlagEntry("us-or", "Oregon", US, "U.S. State", "Salem", 44.9429, -123.0351, "Crater Lake is the deepest lake in the United States."),
        FlagEntry("us-pa", "Pennsylvania", US, "U.S. State", "Harrisburg", 40.2732, -76.8867, "The Declaration of Independence was signed in Philadelphia."),
        FlagEntry("us-ri", "Rhode Island", US, "U.S. State", "Providence", 41.8240, -71.4128, "The smallest U.S. state by land area."),
        FlagEntry("us-sc", "South Carolina", US, "U.S. State", "Columbia", 34.0007, -81.0348, "Its palmetto-and-crescent flag is among the most recognizable state flags."),
        FlagEntry("us-sd", "South Dakota", US, "U.S. State", "Pierre", 44.3683, -100.3510, "Mount Rushmore's four presidential faces are carved here."),
        FlagEntry("us-tn", "Tennessee", US, "U.S. State", "Nashville", 36.1627, -86.7816, "Nashville is renowned as the heart of country music."),
        FlagEntry("us-tx", "Texas", US, "U.S. State", "Austin", 30.2672, -97.7431, "The 'Lone Star State' was once an independent republic."),
        FlagEntry("us-ut", "Utah", US, "U.S. State", "Salt Lake City", 40.7608, -111.8910, "The Great Salt Lake is far saltier than the ocean."),
        FlagEntry("us-vt", "Vermont", US, "U.S. State", "Montpelier", 44.2601, -72.5754, "Produces more maple syrup than any other U.S. state."),
        FlagEntry("us-va", "Virginia", US, "U.S. State", "Richmond", 37.5407, -77.4360, "Eight U.S. presidents were born here, more than any other state."),
        FlagEntry("us-wa", "Washington", US, "U.S. State", "Olympia", 47.0379, -122.9007, "The only U.S. state named after a president."),
        FlagEntry("us-wv", "West Virginia", US, "U.S. State", "Charleston", 38.3498, -81.6326, "Formed when it split from Virginia during the Civil War in 1863."),
        FlagEntry("us-wi", "Wisconsin", US, "U.S. State", "Madison", 43.0731, -89.4012, "Produces about a quarter of all cheese made in the United States."),
        FlagEntry("us-wy", "Wyoming", US, "U.S. State", "Cheyenne", 41.1400, -104.8202, "The first U.S. state or territory to grant women the right to vote."),
        FlagEntry("us-dc", "Washington, D.C.", US, "U.S. Federal District", "Washington", 38.9072, -77.0369, "The three stars and two bars on its flag come from George Washington's family coat of arms."),
        FlagEntry("pr", "Puerto Rico", US, "U.S. Territory", "San Juan", 18.4655, -66.1057, "Home to El Yunque, the only tropical rainforest in the U.S. forest system."),
        FlagEntry("gu", "Guam", US, "U.S. Territory", "Hagåtña", 13.4443, 144.7937, "The westernmost point of the United States, where America's day begins."),
        FlagEntry("vi", "U.S. Virgin Islands", US, "U.S. Territory", "Charlotte Amalie", 18.3419, -64.9307, "The only part of the United States where drivers keep to the left."),
        FlagEntry("as", "American Samoa", US, "U.S. Territory", "Pago Pago", -14.2710, -170.7046, "The only U.S. territory located in the Southern Hemisphere."),
        FlagEntry("mp", "Northern Mariana Islands", US, "U.S. Territory", "Saipan", 15.1850, 145.7467, "The nearby Mariana Trench is the deepest point in any ocean."),
    )
}
