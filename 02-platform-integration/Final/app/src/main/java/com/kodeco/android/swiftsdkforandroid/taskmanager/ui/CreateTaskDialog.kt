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
 * 
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 * 
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.kodeco.android.swiftsdkforandroid.taskmanager.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kodeco.android.swiftsdkforandroid.taskmanager.R
import com.kodeco.android.taskmanagerkit.Priority
import org.swift.swiftkit.core.SwiftArena
import com.kodeco.android.swiftsdkforandroid.taskmanager.repository.ImageProcessingRepository
import com.kodeco.android.swiftsdkforandroid.taskmanager.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskDialog(
  onDismiss: () -> Unit
) {
  val arena = remember { SwiftArena.ofConfined() }

  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var priority by remember { mutableStateOf(Priority.medium(arena)) }
  var expanded by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  // 1
  var photoUri by remember { mutableStateOf<Uri?>(null) }
  var showCamera by remember { mutableStateOf(false) }
  // 2
  var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var displayedBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var isProcessing by remember { mutableStateOf(false) }
  
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current
  

  val priorities = remember(arena) {
    listOf(
      Priority.low(arena),
      Priority.medium(arena),
      Priority.high(arena)
    )
  }
  
  // 3
  if (showCamera) {
    CameraPermissionHandler(
      onPermissionGranted = {
        CameraScreen(
          onPhotoCaptured = { uri ->
            photoUri = uri
            // 4
            originalBitmap = loadBitmapFromUri(context, uri)
            displayedBitmap = originalBitmap
            showCamera = false
          },
          onDismiss = {
            showCamera = false
          }
        )
      },
      onPermissionDenied = {
        showCamera = false
      }
    )
    return
  }
  

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {

      Text(text = stringResource(R.string.add_task))
    },
    text = {

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text(stringResource(R.string.task_title)) },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text(stringResource(R.string.task_description)) },
          modifier = Modifier.fillMaxWidth(),
          minLines = 3,
          maxLines = 5
        )
        

        ExposedDropdownMenuBox(
          expanded = expanded,
          onExpandedChange = { expanded = !expanded }
        ) {

          OutlinedTextField(
            value = priority.rawValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.task_priority)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
              .menuAnchor(MenuAnchorType.PrimaryNotEditable)
              .fillMaxWidth()
          )
          

          ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
          ) {
            priorities.forEach { option ->

              DropdownMenuItem(
                text = { Text(option.rawValue) },
                onClick = {
                  priority = option
                  expanded = false
                }
              )
            }
          }
        }
        
        // 5
        Button(
          onClick = { showCamera = true },
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp)
          )
          Text("Add Photo")
        }
        
        // 6
        displayedBitmap?.let { bitmap ->
          AsyncImage(
            model = bitmap,
            contentDescription = "Task photo preview",
            modifier = Modifier
              .fillMaxWidth()
              .height(150.dp),
            contentScale = ContentScale.Crop
          )
          
          if (isProcessing) {
            Text(
              text = "Processing...",
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(vertical = 4.dp)
            )
          }
          
          // 7
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // 8
            Button(
              onClick = {
                displayedBitmap = originalBitmap
              }
            ) {
              Text("Original")
            }
            
            // 9
            Button(
              onClick = {
                originalBitmap?.let { original ->
                  isProcessing = true
                  coroutineScope.launch {
                    val result = withContext(Dispatchers.Default) {
                      ImageProcessingRepository.applyFilter(
                        original,
                        ImageProcessingRepository.FilterType.GRAYSCALE,
                        context
                      )
                    }
                    displayedBitmap = result
                    isProcessing = false
                  }
                }
              },
              enabled = !isProcessing
            ) {
              Text("Grayscale")
            }
            
            // 10
            Button(
              onClick = {
                originalBitmap?.let { original ->
                  isProcessing = true
                  coroutineScope.launch {
                    val result = withContext(Dispatchers.Default) {
                      ImageProcessingRepository.applyFilter(
                        original,
                        ImageProcessingRepository.FilterType.BLUR,
                        context
                      )
                    }
                    displayedBitmap = result
                    isProcessing = false
                  }
                }
              },
              enabled = !isProcessing
            ) {
              Text("Blur")
            }
            
            // 11
            Button(
              onClick = {
                originalBitmap?.let { original ->
                  isProcessing = true
                  coroutineScope.launch {
                    val result = withContext(Dispatchers.Default) {
                      ImageProcessingRepository.applyFilter(
                        original,
                        ImageProcessingRepository.FilterType.BRIGHTER,
                        context
                      )
                    }
                    displayedBitmap = result
                    isProcessing = false
                  }
                }
              },
              enabled = !isProcessing
            ) {
              Text("Brighter")
            }
            
            // 12
            Button(
              onClick = {
                originalBitmap?.let { original ->
                  isProcessing = true
                  coroutineScope.launch {
                    val result = withContext(Dispatchers.Default) {
                      ImageProcessingRepository.applyFilter(
                        original,
                        ImageProcessingRepository.FilterType.DARKER,
                        context
                      )
                    }
                    displayedBitmap = result
                    isProcessing = false
                  }
                }
              },
              enabled = !isProcessing
            ) {
              Text("Darker")
            }
          }
        }
        
        
        errorMessage?.let { error ->
          Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
          )
        }
      }
    },
    confirmButton = {

      Button(
        onClick = {
          // 13
          val finalUri = if (displayedBitmap != null && displayedBitmap != originalBitmap) {
            saveBitmapAndGetUri(context, displayedBitmap!!)
          } else {
            photoUri
          }

          // 14
          val result = TaskRepository.addTask(
            title = title,
            description = description,
            priority = priority,
            photoUri = finalUri?.toString()
          )
          

          result.onSuccess {

            onDismiss()
          }.onFailure { error ->

            errorMessage = error.message ?: "Validation failed"
          }
        }
      ) {
        Text(stringResource(R.string.save))
      }
    },
    dismissButton = {

      TextButton(onClick = onDismiss) {
        Text(stringResource(R.string.cancel))
      }
    }
  )
}

// 15
private fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
  return try {
    val bitmap = context.contentResolver.openInputStream(uri)?.use { inputStream ->
      BitmapFactory.decodeStream(inputStream)
    } ?: return null
    
    correctBitmapOrientation(context, uri, bitmap)
  } catch (e: Exception) {
    e.printStackTrace()
    null
  }
}

private fun correctBitmapOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
  return try {
    context.contentResolver.openInputStream(uri)?.use { inputStream ->
      val exif = ExifInterface(inputStream)
      val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
      )
      
      when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
        else -> bitmap
      }
    } ?: bitmap
  } catch (e: Exception) {
    e.printStackTrace()
    bitmap
  }
}

private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
  val matrix = android.graphics.Matrix()
  matrix.postRotate(degrees)
  return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

// 16
private fun saveBitmapAndGetUri(context: Context, bitmap: Bitmap): Uri? {
  return try {
    val photoDir = context.getExternalFilesDir("photos") ?: return null
    val file = File(photoDir, "FILTERED_${System.currentTimeMillis()}.jpg")
    
    FileOutputStream(file).use { out ->
      bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    
    Uri.fromFile(file)
  } catch (e: Exception) {
    e.printStackTrace()
    null
  }
}
