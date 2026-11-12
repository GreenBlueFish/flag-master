package com.greenbluefish.flagmaster.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.greenbluefish.flagmaster.data.FlagRepository
import com.greenbluefish.flagmaster.ui.MapView
import com.greenbluefish.flagmaster.ui.components.FlagImage

/**
 * Detail view for a single flag resolved from the {key} nav argument: large
 * flag, name, region, capital, a fun fact, and the offline map with a pin at
 * the entry's lat/lon.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    flagKey: String,
) {
    val entry = FlagRepository.byKey(flagKey)

    Scaffold(
        topBar = { TopAppBar(title = { Text(entry?.name ?: "Flag") }) },
    ) { padding ->
        if (entry == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
            ) {
                Text("Flag not found.", style = MaterialTheme.typography.bodyLarge)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            FlagImage(
                flagKey = entry.key,
                contentDescription = "${entry.name} flag",
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = entry.name,
                style = MaterialTheme.typography.displaySmall,
            )

            LabeledFact(label = "Region", value = entry.region)
            LabeledFact(label = "Capital", value = entry.capital)

            Text(
                text = "Did you know?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = entry.funFact,
                style = MaterialTheme.typography.bodyLarge,
            )

            Text(
                text = "Where it is",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            MapView(
                lat = entry.lat,
                lon = entry.lon,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun LabeledFact(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
