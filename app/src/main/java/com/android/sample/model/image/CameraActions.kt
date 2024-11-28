package com.android.sample.model.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import java.io.IOException

fun takePhoto(
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit,
    applicationContext: Context
) {
  controller.takePicture(
      ContextCompat.getMainExecutor(applicationContext),
      object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
          super.onCaptureSuccess(image)

          val matrix = Matrix().apply { postRotate(image.imageInfo.rotationDegrees.toFloat()) }
          val rotatedBitmap =
              Bitmap.createBitmap(image.toBitmap(), 0, 0, image.width, image.height, matrix, true)

          onPhotoTaken(rotatedBitmap)
        }

        override fun onError(exception: ImageCaptureException) {
          super.onError(exception)
          Toast.makeText(
                  applicationContext,
                  "Error taking picture: ${exception.message}",
                  Toast.LENGTH_SHORT)
              .show()
        }
      })
}

fun flipCamera(cameraSelector: CameraSelector): CameraSelector {
  if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
      return CameraSelector.DEFAULT_FRONT_CAMERA
  else return CameraSelector.DEFAULT_BACK_CAMERA
}

fun uriToBitmap(uri: Uri, context: Context): Bitmap? {
  return try {
    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
  } catch (e: IOException) {
    e.printStackTrace()
    null
  }
}

fun Bitmap.resize(reqWidth: Int): Bitmap {
  val ratio: Float = this.width.toFloat() / this.height.toFloat()
  val height = (reqWidth / ratio).toInt()
  return Bitmap.createScaledBitmap(this, reqWidth, height, true)
}
