package com.greenbluefish.flagmaster.quiz

import androidx.compose.animation.Crossfade
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.greenbluefish.flagmaster.data.Category
import com.greenbluefish.flagmaster.data.Continent
import com.greenbluefish.flagmaster.data.label
import com.greenbluefish.flagmaster.ui.MapView
import com.greenbluefish.flagmaster.ui.components.FlagImage
import com.greenbluefish.flagmaster.ui.components.ScoreBadge
import com.greenbluefish.flagmaster.ui.components.label
import com.greenbluefish.flagmaster.ui.theme.CorrectGreen
import com.greenbluefish.flagmaster.ui.theme.WrongRed

/**
 * Setup screen: three toggles (Countries / US States / Canadian Provinces).
 * When Countries is on, a continent sub-selector appears (all six by default)
 * with a Select-all control; turning off the last continent turns Countries
 * off and hides the selector, and re-enabling Countries resets to all six. A
 * global fuzzy-scope Switch controls whether the spelling hint is drawn from
 * the selected pool (on) or every flag (off). Start is enabled only when the
 * effective pool is non-empty; it then hands the three values to [onStart].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSetupScreen(
    onStart: (Set<Category>, Set<Continent>, Boolean) -> Unit,
    onBack: () -> Unit,
) {
    var selected by remember { mutableStateOf(setOf<Category>()) }
    var selectedContinents by remember { mutableStateOf(Continent.entries.toSet()) }
    var matchSelectionOnly by remember { mutableStateOf(true) }

    val canStart = (Category.COUNTRY in selected && selectedContinents.isNotEmpty()) ||
        Category.US in selected ||
        Category.CANADA in selected

    Scaffold(
        topBar = { TopAppBar(title = { Text("Quiz Setup") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                // Single screen-level scroll container so all category toggles,
                // the expanded six-continent list, the fuzzy-scope switch, and
                // the Start/Back buttons are reachable on S21 portrait. The
                // ContinentSelector below is a plain non-scrolling Column, so
                // there is no nested vertical-scroll conflict. Mirrors DetailScreen.
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Pick what to be quizzed on",
                style = MaterialTheme.typography.headlineMedium,
            )
            Category.entries.forEach { category ->
                CategoryToggle(
                    label = category.label(),
                    checked = category in selected,
                    onCheckedChange = { isOn ->
                        selected = if (isOn) selected + category else selected - category
                        // Turning Countries on resets the continent scope to all six.
                        if (category == Category.COUNTRY && isOn) {
                            selectedContinents = Continent.entries.toSet()
                        }
                    },
                )

                // Continent sub-selector appears only while Countries is selected.
                if (category == Category.COUNTRY) {
                    AnimatedVisibility(visible = Category.COUNTRY in selected) {
                        ContinentSelector(
                            selectedContinents = selectedContinents,
                            onToggleContinent = { continent, isOn ->
                                val next = if (isOn) {
                                    selectedContinents + continent
                                } else {
                                    selectedContinents - continent
                                }
                                if (next.isEmpty()) {
                                    // Last continent off -> Countries off, selector hides.
                                    selected = selected - Category.COUNTRY
                                } else {
                                    selectedContinents = next
                                }
                            },
                            onSelectAll = { allOn ->
                                if (allOn) {
                                    // Was all-on; clearing also turns Countries off.
                                    selected = selected - Category.COUNTRY
                                } else {
                                    selectedContinents = Continent.entries.toSet()
                                }
                            },
                        )
                    }
                }
            }

            FuzzyScopeToggle(
                matchSelectionOnly = matchSelectionOnly,
                onCheckedChange = { matchSelectionOnly = it },
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onStart(selected, selectedContinents, matchSelectionOnly) },
                enabled = canStart,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Start Quiz")
            }
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Back")
            }
        }
    }
}

/**
 * The continent sub-list shown under Countries: a Select-all row plus one
 * checkbox per continent. [onToggleContinent] reports a single continent
 * changing; [onSelectAll] reports the Select-all control being tapped and is
 * given the control's *current* checked state so the caller can decide whether
 * to set all six or clear them.
 */
@Composable
private fun ContinentSelector(
    selectedContinents: Set<Continent>,
    onToggleContinent: (Continent, Boolean) -> Unit,
    onSelectAll: (Boolean) -> Unit,
) {
    val allSelected = selectedContinents.size == Continent.entries.size

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        ContinentCheckboxRow(
            label = "Select all",
            checked = allSelected,
            onCheckedChange = { onSelectAll(allSelected) },
        )
        Continent.entries.forEach { continent ->
            ContinentCheckboxRow(
                label = continent.label(),
                checked = continent in selectedContinents,
                onCheckedChange = { isOn -> onToggleContinent(continent, isOn) },
            )
        }
    }
}

@Composable
private fun ContinentCheckboxRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

/** Global switch controlling the spelling-hint scope. */
@Composable
private fun FuzzyScopeToggle(
    matchSelectionOnly: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Spelling hint matches your selection",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Turn off for a tougher hint drawn from every flag.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(0.dp))
        Switch(checked = matchSelectionOnly, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun CategoryToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = MaterialTheme.typography.titleLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

/**
 * The continuous quiz. Big centered flag, score badge top-right, a text field
 * for the guess, exactly ONE display-only fuzzy suggestion below it (never
 * autofills, never scored), and a Guess button. Correct = green, wrong = red
 * with the answer revealed. Advances to [onComplete] when the queue empties.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onComplete: () -> Unit,
) {
    if (viewModel.isComplete) {
        LaunchedEffect(Unit) { onComplete() }
        return
    }

    val current = viewModel.current ?: return

    // Resets to the flag (false) on every new/re-queued entry, keyed by the
    // entry so the map (which reveals the answer) is never shown first.
    var showMap by remember(current.key) { mutableStateOf(false) }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "Which flag is this?",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(end = 96.dp),
                )

                // Tap the flag to flip to the offline map for this entry and
                // tap again to flip back. The tap ONLY toggles showMap; it
                // never scores or advances.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showMap = !showMap },
                ) {
                    Crossfade(targetState = showMap, label = "flagMapFlip") { mapShown ->
                        if (mapShown) {
                            MapView(
                                lat = current.lat,
                                lon = current.lon,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        } else {
                            FlagImage(
                                flagKey = current.key,
                                contentDescription = "Flag to identify",
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = viewModel.input,
                    onValueChange = viewModel::onInputChange,
                    label = { Text("Type the full name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { viewModel.onSubmit() }),
                )

                // Exactly ONE suggestion, rendered as a hint only. Never used on submit.
                val suggestion = viewModel.suggestion
                Text(
                    text = if (suggestion != null) "Closest match: ${suggestion.name}" else " ",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                )

                when (viewModel.lastResult) {
                    GuessResult.CORRECT -> Text(
                        text = "Correct!",
                        color = CorrectGreen,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    GuessResult.WRONG -> Text(
                        text = "Wrong — it was ${viewModel.revealedAnswer}",
                        color = WrongRed,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    GuessResult.NONE -> Spacer(Modifier.height(0.dp))
                }

                Button(
                    onClick = { viewModel.onSubmit() },
                    enabled = viewModel.input.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Guess")
                }
            }

            ScoreBadge(
                score = viewModel.score,
                remaining = viewModel.remaining,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
            )
        }
    }
}

/** Completion summary with a button back to Home. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizCompleteScreen(
    score: Int,
    total: Int,
    onHome: () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Quiz Complete") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        ) {
            Text(
                text = "All done!",
                style = MaterialTheme.typography.displaySmall,
            )
            Text(
                text = "You identified every flag in the set.",
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = "Score: $score",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Button(onClick = onHome, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Home")
            }
        }
    }
}
