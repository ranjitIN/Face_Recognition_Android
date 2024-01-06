package com.example.facerecognition.classifier

import android.content.Context
import android.graphics.Bitmap
import android.view.Surface
import com.example.facerecognition.data.FaceClassifier
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import android.util.Log
import com.example.facerecognition.data.ClassificationResult

class TfliteFaceClassifier(
    private val context: Context,
    private val threshold: Float = 0.8f,
    private val maxResults: Int = 1
) : FaceClassifier {
    var classifier: ImageClassifier? = null

    private fun setupClassifier() {
        val baseOptions = BaseOptions.builder().setNumThreads(3).build()
        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(baseOptions)
            .setMaxResults(maxResults).setScoreThreshold(threshold).build()
        try {
            classifier = ImageClassifier.createFromFileAndOptions(context, "FaceModel_smartwatch.tflite", options)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    override fun classify(bitMap: Bitmap, rotation: Int): List<ClassificationResult> {
        if (classifier == null) {
            setupClassifier()
        }
        val imageProcessor = ImageProcessor.Builder().build()

        var tesorImage = imageProcessor.process(TensorImage.fromBitmap(bitMap))

        var rot  = getOrientationFromRotation(0)
        val imageProcessingOptions = ImageProcessingOptions.builder()
            .build()
        val results = classifier?.classify(tesorImage, imageProcessingOptions)
//        Log.i("CameraPreviewApp","results"+results.toString())
//        Log.i("CameraPreviewApp","=================[result]============")
//        results?.forEach {
//            it.categories.forEach {it1->
//                Log.i("CameraPreviewApp","${it1.toString()}")
//            }
//
//        }
        return results?.flatMap { classification ->
            classification.categories.map { category ->
                ClassificationResult(
                    name = category.label,
                    score = category.score
                )
            }
        }?.distinctBy { it.name } ?: emptyList()
    }


    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation {
        when (rotation) {
            Surface.ROTATION_270 ->
                return ImageProcessingOptions.Orientation.BOTTOM_RIGHT
            Surface.ROTATION_180 ->
                return ImageProcessingOptions.Orientation.RIGHT_BOTTOM
            Surface.ROTATION_90 ->
                return ImageProcessingOptions.Orientation.TOP_LEFT
            else ->
                return ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }
}