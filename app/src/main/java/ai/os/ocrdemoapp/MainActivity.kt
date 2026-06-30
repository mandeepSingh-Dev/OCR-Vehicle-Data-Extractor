package ai.os.ocrdemoapp

import ai.os.ocrdemoapp.ui.RetrofitInstance
import ai.os.ocrdemoapp.ui.VehicleCertificateDetailsScreen
import ai.os.ocrdemoapp.ui.mapper.toDataClass
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ai.os.ocrdemoapp.ui.theme.OCRDemoAppTheme
import ai.os.ocrdemoapp.ui.theme.blue_dark
import ai.os.ocrdemoapp.ui.theme.theme_color
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.Space
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException


typealias RcEnglishDetails = Map<String,String>
typealias RcMalaysianDetails = Map<String,String>
class MainActivity : ComponentActivity() {
    val visionApi = RetrofitInstance.createService(serviceClass = VisionApi::class.java)

    val _rcEnglishDetails = MutableStateFlow<RcEnglishDetails?>(null)
    val rcEnglishDetails = _rcEnglishDetails.asStateFlow()

    val _rcMalaysianDetails = MutableStateFlow<RcMalaysianDetails?>(null)
    val rcMalaysianDetails = _rcMalaysianDetails.asStateFlow()

    val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap = _bitmap.asStateFlow()

    val _structuredText = MutableStateFlow<String?>(null)
    val structuredText = _structuredText.asStateFlow()

    var isNew = true
    val imageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {

            _rcEnglishDetails.value = null
            _rcMalaysianDetails.value = null

            isNew = true
            val image: InputImage
            try {
                image = InputImage.fromFilePath(this, it)
                TextRecognizeUtil.recognizeImageForNewRc(image, getStructuredTexts = {


                    _structuredText.value = it.filter { it.size >= 2 }.map {
                        it.mapIndexed { index, it -> if (index == 0) "${it.text}::: " else it.text }
                            .joinToString(" -> ")
                    }.joinToString("\n\n\n")
                }, onSuccess = { englishDetails, malaysianDetails ->
                    _rcEnglishDetails.value = englishDetails
                    _rcMalaysianDetails.value = malaysianDetails
                }, onFailure = {

                })
                _bitmap.value = BitmapFactory.decodeStream(contentResolver.openInputStream(it))
            } catch (e: IOException) {
                e.printStackTrace()
            }


//            val inputStream = contentResolver.openInputStream(it)
//            val bitmap = BitmapFactory.decodeStream(inputStream)
//            val bitmapBase64Image = bitmapToBase64(bitmap)
//            Log.d("fkbkfbnkf", it.toString())
//            Log.d("fkbkfbnkf", bitmapBase64Image.toString())

            /*  val requestBody = FirebaseFunctionsUtil.createRequestBody(bitmapBase64Image)
              FirebaseFunctionsUtil.annotateImage(requestBody).addOnCompleteListener { task ->
                  if(task.isSuccessful){
                      Log.d("fkbnkfbnf", task.result.toString() + " Success")
                  }else{
                      Log.d("fkbnkfbnf", " Failed: ${task.exception?.message.toString()}")
                  }
              }*/

        }

    }
    val imageLauncherForOldRc = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            isNew = false

            _rcEnglishDetails.value = null
            _rcMalaysianDetails.value = null

            val image: InputImage
            try {
                image = InputImage.fromFilePath(this, it)
                TextRecognizeUtil.recognizeImageForOldRc(image, getStructuredTexts = {
                    _structuredText.value = it.filter { it.size >= 2 }.map {
                        it.mapIndexed { index, it -> if (index == 0) "${it.text}::: " else it.text }
                            .joinToString(" -> ")
                    }.joinToString("\n\n\n")
                }, onSuccess = { englishDetails, malaysianDetails ->
                    _rcEnglishDetails.value = englishDetails
                    _rcMalaysianDetails.value = malaysianDetails
                }, onFailure = {

                })
                _bitmap.value = BitmapFactory.decodeStream(contentResolver.openInputStream(it))
            } catch (e: IOException) {
                e.printStackTrace()
            }



            val inputStream = contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val bitmapBase64Image = bitmapToBase64(bitmap)
            Log.d("fkbkfbnkf", it.toString())
            Log.d("fkbkfbnkf", bitmapBase64Image.toString())


            lifecycleScope.launch {
                try {
                    val response = visionApi.detectText(
                        apiKey = "AIzaSyBE7x7Y7JqGq-kz_0gN5lld7QSgaQuYUUA",
                        FirebaseFunctionsUtil.createVisionRequestBodyForApi(bitmapBase64Image)
                    )
                    if (response.isSuccessful) {
                        Log.d("fkbnjfbnjfb", "Success")
                    } else {



                        Log.d("bjfbfjvbnf", response.body()?.string().toString())
                        Log.d("293ur9vhve", response.body()?.toString().toString())
                        Log.d("90uininvierv", response.errorBody()?.string().toString())
                        Log.d("fkbnjfbnjfb", "Failure api")
                    }
                }catch (e: Exception){
                    Log.d("fkbnjfbnjfb", "Failure Exception: ${e.message}")
                }

            }
/*

            val requestBody = FirebaseFunctionsUtil.createRequestBody(bitmapBase64Image)
              FirebaseFunctionsUtil.annotateImage(requestBody).addOnCompleteListener { task ->
                  if(task.isSuccessful){
                      Log.d("fkbnkfbnf", task.result.toString() + " Success")
                  }else{
                      Log.d("fkbnkfbnf", " Failed: ${task.exception?.message.toString()}")
                  }
              }
*/
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val byteArray = "Mandeep Singh Yo Yo".toByteArray()
        byteArray.forEach { it
            Log.d("fkbnkbnfj", it.toString())
        }
        val stream = ByteArrayInputStream(byteArray)
        var countt : Int
        while(stream.read().also { countt = it } != -1){
            Log.d("fbknfbjknfb", countt.toString())
            Log.d("fbknfbjknfb", Char(countt).toString())
        }


        setContent {
            OCRDemoAppTheme {

                var showImageSelector by remember { mutableStateOf(false) }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .animateContentSize()
                        .blur(if(showImageSelector) 20.dp else 0.dp)
                ) {
                    val rcEnglishDetails by rcEnglishDetails.collectAsStateWithLifecycle()
                    val rcMalaysianDetails by rcMalaysianDetails.collectAsStateWithLifecycle()
                    val bitmap by bitmap.collectAsStateWithLifecycle()
                    val structuredText by structuredText.collectAsStateWithLifecycle()

                    val localDensity = LocalDensity.current

                    val screenWidth = (LocalConfiguration.current.screenWidthDp).dp.minus(30.dp)

                    VehicleCertificateDetailsScreen(
                        image = bitmap,
                        details = rcEnglishDetails.toDataClass(isNew = isNew),
                        onBrowse = {
                            Log.d("fbknfkbnf", "Hel0")
                            showImageSelector = true
                        },
                        onRemoveImage = {
                            _bitmap.value = null
                        }
                    )


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .animateContentSize()
                    ) {
                        AnimatedVisibility(rcEnglishDetails != null) {
                            rcEnglishDetails?.let {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier
                                        .widthIn(max = screenWidth)
                                        .systemBarsPadding()
                                        .padding(vertical = 10.dp)
                                        .padding(start = 20.dp, end = 5.dp)
                                        .dropShadow(
                                            shape = RoundedCornerShape(20.dp),
                                            shadow = Shadow(
                                                radius = 10.dp,
                                                color = Color.Black.copy(0.3f)
                                            )
                                        )
                                        .background(
                                            color = Color.White,
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .innerShadow(
                                            shape = RoundedCornerShape(20.dp),
                                            shadow = Shadow(
                                                radius = 10.dp,
                                                brush = verticalGradient(colors = listOf(Color.Black.copy(0.3f), blue_dark.copy(0.4f), blue_dark.copy(0.6f))),
                                                offset = DpOffset(x = 10.dp, y = 10.dp)
                                            )
                                        )
                                        .animateContentSize()
                                        .padding(20.dp)
                                ) {

                                    val list = rcEnglishDetails?.map {
                                        Pair(it.key, it.value)
                                    }

                                    Text(
                                        "RC (English)",
                                        style = TextStyle(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color.Red,
                                                    Color.Blue
                                                )
                                            )
                                        ),
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    list?.forEachIndexed { index, pair ->
                                        Text(buildAnnotatedString {
                                            withStyle(
                                                style = SpanStyle(
                                                    color = Color.Black,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            ) {
                                                append(pair.first + ": ")
                                            }
                                            withStyle(style = SpanStyle(color = Color.Black)) {
                                                append(pair.second)
                                            }
                                        }, modifier = Modifier.fillMaxWidth())
                                    }


                                }
                            }
                        }
                        AnimatedVisibility(rcMalaysianDetails != null) {
                            rcMalaysianDetails?.let {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier
                                        .widthIn(max = screenWidth)
                                        .systemBarsPadding()
                                        .padding(vertical = 10.dp)
                                        .padding(start = 10.dp, end = 20.dp)
                                        .dropShadow(
                                            shape = RoundedCornerShape(20.dp),
                                            shadow = Shadow(
                                                radius = 10.dp,
                                                color = Color.Black.copy(0.3f)
                                            )
                                        )
                                        .background(
                                            color = Color.White,
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .innerShadow(
                                            shape = RoundedCornerShape(20.dp),
                                            shadow = Shadow(
                                                radius = 15.dp,
                                                brush = verticalGradient(colors = listOf(Color.Black.copy(0.3f), blue_dark.copy(0.4f), blue_dark.copy(0.6f))),
                                                offset = DpOffset(x = 10.dp, y = 10.dp)
                                            )
                                        )
                                        .animateContentSize()
                                        .padding(20.dp)
                                )
                                {


                                    Text(
                                        "RC (Malay)",
                                        style = TextStyle(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color.Red,
                                                    Color.Blue
                                                )
                                            )
                                        ),
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    val list = rcMalaysianDetails?.map {
                                        Pair(it.key, it.value)
                                    }
                                    list?.forEachIndexed { index, pair ->
                                        Text(buildAnnotatedString {
                                            withStyle(
                                                style = SpanStyle(
                                                    color = Color.Black,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            ) {
                                                append(pair.first + ": ")
                                            }
                                            withStyle(style = SpanStyle(color = Color.Black)) {
                                                append(pair.second)
                                            }
                                        }, modifier = Modifier.fillMaxWidth())
                                    }
                                }
                            }
                        }
                    }
                    structuredText.let {
                        Text(it ?: "")
                    }
                    ImageSelector(
                        show = showImageSelector,
                        onNewImage = {
                            imageLauncher.launch("image/*")
                        },
                        onOldImage = {
                            imageLauncherForOldRc.launch("image/*")

                        },
                        onDismiss = {
                            showImageSelector = false
                        }
                    )
                }


            }
        }
        useBase64()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


fun useBase64() {
    val byteArray = "Mandeep Singh".toByteArray()
    Log.d("fbknfkbnfj", byteArray.joinToString(", "))
    val encodedBase64 = Base64.encode(byteArray, Base64.DEFAULT)

    val r = Base64.decode(encodedBase64, Base64.DEFAULT)
    Log.d("fkbnbkfnbfk", r.decodeToString())

    Log.d("ffjvbvfjbjf", encodedBase64.toString())
}

fun bitmapToBase64(bitmap: Bitmap): String {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageSelector(
    show : Boolean = false,
    onNewImage : () -> Unit,
    onOldImage : () -> Unit,
    onDismiss : () -> Unit,
){

    val coroutineScope = rememberCoroutineScope()
    val sheet = rememberModalBottomSheetState()
    if(show) {
        ModalBottomSheet(sheetState = sheet, onDismissRequest = onDismiss)
        {
            Button(
                onClick = {
                    coroutineScope.launch {
                        sheet.hide()
                        onDismiss()
                        onNewImage()
                    }
                },
                modifier = Modifier.padding(horizontal = 20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = theme_color),
            ) {
                Text("Import RC")
            }

            Spacer(modifier = Modifier.height(5.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        sheet.hide()
                        onDismiss()
                        onOldImage()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = theme_color),
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Text("Import Old Format RC")
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}