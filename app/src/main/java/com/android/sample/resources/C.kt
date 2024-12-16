package com.android.sample.resources

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.model.activity.Category

// Like R, but C
object C {
  object Tag {
    const val greeting = "main_screen_greeting"
    const val greeting_robo = "second_screen_greeting"
    const val blank_screen = "blank_screen"

    const val main_screen_container = "main_screen_container"
    const val second_screen_container = "second_screen_container"

    // for textfields
    val ERROR_TEXTFIELD_PADDING_START = 16.dp
    val ERROR_TEXTFIELD_PADDING_TOP = 5.dp
    val ERROR_TEXTFIELD_FONT_SIZE = 12.sp

    const val CREATED_ACTIVITIES = 0
    const val ENROLLED_ACTIVITIES = 1
    const val PAST_ACTIVITIES = 2

    const val STANDARD_PADDING = 8

    const val MEDIUM_FONTSIZE = 18
    const val LARGE_FONTSIZE = 24
    const val ROW_WIDTH = 408

    const val HALF_SCREEN_TEXT_FIELD_PADDING = 120

    const val DEFAULT_MAX_PRICE = 300f

    const val BUTTON_WIDTH = 300

    const val BUTTON_HEIGHT_SM = 40

    const val BUTTON_HEIGHT_MD = 50

    const val AUTH_BUTTON_HEIGHT = 60

    const val LARGE_BUTTON_HEIGHT = 50
    const val LARGE_FONT_WEIGHT = 500
    const val VERY_LARGE_FONT_WEIGHT = 600
    const val MAXIMUM_FONT_WEIGHT = 700
    const val MEDIUM_PADDING = 16
    const val NORMAL_PADDING = 10
    const val BIG_PADDING = 20

    const val LARGE_PADDING = 24

    const val SMALL_PADDING = 4

    const val EXTRA_LARGE_PADDING = 32

    const val TITLE_FONTSIZE = 24

    const val IMAGE_IN_BUTTON_DEFAULT = 24
    const val TEXT_PADDING = 40

    const val DIALOG_PADDING = 600

    const val SUBTITLE_FONTSIZE = 16

    const val WHITE_COLOR = 0xFFFFFFFF
    const val LIGHT_PURPLE_COLOR = 0xFF8B6EA2
    const val DARK_BLUE_COLOR = 0xFF4A148C

    const val BLACK_COLOR = 0xFF000000
    const val PURPLE_COLOR = 0xFF6200EE
    const val DARK_GRAY = 0xFF212121

    const val SUCCESS_COLOR = 0xFF048531

    const val IMAGE_SIZE = 100

    const val LARGE_IMAGE_SIZE = 200

    const val SMALL_IMAGE_SIZE = 30

    const val MEDIUM_IMAGE_SIZE = 80

    const val BOTTOM_MENU_HEIGHT = 60

    const val BOTTOM_CORNER = 50

    const val TOP_TITLE_SIZE = 30

    const val TEXT_FONTSIZE = 12

    const val WIDTH_FRACTION_MD = 0.8f

    const val WIDTH_FRACTION_SM = 0.7f

    const val CARD_ELEVATION_DEFAULT = 4

    const val ROUNDED_CORNER_SHAPE_DEFAULT = 12

    const val LINE_STROKE = 1

    // ANIMATIONS
    const val SPINNING_DURATION = 1500
    const val FADING_DURATION = 1000

    fun colorOfCategory(category: Category): Color {
      val colorCode =
          when (category) {
            Category.SPORT -> 0xFF4CAF50
            Category.SKILLS -> 0xFF2196F3
            Category.CULTURE -> 0xFF9C27B0
            Category.ENTERTAINMENT -> 0xFFFF9800
          }
      return Color(colorCode)
    }

    const val MIN_PASSWORD_LENGTH = 6

    const val BORDER_STROKE_SM = 1
    // errors
    const val OFFLINE_TOAST_MESSAGE = "You are offline. Action not allowed."

    // colors categories
    val SPORT_COLOR = Color(0xFFC0EDAD)
    val CULTURE_COLOR = Color(0xFFD0B0E0)
    val SKILLS_COLOR = Color(0xFFC8E7F2)
    val ENTERTAINMENT_COLOR = Color(0xFFFFCE7A)

    val OUTDOOR_COLOR = Color(0xFFC8E7F2)
    val INDOOR_COLOR = Color(0xFFE8D8C9)
    val CULTURAL_ACTIVITY_COLOR = Color(0xFFFFCE7A)
    val MUSICAL_ACTIVITY_COLOR = Color(0xFFFBF2C4)
    val ART_ACTIVITY_COLOR = Color(0xFFE4BABE)
  }
}
