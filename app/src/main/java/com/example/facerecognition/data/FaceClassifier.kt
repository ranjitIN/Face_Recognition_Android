package com.example.facerecognition.data

import android.graphics.Bitmap

interface FaceClassifier {
    fun classify(bitMap:Bitmap,rotation:Int):List<ClassificationResult>
}