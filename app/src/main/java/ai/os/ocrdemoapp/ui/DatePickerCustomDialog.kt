package ai.os.ocrdemoapp.ui

import ai.os.ocrdemoapp.ui.theme.Shape_10
import ai.os.ocrdemoapp.ui.theme.Shape_20
import ai.os.ocrdemoapp.ui.theme.theme_color
import ai.os.ocrdemoapp.ui.theme.theme_color_2
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerCustomDialog(
    shouldShow: Boolean,
    datePickerState: DatePickerState,
    title: String?,
    onDismiss: () -> Unit = {},
    onDone : (Long?) -> Unit = {},
    onCancel : () -> Unit = {},
) {


    Log.d("fkvnkfnvf",datePickerState.displayMode.toString())

    if (shouldShow) {
        Box(
            modifier = Modifier
                .width(200.dp)
                .padding(12.dp)
        ) {
            Dialog(
                properties = DialogProperties(usePlatformDefaultWidth = false),
                onDismissRequest = onDismiss) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .background(
                            shape = Shape_20,
                           color = theme_color_2
                        )
                        .innerShadow(
                            shape = Shape_20,
                            shadow = Shadow(
                                radius = 10.dp,
                                spread = 5.dp,
                                brush = Brush.linearGradient(colors = listOf(theme_color_2,
                                    theme_color
                                ))
                            )
                        )
                        .padding(vertical = 10.dp, horizontal = 2.dp)
                ) {
                    Column {
                        DatePicker(
                            state = datePickerState,
                            showModeToggle = false,
                            colors = DatePickerDefaults.colors().copy(
                                containerColor = theme_color_2,

                                yearContentColor = Color.Black,  // year text
                                selectedYearContainerColor = theme_color, // selected year bg
                                disabledSelectedYearContainerColor = theme_color_2,
                                // also set the normal year container background by using the "selected" colors when appropriate,
                                // and tweak dividerColor if needed:
                                dividerColor = theme_color_2
                            ),
                            title = {
                                Text(
                                    title ?: "Select Date",
                                    fontSize = 14.sp,
                                    style = TextStyle(
                                        color = theme_color
                                    ),
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)

                                )
                            }
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier
                                .align(Alignment.End)
                                .fillMaxWidth().padding(vertical = 6.dp, horizontal = 10.dp)) {
                            Button(
                                onClick = onCancel,
                                shape = Shape_10,
                                colors = ButtonDefaults.buttonColors(containerColor = theme_color_2)
                            ) {
                                Text("Cancel", color = theme_color)
                            }
                            Button(
                                onClick = {
                                    onDone(datePickerState.selectedDateMillis)
                                },
                                shape = Shape_10,
                                colors = ButtonDefaults.buttonColors(containerColor = theme_color_2)
                            ) {
                                Text("Done", color = theme_color)
                            }
                        }
                    }
                }
            }
        }
    }
}