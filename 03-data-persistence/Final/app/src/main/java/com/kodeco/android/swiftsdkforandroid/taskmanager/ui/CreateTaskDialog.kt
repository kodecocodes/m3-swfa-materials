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
import android.graphics.Matrix
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
import com.kodeco.android.swiftsdkforandroid.taskmanager.model.Task
import com.kodeco.android.swiftsdkforandroid.taskmanager.repository.ImageProcessingRepository
import com.kodeco.android.swiftsdkforandroid.taskmanager.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


// 28
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskDialog(
  onDismiss: () -> Unit,
  editTask: Task? = null
) {
  // 29
  val isEditMode = editTask != null
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  
  var title by remember { mutableStateOf(editTask?.title ?: "") }
  var description by remember { mutableStateOf(editTask?.description ?: "") }
  var priority by remember { mutableStateOf(editTask?.priority ?: Task.Priority.Medium) }
  var expanded by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var photoUri by remember { mutableStateOf<String?>(editTask?.photoUri) }
  var showCamera by remember { mutableStateOf(false) }
  
  // 67: Load bitmap for edit mode - handle both URIs and file paths
  val initialBitmap = remember(editTask?.photoUri) {
    editTask?.photoUri?.let { path ->
      if (path.startsWith("content://") || path.startsWith("file://")) {
        loadBitmapFromUri(context, Uri.parse(path))
      } else {
        // It's a file path from Swift storage
        loadBitmapFromFile(path)
      }
    }
  }
  
  var originalBitmap by remember { mutableStateOf<Bitmap?>(initialBitmap) }
  var displayedBitmap by remember { mutableStateOf<Bitmap?>(initialBitmap) }
  var isProcessing by remember { mutableStateOf(false) }
  

  val priorities = Task.Priority.values().toList()
  
  if (showCamera) {
    CameraPermissionHandler(
      onPermissionGranted = {
        CameraScreen(
          onPhotoCaptured = { uri ->
            photoUri = uri.toString()
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
      // 30
      Text(text = if (isEditMode) "Edit Task" else stringResource(R.string.add_task))
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
            value = priority.name,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.task_priority)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
          )
          

          ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
          ) {
            priorities.forEach { option ->

              DropdownMenuItem(
                text = { Text(option.name) },
                onClick = {
                  priority = option
                  expanded = false
                }
              )
            }
          }
        }
        
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
          
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                displayedBitmap = originalBitmap
              }
            ) {
              Text("Original")
            }
            
            Button(
              onClick = {
                originalBitmap?.let { original ->
                  isProcessing = true
                  coroutineScope.launch {
                    val result = withContext(Dispatchers.Default) {
                      ImageProcessingRepository.applyFilter(
                        original,
                        ImageProcessingRepository.FilterType.GRAYSCALE
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
            
            Button(
              onClick = {
                originalBitmap?.let { original ->
                  isProcessing = true
                  coroutineScope.launch {
                    val result = withContext(Dispatchers.Default) {
                      ImageProcessingRepository.applyFilter(
                        original,
                        ImageProcessingRepository.FilterType.BLUR
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
            
            Button(
              onClick = {
                originalBitmap?.let { original ->
                  isProcessing = true
                  coroutineScope.launch {
                    val result = withContext(Dispatchers.Default) {
                      ImageProcessingRepository.applyFilter(
                        original,
                        ImageProcessingRepository.FilterType.BRIGHTNESS,
                        amount = 30f
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
            
            Button(
              onClick = {
                originalBitmap?.let { original ->
                  isProcessing = true
                  coroutineScope.launch {
                    val result = withContext(Dispatchers.Default) {
                      ImageProcessingRepository.applyFilter(
                        original,
                        ImageProcessingRepository.FilterType.BRIGHTNESS,
                        amount = -30f
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
      // 31
      Button(
        onClick = {
          // 63
          val finalPhotoPath = if (displayedBitmap != null) {
            // Save the bitmap (original or filtered) and get the file path
            saveBitmapAndGetPath(context, displayedBitmap!!)
          } else {
            // No photo to save
            null
          }

          // 32
          val result = if (isEditMode && editTask != null) {
            TaskRepository.updateTask(
              id = editTask.id,
              title = title,
              description = description,
              priority = priority,
              photoUri = finalPhotoPath,
              context = context
            )
          } else {
            TaskRepository.addTask(
              title = title,
              description = description,
              priority = priority,
              photoUri = finalPhotoPath,
              context = context
            )
          }
          
          // 33
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

// 65
private fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
  return try {
    val bitmap = context.contentResolver.openInputStream(uri)?.use { inputStream ->
      BitmapFactory.decodeStream(inputStream)
    }
    
    // Fix orientation based on EXIF data
    bitmap?.let { fixBitmapOrientation(context, uri, it) }
  } catch (e: Exception) {
    e.printStackTrace()
    null
  }
}

// 66
private fun fixBitmapOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
  return try {
    context.contentResolver.openInputStream(uri)?.use { inputStream ->
      val exif = ExifInterface(inputStream)
      val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
      )
      
      val matrix = Matrix()
      when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
        else -> return bitmap // No rotation needed
      }
      
      Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } ?: bitmap
  } catch (e: Exception) {
    e.printStackTrace()
    bitmap // Return original if EXIF reading fails
  }
}

// 68
private fun loadBitmapFromFile(filePath: String): Bitmap? {
  return try {
    val file = File(filePath)
    if (file.exists()) {
      BitmapFactory.decodeFile(filePath)
    } else {
      null
    }
  } catch (e: Exception) {
    e.printStackTrace()
    null
  }
}

// 64
private fun saveBitmapAndGetPath(context: Context, bitmap: Bitmap): String? {
  return try {
    val photoDir = context.getExternalFilesDir("temp_photos") ?: return null
    if (!photoDir.exists()) {
      photoDir.mkdirs()
    }
    
    val file = File(photoDir, "TEMP_${System.currentTimeMillis()}.jpg")
    
    FileOutputStream(file).use { out ->
      bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    
    file.absolutePath
  } catch (e: Exception) {
    e.printStackTrace()
    null
  }
}
