package com.example.receiptscanner

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        val previewView: PreviewView = findViewById(R.id.preview_view)
        val captureButton: Button = findViewById(R.id.capture_button)
        val processSampleButton: Button = findViewById(R.id.process_sample_button)

        cameraExecutor = Executors.newSingleThreadExecutor()

        // Request camera permissions
        val cameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    startCamera(previewView)
                } else {
                    Toast.makeText(this, "Camera permission is required to use the camera", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)

        // Capture button click listener
        captureButton.setOnClickListener {
            takePhotoAndProcessImage()
        }

        // Process sample button click listener
        processSampleButton.setOnClickListener {
            processDrawableImage(R.drawable.target_test)
        }
    }

    private fun startCamera(previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to start camera: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhotoAndProcessImage() {
        val photoFile = File(externalMediaDirs.firstOrNull(), "${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    processImage(photoFile.absolutePath)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@ScanActivity, "Error capturing image: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun processImage(imagePath: String) {
        val inputImage = InputImage.fromFilePath(this, Uri.parse(imagePath))
        processWithMLKit(inputImage)
    }

    private fun processDrawableImage(drawableId: Int) {
        val bitmap = BitmapFactory.decodeResource(resources, drawableId)
        val inputImage = InputImage.fromBitmap(bitmap, 0) // Rotation = 0 for upright
        processWithMLKit(inputImage)
    }

    private fun processWithMLKit(inputImage: InputImage) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                val rawText = visionText.text
                Log.d("MLKit", "Raw Text Extracted: $rawText")

                try {
                    val receipt = extractReceiptData(rawText)
                    returnResult(receipt)
                } catch (e: Exception) {
                    Log.e("MLKit", "Failed to parse receipt: ${e.message}")
                    Toast.makeText(this, "Failed to parse receipt", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to process image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun extractReceiptData(rawText: String): Receipt {
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotEmpty() }
        var title = "Unknown Store"
        var date = "Unknown Date"
        val items = mutableListOf<Item>()

        for (line in lines) {
            if (line.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))) {
                date = line
            } else if (line.matches(Regex(".*\\$?\\d+\\.\\d{2}.*"))) {
                val parts = line.split(" ")
                val name = parts.dropLast(1).joinToString(" ")
                val price = parts.last().replace("$", "").toDoubleOrNull() ?: 0.0
                items.add(Item(name, price))
            } else {
                title = line
            }
        }

        val total = items.sumOf { it.price }
        return Receipt(title, date, items, total)
    }

    private fun returnResult(receipt: Receipt) {
        val intent = Intent()
        intent.putExtra("receipt", receipt)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
