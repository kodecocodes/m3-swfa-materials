/*
 * Copyright (c) 2026 Kodeco Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 */

package com.kodeco.android.swiftsdkforandroid.taskmanager.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.kodeco.android.swiftsdkforandroid.taskmanager.jni.ImageProcessorJNI
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

object ImageProcessingRepository {
  
  enum class FilterType {
    GRAYSCALE,
    BLUR,
    BRIGHTNESS
  }
  
  fun applyFilter(
    bitmap: Bitmap,
    filterType: FilterType,
    amount: Float = 0f
  ): Bitmap? {
    val width = bitmap.width
    val height = bitmap.height
    
    val pixels = bitmapToRGBAByteArray(bitmap)
    
    val processedPixels = when (filterType) {
      FilterType.GRAYSCALE -> {
        ImageProcessorJNI.applyGrayscaleFilter(pixels, width, height)
      }
      FilterType.BLUR -> {
        val radius = 5
        ImageProcessorJNI.applyBlurFilter(pixels, width, height, radius)
      }
      FilterType.BRIGHTNESS -> {
        ImageProcessorJNI.adjustBrightness(pixels, width, height, amount)
      }
    }
    
    return processedPixels?.let { rgbaByteArrayToBitmap(it, width, height) }
  }
  
  private fun bitmapToRGBAByteArray(bitmap: Bitmap): ByteArray {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
    
    val buffer = ByteBuffer.allocate(width * height * 4)
    
    for (pixel in pixels) {
      buffer.put(((pixel shr 16) and 0xFF).toByte())
      buffer.put(((pixel shr 8) and 0xFF).toByte())
      buffer.put((pixel and 0xFF).toByte())
      buffer.put(((pixel shr 24) and 0xFF).toByte())
    }
    
    return buffer.array()
  }
  
  private fun rgbaByteArrayToBitmap(bytes: ByteArray, width: Int, height: Int): Bitmap {
    val pixels = IntArray(width * height)
    val buffer = ByteBuffer.wrap(bytes)
    
    for (i in pixels.indices) {
      val r = buffer.get().toInt() and 0xFF
      val g = buffer.get().toInt() and 0xFF
      val b = buffer.get().toInt() and 0xFF
      val a = buffer.get().toInt() and 0xFF
      
      pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
    }
    
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    
    return bitmap
  }
}
