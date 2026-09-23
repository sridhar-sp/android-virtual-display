# Android Virtual Display

This project demonstrates creating and managing a **Virtual Display** on Android. The key feature is the ability to:

- Create a virtual Android display using `DisplayManager.createVirtualDisplay()`
- Set up input channels for injecting touch/key events into the virtual display
- Render content via a `SurfaceView` within the virtual display
- Launch activities in the specific virtual display using `activityOptions.launchDisplayId`
- Delegate input events (touch and key) to the virtual display

## Core Features

| Feature | Description |
|---------|-------------|
| **VirtualDisplayViewModel** | Manages virtual display creation, configuration, and input channel setup |
| **SurfaceView Integration** | Uses `AndroidView` with `SurfaceView` to render the virtual display |
| **Input Event Injection** | Supports injecting touch and key events into the virtual display via reflection API calls |
| **Display ID Routing** | Routes activities to specific virtual displays using `launchDisplayId` |
| **Configuration Persistence** | Stores virtual display size and density DPI for reuse |

## How It Works

1. **Setup**: The `VirtualDisplayScreen` composable sets up a `SurfaceView` and observes size/density changes
2. **Creation**: When the surface is created, `startAppInVirtualDisplay()` creates a virtual display via `displayManager.createVirtualDisplay()`
3. **Input Handling**: Touch and key events on the `SurfaceView` are delegated to the virtual display by setting the display ID and injecting events via `InputManager.injectInputEvent()`
4. **Launch**: Activities can be launched in the virtual display using `launchAppInSpecificDisplay()` with the display ID

## Key Code Locations

- `app/src/main/kotlin/.../VirtualDisplayViewModel.kt` - ViewModel managing virtual display lifecycle
- `app/src/main/kotlin/.../VirtualDisplayScreen.kt` - Composable with SurfaceView integration
- `app/src/main/kotlin/.../MainActivity.kt` - Entry point with Hilt injection

## Building

This project uses Gradle Convention Plugins for build configuration. Checkout [This blog](https://medium.com/@sridhar-sp/simplify-your-android-builds-a-guide-to-convention-plugins-b9fea8c5e117) for more details.
