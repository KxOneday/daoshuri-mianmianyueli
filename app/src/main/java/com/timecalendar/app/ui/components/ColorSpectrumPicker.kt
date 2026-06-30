package com.timecalendar.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun ColorSpectrumPicker(
    initialColor: Color = Color(0xFFFF6B9D),
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    var hue by remember { mutableFloatStateOf(0f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    var brightness by remember { mutableFloatStateOf(1f) }
    var spectrumSize by remember { mutableStateOf(IntSize.Zero) }

    // Initialize from initial color
    LaunchedEffect(initialColor) {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(initialColor.toArgb(), hsv)
        hue = hsv[0]
        saturation = hsv[1]
        brightness = hsv[2]
    }

    val selectedColor = Color.hsv(hue, saturation, brightness)

    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Preview
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(selectedColor)
                .border(3.dp, MaterialTheme.colorScheme.outline, CircleShape)
        )

        Text(
            text = "#${Integer.toHexString(selectedColor.toArgb()).substring(2).uppercase()}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hue + Saturation spectrum (2D)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .onSizeChanged { spectrumSize = it }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val x = (offset.x / size.width).coerceIn(0f, 1f)
                        val y = (offset.y / size.height).coerceIn(0f, 1f)
                        hue = x * 360f
                        saturation = 1f - y
                        onColorSelected(Color.hsv(hue, saturation, brightness))
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val x = (change.position.x / size.width).coerceIn(0f, 1f)
                        val y = (change.position.y / size.height).coerceIn(0f, 1f)
                        hue = x * 360f
                        saturation = 1f - y
                        onColorSelected(Color.hsv(hue, saturation, brightness))
                        change.consume()
                    }
                }
        ) {
            // Background: hue gradient (horizontal) + white-to-black gradient (vertical)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = (0..360 step 30).map { Color.hsv(it.toFloat(), 1f, 1f) }
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.White, Color.Transparent, Color.Black)
                        )
                    )
            )

            // Selector dot
            val dotX = (hue / 360f) * spectrumSize.width
            val dotY = (1f - saturation) * spectrumSize.height
            Box(
                modifier = Modifier
                    .offset(
                        x = with(LocalDensity.current) { (dotX - 12).toDp() },
                        y = with(LocalDensity.current) { (dotY - 12).toDp() }
                    )
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(selectedColor)
                    .border(3.dp, Color.White, CircleShape)
                    .border(1.dp, Color.Black, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Brightness slider
        Text(
            "亮度",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.fillMaxWidth().padding(start = 4.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))

        var brightnessBarWidth by remember { mutableIntStateOf(0) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .onSizeChanged { brightnessBarWidth = it.width }
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color.Black, Color.hsv(hue, saturation, 1f))
                    )
                )
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        brightness = (offset.x / size.width).coerceIn(0f, 1f)
                        onColorSelected(Color.hsv(hue, saturation, brightness))
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        brightness = (change.position.x / size.width).coerceIn(0f, 1f)
                        onColorSelected(Color.hsv(hue, saturation, brightness))
                        change.consume()
                    }
                }
        ) {
            // Brightness indicator
            val indicatorX = brightness * brightnessBarWidth
            Box(
                modifier = Modifier
                    .offset(x = with(LocalDensity.current) { (indicatorX - 12).toDp() })
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(selectedColor)
                    .border(3.dp, Color.White, CircleShape)
                    .border(1.dp, Color.Black, CircleShape)
            )
        }
    }
}
