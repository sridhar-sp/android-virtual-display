package io.github.sridhar.sp.virtual.display

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.InputEvent
import android.view.MotionEvent
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import javax.inject.Inject

@HiltViewModel
class VirtualDisplayViewModel @Inject constructor(private val context: Application) : AndroidViewModel(context) {

    private val displayManager by lazy { context.getSystemService(DisplayManager::class.java) }

    lateinit var surfaceViewRef: WeakReference<SurfaceView>
        private set

    private var virtualDisplaySize: IntSize? = null

    private var virtualDisplayDensityDpi = DisplayMetrics.DENSITY_DEFAULT

    private var lastCreatedVirtualDisplay: VirtualDisplay? = null

    private var inputChannel: Any? = null
    private var inputEventReceiver: Any? = null
    private var virtualDisplayDisplayId: Int = -1

    private val inputManagerInstance: Any? by lazy {
        try {
            val inputManagerClass = Class.forName("android.hardware.input.InputManager")
            val getInstanceMethod = inputManagerClass.getMethod("getInstance")
            getInstanceMethod.invoke(null)
        } catch (e: Exception) {
            logE("Failed to get InputManager: $e")
            null
        }
    }

    private val injectInputEventMethod: Method? by lazy {
        try {
            val inputManagerClass = Class.forName("android.hardware.input.InputManager")
            inputManagerClass.getMethod("injectInputEvent", InputEvent::class.java, Int::class.javaPrimitiveType)
        } catch (e: Exception) {
            logE("Failed to get injectInputEvent method: $e")
            null
        }
    }

    init {
        logD("VirtualDisplayViewModel created ${hashCode()}")
    }

    fun updateVirtualDisplayConfig(size: IntSize, densityDpi: Int) {
        virtualDisplaySize = size
        virtualDisplayDensityDpi = densityDpi
    }

    private val handler = Handler(Looper.getMainLooper())

    @SuppressLint("DiscouragedPrivateApi")
    private fun injectInputEventToDisplay(event: InputEvent, displayId: Int): Boolean {
        return try {
            inputManagerInstance?.let { im ->
                injectInputEventMethod?.invoke(im, event, displayId) as? Boolean
            } ?: false
        } catch (e: Exception) {
            logE("injectInputEvent failed: $e")
            false
        }
    }

    @SuppressLint("DiscouragedPrivateApi")
    private fun setInputEventDisplayId(event: InputEvent, displayId: Int) {
        try {
            val setDisplayIdMethod = event.javaClass.getMethod("setDisplayId", Int::class.javaPrimitiveType)
            setDisplayIdMethod.invoke(event, displayId)
        } catch (e: Exception) {
            try {
                val displayIdField = event.javaClass.getDeclaredField("mDisplayId")
                displayIdField.isAccessible = true
                displayIdField.setInt(event, displayId)
            } catch (e2: Exception) {
                logE("Failed to set display ID: $e2")
            }
        }
    }

    fun updateSurfaceView(surfaceView: SurfaceView) {
        surfaceViewRef = WeakReference(surfaceView)

        surfaceView.setOnTouchListener { _, event: MotionEvent ->
            lastCreatedVirtualDisplay?.let { vDisplay ->
                setInputEventDisplayId(event, vDisplay.display.displayId)
                injectInputEventToDisplay(event, vDisplay.display.displayId)
            } ?: run {
                logE("Virtual display not created yet, hence not delegating input event")
            }
            true
        }

        surfaceView.setOnKeyListener { _, keyCode, event ->
            lastCreatedVirtualDisplay?.let { vDisplay ->
                setInputEventDisplayId(event, vDisplay.display.displayId)
                injectInputEventToDisplay(event, vDisplay.display.displayId)
            } ?: run {
                logE("Virtual display not created yet, hence not delegating key event")
            }
            true
        }

        surfaceView.isFocusable = true
        surfaceView.isFocusableInTouchMode = true
        surfaceView.requestFocus()

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                logD("surfaceCreated")
                viewModelScope.launch {
                    delay(1000)
                    startAppInVirtualDisplay(holder.surface)
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder, format: Int, width: Int, height: Int
            ) {
                logD("surfaceChanged")
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                logD("surfaceDestroyed")
                cleanupInputChannel()
            }
        })
    }

    fun startAppInVirtualDisplay(surface: Surface) {
        if (virtualDisplaySize == null) {
            logE("Please set virtual display size first")
            return
        }

        lastCreatedVirtualDisplay = displayManager.createVirtualDisplay(
            "LauncherWidgetVirtualDisplay_${hashCode()}",
            virtualDisplaySize!!.width,
            virtualDisplaySize!!.height,
            virtualDisplayDensityDpi,
            surface,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION,
        )

        lastCreatedVirtualDisplay?.let { vDisplay ->
            virtualDisplayDisplayId = vDisplay.display.displayId
            setupInputChannel()
        }

        launchAppInSpecificDisplay(getApplication(), vPlayComponentName, lastCreatedVirtualDisplay!!.display.displayId)
    }

    @SuppressLint("DiscouragedPrivateApi")
    private fun setupInputChannel() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val createInputChannelMethod = DisplayManager::class.java.getMethod(
                    "createInputChannel",
                    String::class.java
                )
                inputChannel = createInputChannelMethod.invoke(displayManager, "VirtualDisplayInputChannel_${hashCode()}")
                
                inputChannel?.let { channel ->
                    inputEventReceiver = createInputEventReceiver(channel, Looper.getMainLooper())
                }
            }
        } catch (e: Exception) {
            logE("setupInputChannel failed: $e")
        }
    }

    private fun createInputEventReceiver(channel: Any, looper: Looper): Any? {
        return try {
            val receiverClass = Class.forName("android.view.InputEventReceiver")
            val constructor: Constructor<*> = receiverClass.getConstructor(
                Class.forName("android.view.InputChannel"),
                Looper::class.java
            )
            constructor.newInstance(channel, looper)
        } catch (e: Exception) {
            logE("createInputEventReceiver failed: $e")
            null
        }
    }

    private fun cleanupInputChannel() {
        inputEventReceiver?.let { receiver ->
            try {
                val disposeMethod = receiver.javaClass.getMethod("dispose")
                disposeMethod.invoke(receiver)
            } catch (e: Exception) {
                logE("Failed to dispose InputEventReceiver: $e")
            }
        }
        inputEventReceiver = null

        inputChannel?.let { channel ->
            try {
                val disposeMethod = channel.javaClass.getMethod("dispose")
                disposeMethod.invoke(channel)
            } catch (e: Exception) {
                logE("Failed to dispose InputChannel: $e")
            }
        }
        inputChannel = null
    }

    override fun onCleared() {
        super.onCleared()
        cleanupInputChannel()
        lastCreatedVirtualDisplay?.release()
        lastCreatedVirtualDisplay = null
    }


    companion object {

        val vPlayComponentName = ComponentName("com.visteon.play", "com.visteon.play.main.MainActivity")

        fun launchAppInSpecificDisplay(
            context: Context,
            componentName: ComponentName,
            displayId: Int,
        ) {
            val intent = Intent().apply {
                setComponent(componentName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            launchActivityInSpecificDisplay(context, displayId, intent)
        }

        private fun launchActivityInSpecificDisplay(context: Context, displayId: Int, intent: Intent) {
            logD("launchActivityInSpecificDisplay: displayId=$displayId intent - $intent ")
            val activityOptions = ActivityOptions.makeBasic()
            activityOptions.launchDisplayId = displayId
            context.startActivity(intent, activityOptions.toBundle())
        }
    }
}
