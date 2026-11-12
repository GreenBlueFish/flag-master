package com.greenbluefish.flagmaster.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.greenbluefish.flagmaster.data.Category
import com.greenbluefish.flagmaster.data.FlagRepository
import com.greenbluefish.flagmaster.ui.components.CategoryChips
import com.greenbluefish.flagmaster.ui.components.EntryCard

/**
 * Browse every flag. A search field filters by name, the category chips filter
 * by category (no chip selected = show all). Tapping a card navigates to the
 * detail route for that entry's key.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onOpenDetail: (String) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf(setOf<Category>()) }

    val entries = remember(query, selected) {
        val base = if (selected.isEmpty()) FlagRepository.all()
        else FlagRepository.byCategories(selected)
        val q = query.trim().lowercase()
        (if (q.isEmpty()) base else base.filter { it.name.lowercase().contains(q) })
            .sortedBy { it.name }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Explore Flags") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search by name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            CategoryChips(
                selected = selected,
                onToggle = { category ->
                    selected = if (category in selected) selected - category else selected + category
                },
            )

            if (entries.isEmpty()) {
                Text(
                    text = "No flags match your search.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(entries, key = { it.key }) { entry ->
                        EntryCard(entry = entry, onClick = { onOpenDetail(entry.key) })
                    }
                }
            }
        }
    }
}
