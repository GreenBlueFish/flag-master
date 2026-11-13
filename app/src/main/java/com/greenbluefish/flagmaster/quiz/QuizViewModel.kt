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
 * Scope of the display-only spelling hint shown under the quiz input.
 *
 * - [SELECTION] (default): the nearest match is drawn from the selected quiz
 *   pool only, so an unselected category is never surfaced.
 * - [ALL]: the nearest match is drawn from the whole flag set for a tougher
 *   spell challenge.
 * - [OFF]: no hint at all; the matcher is never called and no suggestion is
 *   shown regardless of input.
 */
enum class HintMode { SELECTION, ALL, OFF }

/**
 * Drives the continuous quiz for a fixed set of [categories], with COUNTRY
 * entries scoped to the chosen [continents].
 *
 * The suggestion shown under the input is display-only: [onSubmit] scores the
 * fully typed, trimmed, case-insensitive input against the current answer and
 * never reads the suggestion. [hintMode] selects the suggestion scope:
 * [HintMode.SELECTION] draws from the selected [pool] only (never surfacing an
 * unselected category), [HintMode.ALL] draws from the whole flag set for a
 * harder spell challenge, and [HintMode.OFF] shows no suggestion at all
 * (the matcher is never called). A wrong guess is re-queued min(queue.size, 5)
 * positions ahead so the same flag never reappears immediately.
 */
class QuizViewModel(
    private val categories: Set<Category>,
    private val continents: Set<Continent>,
    private val hintMode: HintMode,
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

    /**
     * The correct answer name to reveal during the ANSWERED/feedback phase.
     * Set on BOTH correct and wrong submits so the user always learns the name;
     * null while in the ASKING phase (awaiting input).
     */
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

    /**
     * Drives the Back button's enabled state. Back is only offered in the
     * ASKING phase (lastResult == NONE): during ANSWERED the user's only action
     * is Continue, and because the wrong-answer re-queue is deferred to
     * [onContinue], the snapshot captured at submit is still a faithful
     * pre-submit state that [goBack] can restore wholesale.
     */
    val canGoBack: Boolean get() = previous != null && lastResult == GuessResult.NONE

    init {
        advance()
    }

    /**
     * Display-only nearest match; never autofilled. [HintMode.SELECTION] scopes
     * it to the selected [pool], [HintMode.ALL] draws from every flag, and
     * [HintMode.OFF] returns null WITHOUT calling the matcher so no hint shows.
     */
    val suggestion: FlagEntry?
        get() = when (hintMode) {
            HintMode.OFF -> null
            HintMode.SELECTION -> FuzzyMatcher.closest(input, pool)
            HintMode.ALL -> FuzzyMatcher.closest(input, FlagRepository.all())
        }

    fun onInputChange(value: String) {
        // Pure assignment: feedback is cleared only by onContinue()/goBack().
        // The field is disabled during ANSWERED, so this never fires then, and
        // keeping it a single statement means lastResult has exactly one setter
        // on the submit side (onSubmit) and one clear side (onContinue/goBack).
        input = value
    }

    /**
     * Scores the fully typed input and enters the ANSWERED/feedback phase WITHOUT
     * advancing. Exact (trimmed, case-insensitive) match to the current answer =>
     * correct (score++); otherwise wrong. In BOTH cases the correct name is
     * revealed so the user learns it, and the flag STAYS on screen until
     * [onContinue]. The queue is NOT mutated here: the wrong-answer re-queue and
     * the advance both happen exactly once in [onContinue].
     *
     * Scoring happens exactly once per question, here, in the correct branch.
     */
    fun onSubmit() {
        val entry = current ?: return
        if (input.isBlank()) return
        // Ignore a stray resubmit while already showing feedback.
        if (lastResult != GuessResult.NONE) return

        // Capture the single-step history BEFORE any scoring, so a later
        // goBack() restores the exact pre-submit state the user saw for this
        // flag. This is the ONLY capture site, which is what makes history
        // exactly one level deep.
        previous = QuizSnapshot(
            entry = entry,
            score = score,
            queueSnapshot = queue.toList(),
        )

        // Score against the current answer ONLY (never the suggestion).
        val isCorrect = input.trim().equals(entry.name, ignoreCase = true)
        if (isCorrect) {
            score++
            lastResult = GuessResult.CORRECT
        } else {
            lastResult = GuessResult.WRONG
        }
        // Reveal the correct name in BOTH cases so the user can learn the flag.
        revealedAnswer = entry.name
    }

    /**
     * Leaves the ANSWERED/feedback phase and advances to the next flag. Performs
     * the deferred queue work EXACTLY ONCE: on a wrong answer the current entry
     * is re-queued min(queue.size, 5) positions ahead (never position 0, so it
     * never reappears immediately), then [advance] pulls the next entry (or
     * completes the quiz). No-op while in ASKING so a stray double-tap cannot
     * advance or re-queue twice. Does NOT score — scoring already happened once
     * in [onSubmit].
     */
    fun onContinue() {
        if (lastResult == GuessResult.NONE) return
        val entry = current ?: return
        if (lastResult == GuessResult.WRONG) {
            // Re-insert ahead, never at position 0 (never immediately next).
            val index = minOf(queue.size, 5)
            queue.add(index, entry)
        }
        // advance() clears input, resets feedback to NONE, and pulls the next
        // entry (or sets isComplete when the queue is empty) — the ASKING reset.
        advance()
    }

    /**
     * Steps back ONE flag to the previous question. Only reachable in ASKING
     * (see [canGoBack]), i.e. after an [onContinue] advanced to a fresh flag,
     * so the queue has already moved past the snapshot. Restores the EXACT
     * pre-submit state captured in [previous]: the queue is replaced wholesale
     * (undoing the removeFirst of advance and, if the previous answer was wrong,
     * the re-insert that onContinue performed),
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
 * [continents] scope, and the [hintMode] spelling-hint mode (Selection / All /
 * Off).
 */
class QuizViewModelFactory(
    private val categories: Set<Category>,
    private val continents: Set<Continent>,
    private val hintMode: HintMode,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        QuizViewModel(categories, continents, hintMode) as T
}
