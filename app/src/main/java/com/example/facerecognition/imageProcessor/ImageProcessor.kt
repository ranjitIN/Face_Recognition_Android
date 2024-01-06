package com.example.facerecognition.imageProcessor

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF

class ImageProcessor {

    companion object {
        private const val TAG = "Image Processor"
    }

    public fun getCropedFacefromBitmap(source: Bitmap?, cropRectF: RectF): Bitmap {
        val resultBitmap = Bitmap.createBitmap(
            cropRectF.width().toInt(),
            cropRectF.height().toInt(),
            Bitmap.Config.ARGB_8888
        )
        val cavas = Canvas(resultBitmap)

        // draw background
        val paint = Paint(Paint.FILTER_BITMAP_FLAG)
        paint.color = Color.WHITE
        cavas.drawRect(
            RectF(0f, 0f, cropRectF.width(), cropRectF.height()),
            paint
        )
        val matrix = Matrix()
        matrix.postTranslate(-cropRectF.left, -cropRectF.top)
        cavas.drawBitmap(source!!, matrix, paint)
        if (source != null && !source.isRecycled) {
            source.recycle()
        }
        return resultBitmap
    }

//    private fun getCropBitmapByCPU(source: Bitmap?, cropRectF: RectF): Bitmap {
//        if (source == null) {
//            throw IllegalArgumentException("Source bitmap cannot be null")
//        }
//
//        // Ensure cropRectF coordinates are within the bounds of the source bitmap
//        val cropLeft = cropRectF.left.coerceAtLeast(0f)
//        val cropTop = cropRectF.top.coerceAtLeast(0f)
//        val cropRight = cropRectF.right.coerceAtMost(source.width.toFloat())
//        val cropBottom = cropRectF.bottom.coerceAtMost(source.height.toFloat())
//
//        // Calculate the width and height of the original crop region
//        val originalCropWidth = cropRight - cropLeft
//        val originalCropHeight = cropBottom - cropTop
//
//        // Calculate the 20% reduction amount
//        val reductionPercentage = 0.2f
//        val reductionWidth = originalCropWidth * reductionPercentage
//        val reductionHeight = originalCropHeight * reductionPercentage
//
//        // Adjust the cropRectF dimensions by reducing 20%
//        val adjustedCropRectF = RectF(
//            cropLeft + reductionWidth / 2,
//            cropTop + reductionHeight / 2,
//            cropRight - reductionWidth / 2,
//            cropBottom - reductionHeight / 2
//        )
//
//        // Create a new Bitmap with the adjusted dimensions
//        val resultBitmap = Bitmap.createBitmap(
//            adjustedCropRectF.width().toInt(),
//            adjustedCropRectF.height().toInt(),
//            Bitmap.Config.ARGB_8888
//        )
//
//        val canvas = Canvas(resultBitmap)
//
//        // Draw a white background
//        val paint = Paint(Paint.FILTER_BITMAP_FLAG)
//        paint.color = Color.WHITE
//        canvas.drawRect(RectF(0f, 0f, adjustedCropRectF.width(), adjustedCropRectF.height()), paint)
//
//        // Create a matrix to translate the source bitmap to the adjusted cropped region
//        val matrix = Matrix()
//        matrix.postTranslate(-adjustedCropRectF.left, -adjustedCropRectF.top)
//
//        // Draw the adjusted cropped portion of the source bitmap onto the resultBitmap
//        canvas.drawBitmap(source, matrix, paint)
//
//        // Recycle the source bitmap if it's not null and not already recycled
//        if (!source.isRecycled) {
//            source.recycle()
//        }
//
//        return resultBitmap
//    }

    public fun rotateBitmap(
        bitmap: Bitmap, rotationDegrees: Int, flipX: Boolean, flipY: Boolean
    ): Bitmap {
        val matrix = Matrix()

        // Rotate the image back to straight.
        matrix.postRotate(rotationDegrees.toFloat())

        // Mirror the image along the X or Y axis.
        matrix.postScale(if (flipX) -1.0f else 1.0f, if (flipY) -1.0f else 1.0f)
        val rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        // Recycle the old bitmap if it has changed.
        if (rotatedBitmap != bitmap) {
            bitmap.recycle()
        }
        return rotatedBitmap
    }

    public fun normalizeBitmap(bitmap: Bitmap): Bitmap {
        val normalizedBitmap =
            Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val pixel = bitmap.getPixel(x, y)

                val red = android.graphics.Color.red(pixel) / 255.0
                val green = android.graphics.Color.green(pixel) / 255.0
                val blue = android.graphics.Color.blue(pixel) / 255.0

                val normalizedPixel = android.graphics.Color.rgb(
                    (red * 255).toInt(),
                    (green * 255).toInt(),
                    (blue * 255).toInt()
                )

                normalizedBitmap.setPixel(x, y, normalizedPixel)
            }
        }

        return normalizedBitmap
    }

    fun flipBitmap(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix()

        if (horizontal) {
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        }

        if (vertical) {
            matrix.postScale(1f, -1f, bitmap.width / 2f, bitmap.height / 2f)
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}