package com.example

import com.example.data.model.Word
import com.example.viewmodel.QuizQuestion
import com.example.viewmodel.QuizState
import com.example.viewmodel.QuizType
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testWordToMeaningQuestionStructure() {
        val target = Word(id = 1, english = "Apple", arabic = "تفاحة", category = "Food")
        val distractors = listOf("برتقال", "موز", "عنب")
        val options = (distractors + target.arabic).shuffled()
        val question = QuizQuestion(
            prompt = target.english,
            subPrompt = "Food • A1",
            options = options,
            correctIndex = options.indexOf(target.arabic),
            word = target,
            type = QuizType.EN_TO_AR,
            typeLabel = "Word → Meaning • كلمة إلى معنى"
        )

        assertEquals("Apple", question.prompt)
        assertEquals(4, question.options.size)
        assertTrue(question.options.contains("تفاحة"))
        assertEquals("تفاحة", question.options[question.correctIndex])
        assertEquals(QuizType.EN_TO_AR, question.type)
    }

    @Test
    fun testMeaningToWordQuestionStructure() {
        val target = Word(id = 2, english = "Book", arabic = "كتاب", category = "Education")
        val distractors = listOf("Pen", "Notebook", "Desk")
        val options = (distractors + target.english).shuffled()
        val question = QuizQuestion(
            prompt = target.arabic,
            subPrompt = "Education • A1",
            options = options,
            correctIndex = options.indexOf(target.english),
            word = target,
            type = QuizType.AR_TO_EN,
            typeLabel = "Meaning → Word • معنى إلى كلمة"
        )

        assertEquals("كتاب", question.prompt)
        assertEquals(4, question.options.size)
        assertTrue(question.options.contains("Book"))
        assertEquals("Book", question.options[question.correctIndex])
        assertEquals(QuizType.AR_TO_EN, question.type)
    }

    @Test
    fun testStreakAndScoreBonusCalculation() {
        var streak = 0
        var score = 0

        // Question 1 correct
        val isCorrect1 = true
        streak = if (isCorrect1) streak + 1 else 0
        val bonus1 = if (isCorrect1 && streak > 1) (streak - 1) * 2 else 0
        score += if (isCorrect1) 10 + bonus1 else 0

        assertEquals(1, streak)
        assertEquals(10, score)

        // Question 2 correct (streak 2 gives bonus 2)
        val isCorrect2 = true
        streak = if (isCorrect2) streak + 1 else 0
        val bonus2 = if (isCorrect2 && streak > 1) (streak - 1) * 2 else 0
        score += if (isCorrect2) 10 + bonus2 else 0

        assertEquals(2, streak)
        assertEquals(22, score)

        // Question 3 wrong (streak resets to 0)
        val isCorrect3 = false
        streak = if (isCorrect3) streak + 1 else 0
        val bonus3 = if (isCorrect3 && streak > 1) (streak - 1) * 2 else 0
        score += if (isCorrect3) 10 + bonus3 else 0

        assertEquals(0, streak)
        assertEquals(22, score)
    }
}

