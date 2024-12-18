package com.android.sample.ui.map

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.android.sample.model.activity.Category
import com.android.sample.model.activity.CategoryColorMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object MapMarkerUtils {
  private val markerIconCache = mutableMapOf<Category, BitmapDescriptor?>()

  private fun createCustomMarkerIcon(context: Context, category: Category): BitmapDescriptor {
    // Conversion of Compose Color to HSV to get the hue
    val color = CategoryColorMap[category] ?: Color.Red
    val androidColor = color.toArgb()

    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(androidColor, hsv)
    val hue = hsv[0]

    return BitmapDescriptorFactory.defaultMarker(hue)
  }

  fun getCachedMarkerIcon(context: Context, category: Category): BitmapDescriptor {
    return markerIconCache.getOrPut(category) { createCustomMarkerIcon(context, category) }
        ?: createCustomMarkerIcon(context, category)
  }
}
