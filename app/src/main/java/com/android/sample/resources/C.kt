package com.android.sample.resources

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

    const val STANDARD_PADDING = 8

    const val HALF_SCREEN_TEXT_FIELD_PADDING = 120

    const val DEFAULT_MAX_PRICE = 300f

    const val BUTTON_WIDTH = 300

    const val BUTTON_HEIGHT = 40

    const val LARGE_BUTTON_HEIGHT = 50

    const val MEDIUM_PADDING = 16

    const val LARGE_PADDING = 24

    const val SMALL_PADDING = 4

    const val TITLE_FONTSIZE = 24

    const val TEXT_PADDING = 40

    const val DIALOG_PADDING = 600

    const val SUBTITLE_FONTSIZE = 16

    const val WHITE_COLOR = 0xFFFFFFFF
    const val LIGHT_PURPLE_COLOR = 0xFFD1C4E9
    const val DARK_BLUE_COLOR = 0xFF4A148C

    const val BLACK_COLOR = 0xFF000000
    const val PURPLE_COLOR = 0xFF6200EE

    const val SUCCESS_COLOR = 0xFF048531

    const val IMAGE_SIZE = 100

    const val LARGE_IMAGE_SIZE = 200

    const val SMALL_IMAGE_SIZE = 30

    const val MEDIUM_IMAGE_SIZE = 80

    const val BOTTOM_MENU_HEIGHT = 60

    const val BOTTOM_CORNER = 50

    const val TOP_TITLE_SIZE = 30

    const val TEXT_FONTSIZE = 12

    const val WIDTH_FRACTION = 0.8f

    const val LINE_STROKE = 1

    // errors
    const val OFFLINE_TOAST_MESSAGE = "You are offline. Action not allowed."
  }
}
