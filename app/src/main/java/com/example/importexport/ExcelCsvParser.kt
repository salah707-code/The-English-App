package com.example.importexport

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.Charset
import java.nio.charset.CharsetDecoder
import java.nio.charset.CodingErrorAction
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object ExcelCsvParser {

    /**
     * Parses an input stream of either .xlsx or .csv/.tsv/.txt into a list of rows.
     * Automatically handles UTF-8 (with or without BOM), Windows-1256 (Arabic Excel),
     * and OpenXML XLSX format.
     */
    fun parseStream(inputStream: InputStream): List<List<String>> {
        val bytes = inputStream.readBytes()
        if (bytes.isEmpty()) return emptyList()

        // Check for XLSX (ZIP archive: PK\x03\x04)
        if (isZipArchive(bytes)) {
            return parseXlsx(bytes)
        }

        // Otherwise parse as CSV/Text with automatic charset detection
        return parseDelimitedText(bytes)
    }

    private fun isZipArchive(bytes: ByteArray): Boolean {
        return bytes.size >= 4 &&
                bytes[0] == 0x50.toByte() &&
                bytes[1] == 0x4B.toByte() &&
                bytes[2] == 0x03.toByte() &&
                bytes[3] == 0x04.toByte()
    }

    /**
     * Parse OpenXML Excel (.xlsx) file bytes
     */
    private fun parseXlsx(bytes: ByteArray): List<List<String>> {
        val sharedStrings = mutableListOf<String>()
        var sheetBytes: ByteArray? = null

        // 1. First pass: extract shared strings and first worksheet
        ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
            var entry: ZipEntry? = zip.nextEntry
            while (entry != null) {
                val name = entry.name.lowercase()
                if (name == "xl/sharedstrings.xml") {
                    sharedStrings.addAll(parseSharedStrings(zip.readBytes()))
                } else if (name == "xl/worksheets/sheet1.xml" || (name.startsWith("xl/worksheets/sheet") && name.endsWith(".xml") && sheetBytes == null)) {
                    sheetBytes = zip.readBytes()
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }

        if (sheetBytes == null) return emptyList()

        // 2. Parse sheet XML
        return parseSheetXml(sheetBytes!!, sharedStrings)
    }

    private fun parseSharedStrings(xmlBytes: ByteArray): List<String> {
        val list = mutableListOf<String>()
        try {
            val parser = Xml.newPullParser()
            parser.setInput(ByteArrayInputStream(xmlBytes), "UTF-8")
            var eventType = parser.eventType
            val currentText = StringBuilder()
            var insideText = false

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (parser.name.equals("t", ignoreCase = true)) {
                            insideText = true
                            currentText.clear()
                        }
                    }
                    XmlPullParser.TEXT -> {
                        if (insideText) {
                            currentText.append(parser.text)
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name.equals("t", ignoreCase = true)) {
                            insideText = false
                            list.add(currentText.toString())
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (_: Exception) {}
        return list
    }

    private fun parseSheetXml(xmlBytes: ByteArray, sharedStrings: List<String>): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        try {
            val parser = Xml.newPullParser()
            parser.setInput(ByteArrayInputStream(xmlBytes), "UTF-8")
            var eventType = parser.eventType

            var currentRow = mutableListOf<String>()
            var currentCellVal = StringBuilder()
            var currentCellType = ""
            var insideValue = false

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (parser.name.lowercase()) {
                            "row" -> {
                                currentRow = mutableListOf()
                            }
                            "c" -> {
                                currentCellType = parser.getAttributeValue(null, "t") ?: ""
                                currentCellVal.clear()
                            }
                            "v" -> {
                                insideValue = true
                                currentCellVal.clear()
                            }
                            "t" -> {
                                insideValue = true
                                currentCellVal.clear()
                            }
                        }
                    }
                    XmlPullParser.TEXT -> {
                        if (insideValue) {
                            currentCellVal.append(parser.text)
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        when (parser.name.lowercase()) {
                            "v", "t" -> {
                                insideValue = false
                            }
                            "c" -> {
                                val raw = currentCellVal.toString().trim()
                                val cellText = if (currentCellType == "s") {
                                    val index = raw.toIntOrNull()
                                    if (index != null && index in sharedStrings.indices) {
                                        sharedStrings[index]
                                    } else raw
                                } else {
                                    raw
                                }
                                currentRow.add(cellText)
                            }
                            "row" -> {
                                if (currentRow.any { it.isNotBlank() }) {
                                    rows.add(currentRow)
                                }
                            }
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (_: Exception) {}
        return rows
    }

    /**
     * Auto-detect encoding for text/CSV and parse rows.
     */
    private fun parseDelimitedText(bytes: ByteArray): List<List<String>> {
        val text = decodeBytesWithAutoCharset(bytes)
        if (text.isBlank()) return emptyList()

        val lines = text.split("\r\n", "\n", "\r")
            .filter { it.isNotBlank() }

        if (lines.isEmpty()) return emptyList()

        // Detect delimiter (comma, tab, semicolon)
        val delimiter = detectDelimiter(lines.take(5))

        return lines.map { line -> parseCsvLine(line, delimiter) }
            .filter { row -> row.any { it.isNotBlank() } }
    }

    private fun decodeBytesWithAutoCharset(bytes: ByteArray): String {
        // 1. Check for UTF-8 BOM (0xEF, 0xBB, 0xBF)
        if (bytes.size >= 3 &&
            bytes[0] == 0xEF.toByte() &&
            bytes[1] == 0xBB.toByte() &&
            bytes[2] == 0xBF.toByte()
        ) {
            return String(bytes, 3, bytes.size - 3, StandardCharsets.UTF_8)
        }

        // 2. Check for UTF-16 LE BOM (0xFF, 0xFE)
        if (bytes.size >= 2 && bytes[0] == 0xFF.toByte() && bytes[1] == 0xFE.toByte()) {
            return String(bytes, 2, bytes.size - 2, StandardCharsets.UTF_16LE)
        }

        // 3. Check for UTF-16 BE BOM (0xFE, 0xFF)
        if (bytes.size >= 2 && bytes[0] == 0xFE.toByte() && bytes[1] == 0xFF.toByte()) {
            return String(bytes, 2, bytes.size - 2, StandardCharsets.UTF_16BE)
        }

        // 4. Try strict UTF-8 decoding
        val utf8Decoder: CharsetDecoder = StandardCharsets.UTF_8.newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT)

        try {
            val charBuffer = utf8Decoder.decode(ByteBuffer.wrap(bytes))
            return charBuffer.toString()
        } catch (_: CharacterCodingException) {
            // Not valid UTF-8! Fall back to Windows-1256 (Arabic Windows code page)
        }

        // 5. Try Windows-1256 for Arabic Excel exports
        try {
            val win1256 = Charset.forName("windows-1256")
            return String(bytes, win1256)
        } catch (_: Exception) {}

        // 6. Final fallback to UTF-8 with replacement chars
        return String(bytes, StandardCharsets.UTF_8)
    }

    private fun detectDelimiter(sampleLines: List<String>): Char {
        var commaCount = 0
        var tabCount = 0
        var semicolonCount = 0

        for (line in sampleLines) {
            commaCount += line.count { it == ',' }
            tabCount += line.count { it == '\t' }
            semicolonCount += line.count { it == ';' }
        }

        return when {
            tabCount > commaCount && tabCount > semicolonCount -> '\t'
            semicolonCount > commaCount && semicolonCount > tabCount -> ';'
            else -> ','
        }
    }

    fun parseCsvLine(line: String, delimiter: Char = ','): List<String> {
        val result = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < line.length) {
            val c = line[i]
            when {
                c == '"' -> {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                        sb.append('"')
                        i++
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                c == delimiter && !inQuotes -> {
                    result.add(sb.toString().trim())
                    sb.clear()
                }
                else -> {
                    sb.append(c)
                }
            }
            i++
        }
        result.add(sb.toString().trim())
        return result
    }
}
