package com.example.data.sample

import com.example.data.model.Article

object StarterArticles {

    fun getStarterArticles(): List<Article> {
        val now = System.currentTimeMillis()
        return listOf(
            Article(
                id = 1,
                title = "The Secrets of Effective Language Learning",
                level = "B1",
                category = "تعليم وتطوير",
                content = """Learning a new language is one of the most rewarding adventures a person can undertake. It opens doors to new cultures, perspectives, and opportunities. However, many learners struggle because they rely solely on passive memorization.

To master a language effectively, consistency is far more powerful than intensity. Studying for twenty minutes every single day produces far better retention than cramming for five hours once a week. Your brain requires regular exposure to transfer new vocabulary from short-term to long-term memory.

Another crucial strategy is active immersion. Surround yourself with the language by listening to podcasts, reading articles, and speaking aloud, even when you are alone. Making mistakes is not a sign of failure; it is the essential catalyst for growth. Embrace every error as a stepping stone toward fluency.""",
                keywords = "Language, Learning, Consistency, Fluency, Habits, Memory",
                orderIndex = 1,
                createdAt = now - 300000,
                updatedAt = now - 300000
            ),
            Article(
                id = 2,
                title = "Artificial Intelligence in Everyday Life",
                level = "B2",
                category = "علوم وتكنولوجيا",
                content = """Artificial intelligence is no longer a concept confined to science fiction novels. Today, intelligent algorithms quietly assist us in almost every aspect of daily life, from navigation apps suggesting the fastest commute to streaming platforms recommending our next favorite melody.

In healthcare, AI assists physicians in diagnosing complex medical conditions with unprecedented accuracy. In environmental science, researchers leverage predictive models to monitor climate patterns, protect endangered ecosystems, and optimize renewable energy distribution.

While these technological breakthroughs bring immense convenience, they also present ethical considerations. Questions concerning data privacy, algorithmic transparency, and workforce transitions demand responsible stewardship from creators and policymakers alike.""",
                keywords = "AI, Technology, Algorithms, Healthcare, Innovation, Ethics",
                orderIndex = 2,
                createdAt = now - 200000,
                updatedAt = now - 200000
            ),
            Article(
                id = 3,
                title = "The Power of Positive Daily Habits",
                level = "A2",
                category = "تطوير الذات والصحة",
                content = """Small daily habits can change your whole life. When you do something small every day, it becomes easy and natural. 

For example, drinking a glass of water every morning gives your body energy. Reading just ten pages of a book every night helps you learn something new and relaxes your mind before sleep. Walking for fifteen minutes outside gives fresh air to your lungs and reduces stress.

Do not try to change everything in one single day. Choose one simple positive habit, practice it daily for three weeks, and watch how it transforms your energy, happiness, and health.""",
                keywords = "Habits, Health, Routine, Energy, Happiness, Mindset",
                orderIndex = 3,
                createdAt = now - 100000,
                updatedAt = now - 100000
            ),
            Article(
                id = 4,
                title = "Wonders of the Deep Ocean",
                level = "B2",
                category = "طبيعة واستكشاف",
                content = """Beneath the sunlit surface of the sea lies a mysterious realm known as the deep ocean. Covering more than sixty percent of Earth's surface, this abyss remains less explored than the surface of Mars.

At depths where sunlight never penetrates, organisms thrive under crushing hydrostatic pressure and freezing temperatures. Many deep-sea creatures exhibit bioluminescence, producing their own enchanting light to communicate, attract mates, or confuse predators in the pitch darkness.

Hydrothermal vents on the ocean floor spew mineral-rich fluids, supporting bizarre ecosystems independent of solar photosynthesis. Exploring these extraordinary environments broadens our understanding of the resilient nature of life on our planet.""",
                keywords = "Ocean, Marine Life, Bioluminescence, Exploration, Nature, Science",
                orderIndex = 4,
                createdAt = now,
                updatedAt = now
            )
        )
    }
}
