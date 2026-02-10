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

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CameraScreen(
  onPhotoCaptured: (Uri) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  
  val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
  var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
  
  Box(modifier = Modifier.fillMaxSize()) {
    AndroidView(
      factory = { ctx ->
        val previewView = PreviewView(ctx)
        
        cameraProviderFuture.addListener({
          val cameraProvider = cameraProviderFuture.get()
          
          val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
          }
          
          val imageCaptureBuilder = ImageCapture.Builder()
          imageCapture = imageCaptureBuilder.build()
          
          val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
          
          try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
              lifecycleOwner,
              cameraSelector,
              preview,
              imageCapture
            )
          } catch (e: Exception) {
            e.printStackTrace()
          }
        }, ContextCompat.getMainExecutor(ctx))
        
        previewView
      },
      modifier = Modifier.fillMaxSize()
    )
    
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      IconButton(
        onClick = onDismiss,
        modifier = Modifier.align(Alignment.End)
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Close camera",
          tint = MaterialTheme.colorScheme.onPrimary
        )
      }
      
      FloatingActionButton(
        onClick = {
          capturePhoto(context, imageCapture) { uri ->
            onPhotoCaptured(uri)
          }
        },
        modifier = Modifier.align(Alignment.CenterHorizontally)
      ) {
        Icon(
          imageVector = Icons.Default.CameraAlt,
          contentDescription = "Capture photo"
        )
      }
    }
  }
}

private fun capturePhoto(
  context: Context,
  imageCapture: ImageCapture?,
  onPhotoCaptured: (Uri) -> Unit
) {
  val photoFile = createPhotoFile(context)
  
  val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
  
  imageCapture?.takePicture(
    outputOptions,
    ContextCompat.getMainExecutor(context),
    object : ImageCapture.OnImageSavedCallback {
      override fun onImageSaved(output: ImageCapture.OutputFileResults) {
        val savedUri = Uri.fromFile(photoFile)
        onPhotoCaptured(savedUri)
      }
      
      override fun onError(exception: ImageCaptureException) {
        exception.printStackTrace()
      }
    }
  )
}

private fun createPhotoFile(context: Context): File {
  val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
  val photoFileName = "TASK_${timeStamp}.jpg"
  val storageDir = context.getExternalFilesDir("photos")
  
  if (storageDir?.exists() == false) {
    storageDir.mkdirs()
  }
  
  return File(storageDir, photoFileName)
}
