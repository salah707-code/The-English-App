package com.example.importexport

import android.content.Context
import android.net.Uri
import com.example.data.model.Article
import com.example.data.model.BackupPayload
import com.example.data.model.Word
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

data class ImportResult(
    val imported: Int = 0,
    val updated: Int = 0,
    val skipped: Int = 0,
    val errors: Int = 0,
    val totalProcessed: Int = 0
)

data class ImportPreview(
    val headers: List<String>,
    val rows: List<List<String>>,
    val totalRows: Int,
    val suggestedMapping: Map<String, Int>
)

class DataImportExportManager(private val context: Context) {

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val backupAdapter = moshi.adapter(BackupPayload::class.java)

    suspend fun parseFilePreview(uri: Uri): ImportPreview = withContext(Dispatchers.IO) {
        val parsedRows = context.contentResolver.openInputStream(uri)?.use { stream ->
            ExcelCsvParser.parseStream(stream)
        } ?: throw IllegalArgumentException("Cannot open file stream")

        if (parsedRows.isEmpty()) {
            return@withContext ImportPreview(emptyList(), emptyList(), 0, emptyMap())
        }

        val header = parsedRows.firstOrNull() ?: emptyList()
        val dataRows = parsedRows.drop(1)

        val mapping = mutableMapOf<String, Int>()
        header.forEachIndexed { index, colName ->
            val col = colName.trim().lowercase()
            when {
                col.contains("english") || col == "en" || col == "word" || col.contains("كلمة") -> mapping["english"] = index
                col.contains("arabic") || col == "ar" || col.contains("ترجمة") || col.contains("معنى") || col == "meaning" -> mapping["arabic"] = index
                col.contains("category") || col.contains("فئة") || col.contains("تصنيف") -> mapping["category"] = index
                col.contains("sub") -> mapping["subcategory"] = index
                col.contains("level") || col.contains("مستوى") -> mapping["level"] = index
                col.contains("part") || col.contains("pos") || col.contains("نوع") -> mapping["partOfSpeech"] = index
                col.contains("pron") || col.contains("ipa") || col.contains("نطق") -> mapping["pronunciation"] = index
                col.contains("example_ar") || col.contains("مثال_عربي") -> mapping["exampleArabic"] = index
                col.contains("example") || col.contains("مثال") -> mapping["example"] = index
                col.contains("audio") || col.contains("صوت") -> mapping["audio"] = index
            }
        }

        // If no explicit mapping found, map 0 to english and 1 to arabic if at least 2 columns
        if (!mapping.containsKey("english") && header.isNotEmpty()) {
            mapping["english"] = 0
        }
        if (!mapping.containsKey("arabic") && header.size > 1) {
            mapping["arabic"] = 1
        }

        ImportPreview(
            headers = header,
            rows = dataRows.take(10),
            totalRows = dataRows.size,
            suggestedMapping = mapping
        )
    }

    suspend fun importCategoryWords(
        uri: Uri,
        targetCategory: String,
        existingWords: List<Word>
    ): Pair<ImportResult, List<Word>> = withContext(Dispatchers.IO) {
        val allRows = context.contentResolver.openInputStream(uri)?.use { stream ->
            ExcelCsvParser.parseStream(stream)
        } ?: throw IllegalArgumentException("Cannot open file stream")

        if (allRows.isEmpty()) {
            return@withContext Pair(ImportResult(), emptyList())
        }

        val header = allRows.first()
        val dataRows = if (allRows.size > 1 && isLikelyHeader(header)) allRows.drop(1) else allRows

        val mapping = mutableMapOf<String, Int>()
        if (isLikelyHeader(header)) {
            header.forEachIndexed { index, colName ->
                val col = colName.trim().lowercase()
                when {
                    col.contains("english") || col == "en" || col == "word" || col.contains("كلمة") -> mapping["english"] = index
                    col.contains("translation") || col.contains("ترجمة") -> mapping["arabic"] = index
                    col.contains("meaning") || col.contains("معنى") || col.contains("شرح") -> mapping["meaning"] = index
                    col.contains("arabic") || col == "ar" -> if (!mapping.containsKey("arabic")) mapping["arabic"] = index
                    col.contains("sentence") || col.contains("جملة") || col.contains("example") || col.contains("مثال") -> mapping["example"] = index
                    col.contains("sound") || col.contains("audio") || col.contains("صوت") || col.contains("نطق") -> mapping["audio"] = index
                    col.contains("level") || col.contains("مستوى") -> mapping["level"] = index
                    col.contains("part") || col.contains("pos") || col.contains("نوع") -> mapping["partOfSpeech"] = index
                    col.contains("example_ar") || col.contains("مثال_عربي") -> mapping["exampleArabic"] = index
                }
            }
        }

        val englishIdx = mapping["english"] ?: 0
        val arabicIdx = mapping["arabic"] ?: mapping["meaning"] ?: 1
        val meaningIdx = mapping["meaning"]
        val levelIdx = mapping["level"]
        val posIdx = mapping["partOfSpeech"]
        val pronIdx = mapping["audio"]
        val exampleIdx = mapping["example"]
        val exampleArIdx = mapping["exampleArabic"]

        val existingMap = existingWords.associateBy { it.english.trim().lowercase() }.toMutableMap()
        val wordsToSave = mutableListOf<Word>()
        var imported = 0
        var updated = 0
        var skipped = 0
        var errors = 0

        for (cols in dataRows) {
            val en = cols.getOrNull(englishIdx)?.trim().orEmpty()
            val ar = cols.getOrNull(arabicIdx)?.trim().orEmpty()

            if (en.isBlank() || ar.isBlank()) {
                errors++
                continue
            }

            val meaning = meaningIdx?.let { cols.getOrNull(it)?.trim() } ?: ""
            val lvl = levelIdx?.let { cols.getOrNull(it)?.trim() }?.ifBlank { "A1" } ?: "A1"
            val pos = posIdx?.let { cols.getOrNull(it)?.trim() }?.ifBlank { "Noun" } ?: "Noun"
            val pron = pronIdx?.let { cols.getOrNull(it)?.trim() } ?: ""
            val ex = exampleIdx?.let { cols.getOrNull(it)?.trim() } ?: ""
            val exAr = exampleArIdx?.let { cols.getOrNull(it)?.trim() } ?: ""

            val key = en.lowercase()
            val existing = existingMap[key]

            if (existing != null) {
                val updatedWord = existing.copy(
                    arabic = ar,
                    category = targetCategory,
                    subcategory = if (meaning.isNotBlank()) meaning else existing.subcategory,
                    level = lvl,
                    partOfSpeech = pos,
                    pronunciation = pron.ifBlank { existing.pronunciation },
                    example = ex.ifBlank { existing.example },
                    exampleArabic = exAr.ifBlank { existing.exampleArabic },
                    isDeleted = false,
                    updatedAt = System.currentTimeMillis()
                )
                wordsToSave.add(updatedWord)
                existingMap[key] = updatedWord
                updated++
            } else {
                val newWord = Word(
                    english = en,
                    arabic = ar,
                    category = targetCategory,
                    subcategory = meaning,
                    level = lvl,
                    partOfSpeech = pos,
                    pronunciation = pron,
                    example = ex,
                    exampleArabic = exAr,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                wordsToSave.add(newWord)
                existingMap[key] = newWord
                imported++
            }
        }

        Pair(
            ImportResult(
                imported = imported,
                updated = updated,
                skipped = skipped,
                errors = errors,
                totalProcessed = dataRows.size
            ),
            wordsToSave
        )
    }

    private fun isLikelyHeader(row: List<String>): Boolean {
        return row.any {
            val lower = it.lowercase()
            lower.contains("en") || lower.contains("ar") || lower.contains("word") ||
            lower.contains("ترجمة") || lower.contains("معنى") || lower.contains("كلمة")
        }
    }

    suspend fun importArticles(uri: Uri): Pair<ImportResult, List<Article>> = withContext(Dispatchers.IO) {
        val allRows = context.contentResolver.openInputStream(uri)?.use { stream ->
            ExcelCsvParser.parseStream(stream)
        } ?: throw IllegalArgumentException("Cannot open file stream")

        if (allRows.isEmpty()) {
            return@withContext Pair(ImportResult(), emptyList())
        }

        val header = allRows.first()
        val hasHeader = isLikelyArticleHeader(header)
        val dataRows = if (allRows.size > 1 && hasHeader) allRows.drop(1) else allRows

        val mapping = mutableMapOf<String, Int>()
        if (hasHeader) {
            header.forEachIndexed { index, colName ->
                val col = colName.trim().lowercase()
                when {
                    col.contains("title") || col.contains("عنوان") || col == "name" || col.contains("اسم") -> mapping["title"] = index
                    col.contains("level") || col.contains("مستوى") || col.contains("صعوبة") -> mapping["level"] = index
                    col.contains("category") || col.contains("تصنيف") || col.contains("فئة") || col.contains("قسم") -> mapping["category"] = index
                    col.contains("content") || col.contains("نص") || col.contains("text") || col.contains("body") || col.contains("محتوى") -> mapping["content"] = index
                    col.contains("keyword") || col.contains("كلمات") || col.contains("مفتاحية") || col.contains("tags") -> mapping["keywords"] = index
                }
            }
        }

        val titleIdx = mapping["title"] ?: 0
        val levelIdx = mapping["level"] ?: 1
        val categoryIdx = mapping["category"] ?: 2
        val contentIdx = mapping["content"] ?: 3
        val keywordsIdx = mapping["keywords"] ?: 4

        val articlesToSave = mutableListOf<Article>()
        var imported = 0
        var skipped = 0
        var errors = 0
        val now = System.currentTimeMillis()

        for ((idx, cols) in dataRows.withIndex()) {
            val title = cols.getOrNull(titleIdx)?.trim().orEmpty()
            val content = cols.getOrNull(contentIdx)?.trim().orEmpty()

            if (title.isBlank() && content.isBlank()) {
                skipped++
                continue
            }

            val finalTitle = if (title.isNotBlank()) title else "مقال بدون عنوان #${idx + 1}"
            val rawLevel = cols.getOrNull(levelIdx)?.trim().orEmpty().uppercase()
            val finalLevel = if (rawLevel in listOf("A1", "A2", "B1", "B2", "C1", "C2")) rawLevel else "B1"
            val rawCat = cols.getOrNull(categoryIdx)?.trim().orEmpty()
            val finalCat = if (rawCat.isNotBlank()) rawCat else "عام"
            val finalContent = if (content.isNotBlank()) content else title
            val finalKeywords = cols.getOrNull(keywordsIdx)?.trim().orEmpty()

            articlesToSave.add(
                Article(
                    id = 0L,
                    title = finalTitle,
                    level = finalLevel,
                    category = finalCat,
                    content = finalContent,
                    keywords = finalKeywords,
                    orderIndex = idx + 1,
                    createdAt = now,
                    updatedAt = now
                )
            )
            imported++
        }

        Pair(
            ImportResult(
                imported = imported,
                updated = 0,
                skipped = skipped,
                errors = errors,
                totalProcessed = dataRows.size
            ),
            articlesToSave
        )
    }

    private fun isLikelyArticleHeader(row: List<String>): Boolean {
        val keywords = listOf("title", "level", "category", "content", "text", "keywords", "عنوان", "مستوى", "تصنيف", "نص", "كلمات")
        return row.any { cell -> keywords.any { k -> cell.contains(k, ignoreCase = true) } }
    }

    suspend fun processImport(
        uri: Uri,
        columnMapping: Map<String, Int>,
        updateDuplicates: Boolean,
        existingWords: List<Word>
    ): Pair<ImportResult, List<Word>> = withContext(Dispatchers.IO) {
        val existingMap = existingWords.associateBy { it.english.trim().lowercase() }.toMutableMap()
        val wordsToSave = mutableListOf<Word>()

        var imported = 0
        var updated = 0
        var skipped = 0
        var errors = 0
        var total = 0

        val englishIdx = columnMapping["english"] ?: 0
        val arabicIdx = columnMapping["arabic"] ?: 1
        val categoryIdx = columnMapping["category"]
        val subcategoryIdx = columnMapping["subcategory"]
        val levelIdx = columnMapping["level"]
        val posIdx = columnMapping["partOfSpeech"]
        val pronIdx = columnMapping["pronunciation"]
        val exampleIdx = columnMapping["example"]
        val exampleArIdx = columnMapping["exampleArabic"]
        val audioIdx = columnMapping["audio"]

        context.contentResolver.openInputStream(uri)?.use { stream ->
            BufferedReader(InputStreamReader(stream)).use { reader ->
                var line = reader.readLine()
                var isHeader = true
                while (line != null) {
                    if (isHeader) {
                        isHeader = false
                        line = reader.readLine()
                        continue
                    }
                    if (line.isBlank()) {
                        line = reader.readLine()
                        continue
                    }

                    total++
                    val cols = parseCsvLine(line)

                    val english = cols.getOrNull(englishIdx)?.trim().orEmpty()
                    val arabic = cols.getOrNull(arabicIdx)?.trim().orEmpty()

                    if (english.isBlank() || arabic.isBlank()) {
                        errors++
                        line = reader.readLine()
                        continue
                    }

                    val cat = categoryIdx?.let { cols.getOrNull(it)?.trim() }?.ifBlank { "General" } ?: "General"
                    val subcat = subcategoryIdx?.let { cols.getOrNull(it)?.trim() } ?: ""
                    val lvl = levelIdx?.let { cols.getOrNull(it)?.trim() }?.ifBlank { "A1" } ?: "A1"
                    val pos = posIdx?.let { cols.getOrNull(it)?.trim() }?.ifBlank { "Noun" } ?: "Noun"
                    val pron = pronIdx?.let { cols.getOrNull(it)?.trim() } ?: ""
                    val ex = exampleIdx?.let { cols.getOrNull(it)?.trim() } ?: ""
                    val exAr = exampleArIdx?.let { cols.getOrNull(it)?.trim() } ?: ""
                    val audio = audioIdx?.let { cols.getOrNull(it)?.trim() } ?: ""

                    val key = english.lowercase()
                    val existing = existingMap[key]

                    if (existing != null) {
                        if (updateDuplicates) {
                            val updatedWord = existing.copy(
                                arabic = arabic,
                                category = cat,
                                subcategory = subcat,
                                level = lvl,
                                partOfSpeech = pos,
                                pronunciation = pron.ifBlank { existing.pronunciation },
                                example = ex.ifBlank { existing.example },
                                exampleArabic = exAr.ifBlank { existing.exampleArabic },
                                audioUrl = audio.ifBlank { existing.audioUrl },
                                isDeleted = false,
                                updatedAt = System.currentTimeMillis()
                            )
                            wordsToSave.add(updatedWord)
                            existingMap[key] = updatedWord
                            updated++
                        } else {
                            skipped++
                        }
                    } else {
                        val newWord = Word(
                            english = english,
                            arabic = arabic,
                            category = cat,
                            subcategory = subcat,
                            level = lvl,
                            partOfSpeech = pos,
                            pronunciation = pron,
                            example = ex,
                            exampleArabic = exAr,
                            audioUrl = audio,
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        wordsToSave.add(newWord)
                        existingMap[key] = newWord
                        imported++
                    }

                    line = reader.readLine()
                }
            }
        }

        val result = ImportResult(
            imported = imported,
            updated = updated,
            skipped = skipped,
            errors = errors,
            totalProcessed = total
        )

        Pair(result, wordsToSave)
    }

    fun exportToCsv(words: List<Word>): String {
        val sb = StringBuilder()
        sb.append("English,Arabic,Category,Subcategory,Level,Part of Speech,Pronunciation,Example,Example Arabic,Audio\n")
        words.forEach { w ->
            sb.append(escapeCsv(w.english)).append(",")
            sb.append(escapeCsv(w.arabic)).append(",")
            sb.append(escapeCsv(w.category)).append(",")
            sb.append(escapeCsv(w.subcategory)).append(",")
            sb.append(escapeCsv(w.level)).append(",")
            sb.append(escapeCsv(w.partOfSpeech)).append(",")
            sb.append(escapeCsv(w.pronunciation)).append(",")
            sb.append(escapeCsv(w.example)).append(",")
            sb.append(escapeCsv(w.exampleArabic)).append(",")
            sb.append(escapeCsv(w.audioUrl)).append("\n")
        }
        return sb.toString()
    }

    fun generateSampleTemplate(): String {
        val sb = StringBuilder()
        sb.append("English,Arabic,Category,Subcategory,Level,Part of Speech,Pronunciation,Example,Example Arabic,Audio\n")
        sb.append("Environment,البيئة,Science & Nature,,B1,Noun,/ɪnˈvaɪrənmənt/,We should protect the environment.,يجب أن نحمي البيئة.,\n")
        sb.append("Opportunity,فرصة,Business,,B1,Noun,/ˌɑːpərˈtuːnəti/,A great opportunity to learn.,فرصة رائعة للتعلم.,\n")
        sb.append("Accomplish,ينجز,Daily Life,,B2,Verb,/əˈkɑːmplɪʃ/,You can accomplish your goals.,يمكنك تحقيق أهدافك.,\n")
        sb.append("Mindfulness,يقظة ذهنية,Health & Mind,,B2,Noun,/ˈmaɪndflnəs/,Mindfulness brings inner peace.,اليقظة الذهنية تجلب السلام الداخلي.,\n")
        sb.append("Piece of cake,أمر سهل جداً,Idioms & Phrases,,A2,Idiom,/piːs əv keɪk/,The exam was a piece of cake.,كان الامتحان سهلاً للغاية.,\n")
        return sb.toString()
    }

    fun createBackupJson(words: List<Word>, settings: Map<String, String>): String {
        val payload = BackupPayload(
            version = 1,
            exportedAt = System.currentTimeMillis(),
            appVersion = "1.0",
            words = words,
            settings = settings
        )
        return backupAdapter.toJson(payload)
    }

    suspend fun restoreBackupJson(uri: Uri): BackupPayload? = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BufferedReader(InputStreamReader(stream)).use { reader ->
                val jsonString = reader.readText()
                backupAdapter.fromJson(jsonString)
            }
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        val tokens = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false

        for (i in line.indices) {
            val c = line[i]
            when {
                c == '\"' -> {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == '\"') {
                        sb.append('\"')
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                c == ',' && !inQuotes -> {
                    tokens.add(sb.toString())
                    sb.clear()
                }
                c == '\t' && !inQuotes -> {
                    tokens.add(sb.toString())
                    sb.clear()
                }
                else -> {
                    sb.append(c)
                }
            }
        }
        tokens.add(sb.toString())
        return tokens.map { it.trim().removeSurrounding("\"") }
    }

    private fun escapeCsv(value: String): String {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\""
        }
        return value
    }
}
