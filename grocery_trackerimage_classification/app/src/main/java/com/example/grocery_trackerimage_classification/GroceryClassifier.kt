package com.example.grocery_trackerimage_classification.classifier

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class GroceryClassifier(context: Context) {

    private val INPUT_SIZE = 224
    private val NUM_CLASSES = 5

    private val labels = listOf(
        "Jelly",
        "Nestamolt",
        "Pelwatte",
        "Rice Araliya",
        "Rice Sathosa"
    )

    private val interpreter: Interpreter = Interpreter(loadModel(context))

    // Load TFLite model from assets
    private fun loadModel(context: Context): ByteBuffer {
        val fd = context.assets.openFd("grocery_model.tflite")
        val input = FileInputStream(fd.fileDescriptor)
        return input.channel.map(FileChannel.MapMode.READ_ONLY, fd.startOffset, fd.declaredLength)
    }

    /**
     * Classify a bitmap directly (faster than precomputing float array externally)
     */
    fun classify(bitmap: Bitmap): Pair<String, Float> {
        // Resize bitmap if needed
        val resized = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)

        // Prepare ByteBuffer (float32, 4 bytes per channel)
        val inputBuffer = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * 3)
        inputBuffer.order(ByteOrder.nativeOrder())

        // Fill ByteBuffer with normalized RGB values
        for (y in 0 until INPUT_SIZE) {
            for (x in 0 until INPUT_SIZE) {
                val pixel = resized.getPixel(x, y)
                inputBuffer.putFloat(Color.red(pixel) / 255f)
                inputBuffer.putFloat(Color.green(pixel) / 255f)
                inputBuffer.putFloat(Color.blue(pixel) / 255f)
            }
        }
        inputBuffer.rewind()

        // Prepare output array
        val output = Array(1) { FloatArray(NUM_CLASSES) }

        // Run inference
        interpreter.run(inputBuffer, output)

        // Find the max confidence
        val scores = output[0]
        val maxIndex = scores.indices.maxByOrNull { scores[it] } ?: 0
        return labels[maxIndex] to scores[maxIndex]
    }

    fun close() {
        interpreter.close()
    }
}
