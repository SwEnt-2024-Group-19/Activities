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

/**
 * Captures a photo using a given camera controller and processes the image to a Bitmap.
 *
 * This function utilizes the camera controller to take a picture and then processes the captured
 * image. If the photo capture is successful, it rotates the image according to its original
 * orientation and converts it to a Bitmap, which is then passed to the `onPhotoTaken` callback. If
 * an error occurs during the capture, a toast message displays the error to the user.
 *
 * @param controller The camera controller used to take the picture.
 * @param onPhotoTaken Callback function that receives the captured and processed Bitmap.
 * @param applicationContext Context used to execute operations and show messages.
 */
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
/**
 * Toggles the camera between the front and back lenses.
 *
 * This function determines the current camera lens being used and switches to the other lens. If
 * the back camera is currently selected, it switches to the front camera, and vice versa.
 *
 * @param cameraSelector The current camera selector indicating which camera is in use.
 * @return The updated camera selector for the opposite camera.
 */
fun flipCamera(cameraSelector: CameraSelector): CameraSelector {
  if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
      return CameraSelector.DEFAULT_FRONT_CAMERA
  else return CameraSelector.DEFAULT_BACK_CAMERA
}
/**
 * Converts a URI to a Bitmap.
 *
 * This function attempts to retrieve an image from the provided URI and convert it into a Bitmap
 * object. If successful, it returns the Bitmap; if there is an IOException during retrieval, it
 * returns null and logs the error.
 *
 * @param uri The URI pointing to the image to be converted.
 * @param context The context used for accessing the ContentResolver.
 * @return A Bitmap if successful, or null if an error occurs.
 */
fun uriToBitmap(uri: Uri, context: Context): Bitmap? {
  return try {
    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
  } catch (e: IOException) {
    e.printStackTrace()
    null
  }
}
/**
 * Resizes a Bitmap to a specified width while maintaining its aspect ratio.
 *
 * This function calculates the necessary height to maintain the aspect ratio based on the specified
 * width, then resizes the Bitmap accordingly. This is useful for ensuring consistent image sizes
 * and formats across different UI elements.
 *
 * @param reqWidth The required width to which the Bitmap should be resized.
 * @return A new Bitmap resized to the specified width while maintaining the original aspect ratio.
 */
fun Bitmap.resize(reqWidth: Int): Bitmap {
  val ratio: Float = this.width.toFloat() / this.height.toFloat()
  val height = (reqWidth / ratio).toInt()
  return Bitmap.createScaledBitmap(this, reqWidth, height, true)
}
