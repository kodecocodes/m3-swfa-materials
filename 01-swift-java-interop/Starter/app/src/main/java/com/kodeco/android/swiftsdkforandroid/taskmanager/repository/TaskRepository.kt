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

import com.kodeco.android.taskmanagerkit.Priority
import com.kodeco.android.taskmanagerkit.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.swift.swiftkit.core.SwiftArena
import java.util.UUID

object TaskRepository {
  private val _tasks = MutableStateFlow<List<Task>>(emptyList())
  val tasks: StateFlow<List<Task>> = _tasks

  val arena = SwiftArena.ofAuto()

  fun addTask(title: String, description: String, priority: Priority): Result<Unit> {
    // TODO: Integrate Swift validation here


    val task = Task.init(
      UUID.randomUUID().toString(),
      title,
      description,
      priority,
      false,
      arena
    )

    _tasks.value += task
    return Result.success(Unit)
  }

  fun toggleTaskCompletion(taskId: String) {
    _tasks.value = _tasks.value.map { task ->
      if (task.id == taskId) {
        // Use the generated setter
        task.isCompleted = !task.isCompleted
      }
      task
    }
  }

  fun deleteTask(taskId: String) {
    _tasks.value = _tasks.value.filter { it.id != taskId }
  }

  fun clearCompleted() {
    _tasks.value = _tasks.value.filter { !it.isCompleted }
  }
}
