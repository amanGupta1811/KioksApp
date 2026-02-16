OVERVIEW

This is a kiosk-style Android application.
The app runs in full-screen immersive mode, displays a live system clock, prevents accidental exits, 
and provides a hidden control panel for device-level operations like refresh, restart, and screenshot capture.

🕒 LIVE CLOCK IMPLEMENTATION

 The current system time is displayed on the main screen.

 Time updates every second using a coroutine running inside the ViewModel.

 Clock updates are lifecycle-aware (startClock() in onStart, stopClock() in onStop) to avoid memory leaks.

 Time formatting is handled through a dedicated GetCurrentTimeUseCase.

🔐 EXIT PROTECTION

 The app cannot be exited using the back button.

 User must tap 3 times within 2 seconds on the screen to trigger exit flow.

 An animated confirmation dialog is shown:

 YES → Exit application

 NO → Continue app

 Dialog uses entry animations (fade + scale) for a polished user experience.

🎛 DEVICE CONTROL PANEL

 A hidden control panel is revealed using a long-press gesture.

 Implemented using a BottomSheetDialogFragment for clean separation.


🔁 RESTART LOGIC

 Restart is handled at the Activity level since it is a platform-level operation.

 On restart:

 Current time is captured

 Restart time is saved to SharedPreferences

 App is relaunched using getLaunchIntentForPackage

 Current process is terminated using Runtime.getRuntime().exit(0)

 On next launch, the last restart time is restored and displayed in the activity log.

 This approach keeps restart logic simple, reliable, and realistic for production apps.

📸 SCREENSHOT METHOD

 Screenshot is captured from the root view using:

 window.decorView.rootView

 Converted into a Bitmap

 Screenshot saving:

 Implemented using MediaStore API (Scoped Storage compliant)

 Saved under Pictures/KioskApp

 Screenshot preview:

 Displayed as a thumbnail in the top-right corner

 Animated pop-in and fade-out (2 seconds)

 Screenshot time is logged and persisted using SharedPreferences.

🔑 RUNTIME PERMISSION HANDLING

 Runtime storage permission is conditionally requested only for Android 9 (API 28) and below.

 Android 10+ uses scoped storage, so no permission is required.

 Permission handling implemented using ActivityResultLauncher for safety and clarity.

🎨 ANIMATION APPROACH

 The app includes multiple lightweight, non-blocking UI animations:

 Screenshot thumbnail pop-up animation

 Control panel slide-up reveal

 Exit dialog animated appearance

 Activity log highlight animation when values update

 All animations:

 Run on the UI thread safely

 Do not block background operations

 Reset view states properly to avoid visual glitches

🧱 ARCHITETURE DESIGN

 Architecture follows MVVM principles:

 View (Activity / Fragment) – UI rendering and user interaction

 ViewModel – business logic and state handling

 UseCases – single-responsibility logic (time, screenshot)

 Repositories – data handling (time source, storage)

 Dependency Injection libraries were intentionally avoided to keep the assignment simple and readable.

 ViewModel is initialized once to maintain a single source of truth.
 
 This structure keeps the app:

 Easy to understand

 Easy to debug

 Realistic for small-to-medium production apps

🚀 BONUS FEATURES

 Hidden gesture to open control panel (long press)

 Fully immersive full-screen mode

 Persistent activity logs across app restarts

👨‍💻 FINAL NOTE

This project prioritizes clarity, stability, and real-world Android practices over unnecessary complexity, while still demonstrating strong understanding of lifecycle management, UI animation, and system-level behavior.
