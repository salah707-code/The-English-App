package com.example.importexport

import android.content.Context
import android.net.Uri
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
        val lines = mutableListOf<String>()
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BufferedReader(InputStreamReader(stream)).use { reader ->
                var line = reader.readLine()
                var count = 0
                while (line != null && count < 100) {
                    if (line.isNotBlank()) {
                        lines.add(line)
                        count++
                    }
                    line = reader.readLine()
                }
            }
        } ?: throw IllegalArgumentException("Cannot open file stream")

        if (lines.isEmpty()) {
            return@withContext ImportPreview(emptyList(), emptyList(), 0, emptyMap())
        }

        val parsedRows = lines.map { parseCsvLine(it) }
        val header = parsedRows.firstOrNull() ?: emptyList()
        val dataRows = parsedRows.drop(1)

        val mapping = mutableMapOf<String, Int>()
        header.forEachIndexed { index, colName ->
            val col = colName.trim().lowercase()
            when {
                col.contains("english") || col == "en" || col == "word" -> mapping["english"] = index
                col.contains("arabic") || col == "ar" || col.contains("ترجمة") || col == "meaning" -> mapping["arabic"] = index
                col.contains("category") || col.contains("فئة") -> mapping["category"] = index
                col.contains("sub") -> mapping["subcategory"] = index
                col.contains("level") || col.contains("مستوى") -> mapping["level"] = index
                col.contains("part") || col.contains("pos") || col.contains("نوع") -> mapping["partOfSpeech"] = index
                col.contains("pron") || col.contains("ipa") || col.contains("نطق") -> mapping["pronunciation"] = index
                col.contains("example_ar") || col.contains("مثال_عربي") -> mapping["exampleArabic"] = index
                col.contains("example") || col.contains("مثال") -> mapping["example"] = index
                col.contains("audio") || col.contains("صوت") -> mapping["audio"] = index
            }
        }

        ImportPreview(
            headers = header,
            rows = dataRows.take(10),
            totalRows = dataRows.size,
            suggestedMapping = mapping
        )
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
