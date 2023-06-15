/**
 * Created by abdulbasit on 15/06/2023.
 */

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas

@Composable
fun ClippedForeground(
    modifier: Modifier,
    provideClipAmount: () -> Float,
    backgroundContent: @Composable () -> Unit,
    foregroundContent: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        backgroundContent()
        Box(modifier = Modifier.clip(provideClipAmount)) {
            foregroundContent()
        }
    }
}

fun Modifier.clip(provideClipAmount: () -> Float) = this.drawWithContent {
    // https://stackoverflow.com/questions/73590695/how-to-clip-or-cut-a-composable
    with(drawContext.canvas.nativeCanvas) {
        saveLayer(null, null)

        val topLeft = Offset((size.width - 1) - size.width * provideClipAmount(), 0f)

        drawContent()

        // Clip content
        // Added a pixel to the left and right, otherwise 1px wide lines could still be seen
        drawRect(
            color = Color.Transparent,
            topLeft = topLeft,
            size = size.copy(width = size.width + 2, height = size.height),
            blendMode = BlendMode.SrcOut
        )

        restore()
    }
}