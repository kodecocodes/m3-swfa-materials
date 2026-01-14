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

import com.kodeco.android.swiftsdkforandroid.taskmanager.model.Task
import com.kodeco.android.swiftsdkforandroid.taskmanager.jni.TaskManagerJNI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONException
import java.io.File
import java.net.URI
import java.util.UUID


object TaskRepository {
  private val _tasks = MutableStateFlow<List<Task>>(emptyList())
  val tasks: StateFlow<List<Task>> = _tasks

  init {
    // 27
    loadTasks()
  }

  // 28
  private fun loadTasks() {
    // Note: loadTasks() is called from init, so we construct the expected path
    // This is safe because Android always uses /data/user/0/{package}/files
    val documentsPath = "/data/user/0/com.kodeco.android.swiftsdkforandroid.taskmanager/files"
    
    try {
      val jsonString = TaskManagerJNI.getAllTasks()
      val jsonArray = JSONArray(jsonString)
      
      val loadedTasks = mutableListOf<Task>()
      for (i in 0 until jsonArray.length()) {
        val jsonTask = jsonArray.getJSONObject(i)
        val taskId = jsonTask.getString("id")
        
        // 60
        val photoPath = TaskManagerJNI.getTaskPhotoPath(taskId, documentsPath)
        
        val task = Task(
          id = taskId,
          title = jsonTask.getString("title"),
          description = jsonTask.getString("description"),
          priority = Task.Priority.valueOf(jsonTask.getString("priority")),
          isCompleted = false,
          photoUri = photoPath
        )
        loadedTasks.add(task)
      }
      
      _tasks.value = loadedTasks
    } catch (e: JSONException) {
      // JSON parsing failed - start with empty list
      _tasks.value = emptyList()
    } catch (e: Exception) {
      // Other errors - start with empty list
      _tasks.value = emptyList()
    }
  }

  fun addTask(
    title: String,
    description: String,
    priority: Task.Priority,
    photoUri: String? = null,
    context: android.content.Context? = null
  ): Result<Unit> {

    if (!TaskManagerJNI.validateTaskTitle(title)) {
      return Result.failure(Exception("Title must be between 3 and 50 characters"))
    }


    if (!TaskManagerJNI.validateTaskDescription(description)) {
      return Result.failure(Exception("Description must be between 10 and 200 characters"))
    }

    val taskId = UUID.randomUUID().toString()

    // 61
    var savedPhotoPath: String? = null
    if (photoUri != null && photoUri.isNotEmpty()) {
      try {
        // Extract file path from URI (handles both file:// and content:// schemes)
        val filePath = if (photoUri.startsWith("file://")) {
          photoUri.removePrefix("file://")
        } else if (photoUri.startsWith("/")) {
          photoUri
        } else {
          null
        }
        
        if (filePath != null) {
          val file = File(filePath)
          
          if (file.exists()) {
            // Read file into byte array
            val photoData = file.readBytes()
            val documentsPath = context?.filesDir?.absolutePath ?: "/data/data/com.kodeco.android.swiftsdkforandroid.taskmanager/files"
            
            val filename = TaskManagerJNI.saveTaskPhoto(taskId, photoData, documentsPath)
            
            if (filename != null && !filename.startsWith("ERROR")) {
              savedPhotoPath = TaskManagerJNI.getTaskPhotoPath(taskId, documentsPath)
            }
          }
        }
      } catch (e: Exception) {
        // Photo save failed - continue without photo
        e.printStackTrace()
      }
    }

    val task = Task(
      id = taskId,
      title = title,
      description = description,
      priority = priority,
      isCompleted = false,
      photoUri = savedPhotoPath
    )


    val success = TaskManagerJNI.createTask(
      id = task.id,
      title = task.title,
      description = task.description,
      priority = task.priority.name
    )


    if (success) {
      _tasks.value = _tasks.value + task
      return Result.success(Unit)
    } else {
      return Result.failure(Exception("Failed to create task in Swift"))
    }
  }


  // 23
  fun updateTask(
    id: String,
    title: String,
    description: String,
    priority: Task.Priority,
    photoUri: String? = null,
    context: android.content.Context? = null
  ): Result<Unit> {
    // Validate with Swift
    if (!TaskManagerJNI.validateTaskTitle(title)) {
      return Result.failure(Exception("Title must be between 3 and 50 characters"))
    }
    
    if (!TaskManagerJNI.validateTaskDescription(description)) {
      return Result.failure(Exception("Description must be between 10 and 200 characters"))
    }
    
    // 62
    var savedPhotoPath: String? = null
    if (photoUri != null && photoUri.isNotEmpty()) {
      try {
        // Extract file path from URI (handles both file:// and content:// schemes)
        val filePath = if (photoUri.startsWith("file://")) {
          photoUri.removePrefix("file://")
        } else if (photoUri.startsWith("/")) {
          photoUri
        } else {
          null
        }
        
        if (filePath != null) {
          val file = File(filePath)
          if (file.exists()) {
            // Read file into byte array
            val photoData = file.readBytes()
            val documentsPath = context?.filesDir?.absolutePath ?: "/data/data/com.kodeco.android.swiftsdkforandroid.taskmanager/files"
            
            val filename = TaskManagerJNI.saveTaskPhoto(id, photoData, documentsPath)
            
            if (filename != null) {
              savedPhotoPath = TaskManagerJNI.getTaskPhotoPath(id, documentsPath)
            }
          }
        }
      } catch (e: Exception) {
        // Photo save failed - keep existing photo
        e.printStackTrace()
        savedPhotoPath = _tasks.value.find { it.id == id }?.photoUri
      }
    } else {
      // No new photo - try to preserve existing
      savedPhotoPath = _tasks.value.find { it.id == id }?.photoUri
    }
    
    // Update in Swift
    val success = TaskManagerJNI.updateTask(
      id = id,
      title = title,
      description = description,
      priority = priority.name
    )
    
    if (success) {
      // Update local state
      _tasks.value = _tasks.value.map { task ->
        if (task.id == id) {
          task.copy(
            title = title,
            description = description,
            priority = priority,
            photoUri = savedPhotoPath
          )
        } else {
          task
        }
      }
      return Result.success(Unit)
    } else {
      return Result.failure(Exception("Failed to update task in Swift"))
    }
  }

  // 24
  fun toggleTaskCompletion(taskId: String) {
    _tasks.value = _tasks.value.map { task ->
      if (task.id == taskId) {
        task.copy(isCompleted = !task.isCompleted)
      } else {
        task
      }
    }
  }

  // 25
  fun deleteTask(taskId: String): Result<Unit> {
    // Delete in Swift
    val success = TaskManagerJNI.deleteTask(taskId)
    
    if (success) {
      // Update local state
      _tasks.value = _tasks.value.filter { it.id != taskId }
      return Result.success(Unit)
    } else {
      return Result.failure(Exception("Failed to delete task in Swift"))
    }
  }

  // 26
  fun clearCompleted() {
    _tasks.value = _tasks.value.filter { !it.isCompleted }
  }
}
