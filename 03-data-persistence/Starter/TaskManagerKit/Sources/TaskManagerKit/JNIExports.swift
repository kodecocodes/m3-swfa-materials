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


@_cdecl("Java_com_kodeco_android_swiftsdkforandroid_taskmanager_jni_TaskManagerJNI_validateTaskTitle")
public func validateTaskTitle(
  env: UnsafeMutableRawPointer,
  obj: UnsafeMutableRawPointer,
  jTitle: UnsafeMutableRawPointer
) -> Bool {

  guard let title = JNIString(env: env, jString: jTitle) else {
    return false
  }

  return TaskValidator.validateTitle(title)
}


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


@_cdecl("Java_com_kodeco_android_swiftsdkforandroid_taskmanager_jni_TaskManagerJNI_createTask")
public func createTask(
  env: UnsafeMutableRawPointer,
  obj: UnsafeMutableRawPointer,
  jId: UnsafeMutableRawPointer,
  jTitle: UnsafeMutableRawPointer,
  jDescription: UnsafeMutableRawPointer,
  jPriority: UnsafeMutableRawPointer
) -> Bool {

  guard 
    let id = JNIString(env: env, jString: jId),
    let title = JNIString(env: env, jString: jTitle),
    let description = JNIString(env: env, jString: jDescription),
    let priorityStr = JNIString(env: env, jString: jPriority),
    let priority = Task.Priority(rawValue: priorityStr)
  else {
    return false
  }
  

  let task = Task(
    id: id,
    title: title,
    description: description,
    priority: priority
  )
  

  return TaskManager.shared.addTask(task)
}


@_cdecl("Java_com_kodeco_android_swiftsdkforandroid_taskmanager_jni_TaskManagerJNI_getAllTasks")
public func getAllTasks(
  env: UnsafeMutableRawPointer,
  obj: UnsafeMutableRawPointer
) -> UnsafeMutableRawPointer? {

  let tasks = TaskManager.shared.getTasks()
  let jsonData = try? JSONEncoder().encode(tasks)
  guard let jsonString = jsonData.flatMap({ String(data: $0, encoding: .utf8) }) else {
    return nil
  }
  

  return createJNIString(env: env, string: jsonString)
}

// TODO: Add updateTask JNI export to allow editing existing tasks from Kotlin

// TODO: Add deleteTask JNI export to allow deleting tasks from Kotlin


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

typealias GetByteArrayRegionFunc = @convention(c) (UnsafeMutableRawPointer?, UnsafeMutableRawPointer?, Int32, Int32, UnsafeMutablePointer<Int8>?) -> Void
typealias GetArrayLengthFunc = @convention(c) (UnsafeMutableRawPointer?, UnsafeMutableRawPointer?) -> Int32
typealias NewByteArrayFunc = @convention(c) (UnsafeMutableRawPointer?, Int32) -> UnsafeMutableRawPointer?
typealias SetByteArrayRegionFunc = @convention(c) (UnsafeMutableRawPointer?, UnsafeMutableRawPointer?, Int32, Int32, UnsafePointer<Int8>?) -> Void

private func JNIByteArrayToData(env: UnsafeMutableRawPointer, jByteArray: UnsafeMutableRawPointer) -> Data? {
  let envPtr = env.assumingMemoryBound(to: UnsafeMutableRawPointer?.self)
  guard let funcTable = envPtr.pointee else { return nil }
  
  let funcTablePtr = funcTable.assumingMemoryBound(to: UnsafeMutableRawPointer?.self)
  
  // GetArrayLength at offset 171, GetByteArrayRegion at offset 200
  guard let getArrayLengthPtr = funcTablePtr.advanced(by: 171).pointee,
        let getByteArrayRegionPtr = funcTablePtr.advanced(by: 200).pointee else {
    return nil
  }
  
  let getArrayLength = unsafeBitCast(getArrayLengthPtr, to: GetArrayLengthFunc.self)
  let getByteArrayRegion = unsafeBitCast(getByteArrayRegionPtr, to: GetByteArrayRegionFunc.self)
  
  let length = getArrayLength(env, jByteArray)
  guard length > 0 else { return nil }
  
  var bytes = [Int8](repeating: 0, count: Int(length))
  getByteArrayRegion(env, jByteArray, 0, length, &bytes)
  
  return Data(bytes: bytes, count: Int(length))
}

private func dataToJNIByteArray(env: UnsafeMutableRawPointer, data: Data) -> UnsafeMutableRawPointer? {
  let envPtr = env.assumingMemoryBound(to: UnsafeMutableRawPointer?.self)
  guard let funcTable = envPtr.pointee else { return nil }
  
  let funcTablePtr = funcTable.assumingMemoryBound(to: UnsafeMutableRawPointer?.self)
  
  // NewByteArray at offset 176, SetByteArrayRegion at offset 208
  guard let newByteArrayPtr = funcTablePtr.advanced(by: 176).pointee,
        let setByteArrayRegionPtr = funcTablePtr.advanced(by: 208).pointee else {
    return nil
  }
  
  let newByteArray = unsafeBitCast(newByteArrayPtr, to: NewByteArrayFunc.self)
  let setByteArrayRegion = unsafeBitCast(setByteArrayRegionPtr, to: SetByteArrayRegionFunc.self)
  
  guard let jByteArray = newByteArray(env, Int32(data.count)) else { return nil }
  
  data.withUnsafeBytes { (bytes: UnsafeRawBufferPointer) in
    if let baseAddress = bytes.baseAddress {
      let int8Ptr = baseAddress.assumingMemoryBound(to: Int8.self)
      setByteArrayRegion(env, jByteArray, 0, Int32(data.count), int8Ptr)
    }
  }
  
  return jByteArray
}

@_cdecl("Java_com_kodeco_android_swiftsdkforandroid_taskmanager_jni_ImageProcessorJNI_applyGrayscaleFilter")
public func applyGrayscaleFilter(
  env: UnsafeMutableRawPointer,
  obj: UnsafeMutableRawPointer,
  jImageData: UnsafeMutableRawPointer,
  width: Int32,
  height: Int32
) -> UnsafeMutableRawPointer? {
  guard let imageData = JNIByteArrayToData(env: env, jByteArray: jImageData) else {
    return nil
  }
  
  guard let processedData = ImageProcessor.applyGrayscaleFilter(
    imageData: imageData,
    width: width,
    height: height
  ) else {
    return nil
  }
  
  return dataToJNIByteArray(env: env, data: processedData)
}

@_cdecl("Java_com_kodeco_android_swiftsdkforandroid_taskmanager_jni_ImageProcessorJNI_applyBlurFilter")
public func applyBlurFilter(
  env: UnsafeMutableRawPointer,
  obj: UnsafeMutableRawPointer,
  jImageData: UnsafeMutableRawPointer,
  width: Int32,
  height: Int32,
  radius: Int32
) -> UnsafeMutableRawPointer? {
  guard let imageData = JNIByteArrayToData(env: env, jByteArray: jImageData) else {
    return nil
  }
  
  guard let processedData = ImageProcessor.applyBlurFilter(
    imageData: imageData,
    width: width,
    height: height,
    radius: radius
  ) else {
    return nil
  }
  
  return dataToJNIByteArray(env: env, data: processedData)
}

@_cdecl("Java_com_kodeco_android_swiftsdkforandroid_taskmanager_jni_ImageProcessorJNI_adjustBrightness")
public func adjustBrightness(
  env: UnsafeMutableRawPointer,
  obj: UnsafeMutableRawPointer,
  jImageData: UnsafeMutableRawPointer,
  width: Int32,
  height: Int32,
  amount: Float
) -> UnsafeMutableRawPointer? {
  guard let imageData = JNIByteArrayToData(env: env, jByteArray: jImageData) else {
    return nil
  }
  
  guard let processedData = ImageProcessor.adjustBrightness(
    imageData: imageData,
    width: width,
    height: height,
    amount: amount
  ) else {
    return nil
  }
  
  return dataToJNIByteArray(env: env, data: processedData)
}
