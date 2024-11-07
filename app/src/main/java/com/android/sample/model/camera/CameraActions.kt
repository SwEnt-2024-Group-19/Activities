package com.android.sample.model.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream

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

fun bitmapToBase64(bitmap: Bitmap): String {
  val byteArrayOutputStream = ByteArrayOutputStream()
  bitmap.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream)
  val byteArray = byteArrayOutputStream.toByteArray()
  return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun base64ToBitmap(encodedString: String): Bitmap? {
  return try {
    val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
    BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
  } catch (e: IllegalArgumentException) {
    e.printStackTrace()
    null
  }
}

fun flipCamera(cameraSelector: CameraSelector): CameraSelector {
  if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
      return CameraSelector.DEFAULT_FRONT_CAMERA
  else return CameraSelector.DEFAULT_BACK_CAMERA
}
