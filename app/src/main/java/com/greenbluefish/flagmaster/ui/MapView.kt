package com.greenbluefish.flagmaster.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Offline world map with a single pin at [lat]/[lon].
 *
 * The map is `assets/world_map.png`, a verified FULL-EXTENT equirectangular
 * (plate carrée) projection that spans exactly lon -180..180 and lat +90..-90
 * at an exact 2:1 ratio (CC0 Blankmap.svg, rendered 3840x1920). Because the
 * image is true full-extent 2:1, drawing it at a fixed 2:1 aspect inside a
 * [Box] with ContentScale.FillBounds introduces NO distortion and NO letterbox
 * offset, so the plain linear mapping is exact and the pin lands straight onto
 * the drawn rect with:
 *   x = (lon + 180) / 360 * width
 *   y = (90 - lat) / 180 * height
 *
 * (The earlier BlankMap-World.svg was NOT full-extent — it cropped Antarctica
 * and was non-2:1 — so this same formula pushed high-latitude pins like Alaska
 * too far south. Swapping to the full-extent image fixes that WITHOUT changing
 * any FlagEntry lat/lon data or this formula.)
 */
@Composable
fun MapView(
    lat: Double,
    lon: Double,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val mapBitmap by produceState<ImageBitmap?>(initialValue = null) {
        value = runCatching {
            context.assets.open("world_map.png").use { stream ->
                BitmapFactory.decodeStream(stream).asImageBitmap()
            }
        }.getOrNull()
    }

    val pinColor = MaterialTheme.colorScheme.error
    val pinRing = MaterialTheme.colorScheme.onError

    Box(
        modifier = modifier
            .fillMaxWidth()
            // Force a 2:1 map rect; the image fills it so there is no letterbox.
            .aspectRatio(2f)
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        val bmp = mapBitmap
        if (bmp != null) {
            Image(
                bitmap = bmp,
                contentDescription = "World map",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize(),
            )
        }

        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val x = ((lon + 180.0) / 360.0 * w).toFloat()
            val y = ((90.0 - lat) / 180.0 * h).toFloat()
            val center = Offset(x, y)
            // Outer ring for contrast against varied map colors, then the pin dot.
            drawCircle(color = pinRing, radius = 9f, center = center)
            drawCircle(color = pinColor, radius = 6f, center = center)
        }
    }
}
