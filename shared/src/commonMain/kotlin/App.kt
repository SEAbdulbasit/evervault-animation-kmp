import androidx.compose.animation.core.EaseInQuad
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxSize()
                .particles(rememberParticleState(starsParticleConfig)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(185.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                var isEncrypting by remember { mutableStateOf(false) }
                TravellingCard { isEncrypting = it }
                GlowingLine()
                DismantleEffect(isEncrypting)
            }
        }
    }
}

expect fun getPlatformName(): String

/**
 * Used for the star field
 */
private val starsParticleConfig = ParticleConfig(
    count = 250,
    minRadius = 1.dp,
    maxRadius = 3.dp,
    maxParallaxFactor = 3.5f,
    loopDuration = 20_000,
    easing = LinearEasing,
    fadeOut = false,
    color = Color(0xFF81D4FA),
)

/**
 * Used for the dismantle effect of the card
 */
private val dismantleParticleConfig = ParticleConfig(
    count = 250,
    minRadius = 1.dp,
    maxRadius = 5.dp,
    loopDuration = 400,
    maxParallaxFactor = 1f,
    easing = EaseInQuad,
    fadeOut = true,
    color = Color(0xFF2196F3),
)

@Composable
fun BoxScope.GlowingLine() {
    Canvas(
        modifier = Modifier
            .align(Alignment.Center)
            .fillMaxHeight()
            .width(16.dp)
            .blur(16.dp, BlurredEdgeTreatment.Unbounded)
    ) {
        drawOval(Color(0xFF2196F3))
    }
    Canvas(
        modifier = Modifier
            .align(Alignment.Center)
            .fillMaxHeight(0.95f)
            .width(4.dp)
    ) {
        drawOval(Color.White, alpha = 0.8f)
    }
}

@Composable
fun DismantleEffect(
    isEncrypting: Boolean
) {
    val alpha by animateFloatAsState(if (isEncrypting) 1f else 0f, spring(stiffness = 100f))
    val brush = Brush.horizontalGradient(listOf(Color(0x882196F3), Color.Transparent))

    Row(
        Modifier.graphicsLayer {
            this.alpha = alpha
        }
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Box(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .background(brush)
                    .clipToBounds()
                    .particles(rememberParticleState(dismantleParticleConfig))
                    .fillMaxHeight()
                    .width(48.dp)
            )
        }
    }
}