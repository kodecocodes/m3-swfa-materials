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

import android.content.Context
import android.graphics.Bitmap
import com.kodeco.android.taskmanagerkit.ImageProcessor
import java.io.File
import java.io.FileOutputStream

object ImageProcessingRepository {
  
  enum class FilterType(val swiftName: String) {
    GRAYSCALE("grayscale"),
    BLUR("blur"),
    BRIGHTER("brighter"),
    DARKER("darker")
  }
  
  fun applyFilter(
    bitmap: Bitmap,
    filterType: FilterType,
    context: Context,
    amount: Float = 0f
  ): Bitmap? {
    try {
      val scaledBitmap = downscaleForProcessing(bitmap, maxDimension = 1024)
      
      val width = scaledBitmap.width
      val height = scaledBitmap.height
      
      val inputFile = File(context.cacheDir, "filter_input_${System.currentTimeMillis()}.rgba")
      FileOutputStream(inputFile).use { out ->
        out.write(width shr 24)
        out.write(width shr 16)
        out.write(width shr 8)
        out.write(width)
        out.write(height shr 24)
        out.write(height shr 16)
        out.write(height shr 8)
        out.write(height)
        
        val pixels = IntArray(width * height)
        scaledBitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        for (pixel in pixels) {
          out.write((pixel shr 16) and 0xFF) // R
          out.write((pixel shr 8) and 0xFF)  // G
          out.write(pixel and 0xFF)          // B
          out.write((pixel shr 24) and 0xFF) // A
        }
      }
      
      val outputFile = File(context.cacheDir, "filter_output_${System.currentTimeMillis()}.rgba")
      
      val success = ImageProcessor.processImageFile(
        inputFile.absolutePath,
        outputFile.absolutePath,
        filterType.swiftName,
        amount
      )
      
      val result = if (success && outputFile.exists()) {
        val bytes = outputFile.readBytes()
        if (bytes.size >= 8) {
          // Read header
          val w = ((bytes[0].toInt() and 0xFF) shl 24) or
                  ((bytes[1].toInt() and 0xFF) shl 16) or
                  ((bytes[2].toInt() and 0xFF) shl 8) or
                  (bytes[3].toInt() and 0xFF)
          val h = ((bytes[4].toInt() and 0xFF) shl 24) or
                  ((bytes[5].toInt() and 0xFF) shl 16) or
                  ((bytes[6].toInt() and 0xFF) shl 8) or
                  (bytes[7].toInt() and 0xFF)
          
          val pixelData = IntArray(w * h)
          var byteIndex = 8
          for (i in pixelData.indices) {
            val r = bytes[byteIndex++].toInt() and 0xFF
            val g = bytes[byteIndex++].toInt() and 0xFF
            val b = bytes[byteIndex++].toInt() and 0xFF
            val a = bytes[byteIndex++].toInt() and 0xFF
            pixelData[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
          }
          
          val resultBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
          resultBitmap.setPixels(pixelData, 0, w, 0, 0, w, h)
          resultBitmap
        } else {
          null
        }
      } else {
        null
      }
      
      inputFile.delete()
      outputFile.delete()
      
      return result
    } catch (e: Exception) {
      e.printStackTrace()
      return null
    }
  }
  
  private fun downscaleForProcessing(bitmap: Bitmap, maxDimension: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    
    if (width <= maxDimension && height <= maxDimension) {
      return bitmap
    }
    
    val scale = if (width > height) {
      maxDimension.toFloat() / width
    } else {
      maxDimension.toFloat() / height
    }
    
    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()
    
    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
  }
}
