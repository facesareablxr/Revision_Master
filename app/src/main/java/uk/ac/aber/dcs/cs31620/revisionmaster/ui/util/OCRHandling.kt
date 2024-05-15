package uk.ac.aber.dcs.cs31620.revisionmaster.ui.util

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


/**
 * Process OCR (Optical Character Recognition) on an image to extract text.
 *
 * @param context The context from which this function is called.
 * @param imageUri The URI of the image to perform OCR on.
 * @param onRecognizedText Callback function to handle the recognized text.
 */
fun processOCR(context: Context, imageUri: String, onRecognizedText: (String) -> Unit) {
    try {
        // Convert image URI to InputImage
        val image = InputImage.fromFilePath(context, Uri.parse(imageUri))

        // Get an instance of TextRecognizer
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        // Process OCR on the image
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // Extract recognized text from VisionText
                val recognizedText = visionText.text

                // Invoke the callback function with recognized text
                onRecognizedText(recognizedText)
            }
            .addOnFailureListener { e ->
                // Notify about OCR failure
                Toast.makeText(context, "OCR failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    } catch (e: Exception) {
        // Handle exceptions related to OCR processing
        Log.e("OCR", "OCR processing failed: ${e.message}", e)
    }
}
