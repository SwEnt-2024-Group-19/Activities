import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import com.android.sample.model.image.flipCamera
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class ImageUtilsTest {

  private lateinit var context: Context
  private lateinit var contentResolver: ContentResolver
  private lateinit var uri: Uri

  @Before
  fun setUp() {
    context = Mockito.mock(Context::class.java)
    contentResolver = Mockito.mock(ContentResolver::class.java)
    uri = Mockito.mock(Uri::class.java)
    Mockito.`when`(context.contentResolver).thenReturn(contentResolver)
    // Set up additional mocks as necessary to return a valid bitmap
  }

  @Test
  fun `flipCamera should toggle between front and back camera`() {
    val initialCamera = CameraSelector.DEFAULT_BACK_CAMERA
    val flippedCamera = flipCamera(initialCamera)
    assertEquals(CameraSelector.DEFAULT_FRONT_CAMERA, flippedCamera)

    // Test flipping back to the initial state
    val flippedBackCamera = flipCamera(flippedCamera)
    assertEquals(CameraSelector.DEFAULT_BACK_CAMERA, flippedBackCamera)
  }
}
