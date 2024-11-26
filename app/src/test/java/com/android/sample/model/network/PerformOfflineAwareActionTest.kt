import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.network.NetworkManager
import com.android.sample.resources.C.Tag.OFFLINE_TOAST_MESSAGE
import com.android.sample.ui.components.performOfflineAwareAction
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
class PerformOfflineAwareActionTest {
  @Test
  fun `when online, action is performed`() {
    // Arrange
    val mockContext = mock(Context::class.java)
    val mockNetworkManager = mock(NetworkManager::class.java)
    `when`(mockNetworkManager.isNetworkAvailable()).thenReturn(true)
    val actionPerformed = arrayOf(false)

    // Act
    performOfflineAwareAction(
        context = mockContext,
        networkManager = mockNetworkManager,
        onPerform = { actionPerformed[0] = true })

    // Assert
    assert(actionPerformed[0]) // The action was performed
  }

  @Test
  fun `when offline, toast is shown`() {
    // Arrange
    val context = ApplicationProvider.getApplicationContext<Context>()
    val mockNetworkManager = mock(NetworkManager::class.java)
    `when`(mockNetworkManager.isNetworkAvailable()).thenReturn(false)

    // Act
    performOfflineAwareAction(
        context = context,
        networkManager = mockNetworkManager,
        onPerform = { error("Action should not be performed when offline") })

    // Assert
    val latestToast =
        ShadowToast.getTextOfLatestToast() // Robolectric API to check the latest Toast
    assertEquals(OFFLINE_TOAST_MESSAGE, latestToast)
  }
}
