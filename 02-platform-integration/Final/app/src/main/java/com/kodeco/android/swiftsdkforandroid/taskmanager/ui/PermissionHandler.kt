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

package com.kodeco.android.swiftsdkforandroid.taskmanager.ui

import android.Manifest
import androidx.compose.runtime.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

// 1
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionHandler(
  onPermissionGranted: @Composable () -> Unit,
  onPermissionDenied: @Composable () -> Unit
) {
  // 2
  val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
  
  // 3
  LaunchedEffect(Unit) {
    if (!cameraPermissionState.status.isGranted) {
      cameraPermissionState.launchPermissionRequest()
    }
  }
  
  // 4
  when {
    cameraPermissionState.status.isGranted -> {
      onPermissionGranted()
    }
    else -> {
      onPermissionDenied()
    }
  }
}
