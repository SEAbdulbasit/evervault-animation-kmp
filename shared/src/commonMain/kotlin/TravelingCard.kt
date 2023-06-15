/**
 * Created by abdulbasit on 15/06/2023.
 */
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

const val VALID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ=1234567890"
const val CHAR_COUNT = 600
const val DEFAULT_HIGHLIGHTED_CHARS = 30

private val encryptedString = buildString {
    repeat(CHAR_COUNT) {
        append(VALID_CHARS.random())
    }
}

private val defaultStyle = SpanStyle(
    fontWeight = FontWeight.Bold,
    fontFamily = FontFamily.Monospace,
    color = Color.White.copy(alpha = 0.36f),
    fontSize = 11.sp,
    letterSpacing = 4.sp,
)

private val defaultStyleHighlighted = defaultStyle.copy(
    color = Color.White.copy(alpha = 0.9f)
)

private fun shuffledEncryptedString(
    style: SpanStyle = defaultStyle,
    styleHighlighted: SpanStyle = defaultStyleHighlighted,
    highlightedCount: Int = DEFAULT_HIGHLIGHTED_CHARS
): AnnotatedString {
    return buildAnnotatedString {
        val shuffled = encryptedString.toList().shuffled().joinToString("")

        withStyle(style) {
            append(shuffled)
        }

        repeat(highlightedCount) {
            Random.nextInt(shuffled.length).also { index ->
                addStyle(styleHighlighted, index, index + 1)
            }
        }
    }
}

@Composable
fun TravellingCard(
    onIsEncryptingChanged: (Boolean) -> Unit
) {
    var parentWidth by remember { mutableStateOf(0) }
    val cardTravelProgress = remember { Animatable(0f) }
    var clipAmount by remember { mutableStateOf(-1f) }
    val isEncrypting by remember {
        derivedStateOf {
            clipAmount in 0.0f..1.0f
        }
    }

    LaunchedEffect(isEncrypting) {
        onIsEncryptingChanged(isEncrypting)
    }

    LaunchedEffect(Unit) {
        cardTravelProgress.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(7_000, easing = LinearEasing))
        )
    }

    ClippedForeground(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1.5f, true)
            .graphicsLayer {
                val xMin = -size.width
                val xMax = -xMin + parentWidth

                translationX = xMin + xMax * cardTravelProgress.value
            }
            .onGloballyPositioned {
                parentWidth = it.parentLayoutCoordinates!!.size.width
                val parentHalfWidth = it.parentLayoutCoordinates!!.size.width / 2f
                clipAmount = (it.boundsInParent().right - parentHalfWidth) / it.size.width
            },
        { clipAmount.coerceIn(0f..1f) },
        backgroundContent = {
            Encrypted(isEncrypting)
        },
        foregroundContent = {
            Card {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "🤫", fontSize = 32.sp)
                }
            }
        }
    )
}

@Composable
fun Encrypted(
    isEncrypting: Boolean
) {
    var text by remember { mutableStateOf(buildAnnotatedString {}) }

    LaunchedEffect(isEncrypting) {
        while (isEncrypting) {
            text = shuffledEncryptedString()
            delay(250)
        }
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            lineHeight = 21.sp,
            textAlign = TextAlign.Justify,
            maxLines = 13
        )
    }
}