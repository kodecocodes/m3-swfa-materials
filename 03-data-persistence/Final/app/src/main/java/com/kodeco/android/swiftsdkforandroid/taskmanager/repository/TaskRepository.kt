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

import com.kodeco.android.taskmanagerkit.Task
import com.kodeco.android.taskmanagerkit.Priority
import org.swift.swiftkit.core.SwiftArena
import com.kodeco.android.taskmanagerkit.TaskValidator
import com.kodeco.android.taskmanagerkit.TaskManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.content.Context
import org.json.JSONArray
import java.io.File
import java.util.Optional
import java.util.UUID

object TaskRepository {
  private val _tasks = MutableStateFlow<List<Task>>(emptyList())
  val tasks: StateFlow<List<Task>> = _tasks
  
  private val arena = SwiftArena.ofAuto()
  private val manager = TaskManager.getShared(arena)

  init {
    loadTasks()
  }
  
  // Call this from MainActivity to properly load photo paths
  fun refreshPhotoPaths(context: Context) {
    val documentsPath = context.filesDir.absolutePath
    _tasks.value = _tasks.value.map { task ->
      // Photo paths managed by Swift PhotoStorage
      // TODO: Implement photo path refresh if needed
      task
    }
  }

  private fun loadTasks() {
    try {
      // Get tasks as JSON from Swift
      val jsonString = TaskManager.getAllTasksJSON()
      val jsonArray = JSONArray(jsonString)
      
      val loadedTasks = mutableListOf<Task>()
      for (i in 0 until jsonArray.length()) {
        val jsonTask = jsonArray.getJSONObject(i)
        val taskId = jsonTask.getString("id")
        
        // Reconstruct photo URI from photoFilename if it exists
        val photoUri = if (jsonTask.has("photoFilename") && !jsonTask.isNull("photoFilename")) {
          val filename = jsonTask.getString("photoFilename")
          // Use dynamic path construction with file:// URI scheme
          // Will be properly resolved after refreshPhotoPaths() is called with Context
          filename // Store just filename for now, will be resolved later
        } else {
          null
        }
        
        // Parse priority string to swift-java Priority
        val priorityString = jsonTask.getString("priority")
        val priority = when (priorityString.lowercase()) {
          "low" -> Priority.low(arena)
          "medium" -> Priority.medium(arena)
          "high" -> Priority.high(arena)
          else -> Priority.medium(arena)
        }
        
        val photoFilename = if (jsonTask.has("photoFilename") && !jsonTask.isNull("photoFilename")) {
          jsonTask.getString("photoFilename")
        } else {
          null
        }
        
        val task = Task.init(
          taskId,
          jsonTask.getString("title"),
          jsonTask.getString("description"),
          priority,
          jsonTask.getBoolean("isCompleted"),
          Optional.ofNullable(photoFilename),
          arena
        )
        loadedTasks.add(task)
      }
      
      _tasks.value = loadedTasks
    } catch (e: Exception) {
      e.printStackTrace()
      _tasks.value = emptyList()
    }
  }

  fun addTask(
    title: String,
    description: String,
    priority: Priority,
    photoUri: String? = null,
    context: android.content.Context? = null
  ): Result<Unit> {
    // Validate using swift-java
    if (!TaskValidator.validateTitle(title)) {
      return Result.failure(Exception("Title must be between 3 and 50 characters"))
    }

    if (!TaskValidator.validateDescription(description)) {
      return Result.failure(Exception("Description must be between 10 and 200 characters"))
    }

    val taskId = UUID.randomUUID().toString()

    // Save photo using file path approach
    val photoFilename = if (photoUri != null && photoUri.isNotEmpty() && context != null) {
      savePhotoViaPath(taskId, photoUri, context)
    } else {
      null
    }
    
    // Create Task with photoFilename
    val task = Task.init(
      taskId,
      title,
      description,
      priority,
      false,
      Optional.ofNullable(photoFilename),
      arena
    )
    
    // Use instance method
    val success = manager.addTask(task)

    if (success) {
      loadTasks()
      return Result.success(Unit)
    } else {
      return Result.failure(Exception("Validation failed"))
    }
  }

  fun updateTask(
    id: String,
    title: String,
    description: String,
    priority: Priority,
    photoUri: String? = null,
    context: android.content.Context? = null
  ): Result<Unit> {
    // Get existing task to preserve isCompleted and photoFilename
    val existingTask = manager.getTask(id, arena).orElse(null) ?: return Result.failure(Exception("Task not found"))
    
    // Save photo if new one provided
    val photoFilename = if (photoUri != null && photoUri.isNotEmpty() && context != null) {
      savePhotoViaPath(id, photoUri, context)
    } else {
      // Keep existing photoFilename (Optional<String>)
      existingTask.getPhotoFilename().orElse(null)
    }
    
    // Create updated Task
    val task = Task.init(
      id,
      title,
      description,
      priority,
      existingTask.isCompleted(),
      Optional.ofNullable(photoFilename),
      arena
    )
    
    // Use instance method
    val success = manager.updateTask(task)
    
    if (success) {
      loadTasks()
      return Result.success(Unit)
    } else {
      return Result.failure(Exception("Update failed"))
    }
  }

  fun toggleTaskCompletion(taskId: String) {
    _tasks.value = _tasks.value.map { task ->
      if (task.id == taskId) {
        task.setCompleted(!task.isCompleted())
      }
      task
    }
  }

  fun deleteTask(taskId: String): Result<Unit> {
    // Use instance method
    val success = manager.deleteTask(taskId)
    
    if (success) {
      loadTasks()
      return Result.success(Unit)
    } else {
      return Result.failure(Exception("Failed to delete task"))
    }
  }

  fun clearCompleted() {
    _tasks.value = _tasks.value.filter { !it.isCompleted }
  }
  
  // Helper function to save photo via file path (pure swift-java!)
  private fun savePhotoViaPath(taskId: String, photoUri: String, context: Context): String? {
    try {
      // Convert photoUri to source file
      val sourceFile = when {
        photoUri.startsWith("file://") -> File(photoUri.removePrefix("file://"))
        photoUri.startsWith("/") -> File(photoUri)
        else -> {
          println("Invalid photo URI format: $photoUri")
          return null
        }
      }
      
      if (!sourceFile.exists()) {
        println("Source photo file doesn't exist: ${sourceFile.absolutePath}")
        return null
      }
      
      // Create temp file in cache dir
      val tempFile = File(context.cacheDir, "temp_photo_$taskId.jpg")
      try {
        // Copy source to temp file
        sourceFile.copyTo(tempFile, overwrite = true)
        
        // Get documents path for Swift
        val documentsPath = context.filesDir.absolutePath
        
        // Call Swift via swift-java with file path!
        val savedPath = TaskManager.savePhotoFromPath(taskId, tempFile.absolutePath, documentsPath)
        
        return savedPath.orElse(null)
      } finally {
        // Clean up temp file
        if (tempFile.exists()) {
          tempFile.delete()
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
      println("Failed to save photo: ${e.message}")
      return null
    }
  }
}
