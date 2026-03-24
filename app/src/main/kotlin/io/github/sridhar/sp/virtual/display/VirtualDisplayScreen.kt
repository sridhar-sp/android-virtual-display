package io.github.sridhar.sp.virtual.display

import android.view.SurfaceView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun VirtualDisplayScreen(viewModel: VirtualDisplayViewModel = hiltViewModel()) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Unspecified, MaterialTheme.shapes.large)
    ) {
        val boxSize = with(LocalDensity.current) {
            IntSize(
                width = this@BoxWithConstraints.maxWidth.toPx().toInt(),
                height = this@BoxWithConstraints.maxHeight.toPx().toInt()
            )
        }

        val densityDpi = LocalContext.current.resources.displayMetrics.densityDpi
//        val densityDpi = LocalResources.current.displayMetrics.densityDpi

        LaunchedEffect(key1 = boxSize, key2 = densityDpi) {
            logD("VirtualDisplayScreen: boxSize=$boxSize densityDpi $densityDpi")
            viewModel.updateVirtualDisplayConfig(size = boxSize, densityDpi = densityDpi)
        }
        AndroidView(factory = { context ->
            val surfaceView = SurfaceView(context)
            viewModel.updateSurfaceView(surfaceView)
            surfaceView
        }, modifier = Modifier.fillMaxSize(), update = { _ -> })
    }
}