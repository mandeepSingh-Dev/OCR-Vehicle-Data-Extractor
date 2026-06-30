package ai.os.ocrdemoapp

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface VisionApi {

        @POST("v1/images:annotate")
        suspend fun detectText(
            @Query("key") apiKey: String,
            @Body request: VisionRequestBody
        ): retrofit2.Response<ResponseBody>
    }

