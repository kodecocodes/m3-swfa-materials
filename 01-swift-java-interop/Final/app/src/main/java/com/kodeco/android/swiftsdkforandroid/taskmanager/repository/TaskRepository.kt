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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

// 1
object TaskRepository {
  private val _tasks = MutableStateFlow<List<Task>>(emptyList())
  val tasks: StateFlow<List<Task>> = _tasks
  
  private val arena = SwiftArena.ofAuto()

  // 2
  fun addTask(title: String, description: String, priority: Priority): Result<Unit> {
    // 3 - Validate using swift-java
    if (!TaskValidator.validateTitle(title)) {
      return Result.failure(Exception("Title must be between 3 and 50 characters"))
    }

    // 4
    if (!TaskValidator.validateDescription(description)) {
      return Result.failure(Exception("Description must be between 10 and 200 characters"))
    }

    // 5
    val task = Task.init(
      UUID.randomUUID().toString(),
      title,
      description,
      priority,
      false,
      arena
    )

    // 6 - Add task to memory (Lesson 1: in-memory only, no persistence)
      _tasks.value += task
    return Result.success(Unit)
  }

  // 8
  fun toggleTaskCompletion(taskId: String) {
    _tasks.value = _tasks.value.map { task ->
      if (task.id == taskId) {
        // Use the generated setter
          task.isCompleted = !task.isCompleted
      }
      task
    }
  }

  // 9
  fun deleteTask(taskId: String) {
    _tasks.value = _tasks.value.filter { it.id != taskId }
  }

  // 10
  fun clearCompleted() {
    _tasks.value = _tasks.value.filter { !it.isCompleted }
  }
}
