package com.greenbluefish.flagmaster.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import com.greenbluefish.flagmaster.ui.components.FlagImage
import com.greenbluefish.flagmaster.ui.components.ScoreBadge
import com.greenbluefish.flagmaster.ui.components.label
import com.greenbluefish.flagmaster.ui.theme.CorrectGreen
import com.greenbluefish.flagmaster.ui.theme.WrongRed

/**
 * Setup screen: three toggles (Countries / US States / Canadian Provinces).
 * Start is disabled until at least one category is selected; it then hands the
 * selected [Set] to [onStart].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSetupScreen(
    onStart: (Set<Category>) -> Unit,
    onBack: () -> Unit,
) {
    var selected by remember { mutableStateOf(setOf<Category>()) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Quiz Setup") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                    },
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onStart(selected) },
                enabled = selected.isNotEmpty(),
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

                FlagImage(
                    flagKey = current.key,
                    contentDescription = "Flag to identify",
                    modifier = Modifier.fillMaxWidth(),
                )

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
