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

import Foundation

// 1
@_cdecl("Java_com_kodeco_android_swiftsdkforandroid_taskmanager_jni_TaskManagerJNI_validateTaskTitle")
public func validateTaskTitle(
  env: UnsafeMutableRawPointer,
  obj: UnsafeMutableRawPointer,
  jTitle: UnsafeMutableRawPointer
) -> Bool {
  // 2
  guard let title = JNIString(env: env, jString: jTitle) else {
    return false
  }
  // 3
  return TaskValidator.validateTitle(title)
}

// 4
@_cdecl("Java_com_kodeco_android_swiftsdkforandroid_taskmanager_jni_TaskManagerJNI_validateTaskDescription")
public func validateTaskDescription(
  env: UnsafeMutableRawPointer,
  obj: UnsafeMutableRawPointer,
  jDescription: UnsafeMutableRawPointer
) -> Bool {
  guard let description = JNIString(env: env, jString: jDescription) else {
    return false
  }
  return TaskValidator.validateDescription(description)
}

// 5
@_cdecl("Java_com_kodeco_android_swiftsdkforandroid_taskmanager_jni_TaskManagerJNI_createTask")
public func createTask(
  env: UnsafeMutableRawPointer,
  obj: UnsafeMutableRawPointer,
  jId: UnsafeMutableRawPointer,
  jTitle: UnsafeMutableRawPointer,
  jDescription: UnsafeMutableRawPointer,
  jPriority: UnsafeMutableRawPointer
) -> Bool {
  // 6
  guard 
    let id = JNIString(env: env, jString: jId),
    let title = JNIString(env: env, jString: jTitle),
    let description = JNIString(env: env, jString: jDescription),
    let priorityStr = JNIString(env: env, jString: jPriority),
    let priority = Task.Priority(rawValue: priorityStr)
  else {
    return false
  }
  
  // 7
  let task = Task(
    id: id,
    title: title,
    description: description,
    priority: priority
  )
  
  // 8
  return TaskManager.shared.addTask(task)
}

// 9
@_cdecl("Java_com_kodeco_android_swiftsdkforandroid_taskmanager_jni_TaskManagerJNI_getAllTasks")
public func getAllTasks(
  env: UnsafeMutableRawPointer,
  obj: UnsafeMutableRawPointer
) -> UnsafeMutableRawPointer? {
  // 10
  let tasks = TaskManager.shared.getTasks()
  let jsonData = try? JSONEncoder().encode(tasks)
  guard let jsonString = jsonData.flatMap({ String(data: $0, encoding: .utf8) }) else {
    return nil
  }
  
  // 11
  return createJNIString(env: env, string: jsonString)
}

// 12
// JNI function type definitions
typealias GetStringUTFCharsFunc = @convention(c) (UnsafeMutableRawPointer?, UnsafeMutableRawPointer?, UnsafeMutablePointer<UInt8>?) -> UnsafePointer<CChar>?
typealias ReleaseStringUTFCharsFunc = @convention(c) (UnsafeMutableRawPointer?, UnsafeMutableRawPointer?, UnsafePointer<CChar>?) -> Void
typealias NewStringUTFFunc = @convention(c) (UnsafeMutableRawPointer?, UnsafePointer<CChar>?) -> UnsafeMutableRawPointer?

private func JNIString(env: UnsafeMutableRawPointer, jString: UnsafeMutableRawPointer) -> String? {
  // This is a simplified JNI string conversion
  // For production apps, use swift-java library for automatic binding generation
  
  // Get JNI function table
  let envPtr = env.assumingMemoryBound(to: UnsafeMutableRawPointer?.self)
  guard let funcTable = envPtr.pointee else { return nil }
  
  // JNI function offsets in the JNINativeInterface struct
  // GetStringUTFChars is at offset 169 (169 * 8 bytes = 1352 bytes)
  let getStringUTFCharsOffset = 169
  let releaseStringUTFCharsOffset = 170
  
  // Get function pointers by offset
  let funcTablePtr = funcTable.assumingMemoryBound(to: UnsafeMutableRawPointer?.self)
  guard let getStringUTFCharsPtr = funcTablePtr.advanced(by: getStringUTFCharsOffset).pointee else { return nil }
  guard let releaseStringUTFCharsPtr = funcTablePtr.advanced(by: releaseStringUTFCharsOffset).pointee else { return nil }
  
  // Cast to proper function types
  let getStringUTFChars = unsafeBitCast(getStringUTFCharsPtr, to: GetStringUTFCharsFunc.self)
  let releaseStringUTFChars = unsafeBitCast(releaseStringUTFCharsPtr, to: ReleaseStringUTFCharsFunc.self)
  
  // Convert Java string to C string
  guard let cString = getStringUTFChars(env, jString, nil) else { return nil }
  
  // Convert C string to Swift string
  let swiftString = String(cString: cString)
  
  // Release the C string
  releaseStringUTFChars(env, jString, cString)
  
  return swiftString
}

// 13
private func createJNIString(env: UnsafeMutableRawPointer, string: String) -> UnsafeMutableRawPointer? {
  // This is a simplified JNI string creation
  // For production apps, use swift-java library
  
  let envPtr = env.assumingMemoryBound(to: UnsafeMutableRawPointer?.self)
  guard let funcTable = envPtr.pointee else { return nil }
  
  // NewStringUTF is at offset 167
  let newStringUTFOffset = 167
  
  let funcTablePtr = funcTable.assumingMemoryBound(to: UnsafeMutableRawPointer?.self)
  guard let newStringUTFPtr = funcTablePtr.advanced(by: newStringUTFOffset).pointee else { return nil }
  
  let newStringUTF = unsafeBitCast(newStringUTFPtr, to: NewStringUTFFunc.self)
  
  // Create Java string from Swift string
  return string.withCString { cString in
    return newStringUTF(env, cString)
  }
}
