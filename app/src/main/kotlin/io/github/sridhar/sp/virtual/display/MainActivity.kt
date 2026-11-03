package io.github.sridhar.sp.virtual.display

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
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
import io.github.sridhar_sp.appview.VirtualDisplayController

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var virtualDisplayController: VirtualDisplayController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        virtualDisplayController =
            VirtualDisplayController(this@MainActivity.application, onDisplayReady = { displayId ->
                launchAppInSpecificDisplay(
                    context = this@MainActivity,
                    intent = Intent(Settings.ACTION_SETTINGS),
                    displayId = displayId
                )
            })

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

    override fun onStop() {
        super.onStop()
        virtualDisplayController.cleanUp()
    }

    companion object {
        fun launchAppInSpecificDisplay(
            context: Context,
            intent: Intent,
            displayId: Int,
        ) {
            val intentWithFlags = intent.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            launchActivityInSpecificDisplay(context, displayId, intentWithFlags)
        }

        private fun launchActivityInSpecificDisplay(context: Context, displayId: Int, intent: Intent) {
            VirtualDisplayController.logD("launchActivityInSpecificDisplay: displayId=$displayId intent - $intent ")
            val activityOptions = ActivityOptions.makeBasic()
            activityOptions.launchDisplayId = displayId
            context.startActivity(intent, activityOptions.toBundle())
        }

    }
}