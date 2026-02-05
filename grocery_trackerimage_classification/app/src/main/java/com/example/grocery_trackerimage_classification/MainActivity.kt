package com.example.grocery_trackerimage_classification

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.grocery_trackerimage_classification.classifier.GroceryClassifier
import com.example.grocery_trackerimage_classification.databinding.FragmentCameraBinding
import com.example.grocery_trackerimage_classification.ui.ConfirmProductDialogFragment
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: FragmentCameraBinding
    private val classifier by lazy { GroceryClassifier(this) }
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Add a subtle breathing animation to the scanner frame
        val pulse = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        binding.scannerReticle.startAnimation(pulse)

        if (allPermissionsGranted()) startCamera()
        else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 10)

        // The Quick Add button triggers your confirmation dialog
        binding.btnQuickAdd.setOnClickListener {
            val product = binding.tvScanHint.text.toString()
            if (product != "Aim at product..." && product.isNotEmpty()) {
                ConfirmProductDialogFragment.newInstance(product) {
                    // Success callback
                }.show(supportFragmentManager, "confirm")
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        val bitmap = imageProxy.toBitmap()
                        if (bitmap != null) {
                            val result = classifier.classify(bitmap)
                            runOnUiThread {
                                // Only update if confidence is reasonable (e.g., > 50%)
                                if (result.second > 0.50f) {
                                    binding.tvScanHint.text = result.first
                                    binding.btnQuickAdd.isEnabled = true
                                }
                            }
                        }
                        imageProxy.close()
                    }
                }

            cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analyzer)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun ImageProxy.toBitmap(): Bitmap? {
        val bitmap = this.toBitmap() ?: return null
        val rotation = this.imageInfo.rotationDegrees
        if (rotation == 0) return bitmap
        val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        classifier.close()
    }
}