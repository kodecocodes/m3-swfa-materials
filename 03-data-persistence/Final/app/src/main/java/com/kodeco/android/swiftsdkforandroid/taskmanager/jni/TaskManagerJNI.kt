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


object TaskManagerJNI {
  init {

    System.loadLibrary("TaskManagerKit")
  }


  external fun validateTaskTitle(title: String): Boolean
  

  external fun validateTaskDescription(description: String): Boolean
  

  external fun createTask(
    id: String,
    title: String,
    description: String,
    priority: String
  ): Boolean
  
  // 21
  external fun updateTask(
    id: String,
    title: String,
    description: String,
    priority: String
  ): Boolean
  
  // 22
  external fun deleteTask(id: String): Boolean

  external fun getAllTasks(): String
  
  // 58
  external fun saveTaskPhoto(taskId: String, photoData: ByteArray, documentsPath: String): String?
  
  // 59
  external fun getTaskPhotoPath(taskId: String, documentsPath: String): String?
}
