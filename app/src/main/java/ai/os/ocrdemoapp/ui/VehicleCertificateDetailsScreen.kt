package ai.os.ocrdemoapp.ui

import ai.os.ocrdemoapp.R
import ai.os.ocrdemoapp.ui.model.NewRCDataItem
import ai.os.ocrdemoapp.ui.model.OldRCDataItem
import ai.os.ocrdemoapp.ui.theme.Shape_10
import ai.os.ocrdemoapp.ui.theme.Shape_15
import ai.os.ocrdemoapp.ui.theme.Shape_20
import ai.os.ocrdemoapp.ui.theme.brush_bg
import ai.os.ocrdemoapp.ui.theme.green_1
import ai.os.ocrdemoapp.ui.theme.grren_2
import ai.os.ocrdemoapp.ui.theme.grren_3
import ai.os.ocrdemoapp.ui.theme.theme_color
import ai.os.ocrdemoapp.ui.theme.theme_color_2
import ai.os.ocrdemoapp.ui.theme.theme_color_3
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.nio.InvalidMarkException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

@Composable
fun VehicleCertificateDetailsScreen(
    onBrowse: () -> Unit,
    details: Any? = null,
    image: Bitmap?,
    onRemoveImage : () -> Unit
) {

    Log.d("fbkfkbnfk", details.toString())

    var name by rememberSaveable { mutableStateOf<String?>(null) }
    var iCnumber by rememberSaveable { mutableStateOf<String?>(null) }
    var vehicleNumber by rememberSaveable { mutableStateOf<String?>(null) }

    if (details is NewRCDataItem) {
        name = details.ownerName
        iCnumber = details.noId
        vehicleNumber = details.registrationNo
    } else if (details is OldRCDataItem) {
        name = details.ownerName
        iCnumber = null
        vehicleNumber = null
    } else {
        name = null
        iCnumber = null
        vehicleNumber = null
    }

    var showSubmitBtn by rememberSaveable{ mutableStateOf(false)}

    LaunchedEffect(Unit) {
        delay(400)
        showSubmitBtn = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = brush_bg)
            .systemBarsPadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 8.dp)
        )
        {
            Column(modifier = Modifier) {
                Image(
                    painter = painterResource(R.drawable.logo_1__1_), contentDescription = null,
                    modifier = Modifier.widthIn(max = 100.dp)
                )
                Text(
                    "Client Portal",
                    color = theme_color,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(R.drawable.baseline_notifications_none_24),
                contentDescription = null
            )
            Image(
                painter = painterResource(R.drawable.outline_account_circle_24),
                contentDescription = null
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .animateContentSize()
        )
        {
            vehicleChecksRow(modifier = Modifier.padding(top = 5.dp))
            IntroCard(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 30.dp))
            UploadVehicleCertificateCard(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp),
                onBrowse = onBrowse,
                bitmap = image,
                onRemoveImage = onRemoveImage
            )
            OwnerAndVehicleDetailsCard(
                name = name,
                iCnumber = iCnumber,
                vehicleNumber = vehicleNumber,
                modifier = Modifier.padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 20.dp
                )
            )
            PersonalDetailsCard(
                modifier = Modifier.padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 20.dp
                )
            )
            AddOnsCard(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        AnimatedVisibility(showSubmitBtn,
            enter = slideInVertically(initialOffsetY = {
                it
            }, animationSpec = spring(Spring.DampingRatioLowBouncy,
                Spring.StiffnessVeryLow))
        ) {
            Column {
                Button(
                    onClick = {},
                    shape = Shape_10,
                    colors = ButtonDefaults.buttonColors(containerColor = theme_color),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.baseline_check_24),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = Color.White)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Submit & Continue", color = Color.White)
                }
                Text(
                    "By submitting, you agree to our Terms of Service and Privacy policy",
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 5.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
    }

}

@Preview
@Composable
fun vehicleChecksRow(modifier: Modifier = Modifier) {

    Row(modifier = modifier.padding(top = 20.dp)) {

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                HorizontalDivider(
                    color = Color.Transparent,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(25.dp)
                        .background(color = theme_color, shape = CircleShape)
                        .padding(2.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.baseline_check_24),
                        colorFilter = ColorFilter.tint(color = Color.White),
                        contentDescription = null
                    )
                }
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .background(color = Color.Black)
                )
            }
            Text(
                "Vehicle Type",
                color = theme_color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .background(color = Color.Black)
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(25.dp)
                        .background(color = theme_color, shape = CircleShape)
                        .padding(2.dp)
                ) {
                    Text("2", color = Color.White, fontWeight = FontWeight.Bold)
                }
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .background(color = Color.Black)
                )
            }
            Text(
                "Vehicle Details",
                color = theme_color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .background(color = Color.Black)
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(25.dp)
                        .border(width = 1.dp, shape = CircleShape, color = Color.Gray)
                        .background(color = Color.Gray.copy(alpha = 0.1f), shape = CircleShape)
                        .padding(2.dp)
                ) {
                    Text("3", color = Color.Black, fontWeight = FontWeight.Bold)
                }
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f),
                    color = Color.Transparent
                )
            }
            Text(
                "Insurance Quotes",
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }

    }
}

@Preview
@Composable
fun IntroCard(modifier: Modifier = Modifier) {

    Row(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text("Hi Ahmad \uD83D\uDC4B", fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Text(
                "Upload your vehicle certificate (Grant) and confirm your details below.",
                style = TextStyle(lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,)
            )
        }
        Image(
            painter = painterResource(R.drawable.carimage), contentDescription = null,
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}

@Composable
fun UploadVehicleCertificateCard(
    modifier: Modifier = Modifier,
    bitmap: Bitmap? = null,
    onRemoveImage : () -> Unit,
    onBrowse: () -> Unit
) {

    val localDensity = LocalDensity.current
    var dragCardHeight by remember { mutableStateOf(200) }

    val dragCardHeightDp by remember {
        derivedStateOf {
            with(localDensity){dragCardHeight.toDp()}
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(
                shape = Shape_10,
                shadow = Shadow(color = theme_color.copy(alpha = 0.1f), radius = 20.dp)
            )
            .clip(Shape_20)
            .background(color = Color.White, Shape_20)
            .padding(15.dp)
            .animateContentSize()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(25.dp)
                    .background(color = theme_color, shape = CircleShape)
                    .padding(2.dp)
            ) {
                Text("1", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Text(
                "Upload Vehicle Certificate ",
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f, fill = false)
            )
            Text("(Grant)", color = theme_color, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }

        Box(

            modifier = Modifier
                .animateContentSize()
                .background(color = theme_color_3,Shape_10)
                .drawBehind {
                    drawRoundRect(
                        color = Color.Gray,
                        cornerRadius = CornerRadius(10f, 10f),
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(10f, 10f), // dash, gap 0f)
                            )
                        )
                    )
                }
        ) {
            bitmap?.let {
                Box(modifier = Modifier.fillMaxWidth()
                    .height(dragCardHeightDp)) {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize()
                    )
                    Image(painter = painterResource(R.drawable.baseline_cancel_24), contentDescription = null,
                        modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)
                            .clickable{
                                onRemoveImage()
                            }
                    )
                }
            }
            Row {
                AnimatedVisibility(bitmap == null) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(10.dp)
                            .onSizeChanged{
                                dragCardHeight = it.height
                            }
                    ) {
                        Image(
                            painter = painterResource(R.drawable.famicons_clouduploadoutline),
                            contentDescription = null,
                            modifier = Modifier.padding(top = 20.dp)
                        )
                        Text(
                            "Drag & Drop your vehicle certificate here",
                            modifier = Modifier.padding(horizontal = 40.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = onBrowse,
                            shape = Shape_10,
                            colors = ButtonDefaults.buttonColors(containerColor = theme_color)
                        ) {
                            Text("Browse Files", color = Color.White)
                        }

                        Text("JPG, PNG, PDF. Max.size:5MB", color = Color.Gray)
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .padding(top = 5.dp)
                .clip(Shape_10)
                .background(color = theme_color_2, Shape_10)
                .padding(horizontal = 15.dp, vertical = 10.dp)
        ) {
            Text("\uD83D\uDC4B Tip: ", fontWeight = FontWeight.Bold, color = Color.Black)
            Text(
                "Upload a clear copy of your vehicle grant/registration for best OCR accuracy.",
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
fun OwnerAndVehicleDetailsCard(
    name: String?,
    iCnumber: String?,
    vehicleNumber: String?,
    modifier: Modifier = Modifier
) {

    Log.d("fnbknbf", (!name.isNullOrEmpty() || !iCnumber.isNullOrEmpty() || !vehicleNumber.isNullOrEmpty()).toString())
    val isAutoDetected by remember(name, iCnumber, vehicleNumber) {
        derivedStateOf {
            !name.isNullOrEmpty() || !iCnumber.isNullOrEmpty() || !vehicleNumber.isNullOrEmpty()
        }
    }
    Log.d("fknbkfnbkf", name.toString())
    Log.d("fknbkfnbkf", isAutoDetected.toString())

    var name by rememberSaveable(name) { mutableStateOf<String?>(name) }
    var iCnumber by rememberSaveable(iCnumber) { mutableStateOf<String?>(iCnumber) }
    var vehicleNumber by rememberSaveable(vehicleNumber) { mutableStateOf<String?>(vehicleNumber) }


    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(
                shape = Shape_10,
                shadow = Shadow(color = Color.Black.copy(alpha = 0.1f), radius = 20.dp)
            )
            .clip(Shape_20)
            .background(color = Color.White, Shape_20)
            .padding(15.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(25.dp)
                    .background(color = theme_color, shape = CircleShape)
                    .padding(2.dp)
            ) {
                Text("2", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Text(
                "Owner & Vehicle Details ",
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f, fill = false)
            )
            AnimatedVisibility(isAutoDetected) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .dropShadow(
                            shape = CircleShape,
                            shadow = Shadow(radius = 10.dp, color = theme_color.copy(alpha = 0.1f))
                        )
                        .clip(CircleShape)
                        .background(color = green_1, shape = CircleShape)
                        .border(
                            width = 1.dp,
                            color = grren_2.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                        .padding(vertical = 2.dp, horizontal = 8.dp)
                ) {
                    Text(
                        "Auto Detected",
                        color = grren_2,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(color = grren_3, Shape_20)
                .drawBehind {
                    drawRoundRect(
                        color = Color.Gray,
                        cornerRadius = CornerRadius(25f, 25f),
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(10f, 10f), // dash, gap 0f)
                            )
                        )
                    )
                }
                .padding(horizontal = 15.dp, 10.dp))
        {
            Text("Name", color = grren_2, fontWeight = FontWeight.SemiBold)
            Log.d("kfnbknbf", name.toString())
            BasicTextField(
                value = name ?: "",
                onValueChange = {name = it},
                textStyle = TextStyle(
                    textMotion = TextMotion.Animated,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                decorationBox = {innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (name.isNullOrEmpty()) {
                            Text(
                                text = "Enter name",
                                color = Color.Gray
                            )
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .defaultMinSize(minHeight = 30.dp)
            )
            checkBoxCircular(
                modifier = Modifier.align(Alignment.CenterVertically),
                color = grren_2
            )

        }


        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(color = grren_3, Shape_20)
                .drawBehind {
                    drawRoundRect(
                        color = Color.Gray,
                        cornerRadius = CornerRadius(25f, 25f),
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(10f, 10f), // dash, gap 0f)
                            )
                        )
                    )
                }
                .padding(horizontal = 15.dp, 10.dp))
        {
            Text("IC Number", color = grren_2, fontWeight = FontWeight.SemiBold)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Image(
                    painter = painterResource(R.drawable.materialsymbols_idcardrounded),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                BasicTextField(
                    value = iCnumber ?: "",
                    onValueChange = { iCnumber = it },
                    textStyle = TextStyle(
                        textMotion = TextMotion.Animated,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    decorationBox = {innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (iCnumber.isNullOrEmpty()) {
                                Text(
                                    text = "Enter IC number",
                                    color = Color.Gray
                                )
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                )
                checkBoxCircular(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    color = grren_2
                )
            }


        }
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(color = grren_3, Shape_20)
                .drawBehind {
                    drawRoundRect(
                        color = Color.Gray,
                        cornerRadius = CornerRadius(25f, 25f),
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(10f, 10f), // dash, gap 0f)
                            )
                        )
                    )
                }
                .padding(horizontal = 15.dp, 10.dp))
        {
            Text("Vehicle Number", color = grren_2, fontWeight = FontWeight.SemiBold)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Image(
                    painter = painterResource(R.drawable.materialsymbols_idcardrounded),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                BasicTextField(
                    value = vehicleNumber ?: "",
                    onValueChange = {vehicleNumber = it},
                    textStyle = TextStyle(
                        textMotion = TextMotion.Animated,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    decorationBox = {innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (vehicleNumber.isNullOrEmpty()) {
                                Text(
                                    text = "Enter Vehicle number",
                                    color = Color.Gray
                                )
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                )
                checkBoxCircular(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    color = grren_2
                )
            }


        }


        Text(
            "These details are captured from your vehicle grant/registration checking.",
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 5.dp)
        )
    }
}

@Preview
@Composable
fun PersonalDetailsCard(modifier: Modifier = Modifier) {

    var gender by remember { mutableIntStateOf(1) }
    var maritalStatus by remember { mutableStateOf("") }


    var showMaritalStatus by remember { mutableStateOf(false)}
    var showDatePickerDialog by remember { mutableStateOf(false)}

    var insuranceDate by remember { mutableStateOf("") }


    DatePickerCustomDialog(
        shouldShow = showDatePickerDialog,
        title = "Preferred Insurance Expiry Date",
        onDismiss = {
            showDatePickerDialog = false
        },
        datePickerState = rememberDatePickerState(),
        onCancel = {
            showDatePickerDialog = false
        },
        onDone = {
            val dob = it ?: Date().time
            insuranceDate = dob.convertToDateFormat("dd/MM/yyyy") ?: ""

            showDatePickerDialog = false
        },
    )


    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(
                shape = Shape_10,
                shadow = Shadow(color = Color.Black.copy(alpha = 0.1f), radius = 20.dp)
            )
            .clip(Shape_20)
            .background(color = Color.White, Shape_20)
            .padding(15.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(25.dp)
                    .background(color = theme_color, shape = CircleShape)
                    .padding(2.dp)
            ) {
                Text("3", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Text(
                "Personal Details",
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f, fill = false)
            )
        }

        Text("Gender", fontWeight = FontWeight.Bold, color = Color.Black)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, shape = Shape_10, color = Color.Gray.copy(alpha = 0.3f))
                .background(color = theme_color_3, shape = Shape_10)
        )
        {

            Row(
                modifier = Modifier.weight(1f)
                    .clip(Shape_10)
                    .background(color = if(gender == 1) theme_color else Color.Transparent,Shape_10)
                    .clickable{
                        gender = 1
                    }
                    .padding(horizontal = 15.dp, vertical = 7.dp), horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.ion_maleoutline),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = if(gender == 1) Color.White else Color.Gray)
                )
                Text("Male", fontWeight = FontWeight.Bold, color = if(gender == 1) Color.White else Color.Black)
            }
            Row(
                modifier = Modifier.weight(1f)
                    .clip(Shape_10)
                    .background(color = if(gender == 2) theme_color else Color.Transparent,Shape_10)
                    .clickable{
                        gender = 2
                    }
                    .padding(horizontal = 15.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ion_femaleoutline),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = if(gender == 2) Color.White else Color.Gray)
                )
                Text("Female", fontWeight = FontWeight.Bold,color = if(gender == 2) Color.White else Color.Black)
            }
        }

        Text("Marital Status", fontWeight = FontWeight.Bold, color = Color.Black)

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(Shape_10)
                .border(width = 1.dp, shape = Shape_10, color = Color.Gray.copy(alpha = 0.3f))
                .background(color = theme_color_3, shape = Shape_10)
                .clickable{
                    showMaritalStatus = true
                }
                .padding(horizontal = 15.dp, vertical = 7.dp),

        )
        {
            Image(
                painter = painterResource(R.drawable.materialsymbols_idcardrounded),
                contentDescription = null
            )
            Text(
                maritalStatus.ifEmpty { "Select a status..." },
                color = if(maritalStatus.isNotEmpty()) Color.Black else Color.Gray.copy(0.8f),
                fontWeight = TextFieldFontWeight,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(R.drawable.baseline_keyboard_arrow_down_24),
                contentDescription = null
            )
        }

        Text("Preferred Insurance Expiry Date", fontWeight = FontWeight.Bold, color = Color.Black)
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(Shape_10)
                .border(width = 1.dp, shape = Shape_10, color = Color.Gray.copy(alpha = 0.3f))
                .background(color = theme_color_3, shape = Shape_10)
                .clickable{
                    showDatePickerDialog = true
                }
                .padding(horizontal = 15.dp, vertical = 8.dp)
        )
        {
            Image(
                painter = painterResource(R.drawable.letsicons_datetodayduotoneline),
                contentDescription = null
            )
            Text(
                insuranceDate.ifEmpty {  "Select date"},
                color = if(insuranceDate.isNotEmpty()) Color.Black else Color.Gray.copy(0.8f),
                fontWeight = TextFieldFontWeight,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            "You can change the expiry date as per your preference.",
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontSize = 11.sp
        )
    }

    MaritalsStatusBottomSheet(show = showMaritalStatus,
        onDismiss = {
            showMaritalStatus = false
        }, onSelect = {
            maritalStatus = it
            showMaritalStatus = false
        })
}

@Preview
@Composable
fun AddOnsCard(modifier: Modifier = Modifier) {

    var compensationCheck by remember { mutableStateOf(false) }
    var currentYearCheck by remember { mutableStateOf(false) }
    var legalLiabilityCheck by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(
                shape = Shape_10,
                shadow = Shadow(color = Color.Black.copy(alpha = 0.1f), radius = 20.dp)
            )
            .clip(Shape_20)
            .background(color = Color.White, Shape_20)
            .padding(15.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(25.dp)
                    .background(color = theme_color, shape = CircleShape)
                    .padding(2.dp)
            ) {
                Text("4", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 17.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Add-Ons ")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("(Choose as Needed)")
                    }
                }, modifier = Modifier.weight(1f)
            )
            Text(
                "Select All", fontSize = 14.sp,
                style = TextStyle(
                    color = theme_color,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline,
                ),
                modifier = Modifier.clickable{
                    compensationCheck = true
                    currentYearCheck = true
                    legalLiabilityCheck = true
                }
            )
        }

        Column(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = Color.Gray.copy(0.4f),
                    shape = Shape_15
                )
                .padding(15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().clip(Shape_10).clickable{
                    compensationCheck = !compensationCheck
                }.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Checkbox(
                    checked = compensationCheck,
                    onCheckedChange = {compensationCheck = it},
                    modifier = Modifier
                        .size(20.dp)
                        .clip(Shape_10),
                    colors = CheckboxDefaults.colors().copy(
                        checkedBoxColor = theme_color,
                        uncheckedBoxColor = Color.White,
                        checkedBorderColor = theme_color,
                        uncheckedBorderColor = theme_color,
                        checkedCheckmarkColor = Color.White,
                        uncheckedCheckmarkColor = Color.Transparent
                    ),
                )
                Text("Compensation for Assessed Repair Time (CART)", modifier = Modifier.weight(1f))

            }
            HorizontalDivider(
                color = Color.Gray.copy(0.1f),
            )

            Row(
                modifier = Modifier.fillMaxWidth().clip(Shape_10)
                    .clickable{
                        currentYearCheck = !currentYearCheck
                    }.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Checkbox(
                    checked = currentYearCheck,
                    onCheckedChange = {currentYearCheck = it},
                    modifier = Modifier
                        .size(20.dp)
                        .clip(Shape_10),
                    colors = CheckboxDefaults.colors().copy(
                        checkedBoxColor = theme_color,
                        uncheckedBoxColor = Color.White,
                        checkedBorderColor = theme_color,
                        uncheckedBorderColor = theme_color,
                        checkedCheckmarkColor = Color.White,
                        uncheckedCheckmarkColor = Color.Transparent
                    ),
                )
                Text(
                    "Current Year NCD Relief", modifier = Modifier.weight(1f),
                    color = Color.Black
                )
                Text(
                    "Click to Understand", fontSize = 12.sp, modifier = Modifier,
                    color = theme_color,
                    textDecoration = TextDecoration.Underline
                )

            }
            HorizontalDivider(
                color = Color.Gray.copy(0.1f),
            )

            Row(
                modifier = Modifier.fillMaxWidth().clip(Shape_10).clickable{
                    legalLiabilityCheck = !legalLiabilityCheck
                }.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Checkbox(
                    checked = legalLiabilityCheck,
                    onCheckedChange = {legalLiabilityCheck = it},
                    modifier = Modifier
                        .size(20.dp)
                        .clip(Shape_10),
                    colors = CheckboxDefaults.colors().copy(
                        checkedBoxColor = theme_color,
                        uncheckedBoxColor = Color.White,
                        checkedBorderColor = theme_color,
                        uncheckedBorderColor = theme_color,
                        checkedCheckmarkColor = Color.White,
                        uncheckedCheckmarkColor = Color.Transparent
                    ),
                )
                Text(
                    "Legal Liability of Passengers", modifier = Modifier.weight(1f),
                    color = Color.Black
                )
            }
        }


    }
}

val TextFieldFontWeight = FontWeight.SemiBold

@Composable
fun checkBoxCircular(modifier: Modifier, color: Color = theme_color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(20.dp)
            .background(color = color, shape = CircleShape)
            .padding(2.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.baseline_check_24),
            colorFilter = ColorFilter.tint(color = Color.White),
            contentDescription = null
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaritalsStatusBottomSheet(show : Boolean = false,
                              onDismiss : () -> Unit,
                              onSelect : (String) -> Unit

){

    val maritalStatusList = remember {
        listOf(
            "Single",
            "Married",
            "Divorced",
            "Widowed",
            "Separated"
        )
    }
    if(show) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(onDismissRequest = onDismiss,
            dragHandle = null,
            sheetState = sheetState) {
            Column(modifier = Modifier.padding(20.dp)) {
                maritalStatusList.forEach {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .clip(Shape_10)
                            .clickable {
                                onSelect(it)
                            }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(it, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }

}


@SuppressLint("SimpleDateFormat")
fun Long.convertToDateFormat(toFormat: String? = "dd/MM/yyyy", isUtc : Boolean = false): String? {
    return try {
        val outputDateFormat = SimpleDateFormat(toFormat)
        if(isUtc) {
            outputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        }
        outputDateFormat.format(this)

    } catch (e: Exception) {
        Log.d("dlvmkmv",e.message.toString())
        null // Handle the error appropriately
    }

}
