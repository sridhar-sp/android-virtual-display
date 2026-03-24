package io.github.sridhar.sp.virtual.display

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.droidstarter.designsystem.theme.DroidActivityTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DroidActivityTheme(activity = this, isDarkTheme = false) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp), color = MaterialTheme.colorScheme.background
                ) {
                    VirtualDisplayScreen()
                }
            }
        }
    }
}