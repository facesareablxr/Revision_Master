package uk.ac.aber.dcs.cs31620.revisionmaster.ui.util

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


fun processOCR(context: Context, imageUri: String, onRecognizedText: (String) -> Unit) {
    try {
        val image = InputImage.fromFilePath(context, Uri.parse(imageUri))
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val recognizedText = visionText.text
                onRecognizedText(recognizedText)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "OCR failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    } catch (e: Exception) {
        // Handle OCR processing failure
        Log.e("OCR", "OCR processing failed: ${e.message}", e)
    }
}