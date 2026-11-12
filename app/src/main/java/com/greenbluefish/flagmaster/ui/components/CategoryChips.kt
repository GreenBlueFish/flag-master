package com.greenbluefish.flagmaster.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.greenbluefish.flagmaster.data.Category

/** Human-readable label for each quiz/explore category. */
fun Category.label(): String = when (this) {
    Category.COUNTRY -> "Countries"
    Category.US -> "US States"
    Category.CANADA -> "Canadian Provinces"
}

/**
 * A row of Material 3 FilterChips, one per category, reflecting [selected].
 * Used both for quiz setup toggles and the explore filter. Selection logic is
 * owned by the caller via [onToggle].
 */
@Composable
fun CategoryChips(
    selected: Set<Category>,
    onToggle: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Category.entries.forEach { category ->
            FilterChip(
                selected = category in selected,
                onClick = { onToggle(category) },
                label = { Text(category.label()) },
            )
        }
    }
}
