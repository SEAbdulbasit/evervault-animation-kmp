/**
 * Created by abdulbasit on 15/06/2023.
 */
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random

fun Modifier.particles(state: ParticlesState) = this.drawBehind {
    state.particles.forEach { particle ->
        val startX = -particle.radius
        val endX = size.width + particle.radius
        val easedValue =
            state.easing.transform((state.progress.value + particle.initialCenterX).mod(1f))

        drawCircle(
            color = particle.color,
            radius = particle.radius,
            center = Offset(
                x = startX + (-startX + endX) * easedValue * particle.parallaxFactor,
                y = particle.centerY * size.height
            ),
            alpha = if (state.fadeOut) (particle.alpha * (1f - easedValue * 1.5f)).coerceAtLeast(0f) else particle.alpha,
        )
    }
}

@Composable
fun rememberParticleState(particleConfig: ParticleConfig): ParticlesState = with(particleConfig) {
    val scope = rememberCoroutineScope()

    val (minRadiusFloat, maxRadiusFloat) = LocalDensity.current.run { minRadius.toPx() to maxRadius.toPx() }

    remember(this) {
        ParticlesState(
            count = count,
            minRadius = minRadiusFloat,
            maxRadius = maxRadiusFloat,
            maxParallaxFactor = maxParallaxFactor,
            loopDuration = loopDuration,
            scope = scope,
            easing = easing,
            fadeOut = fadeOut,
            color = color
        )
    }
}

class ParticlesState internal constructor(
    count: Int,
    scope: CoroutineScope,
    val easing: Easing,
    val fadeOut: Boolean,
    private val minRadius: Float,
    private val maxRadius: Float,
    private val maxParallaxFactor: Float,
    private val loopDuration: Int,
    private val color: Color
) {

    val particles = mutableListOf<Particle>()
    val progress = Animatable(0f)

    init {
        repeat(count) {
            addParticle()
        }

        scope.launch {
            loop()
        }
    }

    /**
     * Loops between 0 and 1. Position of particles is based on [progress] and the parents width
     */
    private suspend fun loop() {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(loopDuration, easing = LinearEasing))
        )
    }

    private fun addParticle() {
        val initialCenterX = Random.nextFloat()
        val centerY = Random.nextFloat()
        val alpha = Random.nextDouble(0.1, 1.0).toFloat()
        val radius = Random.nextDouble(
            from = minRadius.toDouble(),
            until = maxRadius.toDouble()
        ).toFloat()
        val parallaxFactor = if (maxParallaxFactor == 1.0f) {
            1.0f
        } else {
            Random.nextDouble(1.0, maxParallaxFactor.toDouble()).toFloat()
        }
        val color = lerp(Color.White, color, Random.nextFloat())

        particles += Particle(
            initialCenterX = initialCenterX,
            centerY = centerY,
            radius = radius,
            parallaxFactor = parallaxFactor,
            alpha = alpha,
            color = color
        )
    }
}

@Immutable
data class ParticleConfig(
    /**
     * The number of created particles
     */
    val count: Int,

    /**
     * Min radius of a particle
     */
    val minRadius: Dp,

    /**
     * Max radius of a particle
     */
    val maxRadius: Dp,

    /**
     * Max parallax factor of a particle, see [Particle.parallaxFactor]. [1f..Float.MAX_VALUE]
     */
    val maxParallaxFactor: Float,

    /**
     * Duration of a loop, in ms
     */
    val loopDuration: Int,

    /**
     * Easing of the particles
     */
    val easing: Easing,

    /**
     * Whether or not the particle should fade out when approaching the end of a loop
     */
    val fadeOut: Boolean,

    /**
     * The final color of a particle is a random "lerp" between white and [color]
     */
    val color: Color
)

@Immutable
data class Particle(
    /**
     * Initial relative horizontal position inside its parent. [0f..1f]
     */
    val initialCenterX: Float,

    /**
     * Relative vertical position inside its parent. [0f..1f]
     */
    var centerY: Float,

    /**
     * Radius in pixels
     */
    val radius: Float,

    /**
     * Is multiplied with the horizontal position at a given time. [1f..Float.MAX_VALUE]
     * A particle with a [parallaxFactor] of 2f reaches the end twice as fast
     */
    val parallaxFactor: Float,

    /**
     * Alpha [0f..1f]
     */
    val alpha: Float,

    /**
     * Color
     */
    val color: Color
)