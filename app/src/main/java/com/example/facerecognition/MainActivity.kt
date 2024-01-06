package com.example.facerecognition

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.facerecognition.classifier.TfliteFaceClassifier
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.ImageProcessor
import java.nio.MappedByteBuffer
import java.text.DecimalFormat
import java.util.concurrent.Executors


@ExperimentalGetImage
class MainActivity : AppCompatActivity() {
    private val CAMERA_PERMISSION_REQUEST = 100
    private lateinit var cameraManager: CameraManager
    private lateinit var previewView: PreviewView
    var faceImages = mutableListOf<Bitmap>()
    private val TAG = "Mainactivity"
    private val executor = Executors.newSingleThreadExecutor()
    private var imagePreview: ImageView? = null
    var faceDetection: TfliteFaceClassifier? = null
    var model: MappedByteBuffer? = null
    var tflite: Interpreter? = null
    private lateinit var  resultView: TextView
    val matrix = Matrix()
    var imageView: ImageView? = null
    private val facialRecogDict: Map<String, String> = mapOf()
    val imageProcessor = ImageProcessor.Builder().build()
    val df = DecimalFormat("#.##")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestCameraPermission(this)
        Log.d(TAG,"first ${resultView.toString()}")
    }

    private fun initCameraManager() {
        resultView = findViewById(R.id.textView)
        previewView = findViewById(R.id.previewView);
        resultView.text = "init camera"
        Log.d(TAG,"preview view ${previewView.toString()}")
        cameraManager = CameraManager(
            this,
            previewView!!,
            this,
            resultView!!
        )
        cameraManager.startCamera()
    }



    fun requestCameraPermission(context: Context){
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initCameraManager()
//            initializeFaceDetector()
//            faceDetection = TfliteImageClassifier(this)
//            model = loadModelFile(this)
//            tflite = Interpreter(model!!, Interpreter.Options())
//            startCamera()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                AlertDialog.Builder(this).setTitle("Required Camera Permission")
                    .setMessage("To Access Your camera And Capture your Face")
                    .setPositiveButton("OK") { dialog, which ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.CAMERA),
                            CAMERA_PERMISSION_REQUEST
                        )
                    }.setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }.create().show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    initCameraManager()
//                    initializeFaceDetector()
//                    faceDetection = TfliteImageClassifier(this)
//                    model = loadModelFile(this)
//                    tflite = Interpreter(model!!, Interpreter.Options())
//                    startCamera()
                } else {
                    AlertDialog.Builder(this).setTitle("Required Camera Permission")
                        .setMessage("To Access Your camera And Capture your Face")
                        .setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                        }.setNegativeButton("Cancel") { dialog, which ->
                            dialog.dismiss()
                        }.create().show()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


}