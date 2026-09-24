package io.github.sridhar.sp.virtual.display

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.droidstarter.designsystem.theme.DroidActivityTheme
import com.droidstarter.designsystem.theme.appDimens
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val virtualDisplayController = VirtualDisplayController(this@MainActivity.application)
        setContent {
            DroidActivityTheme(activity = this, isDarkTheme = false) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp), color = MaterialTheme.colorScheme.background
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.appDimens.smallPadding)
                    ) {
                        VirtualDisplayScreen(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f),
                            virtualDisplayController
                        )
                    }
                }
            }
        }
    }
}