package com.example.myapplication

import androidx.annotation.FloatRange
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberScrollableController
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ConfigurationAmbient
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.platform.InspectableParameter
import androidx.compose.ui.platform.ParameterElement
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.model.Movie
import com.example.myapplication.model.movies
import dev.chrisbanes.accompanist.coil.CoilImage

val posterAspectRatio = 0.647f

@Composable
fun Screen() {
    val configuration = ConfigurationAmbient.current
    val density = DensityAmbient.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val offset = remember { mutableStateOf(0f) }
    val ctrlr = rememberScrollableController {
        offset.value += it
        it
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
        movies.forEachIndexed { index, movie ->
            val indexFraction =
            CoilImage(
                data = movie.bgUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(posterAspectRatio)
            )
        }
        Spacer(
            modifier = Modifier
                .gravity(Alignment.BottomEnd)
                .verticalGradient(
                    0f to Color.Transparent,
                    0.3f to Color.White,
                    0f to Color.White
                )
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
        )
        movies.forEachIndexed { index, movie ->
            MoviePoster(movie = movie,
                modifier = Modifier.offset(
                    getX = { screenWidth * index + offset.value.dp },
                    getY = { 0.dp }
                ).width(screenWidth * .75f)
                    .gravity(Alignment.BottomCenter))
        }
//        Spacer(Modifier.height(30.dp))
//        BuyTicketButton(onClick = {})
    }
}

fun Modifier.verticalGradient(vararg colors: ColorStop) = this then object : DrawModifier, InspectableParameter {

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
                *colors.map{it.first * size.height to it.second}.toTypedArray(),
                startY = 0f,
                endY = size.height)
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
    getX: () -> Dp,
    getY: () -> Dp,
    rtlAware: Boolean = true
) = this then object : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureScope.MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            if (rtlAware) {
                placeable.placeRelative(getX().toIntPx(), getY().toIntPx())
            } else {
                placeable.place(getX().toIntPx(), getY().toIntPx())
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
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
fun BuyTicketButton(onClick: () -> Unit) {
    Button(
        onClick,
        backgroundColor = Color.DarkGray,
        elevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
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