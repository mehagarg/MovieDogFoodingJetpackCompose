package com.example.myapplication

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.asDisposableClock
import androidx.compose.animation.core.AnimatedFloat
import androidx.compose.animation.core.AnimationClockObservable
import androidx.compose.animation.core.TargetAnimation
import androidx.compose.foundation.Text
import androidx.compose.foundation.animation.AndroidFlingDecaySpec
import androidx.compose.foundation.animation.FlingConfig
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.ScrollableController
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import com.example.myapplication.model.Movie
import com.example.myapplication.model.movies
import dev.chrisbanes.accompanist.coil.CoilImage
import java.lang.Math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.reflect.KProperty

val posterAspectRatio = 0.647f

@Composable
fun Screen() {
    val configuration = ConfigurationAmbient.current
    val screenWidth = configuration.screenWidthDp.dp
    val posterWidthDp = screenWidth * 0.6f
    val posterSpacingDp = posterWidthDp + 20.dp
    val carouselState = rememberCarouselState()

    Box {
        Carousel(
            items = movies,
            carouselState = carouselState,
            spacing = posterSpacingDp,
            getBackgroundImage = { it.bgUrl },
            getForegroundImage = { it.posterUrl })
        { movie, expanded ->
            MoviePoster(
                movie = movie,
                modifier = Modifier.width(posterWidthDp)
            )
        }
        BuyTicketButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(posterWidthDp)
                .padding(20.dp),
            onClick = {
                // Navigate to the page for that movie
                carouselState.expandSelectedItem()
            }
        )
    }
}

class ValueHolder<T>(var value: T)

operator fun <T> ValueHolder<T>.getValue(thisRef: Any?, property: KProperty<*>) = value
operator fun <T> ValueHolder<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    this.value = value
}

private fun indexToOffset(index: Int, spacingPx: Float): Float = -1 * index * spacingPx
private fun offsetToIndex(offset: Float, spacingPx: Float): Int =
    floor(-1 * offset / spacingPx).toInt()

@Composable
fun rememberCarouselState(): CarouselState {
    val density = DensityAmbient.current
    val clock = AnimationClockAmbient.current.asDisposableClock()
    val animatedOffset = animatedFloat(0f)
    val expanded = animatedFloat(initVal = 0f) // 0 is unexpanded :: 1 is expanded.
    return remember(clock, density) {
        CarouselState(
            density = density,
            animatedOffset = animatedOffset,
            expanded = expanded,
            clock = clock
        )
    }
}

class CarouselState(
    private val density: Density,
    internal val animatedOffset: AnimatedFloat,
    internal val expanded: AnimatedFloat,
    clock: AnimationClockObservable
) {

    val flingConfig = FlingConfig(AndroidFlingDecaySpec(density)) { adjustTarget(it) }
    val scrollableController =
        ScrollableController({ this.consumeScrollDelta(it) }, flingConfig, clock)
    private var itemCount: Int = 0
    var spacingPx: Float = 0f
    fun update(count: Int, spacing: Dp) {
        itemCount = count
        spacingPx = with(density) { spacing.toPx() }
    }

    val upperBound: Float get() = 0f
    val lowerBound: Float get() = -1 * (itemCount - 1) * spacingPx
    fun adjustTarget(target: Float): TargetAnimation? {
        return TargetAnimation((target / spacingPx).roundToInt() * spacingPx)
    }

    fun consumeScrollDelta(delta: Float): Float {
        var target = animatedOffset.value + delta
        var consumed = delta
        when {
            target > upperBound -> {
                consumed = upperBound - animatedOffset.value
                target = upperBound
            }
            target < lowerBound -> {
                consumed = lowerBound - animatedOffset.value
                target = lowerBound
            }
        }
        val targetIndex = offsetToIndex(target, spacingPx)
        animatedOffset.snapTo(target)
        return consumed
    }

    fun expandSelectedItem() {
        if (expanded.value == 1f) {
            expanded.animateTo(0f)
        } else {
            expanded.animateTo(1f)
        }
    }

    val selectedIndex: Int get() = offsetToIndex(animatedOffset.value, spacingPx)
}

@Composable
fun <T> Carousel(
    items: List<T>,
    spacing: Dp,
    carouselState: CarouselState,
    getBackgroundImage: (T) -> Any,
    getForegroundImage: (T) -> Any,
    foregroundContent: @Composable (item: T, expanded: Float) -> Unit
) {
    // TODO: think more about this
    carouselState.update(items.size, spacing)

    val spacingPx = carouselState.spacingPx
    val animatedOffset = carouselState.animatedOffset
    val expanded = carouselState.expanded
    Stack(
        Modifier
            .background(Color.Black)
            .fillMaxSize()
            .scrollable(
                orientation = Orientation.Horizontal,
                controller = carouselState.scrollableController
            )
    ) {
        items.forEachIndexed { index, item ->
            CoilImage(
                data = getBackgroundImage(item),
                modifier = Modifier
                    .carouselBackground(
                        index = index,
                        getIndexFraction = { -1 * animatedOffset.value / spacingPx },
                        getExpandedFraction = { expanded.value }
                    )
                    .fillMaxWidth()
                    .aspectRatio(posterAspectRatio)
            )
            CoilImage(
                data = getForegroundImage(item),
                modifier = Modifier
                    .carouselExpandedBackground(
                        index = index,
                        getIndexFraction = { -1 * animatedOffset.value / spacingPx },
                        getExpandedFraction = { expanded.value }
                    )
                    .offset(getX = {
                        (index - carouselState.selectedIndex) * 0.75f * spacingPx
                    }, getY = { 0f })
                    .align(Alignment.BottomCenter)
                    .width(spacing)
                    .aspectRatio(posterAspectRatio)
            )
        }
        Spacer(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .verticalGradient(
                    0f to Color.Transparent,
                    0.3f to Color.White,
                    1f to Color.White
                )
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
        )
        items.forEachIndexed { index, item ->
            val center = spacingPx * index
            Column(
                Modifier
                    .offset(
                        getX = {
                            center + animatedOffset.value
                        },
                        getY = {
                            val distanceFromCenter = abs(animatedOffset.value + center) / spacingPx
                            lerp(0f, 50f, distanceFromCenter)
                        }
                    )
                    .align(Alignment.BottomCenter)
            ) {
                foregroundContent(item, 0f)
//                onSelectedIndexChange(index) // me added
            }
        }
    }
}

fun Modifier.carouselExpandedBackground(
    index: Int,
    getIndexFraction: () -> Float,
    getExpandedFraction: () -> Float,
) = this then object : DrawLayerModifier {
    override val alpha: Float
        get() {
            return getExpandedFraction()
        }
    override val translationY: Float
        get() {
            return lerp(0f, -400f, getExpandedFraction())
        }
}

fun Modifier.carouselBackground(
    index: Int,
    getIndexFraction: () -> Float,
    getExpandedFraction: () -> Float,
) = this then object : DrawLayerModifier {
    override val alpha: Float
        get() {
            val indexFraction = getIndexFraction()
            val leftIndex = floor(indexFraction).toInt()
            val rightIndex = ceil(indexFraction).toInt()
            return if (index == leftIndex || index == rightIndex)
                1f - getExpandedFraction()
            else
                0f
        }
    override val shape: Shape
        get() {
            val indexFraction = getIndexFraction()
            val leftIndex = floor(indexFraction).toInt()
            val rightIndex = ceil(indexFraction).toInt()
            return when (index) {
                rightIndex -> {
                    // index - 1, index-fraction = 0.25 -> 0f, 0.25
                    val fraction = indexFraction - index + 1
                    FractionalRectangleShape(0f, fraction.coerceIn(Float.MIN_VALUE, 1f))
                }
                leftIndex -> {
                    // index - 0, index-fraction = 0.25 ->0.25, 1f
                    val fraction = indexFraction - index
                    FractionalRectangleShape(fraction.coerceIn(0f, 1f - Float.MIN_VALUE), 1f)
                }
                else -> RectangleShape
            }
        }
    override val clip: Boolean
        get() = true
}

// Actual issue was FRm small fractions were passed. startFr close to 1, endFra close to 0
//actual size of rect fall below 1px. Drawing sys would ignore the shape
//
fun FractionalRectangleShape(startFraction: Float, endFraction: Float) = object : Shape {
    override fun createOutline(size: Size, density: Density) =
        Outline.Rectangle(
            Rect(
                top = 0f,
                left = (startFraction * size.width).coerceAtMost(size.width - 1f),
                bottom = size.height,
                right = (endFraction * size.width).coerceAtLeast(1f)
            )
        )
}

fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return (1 - fraction) * start + fraction * stop
}

fun Modifier.verticalGradient(vararg colors: ColorStop) =
    this then object : DrawModifier, InspectableParameter {

        // naive cache outline calculation if size is the same
        private var lastSize: Size? = null
        private var lastBrush: Brush? = null

        override fun ContentDrawScope.draw() {
            drawRect()
            drawContent()
        }

        private fun ContentDrawScope.drawRect() {
            var brush = lastBrush
            if (size != lastSize || brush == null) {
                brush = VerticalGradient(
                    *colors/*.map{it.first * size.height to it.second}.toTypedArray()*/,
                    startY = 0f,
                    endY = size.height
                )
                lastSize = size
                lastBrush = brush
            }
            drawRect(brush = brush, alpha = 1f)
        }

        override val nameFallback = "verticalGradient"

        override val valueOverride: Any?
            get() = colors

        override val inspectableElements: Sequence<ParameterElement>
            get() = sequenceOf(
                ParameterElement("color", colors)
            )
    }

fun Modifier.offset(
    getX: () -> Float,
    getY: () -> Float,
    rtlAware: Boolean = true
) = this then object : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureScope.MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            if (rtlAware) {
                placeable.placeRelative(getX().roundToInt(), getY().roundToInt())
            } else {
                placeable.place(getX().roundToInt(), getY().roundToInt())
            }
        }
    }
}

@Composable
private fun MoviePoster(movie: Movie, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(20.dp)
            .padding(bottom = 60.dp)
    ) {
        CoilImage(
            data = movie.posterUrl,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(posterAspectRatio)
                .clip(RoundedCornerShape(10.dp))
        )

        Text(
            movie.title,
            fontSize = 24.sp,
            color = Color.Black
        )

        Row {
            for (chip in movie.chips) {
                Chip(chip)
            }
        }
        StarRating(9.0f)
    }
}

@Composable
fun BuyTicketButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick,
        backgroundColor = Color.DarkGray,
        elevation = 0.dp,
        modifier = modifier
            .padding(vertical = 10.dp)
    ) {
        Text("Buy Ticket", color = Color.White)
    }
}

@Composable
fun StarRating(rating: Float) {

}

@Composable
fun Chip(label: String, modifier: Modifier = Modifier) {
    Text(
        label,
        color = Color.Gray,
        fontSize = 9.sp,
        modifier = modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 2.dp)
    )
}