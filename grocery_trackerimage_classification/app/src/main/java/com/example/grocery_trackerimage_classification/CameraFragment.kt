package com.example.grocery_trackerimage_classification.camera

import android.graphics.*
import android.os.Bundle
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.grocery_trackerimage_classification.R
import com.example.grocery_trackerimage_classification.classifier.GroceryClassifier
import com.example.grocery_trackerimage_classification.ui.ConfirmProductDialogFragment
import java.io.ByteArrayOutputStream

class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var classifier: GroceryClassifier
    private lateinit var previewView: PreviewView
    private var isDialogOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        classifier = GroceryClassifier(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        previewView = view.findViewById(R.id.previewView)
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analysis.setAnalyzer(
                ContextCompat.getMainExecutor(requireContext())
            ) { imageProxy ->
                if (!isDialogOpen) {
                    val bitmap = imageProxy.toBitmap()
                    runDetection(bitmap)
                }
                imageProxy.close()
            }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                analysis
            )

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun runDetection(bitmap: Bitmap) {
        val (product, _) = classifier.classify(bitmap) // ignore confidence now

        // Only open dialog if not already open
        if (isDialogOpen) return

        isDialogOpen = true
        ConfirmProductDialogFragment.newInstance(product) {
            isDialogOpen = false
        }.show(parentFragmentManager, "confirm_dialog")
    }

    override fun onDestroy() {
        super.onDestroy()
        classifier.close()
    }
}

/** Extension function to convert ImageProxy → Bitmap */
fun ImageProxy.toBitmap(): Bitmap {
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val image = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val out = ByteArrayOutputStream()
    image.compressToJpeg(Rect(0, 0, width, height), 90, out)

    return BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size())
}
