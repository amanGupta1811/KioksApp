package com.example.kioks.presentation.main

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat.postDelayed
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.kioks.dataRepostry.ScreenshotRepositoryImpl
import com.example.kioks.dataRepostry.TimeRepositoryImpl
import com.example.kioks.databinding.ActivityMainBinding
import com.example.kioks.domain.usecase.GetCurrentTimeUseCase
import com.example.kioks.domain.usecase.SaveScreenshotUseCase
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private val storagePermissionLauncher =
        registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                takeScreenshotFromPanel()
            } else {
                Toast.makeText(
                    this,
                    "Storage permission required to save screenshot",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val timeRepository = TimeRepositoryImpl()
        val getCurrentTimeUseCase = GetCurrentTimeUseCase(timeRepository)

        val screenshotRepo = ScreenshotRepositoryImpl(this)
        val saveScreenshotUseCase = SaveScreenshotUseCase(screenshotRepo)

        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(
                getCurrentTimeUseCase,
                saveScreenshotUseCase
            )
        )[MainViewModel::class.java]

        loadLogs()

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                }
            }
        )

        enableFullScreen()
        setupExitProtection()
        setupControlPanelGesture()

        observeUi()
    }

    override fun onStart() {
        super.onStart()
        viewModel.startClock()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopClock()
    }

    private fun observeUi() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {

                        is MainUiState.TimeUpdated -> {
                            binding.tvClock.text = state.time
                        }

                        is MainUiState.RefreshSuccess -> {
                            updateLastRefresh(state.time)
                            Toast.makeText(this@MainActivity, "App Refreshed", Toast.LENGTH_SHORT).show()
                        }

                        is MainUiState.ScreenshotTaken -> {
                            showScreenshotPreview(state.bitmap)
                            updateLastScreenshot()
                        }

                        is MainUiState.RestartSuccess -> {
                            updateLastRestart(state.time)
                        }

                        is MainUiState.ScreenshotSaved -> {

                            // preview
                            showScreenshotPreview(state.bitmap)

                            // save last screenshot time
                            getSharedPreferences("logs", MODE_PRIVATE)
                                .edit()
                                .putString("last_screenshot", state.time)
                                .apply()

                            binding.tvLastScreenshot.text =
                                "Last Screenshot: ${state.time}"
                        }



                        else -> {}
                    }
                }
            }
        }
    }

    private fun updateLastRefresh(time: String) {

        getSharedPreferences("logs", MODE_PRIVATE)
            .edit()
            .putString("last_refresh", time)
            .apply()

        binding.tvLastRefresh.apply {
            text = "Last Refresh: $time"
            alpha = 0f

            setBackgroundColor(
                resources.getColor(android.R.color.holo_red_light)
            )
            animate().alpha(1f).setDuration(300).withEndAction {
                postDelayed({
                    setBackgroundColor(
                        resources.getColor(android.R.color.transparent)
                    )
                }, 600)
            }.start()
        }
    }


    private fun updateLastRestart(time: String) {


        binding.tvLastRestart.apply {
            text = "Last Restart: $time"
            alpha = 0f
            setBackgroundColor(
                resources.getColor(android.R.color.holo_green_light)
            )

            animate()
                .alpha(1f)
                .setDuration(3000)
                .withEndAction {
                    postDelayed({
                        setBackgroundColor(
                            resources.getColor(android.R.color.transparent)
                        )
                    }, 6000)
                }
                .start()
        }
    }

    private fun updateLastScreenshot() {
        binding.tvLastScreenshot.apply {
            alpha = 0f
            setBackgroundColor(
                resources.getColor(android.R.color.holo_blue_light)
            )
            animate()
                .alpha(1f)
                .setDuration(300)
                .withEndAction {
                    postDelayed({
                        setBackgroundColor(
                            resources.getColor(android.R.color.transparent)
                        )
                    }, 600)
                }
                .start()
        }
    }



    fun takeScreenshotFromPanel() {

        if (!hasStoragePermission()) {
            storagePermissionLauncher.launch(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            return
        }

        val rootView = window.decorView.rootView
        rootView.isDrawingCacheEnabled = true

        val bitmap = Bitmap.createBitmap(rootView.drawingCache)
        rootView.isDrawingCacheEnabled = false

        val time = viewModel.getCurrentTime()
        getSharedPreferences("logs", MODE_PRIVATE)
            .edit()
            .putString("last_screenshot", time)
            .apply()

        viewModel.saveScreenshot(bitmap)
    }


    private fun showScreenshotPreview(bitmap: android.graphics.Bitmap) {

        binding.ivThumbnail.apply {
            clearAnimation()
            setImageBitmap(bitmap)
            visibility = View.VISIBLE
            alpha = 0f
            scaleX = 0.9f
            scaleY = 0.9f

            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .withEndAction {
                    postDelayed({
                        animate()
                            .alpha(0f)
                            .setDuration(300)
                            .withEndAction {
                                visibility = View.GONE
                            }
                            .start()
                    }, 2000)
                }
                .start()
        }
    }

    private fun enableFullScreen() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    private fun setupControlPanelGesture() {
        binding.root.setOnLongClickListener {
            ControlPanelFragment().show(
                supportFragmentManager,
                "control_panel"
            )
            true
        }
    }

    fun refreshAppFromPanel(){
        viewModel.refreshApp()
    }

    fun restartAppFromPanel() {

        val time = viewModel.getCurrentTime()

        Log.d("RESTART_SAVE", "Saving restart time = $time")

        val prefs = getSharedPreferences("logs", MODE_PRIVATE)
        prefs.edit()
            .putString("last_restart", time)
            .commit()

        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        )

        startActivity(intent)
        Runtime.getRuntime().exit(0)
    }



    private fun loadLogs() {

        val prefs = getSharedPreferences("logs", MODE_PRIVATE)

        val restartTime = prefs.getString("last_restart", null)
        val refreshTime = prefs.getString("last_refresh", null)
        val screenshotTime = prefs.getString("last_screenshot", null)

        Log.d("LOG_CHECK", "Restart = ${prefs.getString("last_restart", "NULL")}")

        restartTime?.let {
            binding.tvLastRestart.text = "Last Restart: $it"
        }

        refreshTime?.let {
            binding.tvLastRefresh.text = "Last Refresh: $it"
        }

        screenshotTime?.let{
            binding.tvLastScreenshot.text="Last Screenshot: $it"
        }
        Log.d("LOG_CHECK", "Restart = ${prefs.getString("last_restart", "NULL")}")


    }
    private var tapCount = 0
    private var firstTapTime: Long = 0

    private fun setupExitProtection() {
        binding.root.setOnClickListener {
            val currentTime = System.currentTimeMillis()

            if (tapCount == 0) {
                firstTapTime = currentTime
            }

            tapCount++

            if (tapCount == 3 && currentTime - firstTapTime <= 2000) {
                showExitDialog()
                tapCount = 0
            }

            if (currentTime - firstTapTime > 2000) {
                tapCount = 1
                firstTapTime = currentTime
            }
        }
    }

    private fun showExitDialog() {
        ExitDialogFragment().show(
            supportFragmentManager,
            "exit_dialog"
        )
    }

    private fun hasStoragePermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            true
        } else {
            checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

}
