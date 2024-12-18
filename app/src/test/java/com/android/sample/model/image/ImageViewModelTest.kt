import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import androidx.camera.core.CameraSelector
import com.android.sample.model.image.ImageRepositoryFirestore
import com.android.sample.model.image.ImageViewModel
import com.android.sample.model.image.flipCamera
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

class ImageUtilsTest {

  private lateinit var context: Context
  private lateinit var contentResolver: ContentResolver
  private lateinit var uri: Uri
  private lateinit var mockRepository: ImageRepositoryFirestore
  private lateinit var viewModel: ImageViewModel
  private lateinit var sharedPreferences: SharedPreferences
  private lateinit var mockEditor: SharedPreferences.Editor

  @Before
  fun setUp() {
    context = mock(Context::class.java)
    contentResolver = mock(ContentResolver::class.java)
    uri = mock(Uri::class.java)
    `when`(context.contentResolver).thenReturn(contentResolver)
    // Set up additional mocks as necessary to return a valid bitmap
    MockitoAnnotations.openMocks(this)
    mockRepository = mock(ImageRepositoryFirestore::class.java)
    sharedPreferences = mock(SharedPreferences::class.java)
    mockEditor = mock(SharedPreferences.Editor::class.java)
    `when`(sharedPreferences.edit()).thenReturn(mockEditor)
    `when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
    doNothing().`when`(mockEditor).apply()
    viewModel = ImageViewModel(mockRepository, sharedPreferences)
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

  @Test
  fun uploadProfilePicture_success() {
    val userId = "userId"
    val bitmap = mock(Bitmap::class.java)
    val expectedUrl = "https://example.com/profile.jpg"

    doAnswer {
          val onSuccess = it.getArgument<(String) -> Unit>(2)
          onSuccess(expectedUrl)
        }
        .whenever(mockRepository)
        .uploadProfilePicture(eq(userId), eq(bitmap), any(), any())

    var resultUrl: String? = null
    viewModel.uploadProfilePicture(userId, bitmap, { url -> resultUrl = url }, {})

    assert(resultUrl == expectedUrl)
  }

  @Test
  fun uploadProfilePicture_failure() {
    val userId = "userId"
    val bitmap = mock(Bitmap::class.java)
    val exception = Exception("Upload failed")

    doAnswer {
          val onFailure = it.getArgument<(Exception) -> Unit>(3)
          onFailure(exception)
        }
        .whenever(mockRepository)
        .uploadProfilePicture(eq(userId), eq(bitmap), any(), any())

    var errorOccurred: Exception? = null
    viewModel.uploadProfilePicture(userId, bitmap, {}, { errorOccurred = it })

    assert(errorOccurred == exception)
  }

  @Test
  fun uploadActivityImages_success() {
    val activityId = "activityId"
    val bitmaps = listOf(mock(Bitmap::class.java), mock(Bitmap::class.java))
    val expectedUrls = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg")

    doAnswer {
          val onSuccess = it.getArgument<(List<String>) -> Unit>(2)
          onSuccess(expectedUrls)
        }
        .whenever(mockRepository)
        .uploadActivityImages(eq(activityId), eq(bitmaps), any(), any())

    var resultUrls: List<String>? = null
    viewModel.uploadActivityImages(activityId, bitmaps, { resultUrls = it }, {})

    assert(resultUrls == expectedUrls)
  }

  @Test
  fun fetchProfileImageUrl_success() {
    val userId = "testUserId"
    val expectedUrl = "https://example.com/profile.jpg"

    doAnswer {
          val onSuccess = it.getArgument<(String) -> Unit>(1)
          onSuccess(expectedUrl)
        }
        .whenever(mockRepository)
        .fetchProfileImageUrl(eq(userId), any(), any())

    var resultUrl: String? = null
    viewModel.fetchProfileImageUrl(userId, { resultUrl = it }, {})

    assert(resultUrl == expectedUrl)
  }

  @Test
  fun fetchProfileImageUrl_failure() {
    val userId = "testUserId"
    val exception = Exception("Failed to fetch URL")

    doAnswer {
          val onFailure = it.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
        }
        .whenever(mockRepository)
        .fetchProfileImageUrl(eq(userId), any(), any())

    var errorOccurred: Exception? = null
    viewModel.fetchProfileImageUrl(userId, {}, { errorOccurred = it })

    assert(errorOccurred == exception)
  }

  @Test
  fun deleteProfilePicture_success() {
    val userId = "testUserId"

    doAnswer {
          val onSuccess = it.getArgument<() -> Unit>(1)
          onSuccess()
        }
        .whenever(mockRepository)
        .deleteProfilePicture(eq(userId), any(), any())

    var success = false
    viewModel.deleteProfilePicture(userId, { success = true }, {})

    assert(success)
  }

  @Test
  fun deleteProfilePicture_failure() {
    val userId = "testUserId"
    val exception = Exception("Failed to delete profile picture")

    doAnswer {
          val onFailure = it.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
        }
        .whenever(mockRepository)
        .deleteProfilePicture(eq(userId), any(), any())

    var errorOccurred: Exception? = null
    viewModel.deleteProfilePicture(userId, {}, { errorOccurred = it })

    assert(errorOccurred == exception)
  }

  @Test
  fun fetchActivityImagesAsBitmaps_success() {
    val activityId = "activityId"
    val expectedBitmaps = listOf(mock(Bitmap::class.java), mock(Bitmap::class.java))

    doAnswer {
          val onSuccess = it.getArgument<(List<Bitmap>) -> Unit>(1)
          onSuccess(expectedBitmaps)
        }
        .whenever(mockRepository)
        .fetchActivityImagesAsBitmaps(eq(activityId), any(), any())

    var resultBitmaps: List<Bitmap>? = null
    viewModel.fetchActivityImagesAsBitmaps(activityId, { resultBitmaps = it }, {})

    assert(resultBitmaps == expectedBitmaps)
  }

  @Test
  fun fetchActivityImagesAsBitmaps_failure() {
    val activityId = "activityId"
    val exception = Exception("Failed to fetch images")

    doAnswer {
          val onFailure = it.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
        }
        .whenever(mockRepository)
        .fetchActivityImagesAsBitmaps(eq(activityId), any(), any())

    var errorOccurred: Exception? = null
    viewModel.fetchActivityImagesAsBitmaps(activityId, {}, { errorOccurred = it })

    assert(errorOccurred == exception)
  }

  @Test
  fun removeAllActivityImages_success() {
    val activityId = "activityId"

    doAnswer {
          val onSuccess = it.getArgument<() -> Unit>(1)
          onSuccess()
        }
        .whenever(mockRepository)
        .removeAllActivityImages(eq(activityId), any(), any())

    var success = false
    viewModel.removeAllActivityImages(activityId, { success = true }, {})

    assert(success)
  }

  @Test
  fun removeAllActivityImages_failure() {
    val activityId = "activityId"
    val exception = Exception("Failed to remove images")

    doAnswer {
          val onFailure = it.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
        }
        .whenever(mockRepository)
        .removeAllActivityImages(eq(activityId), any(), any())

    var errorOccurred: Exception? = null
    viewModel.removeAllActivityImages(activityId, {}, { errorOccurred = it })

    assert(errorOccurred == exception)
  }
}
