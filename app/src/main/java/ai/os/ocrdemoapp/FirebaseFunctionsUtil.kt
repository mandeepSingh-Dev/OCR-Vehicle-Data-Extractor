package ai.os.ocrdemoapp

import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive

object FirebaseFunctionsUtil {

    val functions = Firebase.functions

    fun createRequestBody(base64encoded : String?): String {
        // Create json request to cloud vision
        val request = JsonObject()
        // Add image to request
        val image = JsonObject()
        image.add("content", JsonPrimitive(base64encoded))
        request.add("image", image)
        // Add features to the request
        val feature = JsonObject()
        feature.add("type", JsonPrimitive("TEXT_DETECTION"))
        // Alternatively, for DOCUMENT_TEXT_DETECTION:
// feature.add("type", JsonPrimitive("DOCUMENT_TEXT_DETECTION"))
        val features = JsonArray()
        features.add(feature)
        request.add("features", features)
        return request.toString()
    }

    fun createVisionRequestBodyForApi(base64encoded: String?): VisionRequestBody {
       return VisionRequestBody(
            requests = listOf(VisionRequestBody.VisionRequest(features = listOf(VisionRequestBody.VisionRequest.VisionFeature("TEXT_DETECTION")),
                image = VisionRequestBody.VisionRequest.VisionImage(content = base64encoded)
            ))
        )
    }

     fun annotateImage(requestJson: String): Task<JsonElement> {
        return functions
            .getHttpsCallable("annotateImage")
            .call(requestJson)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data
                val jsonElement = JsonParser.parseString(Gson().toJson(result))
                jsonElement
            }
    }

}
