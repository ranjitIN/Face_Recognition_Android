package com.example.facerecognition.facefinder

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import android.widget.TextView
import com.example.facerecognition.BaseImageAnalyzer
import com.example.facerecognition.classifier.TfliteFaceClassifier
import com.example.facerecognition.imageProcessor.ImageProcessor
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.IOException

class FaceFindProcessor(private val view: TextView,private val faceClassifier: TfliteFaceClassifier) :
    BaseImageAnalyzer<List<Face>>() {

    private val imageProcessor:ImageProcessor = ImageProcessor()

    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
        .build()

    private val detector = FaceDetection.getClient(realTimeOpts)

    override val resultTextView: TextView
        get() = view

    override fun detectInImage(image: InputImage): Task<List<Face>> {
        return detector.process(image)
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Face Detector: $e")
        }
    }

    override fun onSuccess(
        results: List<Face>,
        imageFrame: Bitmap,
        imageRotation:Int,
        resultTextView: TextView,
        rect: Rect
    ) {
//        resultTextView.text = "${results.size}"
        Log.i(TAG, "face results: ${results.size}")
        if(results.isEmpty()){
            resultTextView.text = "Detecting Face"
        } else {
            for (face in results) {
                try {
                    val rotated_image: Bitmap =
                        imageProcessor.rotateBitmap(imageFrame, imageRotation, false, false)
                    val boundingBox = RectF(face.boundingBox)
                    val croppedFace: Bitmap =
                        imageProcessor.getCropedFacefromBitmap(rotated_image, boundingBox)
                    val flipped =
                        imageProcessor.flipBitmap(croppedFace, horizontal = true, vertical = false)
                    val normalizeImage = imageProcessor.normalizeBitmap(flipped)
                    val results1 = faceClassifier?.classify(normalizeImage, 0)
                    Log.i(TAG, "result 1 : ${results1.toString()}")
                    resultTextView?.text =
                        "${results1?.get(0)?.name} [${results1?.get(0)?.score}] 😍"
                } catch (e: Exception) {
                    Log.e(TAG, e.fillInStackTrace().toString())
                }
            }
        }
//        resultTextView.clear()
//        results.forEach {
//            val faceGraphic = FaceContourGraphic(graphicOverlay, it, rect)
//            graphicOverlay.add(faceGraphic)
//        }
//        graphicOverlay.postInvalidate()
    }



    override fun onFailure(e: Exception) {
        Log.w(TAG, "Face Detector failed.$e")
    }

    companion object {
        private const val TAG = "FaceDetectorProcessor"
    }

}