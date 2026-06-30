package ai.os.ocrdemoapp

import ai.os.ocrdemoapp.ui.NewRCEnglishFields
import ai.os.ocrdemoapp.ui.NewRCFields
import ai.os.ocrdemoapp.ui.OldRCEnglishFields
import ai.os.ocrdemoapp.ui.OldRCFields
import android.graphics.Rect
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlin.math.abs

object TextRecognizeUtil {

    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)



    fun recognizeImageForNewRc(image : InputImage, getStructuredTexts : (List<List<Text.Line>>) -> Unit, onSuccess : (englishDetails: Map<String, String>, malaysianDetails: Map<String, String>) -> Unit, onFailure : () -> Unit) {
        val result = recognizer.process(image)
        .addOnSuccessListener { visionText ->


            visionText.textBlocks.forEach{
                Log.d("fknbkfnbfj", it.text.toString())
            }

            processOldRc2(visionText).forEach {
                Log.d("flbkfnbkf", it.key.toString() + " -> "  + it.value.toString())
            }


//            val t = processBlocksByCoordinates(visionText)
            val t = processBlocksByCoordinates(visionText)

            val  malaysianDetails = extractDetailsFromRowsInMalaysian(t)
            val  englishDetails = extractDetailsInEnglishFromRows(t)
            onSuccess(englishDetails, malaysianDetails)

            visionText.textBlocks.forEach {
                Log.d("kfnbkf", it.text.toString())
            }

            Log.d("fkbnfbfjmb", visionText.text.toString())
        }
            .addOnFailureListener { e ->
                Log.d("fkbnfbfjmb", e.message.toString())
                onFailure()
            }
    }

    fun recognizeImageForOldRc(image : InputImage, getStructuredTexts : (List<List<Text.Line>>) -> Unit, onSuccess : (englishDetails: Map<String, String>, malaysianDetails: Map<String, String>) -> Unit, onFailure : () -> Unit) {
        val result = recognizer.process(image)
        .addOnSuccessListener { visionText ->


            visionText.textBlocks.forEach{
                Log.d("fknbkfnbfj", it.text.toString())
            }

            processOldRc2(visionText).forEach {
                Log.d("flbkfnbkf", it.key.toString() + " -> "  + it.value.toString())
            }


            val t1 = processBlocksByCoordinatesForOldRC(visionText)

            val  malaysianDetailsOLD = extractDetailsFromRowsInMalaysianOLD(t1)
            val  englishDetailsOLD = extractDetailsFromRowsInEnglishOLD(t1)
            onSuccess(englishDetailsOLD, malaysianDetailsOLD)

            malaysianDetailsOLD.forEach { string, string1 ->
                Log.d("fkbkbnfk", "$string       ->     $string1")
            }

            visionText.textBlocks.forEach {
                Log.d("kfnbkf", it.text.toString())
            }

            Log.d("fkbnfbfjmb", visionText.text.toString())
        }
            .addOnFailureListener { e ->
                Log.d("fkbnfbfjmb", e.message.toString())
                onFailure()
            }
    }
    fun extractRcDetails(text: String): RcDetails {

        val ownerName = Regex(
            "(?:OWNER NAME|NAME)\\s*[:\\-]?\\s*([A-Z ]+)",
            RegexOption.IGNORE_CASE
        ).find(text)?.groupValues?.get(1)?.trim()

        val chassisNumber = Regex(
            "(?:CHASSIS NO|CHASSIS NUMBER)\\s*[:\\-]?\\s*([A-Z0-9]+)",
            RegexOption.IGNORE_CASE
        ).find(text)?.groupValues?.get(1)?.trim()

        val engineNumber = Regex(
            "(?:ENGINE NO|ENGINE NUMBER)\\s*[:\\-]?\\s*([A-Z0-9]+)",
            RegexOption.IGNORE_CASE
        ).find(text)?.groupValues?.get(1)?.trim()

        val registrationNumber = Regex(
            "[A-Z]{2}[0-9]{1,2}[A-Z]{1,3}[0-9]{4}",
            RegexOption.IGNORE_CASE
        ).find(text)?.value

        return RcDetails(
            ownerName = ownerName,
            chassisNumber = chassisNumber,
            engineNumber = engineNumber,
            registrationNumber = registrationNumber
        )
    }

    data class RcDetails(
        val ownerName: String?,
        val chassisNumber: String?,
        val engineNumber: String?,
        val registrationNumber: String?
    )
    fun parseMalaysianRC(rawText: String): Map<String, String> {
        val details = mutableMapOf<String, String>()

        // 1. Extract Registration Number (usually follows a pattern like VHH8774)
        val regMatch = Regex("[A-Z]{1,3}\\s?\\d{1,4}[A-Z]?").find(rawText)
        details["Registration_No"] = regMatch?.value ?: "Not Found"

        // 2. Extract Chassis & Engine Number
        // Looking for the pattern: PMYUGO... / G3G5E...
        val chassisEngineRegex = Regex("([A-Z0-9]+)\\s*/\\s*([A-Z0-9\\-]+)")
        val chassisMatch = chassisEngineRegex.find(rawText)
        if (chassisMatch != null) {
            details["Chassis_No"] = chassisMatch.groupValues[1]
            details["Engine_No"] = chassisMatch.groupValues[2]
        }

        // 3. Extract Make and Model (e.g., YAMAHA / 135LC)
        val modelRegex = Regex("(?:Buatan|Nama Model).*?:?\\s*([A-Z]+)\\s*/\\s*([A-Z0-9]+)", RegexOption.IGNORE_CASE)
        // If the labels and values are split, search for the brand directly
        val brandRegex = Regex("(YAMAHA|HONDA|TOYOTA|PERODUA|PROTON)\\s*/\\s*([A-Z0-9\\s]+)")
        val brandMatch = brandRegex.find(rawText)
        if (brandMatch != null) {
            details["Make"] = brandMatch.groupValues[1]
            details["Model"] = brandMatch.groupValues[2]
        }

        // 4. Extract Owner Name
        // In your text, the name appears right after the Registration Number
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val regIndex = lines.indexOfFirst { it.contains(details["Registration_No"] ?: "XYZ") }
        if (regIndex != -1 && regIndex + 1 < lines.size) {
            details["Owner_Name"] = lines[regIndex + 1]
        }

        return details
    }


    data class FlexibleRow(
        var averageTop: Int,
        val lines: MutableList<Text.Line> = mutableListOf()
    )

    fun processBlocksByCoordinates(visionText: Text): List<List<Text.Line>> {
        val allLines = mutableListOf<Text.Line>()

        // 1. Flatten all lines from all blocks into a single list
        visionText.textBlocks.forEachIndexed { index, block ->
            Log.d("fbklfkbnf", "Block $index")
            for (line in block.lines) {
                Log.d("fbklfkbnf", line.text.toString() + "and Top is " + line.boundingBox?.top)
                allLines.add(line)
                Log.d("fbklfkbnf", "=================\n\n")
            }
        }


        val rows = mutableListOf<FlexibleRow>()
        // A threshold (in pixels) to determine if two lines belong to the same row.
        // Adjust this based on your camera resolution / image scaling.
        val yTolerance = 15

        // 2. Group lines based on their vertical (Y) alignment

        for (line in allLines) {
            val box = line.boundingBox ?: continue
            val lineTop = box.top
            Log.d("fbknfkbnfkb", "Line is : ${line.text.toString()} and bounding top is ${box.top}")

            // Check if we already have a row that matches this vertical position
            val matchingRow = rows.find { abs(it.averageTop - lineTop) <= yTolerance }
            Log.d("fkbnkfkfn33043","${line.text}(${lineTop})" + " ===>>>>  " + matchingRow?.lines?.map { "${it.text} - ${it.boundingBox?.top}" }.toString())

            if (matchingRow != null) {
                matchingRow.lines.add(line)
                // Recalculate average top to keep it stable
                matchingRow.averageTop = matchingRow.lines.map { it.boundingBox?.top ?: 0 }.average().toInt()
            } else {
                rows.add(FlexibleRow(averageTop = lineTop, lines = mutableListOf(line)))
            }
        }

        // 3. Sort rows from top to bottom
        rows.sortBy { it.averageTop }

        // 4. Within each row, sort lines from left to right (X coordinate)
        val structuredDocument = mutableListOf<List<Text.Line>>()
        for (row in rows) {
            val sortedRow = row.lines.sortedBy { it.boundingBox?.left ?: 0 }
            structuredDocument.add(sortedRow)
        }

        return structuredDocument
    }
    fun processBlocksByCoordinatesForOldRC(visionText: Text): MutableList<RowItem> {

        val allLines = mutableListOf<Text.Line>()

        // 1. Flatten all lines from all blocks into a single list
        visionText.textBlocks.forEachIndexed { index, block ->
            Log.d("fbklfkbnf", "Block $index")
            for (line in block.lines) {
                Log.d("fbklfkbnf", line.text.toString() + "and Top is " + line.boundingBox?.top)
                allLines.add(line)
                Log.d("fbklfkbnf", "=================\n\n")
            }
        }



        val rowItems = mutableListOf<RowItem>()
        val rows = mutableListOf<FlexibleRow>()
        // A threshold (in pixels) to determine if two lines belong to the same row.
        // Adjust this based on your camera resolution / image scaling.
        val yTolerance = 30

        // 2. Group lines based on their vertical (Y) alignment

        for (line in allLines) {
            val text = line.text
            val box = line.boundingBox ?: continue
            val lineTop = box.top

            val centerY = (box.top + box.bottom) / 2

            val rowItem = rowItems.find {

                val rowBox = it.boundingBox ?: return@find false
                val rowCenterY = (rowBox.top + rowBox.bottom) / 2

                // Dynamic tolerance based on average height
                val tolerance = ((rowBox.height() + box.height()) / 2f * 0.5f).toInt()


                abs(rowCenterY - centerY) <= tolerance
            }

//            val rowItem = rowItems.find {  abs(it.boundingBox?.top?.minus(lineTop) ?: 0) <= yTolerance }
//            val rowItem = rowItems.find {  abs(it.boundingBox?.top?.minus(lineTop) ?: 0) <= (box.height().times(0.5)) }

            if(rowItem == null){
                rowItems.add(RowItem(boundingBox = box, value1 = text))
            }else{
                if(rowItem.value2.isNullOrEmpty()) {         // Condition to avoid 3rd and 4th value in same row and it only allows upto second column value of image.
                    val index = rowItems.indexOf(rowItem)
                    val uRowItem = rowItem.copy(value2 = text)
                    if (index != -1) {
                        rowItems.set(index, uRowItem)
                    }
                }
            }


            val tolerance = ((box.height() + box.height()) / 2f * 0.5f).toInt()

            Log.d("fbknfkbnfkb", "Line is : ${line.text.toString()} and bounding top is ${box.top} and height is ${box.height()} and centerY : $centerY and intolerance: $tolerance")

            // Check if we already have a row that matches this vertical position
            val matchingRow = rows.find { abs(it.averageTop - lineTop) <= yTolerance }
            Log.d("fkbnkfkfn33043","${line.text}(${lineTop})" + " ===>>>>  " + matchingRow?.lines?.map { "${it.text} - ${it.boundingBox?.top}" }.toString())

            if (matchingRow != null) {
                matchingRow.lines.add(line)
                // Recalculate average top to keep it stable
                matchingRow.averageTop = matchingRow.lines.map { it.boundingBox?.top ?: 0 }.average().toInt()
            } else {
                rows.add(FlexibleRow(averageTop = lineTop, lines = mutableListOf(line)))
            }
        }

        rowItems.filter { !it.value1.isNullOrEmpty() && !it.value2.isNullOrEmpty() }.forEach {
            Log.d("fbnfbknfbfk", it.toString())
        }

        // 3. Sort rows from top to bottom
        rows.sortBy { it.averageTop }

        // 4. Within each row, sort lines from left to right (X coordinate)
        val structuredDocument = mutableListOf<List<Text.Line>>()
        for (row in rows) {
            val sortedRow = row.lines.sortedBy { it.boundingBox?.left ?: 0 }
            structuredDocument.add(sortedRow)
        }

        return rowItems
    }

    data class StructuredRow(
        val leftText: String,
        val rightText: String,
        val top: Int
    )

    fun processOldRc(
        visionText: Text
    ): List<StructuredRow> {

        val rows = processBlocksByCoordinates(visionText)

        val allBoxes = visionText.textBlocks
            .flatMap { it.lines }
            .mapNotNull { it.boundingBox }

        val documentCenterX =
            allBoxes.map { it.centerX() }.average().toInt()

        val structuredRows = mutableListOf<StructuredRow>()

        rows.forEach { row ->

            val leftParts = mutableListOf<String>()
            val rightParts = mutableListOf<String>()

            row.forEach { line ->

                val centerX =
                    line.boundingBox?.centerX() ?: 0

                if (centerX < documentCenterX) {
                    leftParts.add(line.text)
                } else {
                    rightParts.add(line.text)
                }
            }

            structuredRows.add(
                StructuredRow(
                    leftText = leftParts.joinToString(" ").trim(),
                    rightText = rightParts.joinToString(" ").trim(),
                    top = row.firstOrNull()?.boundingBox?.top ?: 0
                )
            )
        }

        return structuredRows
    }


    //FOR NEW FORMAT RC
    fun extractDetailsInEnglishFromRows(structuredDocument: List<List<Text.Line>>): Map<String, String> {
        val extractedData = mutableMapOf<String, String>()

        structuredDocument.forEach {
            Log.d("fbknfjkbnfjkb",it.map {
                it.text
            }.joinToString(", "))
        }

        for (row in structuredDocument) {
            // If a row has multiple segments (e.g., [No. Pendaftaran] and [:VHH8774])
            if (row.size >= 2) {
                val leftText = row.first().text.trim().lowercase()
                val normalizeText = normalize(row.first().text.trim())
                val rightText = row.getOrNull(1)?.text?.trim()?.removePrefix(":")?.trim() ?: ""
                Log.d("fknbkfnbfk", normalizeText.toString())

                when {
                    normalizeText.contains("nopendaftaran", ignoreCase = true) -> extractedData[NewRCEnglishFields.NO_PENDAFTARAN] = rightText
                    //name
                    leftText.contains("id") || leftText.contains("noid") -> extractedData[NewRCEnglishFields.NO_ID] = rightText
                    leftText.contains("pemunya") || leftText.contains("berdaftar") -> extractedData[NewRCEnglishFields.NAMA_PEMUNYA_BERDAFTAR] = rightText
                    //address
                    normalizeText.contains("alamat", ignoreCase = true) -> extractedData[NewRCEnglishFields.ALAMAT] = rightText
                    //New chasis and engine
                    normalizeText.contains("chasis", ignoreCase = true) -> {
                        // Splits "PMYUGO810M0291059/ G3G5E-291099" into Chassis and Engine
                        val parts = rightText.split("/")
                        if (parts.size == 2) {
                            extractedData[NewRCEnglishFields.NO_CHASIS] = parts[0].trim()
                            extractedData[NewRCEnglishFields.NO_ENJIN] = parts[1].trim()
                        } else {
                            extractedData["Chassis_Raw"] = rightText
                        }
                    }



                    //Engine Capacity
                    leftText.contains("keupayaan",ignoreCase = true) -> extractedData[NewRCEnglishFields.KEUPAYAAN_ENJIN] = rightText
//                    normalizeText.contains("keupayaanengin", ignoreCase = true) -> extractedData["Engine Capacity"] = rightText


                    normalizeText.contains("buatan", ignoreCase = true) -> extractedData[NewRCEnglishFields.BUATAN_NAMA_MODEL] = rightText

                    //Fuel Type
                    leftText.contains("bahan") || leftText.contains("bakar") -> extractedData[NewRCEnglishFields.BAHAN_BAKAR] = rightText

                                       //Original Status
                    leftText.contains("status") || leftText.contains("asal") -> extractedData[NewRCEnglishFields.STATUS_ASAL] = rightText


                    //Vehicle Usage Class
                    leftText.contains("kelas") || leftText.contains("kegunaan") -> extractedData[NewRCEnglishFields.KELAS_KEGUNAAN] = rightText

                    //Pick-up and year manufactured
                    leftText.contains("jenis") || leftText.contains("badan") || leftText.contains("tahun") || leftText.contains("dibuat") -> extractedData[NewRCEnglishFields.JENIS_BADAN_TAHUN_DIBUAT] = rightText


                    normalizeText.contains("tarikh", ignoreCase = true) -> extractedData[NewRCEnglishFields.TARIKH_PENDAFTARAN] = rightText

                    //B.D.M/B.G.K/BTM
                    leftText.contains("b.d.m") || leftText.contains("b.g.k") || leftText.contains("b.t.m") || leftText.contains("btm")  -> extractedData[NewRCEnglishFields.BDM_BGK_BTM] = rightText

                }
            }
            // Handle Edge Case: Sometimes Owner Address spans multiple rows underneath without labels
            else if (row.size == 1) {
                val text = row.first().text.trim()
                // You can append this text to an address field if it matches address patterns
            }
        }
        NewRCEnglishFields.fieldsList.map { field ->
            if(!extractedData.contains(field)){
                field
            }else{
                null
            }
        }.filterNotNull().forEach {
            extractedData[it] = "-"
        }
        return extractedData
    }
    //FOR NEW FORMAT RC
    fun extractDetailsFromRowsInMalaysian(structuredDocument: List<List<Text.Line>>): Map<String, String> {
        val extractedData = mutableMapOf<String, String>()

        structuredDocument.forEach {
            Log.d("fbknfjkbnfjkb",it.map {
                it.text
            }.joinToString(", "))
        }

        for (row in structuredDocument) {
            // If a row has multiple segments (e.g., [No. Pendaftaran] and [:VHH8774])
            if (row.size >= 2) {

                val leftText = row.first().text.trim().lowercase()
                val normalizeText = normalize(row.first().text.trim())
                val rightText = row.getOrNull(1)?.text?.trim()?.removePrefix(":")?.trim() ?: ""
                Log.d("fknbkfnbfk", normalizeText.toString())

                when {
                    normalizeText.contains("nopendaftaran", ignoreCase = true) -> extractedData[NewRCFields.NO_PENDAFTARAN] = rightText
                    leftText.contains("id") || leftText.contains("noid") -> extractedData[NewRCFields.NO_ID] = rightText
                    //name
                    leftText.contains("pemunya") || leftText.contains("berdaftar") -> extractedData[NewRCFields.NAMA_PEMUNYA_BERDAFTAR] = rightText
                    //address
                    normalizeText.contains("alamat", ignoreCase = true) -> extractedData[NewRCFields.ALAMAT] = rightText
                    //New chasis and engine
                    normalizeText.contains("chasis", ignoreCase = true) -> {
                        // Splits "PMYUGO810M0291059/ G3G5E-291099" into Chassis and Engine
                        val parts = rightText.split("/")
                        if (parts.size == 2) {
                            extractedData[NewRCFields.NO_CHASIS] = parts[0].trim()
                            extractedData[NewRCFields.NO_ENJIN] = parts[1].trim()
                        } else {
                            extractedData["Chassis_Raw"] = rightText
                        }
                    }

                    //Engine Capacity
                    leftText.contains("keupayaan",ignoreCase = true) -> extractedData[NewRCFields.KEUPAYAAN_ENJIN] = rightText
//                    normalizeText.contains("keupayaanengin", ignoreCase = true) -> extractedData["Engine Capacity"] = rightText


                    normalizeText.contains("buatan", ignoreCase = true) -> extractedData[NewRCFields.BUATAN_NAMA_MODEL] = rightText

                    //Fuel Type
                    leftText.contains("bahan") || leftText.contains("bakar") -> extractedData[NewRCFields.BAHAN_BAKAR] = rightText

                    //Original Status
                    leftText.contains("status") || leftText.contains("asal") -> extractedData[NewRCFields.STATUS_ASAL] = rightText


                    //Vehicle Usage Class
                    leftText.contains("kelas") || leftText.contains("kegunaan") -> extractedData[NewRCFields.KELAS_KEGUNAAN] = rightText

                    //Pick-up and year manufactured
                    leftText.contains("jenis") || leftText.contains("badan") || leftText.contains("tahun") || leftText.contains("dibuat") -> extractedData[NewRCFields.JENIS_BADAN_TAHUN_DIBUAT] = rightText

                    normalizeText.contains("tarikh", ignoreCase = true) -> extractedData[NewRCFields.TARIKH_PENDAFTARAN] = rightText

                    //B.D.M/B.G.K/BTM
                    leftText.contains("b.d.m") || leftText.contains("b.g.k") || leftText.contains("b.t.m") || leftText.contains("btm")  -> extractedData[NewRCFields.BDM_BGK_BTM] = rightText

                }
            }
            // Handle Edge Case: Sometimes Owner Address spans multiple rows underneath without labels
            else if (row.size == 1) {
                val text = row.first().text.trim()
                // You can append this text to an address field if it matches address patterns
            }
        }

        NewRCFields.fieldsList.map { field ->
            if(!extractedData.contains(field)){
                field
            }else{
                null
            }
        }.filterNotNull().forEach {
            extractedData[it] = "-"
        }


        return extractedData
    }


    //FOR OLD FORMAT RC
    fun extractDetailsFromRowsInEnglishOLD(rowItems: List<RowItem>): Map<String, String> {
        val extractedData = mutableMapOf<String, String>()

        val scoreThreshold = 70

        for (row in rowItems) {
            // If a row has multiple segments (e.g., [No. Pendaftaran] and [:VHH8774])
            if (!row.value1.isNullOrEmpty() && !row.value2.isNullOrEmpty()) {

                when {
                    similarityScore(row.value1, OldRCFields.NAMA_PEMUNYA) > scoreThreshold -> {
                        extractedData[OldRCEnglishFields.NAMA_PEMUNYA] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.NO_ENJIN) > scoreThreshold -> {
                        extractedData[OldRCEnglishFields.NO_ENJIN] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.NO_CASIS) > scoreThreshold -> {
                        extractedData[OldRCEnglishFields.NO_CASIS] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.ALAMAT) > scoreThreshold -> {
                        extractedData[OldRCEnglishFields.ALAMAT] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.BUATAN) > scoreThreshold -> {
                        extractedData[OldRCEnglishFields.BUATAN] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.NAMA_MODEL) > scoreThreshold -> {
                        extractedData[OldRCEnglishFields.NAMA_MODEL] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.KEUPAYAAN_ENJIN) > scoreThreshold -> {
                        extractedData[OldRCEnglishFields.KEUPAYAAN_ENJIN] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.BAHAN_BAKAR) > scoreThreshold -> {
                        extractedData[OldRCEnglishFields.BAHAN_BAKAR] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.WARNA) > scoreThreshold -> {
                        extractedData[OldRCEnglishFields.WARNA] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.KELAS_KEGUNAAN) > scoreThreshold -> {
                        extractedData[OldRCEnglishFields.KELAS_KEGUNAAN] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.JENIS_BADAN) > scoreThreshold -> {
                        extractedData[OldRCEnglishFields.JENIS_BADAN] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.TAHUN_DIBUAT) > scoreThreshold -> {
                        extractedData[OldRCEnglishFields.TAHUN_DIBUAT] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.TARIKH_PENDAFTARAN) > scoreThreshold -> {
                        extractedData[OldRCEnglishFields.TARIKH_PENDAFTARAN] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.STATUS_PEMUNYA) > scoreThreshold -> {
                        extractedData[OldRCEnglishFields.STATUS_PEMUNYA] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.MUATAN_TEMPAT) > scoreThreshold -> {
                        extractedData[OldRCEnglishFields.MUATAN_TEMPAT] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.DUDUK) > scoreThreshold -> {
                        extractedData[OldRCEnglishFields.DUDUK] = row.value2
                    }

                    similarityScore(row.value1, OldRCFields.KADAR_LESEN) > scoreThreshold -> {
                        extractedData[OldRCEnglishFields.KADAR_LESEN] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.KENDERAAN_MOTOR) > scoreThreshold -> {
                        extractedData[OldRCEnglishFields.KENDERAAN_MOTOR] = row.value2
                    }

                }
            }
        }
        extractedData.forEach {
            Log.d("fkbnkfnbf","${it.key} -> ${it.value.toString()}")
        }
        OldRCEnglishFields.fieldsList.map { field ->
            if(!extractedData.contains(field)){
                field
            }else{
                null
            }
        }.filterNotNull().forEach {
            extractedData[it] = "-"
        }

        return extractedData
    }
    //FOR OLD FORMAT RC
    fun extractDetailsFromRowsInMalaysianOLD(rowItems: List<RowItem>): Map<String, String> {
        val extractedData = mutableMapOf<String, String>()

        val scoreThreshold = 70

        for (row in rowItems) {
            // If a row has multiple segments (e.g., [No. Pendaftaran] and [:VHH8774])
            if (!row.value1.isNullOrEmpty() && !row.value2.isNullOrEmpty()) {

                when {
                    similarityScore(row.value1, OldRCFields.NAMA_PEMUNYA) > scoreThreshold -> {
                        extractedData[OldRCFields.NAMA_PEMUNYA] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.NO_ENJIN) > scoreThreshold -> {
                        extractedData[OldRCFields.NO_ENJIN] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.NO_CASIS) > scoreThreshold -> {
                        extractedData[OldRCFields.NO_CASIS] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.ALAMAT) > scoreThreshold -> {
                        extractedData[OldRCFields.ALAMAT] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.BUATAN) > scoreThreshold -> {
                        extractedData[OldRCFields.BUATAN] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.NAMA_MODEL) > scoreThreshold -> {
                        extractedData[OldRCFields.NAMA_MODEL] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.KEUPAYAAN_ENJIN) > scoreThreshold -> {
                        extractedData[OldRCFields.KEUPAYAAN_ENJIN] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.BAHAN_BAKAR) > scoreThreshold -> {
                        extractedData[OldRCFields.BAHAN_BAKAR] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.WARNA) > scoreThreshold -> {
                        extractedData[OldRCFields.WARNA] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.KELAS_KEGUNAAN) > scoreThreshold -> {
                        extractedData[OldRCFields.KELAS_KEGUNAAN] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.JENIS_BADAN) > scoreThreshold -> {
                        extractedData[OldRCFields.JENIS_BADAN] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.TAHUN_DIBUAT) > scoreThreshold -> {
                        extractedData[OldRCFields.TAHUN_DIBUAT] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.TARIKH_PENDAFTARAN) > scoreThreshold -> {
                        extractedData[OldRCFields.TARIKH_PENDAFTARAN] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.STATUS_PEMUNYA) > scoreThreshold -> {
                        extractedData[OldRCFields.STATUS_PEMUNYA] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.MUATAN_TEMPAT) > scoreThreshold -> {
                        extractedData[OldRCFields.MUATAN_TEMPAT] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.DUDUK) > scoreThreshold -> {
                        extractedData[OldRCFields.DUDUK] = row.value2
                    }

                    similarityScore(row.value1, OldRCFields.KADAR_LESEN) > scoreThreshold -> {
                        extractedData[OldRCFields.KADAR_LESEN] = row.value2
                    }
                    similarityScore(row.value1, OldRCFields.KENDERAAN_MOTOR) > scoreThreshold -> {
                        extractedData[OldRCFields.KENDERAAN_MOTOR] = row.value2
                    }
                }
            }
            // Handle Edge Case: Sometimes Owner Address spans multiple rows underneath without labels
//            else if (row.size == 1) {
//                val text = row.first().text.trim()
//                 You can append this text to an address field if it matches address patterns
//            }
        }

        OldRCFields.fieldsList.map { field ->
            if(!extractedData.contains(field)){
                field
            }else{
                null
            }
        }.filterNotNull().forEach {
            extractedData[it] = "-"
        }

        return extractedData
    }




    fun normalize(text: String): String {
        return text.lowercase()
            .replace(" ", "")
            .replace(".", "")
            .replace(":", "")
    }


    fun processOldRc2(
        visionText: Text
    ): Map<String, String> {

        val resultMap = mutableMapOf<String, String>()

        // Extract all lines with their bounding boxes
        val lines = visionText.textBlocks
            .flatMap { it.lines }
            .mapNotNull { line ->
                line.boundingBox?.let { box ->
                    Triple(
                        line.text,
                        box,
                        line
                    )
                }
            }
            .sortedBy { it.second.top } // Sort by Y position

        // Define known label patterns (case insensitive)
        val labelPatterns = listOf(
            "NO. PENDAFTARAN:", "Nama Pemunyai:", "Berdaftar:", "Alamat:",
            "No. Enjin:", "No. Casis:", "Buatan:", "Nama Model:", "Keupayaan Enjin:",
            "Bahan Bakar:", "Warna:", "Kelas Kegunaan:", "Jenis Badan:",
            "Tahun Dibuat:", "Tarikh Pendaftaran:", "Status Pemunya:",
            "Muatan Tempat:", "Duduk:", "Kadar Lesen:", "Kenderaan Motor:",
            "B.D.M/B.G.K:", "B.T.T:", "Berat Kerb:", "B.T.M:", "Berat Dengan Muatan:"
        )

        var i = 0
        while (i < lines.size) {
            val (text, box, _) = lines[i]

            // Check if this line contains a label
            val matchedLabel = labelPatterns.find {
                text.contains(it, ignoreCase = true)
            }

            if (matchedLabel != null) {
                // Extract value from same line after colon
                val afterColon = text.substringAfter(matchedLabel).trim()

                if (afterColon.isNotEmpty()) {
                    resultMap[matchedLabel] = afterColon
                } else {
                    // Value might be on next line(s)
                    val valueLines = mutableListOf<String>()
                    var j = i + 1
                    while (j < lines.size && !lines[j].first.containsAny(labelPatterns)) {
                        valueLines.add(lines[j].first.trim())
                        j++
                    }
                    resultMap[matchedLabel] = valueLines.joinToString(" ")
                    i = j - 1
                }
            }
            i++
        }

        // Handle special fields outside normal label-value pairs
        handleSpecialFields(lines, resultMap)

        return resultMap
    }

    fun String.containsAny(patterns: List<String>): Boolean {
        return patterns.any { this.contains(it, ignoreCase = true) }
    }

    fun handleSpecialFields(lines: List<Triple<String, Rect, Text.Line>>, resultMap: MutableMap<String, String>) {
        // Look for registration number pattern (WXW424104)
        val regPattern = Regex("[A-Z]{3}\\d{6}")
        lines.forEach { (text, _, _) ->
            regPattern.find(text)?.let {
                if (!resultMap.containsKey("NO. PENDAFTARAN:")) {
                    resultMap["NO. PENDAFTARAN:"] = it.value
                }
            }
        }

        // Look for "WEI VII" patterns
        lines.forEach { (text, _, _) ->
            if (text.contains("WEI VII", ignoreCase = true) ||
                text.contains("SBN", ignoreCase = true)) {
                resultMap["Additional Info"] = resultMap.getOrDefault("Additional Info", "") + " " + text
            }
        }
    }
}


data class RowItem(
    val boundingBox : Rect? = null,
    val value1 : String? = null,
    val value2 : String? = null
){
    override fun toString(): String {
        return "Top: ${boundingBox?.top}" +
                " & height : ${boundingBox?.height()}" +
                " & 0.5 height : ${boundingBox?.height()?.times(0.5)}" +
                " & Label: ${value1} " +
                "& Value: ${value2} "
    }
}

fun similarityScore(expected: String, actual: String): Int {

    val s1 = expected
        .lowercase()
        .replace("\\s+".toRegex(), "")
        .replace("[^a-z0-9]".toRegex(), "")

    val s2 = actual
        .lowercase()
        .replace("\\s+".toRegex(), "")
        .replace("[^a-z0-9]".toRegex(), "")

    val distance = levenshteinDistance(s1, s2)

    val maxLength = maxOf(s1.length, s2.length)

    if (maxLength == 0) return 100

    return (((maxLength - distance).toDouble() / maxLength) * 100).toInt()
}

private fun levenshteinDistance(a: String, b: String): Int {

    val dp = Array(a.length + 1) {
        IntArray(b.length + 1)
    }

    for (i in 0..a.length) {
        dp[i][0] = i
    }

    for (j in 0..b.length) {
        dp[0][j] = j
    }

    for (i in 1..a.length) {
        for (j in 1..b.length) {

            val cost = if (a[i - 1] == b[j - 1]) 0 else 1

            dp[i][j] = minOf(
                dp[i - 1][j] + 1,
                dp[i][j - 1] + 1,
                dp[i - 1][j - 1] + cost
            )
        }
    }

    return dp[a.length][b.length]
}


//data class RcFieldsModel(
//
//)