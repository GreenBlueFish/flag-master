package com.greenbluefish.flagmaster.quiz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.greenbluefish.flagmaster.data.Category
import com.greenbluefish.flagmaster.data.Continent
import com.greenbluefish.flagmaster.data.FlagEntry
import com.greenbluefish.flagmaster.data.FlagRepository

/** Outcome of the most recent submission, used to drive the green/red reveal. */
enum class GuessResult { NONE, CORRECT, WRONG }

/**
 * Drives the continuous quiz for a fixed set of [categories], with COUNTRY
 * entries scoped to the chosen [continents].
 *
 * The suggestion shown under the input is display-only: [onSubmit] scores the
 * fully typed, trimmed, case-insensitive input against the current answer and
 * never reads the suggestion. When [matchSelectionOnly] is true the suggestion
 * is drawn from the selected [pool] only (never surfacing an unselected
 * category); when false it is drawn from the whole flag set for a harder spell
 * challenge. A wrong guess is re-queued min(queue.size, 5) positions ahead so
 * the same flag never reappears immediately.
 */
class QuizViewModel(
    private val categories: Set<Category>,
    private val continents: Set<Continent>,
    private val matchSelectionOnly: Boolean,
) : ViewModel() {

    /** The pool backing both the queue and (when scoped) the fuzzy suggestion. */
    private val pool: List<FlagEntry> = FlagRepository.poolFor(categories, continents)

    private val queue: ArrayDeque<FlagEntry> = ArrayDeque(pool.shuffled())

    private val totalCount: Int = pool.size

    var current by mutableStateOf<FlagEntry?>(null)
        private set

    var score by mutableStateOf(0)
        private set

    /** Entries still to be answered, including the current one. */
    var remaining by mutableStateOf(0)
        private set

    var input by mutableStateOf("")
        private set

    var lastResult by mutableStateOf(GuessResult.NONE)
        private set

    /** The answer revealed after a wrong guess (null while awaiting input). */
    var revealedAnswer by mutableStateOf<String?>(null)
        private set

    var isComplete by mutableStateOf(false)
        private set

    /**
     * One-deep history for the "Back" button. A snapshot captures the EXACT
     * pre-submit state of the question the user last saw: the on-screen entry,
     * the score as it stood while that flag was shown, and a full copy of the
     * queue as it was then.
     */
    private data class QuizSnapshot(
        val entry: FlagEntry,
        val score: Int,
        val queueSnapshot: List<FlagEntry>,
    )

    /**
     * The single history slot (null = nothing to go back to). Captured at the
     * TOP of [onSubmit] before any scoring/mutation, and CONSUMED (set null) by
     * [goBack]. Storing exactly one snapshot — never a stack — guarantees only
     * one level of history.
     */
    private var previous by mutableStateOf<QuizSnapshot?>(null)

    /** Drives the Back button's enabled state. */
    val canGoBack: Boolean get() = previous != null

    init {
        advance()
    }

    /**
     * Display-only nearest match; never autofilled. Scoped to the selected
     * [pool] when [matchSelectionOnly] is true, otherwise drawn from every flag.
     */
    val suggestion: FlagEntry?
        get() = FuzzyMatcher.closest(input, if (matchSelectionOnly) pool else FlagRepository.all())

    fun onInputChange(value: String) {
        input = value
        // Typing clears a prior reveal so the hint and field feel live again.
        if (lastResult != GuessResult.NONE) {
            lastResult = GuessResult.NONE
            revealedAnswer = null
        }
    }

    /**
     * Scores the fully typed input. Exact (trimmed, case-insensitive) match to
     * the current answer => correct, pop and advance. Otherwise wrong: reveal
     * the answer and re-queue the current entry min(queue.size, 5) ahead.
     */
    fun onSubmit() {
        val entry = current ?: return
        if (input.isBlank()) return

        // Capture the single-step history BEFORE any scoring/queue mutation, so
        // a later goBack() restores the exact state the user saw for this flag.
        // A blank submit no-ops above and never captures. This is the ONLY
        // capture site, which is what makes history exactly one level deep.
        previous = QuizSnapshot(
            entry = entry,
            score = score,
            queueSnapshot = queue.toList(),
        )

        // Score against the current answer ONLY (never the suggestion).
        val isCorrect = input.trim().equals(current!!.name, ignoreCase = true)
        if (isCorrect) {
            score++
            lastResult = GuessResult.CORRECT
            revealedAnswer = null
            advance()
        } else {
            lastResult = GuessResult.WRONG
            revealedAnswer = current!!.name
            // Re-insert ahead, never at position 0 (never immediately next).
            val index = minOf(queue.size, 5)
            queue.add(index, entry)
            advance(resetFeedback = false)
        }
    }

    /**
     * Steps back ONE flag to the previous question. Restores the EXACT
     * pre-submit state captured in [previous]: the queue is replaced wholesale
     * (undoing both the removeFirst of advance and any wrong-answer re-insert),
     * the score is restored (not decremented, so it can never double-count or
     * go negative), the input is cleared, and any reveal is cleared. The slot
     * is then consumed (previous = null) so a second immediate back is disabled
     * until the user submits again and captures a fresh snapshot.
     *
     * Because QuizScreen keys its tap-to-flip `showMap` on `current.key`,
     * restoring `current` to the previous entry resets the flip to FLAG-first
     * automatically — do not remove that keying or back would show the map.
     */
    fun goBack() {
        val snap = previous ?: return
        queue.clear()
        queue.addAll(snap.queueSnapshot)
        current = snap.entry
        score = snap.score
        remaining = queue.size + 1
        input = ""
        lastResult = GuessResult.NONE
        revealedAnswer = null
        isComplete = false
        previous = null
    }

    /** Pull the next entry off the queue, or finish when the queue empties. */
    private fun advance(resetFeedback: Boolean = true) {
        input = ""
        if (resetFeedback) {
            lastResult = GuessResult.NONE
            revealedAnswer = null
        }
        val next = queue.removeFirstOrNull()
        if (next == null) {
            current = null
            remaining = 0
            isComplete = true
        } else {
            current = next
            // +1 for the entry currently on screen.
            remaining = queue.size + 1
        }
    }

    /** Total answered-correct target for the completion summary. */
    val total: Int get() = totalCount
}

/**
 * Builds a [QuizViewModel] seeded with the chosen [categories], the COUNTRY
 * [continents] scope, and the [matchSelectionOnly] suggestion switch.
 */
class QuizViewModelFactory(
    private val categories: Set<Category>,
    private val continents: Set<Continent>,
    private val matchSelectionOnly: Boolean,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        QuizViewModel(categories, continents, matchSelectionOnly) as T
}
