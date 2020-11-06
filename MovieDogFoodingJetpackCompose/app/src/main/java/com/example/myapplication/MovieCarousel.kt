package com.example.myapplication

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.asDisposableClock
import androidx.compose.animation.core.TargetAnimation
import androidx.compose.foundation.Text
import androidx.compose.foundation.animation.FlingConfig
import androidx.compose.foundation.animation.defaultFlingConfig
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.ScrollableController
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
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
import kotlin.math.roundToInt

val posterAspectRatio = 0.647f

@Composable
fun Screen() {
    val configuration = ConfigurationAmbient.current
    val screenWidth = configuration.screenWidthDp.dp
    val posterWidthDp = screenWidth * 0.6f
    val posterSpacingDp = posterWidthDp + 20.dp
    var selectedIndex = remember { mutableStateOf(0) }

    Box {
        Carousel(
            items = movies,
            selectedIndex = selectedIndex.value,
            onSelectedIndexChange = { selectedIndex.value = it },
            backgroundContent = { index, movie ->
                CoilImage(
                    data = movie.bgUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(posterAspectRatio)
                )
            },
            foregroundContent = { index, movie ->
                MoviePoster(
                    movie = movie,
                    modifier = Modifier.width(posterWidthDp)
                )
            },
            spacing = posterSpacingDp
        )
        BuyTicketButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(posterWidthDp)
                .padding(20.dp),
            onClick = {
                // Navigate to the page for that movie
            }
        )
    }
}

@Composable
fun <T> Carousel(
    items: List<T>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    spacing: Dp,
    backgroundContent: @Composable (Int, T) -> Unit,
    foregroundContent: @Composable (Int, T) -> Unit
) {
    var offset = remember { mutableStateOf(0f) }
    val animatedOffset = animatedFloat(initVal = 0f)


    val spacingPx = with(DensityAmbient.current) { spacing.toPx() }

    val flingConfig = defaultFlingConfig {
        TargetAnimation((it / spacingPx).roundToInt() * spacingPx)
    }
    val upperBound = 0f
    val lowerBound = -1 * (items.size - 1) * spacingPx
    val ctrlr = rememberScrollableController(flingConfig = flingConfig) {
        // Stop consuming the fling when pass the bounds
        val target = animatedOffset.value + it
        when {
            target > upperBound -> {
                val consumed = upperBound - animatedOffset.value
                animatedOffset.snapTo(upperBound)
//                offset.value = upperBound
                consumed
            }
            target < lowerBound -> {
                val consumed = lowerBound - animatedOffset.value
                animatedOffset.snapTo(lowerBound)
//                offset.value = lowerBound
                consumed
            }
            else -> {
//                offset.value = target
                animatedOffset.snapTo(target)
                it
            }//return pixels consumes
        }
    }

    Stack(
        Modifier
            .background(Color.Black)
            .fillMaxSize()
            .scrollable(
                orientation = Orientation.Horizontal,
                controller = ctrlr
            )
    ) {
        items.forEachIndexed { index, item ->
            Column(
                Modifier
                    .carouselBackground(
                        index = index,
                        getIndexFraction = { -1 * animatedOffset.value / spacingPx },
                    )
                    .fillMaxSize()
            ) {
                backgroundContent(index, item)
            }
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
                foregroundContent(index, item)
//                onSelectedIndexChange(index) // me added
            }
        }
    }
}

fun Modifier.carouselBackground(
    index: Int,
    getIndexFraction: () -> Float
) = this then object : DrawLayerModifier {
    override val alpha: Float
        get() {
            val indexFraction = getIndexFraction()
            val leftIndex = kotlin.math.floor(indexFraction).toInt()
            val rightIndex = ceil(indexFraction).toInt()
            return if (index == leftIndex || index == rightIndex) 1f else 0f
        }
    override val shape: Shape
        get() {
//            val indexFraction = -1 * offset.value / spacingPx
            val indexFraction = getIndexFraction()
            val leftIndex = kotlin.math.floor(indexFraction).toInt()
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

@Composable
fun rememberScrollableController(
    flingConfig: FlingConfig = defaultFlingConfig(),
    consumeScrollDelta: (Float) -> Float
): ScrollableController {
    val clocks = AnimationClockAmbient.current.asDisposableClock()
    return remember(clocks, flingConfig) {
        ScrollableController(consumeScrollDelta, flingConfig, clocks)
    }
}

fun FractionalRectangleShape(startFraction: Float, endFraction: Float) = object : Shape {
    override fun createOutline(size: Size, density: Density) =
        Outline.Rectangle(
            Rect(
                top = 0f,
                left = startFraction * size.width,
                bottom = size.height,
                right = endFraction * size.width
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