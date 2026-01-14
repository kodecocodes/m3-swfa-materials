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

// 1
object TaskManagerJNI {
  init {
    // 2
    System.loadLibrary("TaskManagerKit")
  }

  // 3
  external fun validateTaskTitle(title: String): Boolean
  
  // 4
  external fun validateTaskDescription(description: String): Boolean
  
  // 5
  external fun createTask(
    id: String,
    title: String,
    description: String,
    priority: String
  ): Boolean
  
  // 6
  external fun getAllTasks(): String
}
