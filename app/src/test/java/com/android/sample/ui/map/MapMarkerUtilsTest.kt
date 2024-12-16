package com.android.sample.ui.map

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.android.sample.model.activity.Category
import com.android.sample.model.activity.CategoryColorMap
import org.junit.Test

class MapMarkerUtilsTest {

  @Test
  fun testColorToHsvConversionForAllCategories() {
    Category.values().forEach { category ->
      val color = CategoryColorMap[category] ?: Color.Red
      val androidColor = color.toArgb()

      val hsv = FloatArray(3)
      android.graphics.Color.colorToHSV(androidColor, hsv)

      assert(hsv[0] in 0f..360f) { "Hue should be between 0 and 360 for category $category" }
    }
  }

  @Test
  fun testAllCategoriesHaveColors() {
    Category.values().forEach { category ->
      val color = CategoryColorMap[category]
      assert(color != null) { "Category $category should have a color defined" }
    }
  }

  @Test
  fun testAllCategoriesHaveDistinctColors() {
    val colors = Category.values().mapNotNull { CategoryColorMap[it] }
    val uniqueColors = colors.toSet()
    assert(colors.size == uniqueColors.size) { "All categories should have distinct colors" }
  }

  @Test
  fun testColorValuesAreNotTransparent() {
    Category.values().forEach { category ->
      val color = CategoryColorMap[category] ?: Color.Red
      val alpha = color.alpha
      assert(alpha == 1f) { "Color for category $category should not be transparent" }
    }
  }
}
