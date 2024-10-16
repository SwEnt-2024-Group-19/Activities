import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.sample.model.activity.ListActivitiesViewModel
import com.android.sample.ui.activity.CreateActivityScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class CreateActivityScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockViewModel = mockk<ListActivitiesViewModel>()
  private val mockNavigationActions = mock<NavigationActions>()

  @Test
  fun createActivityScreen_displaysTitleField() {
    composeTestRule.setContent { CreateActivityScreen(mockViewModel, mockNavigationActions) }
    composeTestRule.onNodeWithTag("inputTitleCreate").assertExists()
    composeTestRule.onNodeWithTag("inputTitleCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_displaysDescriptionField() {
    composeTestRule.setContent { CreateActivityScreen(mockViewModel, mockNavigationActions) }
    composeTestRule.onNodeWithTag("inputDescriptionCreate").assertExists()
    composeTestRule.onNodeWithTag("inputDescriptionCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_displaysDateField() {
    composeTestRule.setContent { CreateActivityScreen(mockViewModel, mockNavigationActions) }
    composeTestRule.onNodeWithTag("inputDateCreate").assertExists()
    composeTestRule.onNodeWithTag("inputDateCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_displaysPriceField() {
    composeTestRule.setContent { CreateActivityScreen(mockViewModel, mockNavigationActions) }
    composeTestRule.onNodeWithTag("inputPriceCreate").assertExists()
    composeTestRule.onNodeWithTag("inputPriceCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_displaysPlacesLeftField() {
    composeTestRule.setContent { CreateActivityScreen(mockViewModel, mockNavigationActions) }
    composeTestRule.onNodeWithTag("inputPlacesCreate").assertExists()
    composeTestRule.onNodeWithTag("inputPlacesCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_displaysLocationField() {
    composeTestRule.setContent { CreateActivityScreen(mockViewModel, mockNavigationActions) }
    composeTestRule.onNodeWithTag("inputLocationCreate").assertExists()
    composeTestRule.onNodeWithTag("inputLocationCreate").assertIsDisplayed()
  }

  @Test
  fun createActivityScreen_clickCreateButton_navigatesToOverview() {
    composeTestRule.setContent { CreateActivityScreen(mockViewModel, mockNavigationActions) }
    composeTestRule.onNodeWithTag("createButton").assertIsOff()
    composeTestRule.onNodeWithTag("inputTitleCreate").performTextInput("Title")
    composeTestRule.onNodeWithTag("inputDescriptionCreate").performTextInput("Description")
    composeTestRule.onNodeWithTag("inputDateCreate").performTextInput("01/01/2022")
    composeTestRule.onNodeWithTag("inputPriceCreate").performTextInput("100")
    composeTestRule.onNodeWithTag("createButton").assertIsOn()
    composeTestRule.onNodeWithTag("createButton").performClick()
    verify(mockNavigationActions).navigateTo(Screen.OVERVIEW)
  }
}
