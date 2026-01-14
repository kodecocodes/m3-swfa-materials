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

package com.kodeco.android.swiftsdkforandroid.taskmanager.jni

object ImageProcessorJNI {
  init {
    System.loadLibrary("TaskManagerKit")
  }
  
  external fun applyGrayscaleFilter(
    imageData: ByteArray,
    width: Int,
    height: Int
  ): ByteArray?
  
  external fun applyBlurFilter(
    imageData: ByteArray,
    width: Int,
    height: Int,
    radius: Int
  ): ByteArray?
  
  external fun adjustBrightness(
    imageData: ByteArray,
    width: Int,
    height: Int,
    amount: Float
  ): ByteArray?
}
