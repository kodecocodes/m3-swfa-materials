# Lesson 2: Platform Integration - What You'll Build

## Overview
In this lesson, you'll add camera capture and Swift-powered image processing to your Task Manager app. You'll learn how to integrate native Android camera APIs with Swift image filters.

## Features You'll Implement

### 1. Camera Integration
- [ ] Add CameraX dependencies to `app/build.gradle`
- [ ] Add camera permission to `AndroidManifest.xml`
- [ ] Create `CameraScreen.kt` with CameraX preview and capture
- [ ] Create `PermissionHandler.kt` for runtime permission requests

### 2. Swift Image Processing
- [ ] Create `ImageProcessor.swift` with filter algorithms:
  - Grayscale filter (RGB to B&W conversion)
  - Blur filter (box blur algorithm)
  - Brightness adjustment (add/subtract brightness values)
- [ ] Export image processing functions via JNI in `JNIExports.swift`

### 3. Kotlin Integration
- [ ] Create `ImageProcessorJNI.kt` with external function declarations
- [ ] Create `ImageProcessingRepository.kt` with Bitmap conversion
- [ ] Update `CreateTaskDialog.kt` to add camera capture button
- [ ] Add filter buttons (Original, Grayscale, Blur, Brighter, Darker)

### 4. UI Enhancements
- [ ] Update `Task.kt` model to include `photoUri` field
- [ ] Update `TaskCard.kt` to display task photos
- [ ] Handle photo display with Coil AsyncImage library

## Key Learning Goals

- **Platform Integration**: Learn how to integrate Android platform APIs (CameraX) with Swift
- **Image Processing**: Understand pixel manipulation algorithms in Swift
- **JNI Data Passing**: Master passing byte arrays across the JNI boundary
- **Background Threading**: Use Kotlin coroutines for image processing

## Files You'll Create

**New Kotlin Files:**
- `ui/CameraScreen.kt` - CameraX camera preview and capture
- `ui/PermissionHandler.kt` - Runtime permission wrapper
- `jni/ImageProcessorJNI.kt` - JNI declarations for image processing
- `repository/ImageProcessingRepository.kt` - Bitmap conversion and filter API

**New Swift Files:**
- `ImageProcessor.swift` - Image filter implementations

**Files You'll Modify:**
- `app/build.gradle` - Add CameraX and Coil dependencies
- `AndroidManifest.xml` - Add camera permission
- `Task.kt` - Add photoUri property
- `TaskCard.kt` - Display photos
- `CreateTaskDialog.kt` - Add camera button and filter UI
- `TaskRepository.kt` - Handle photo URIs
- `JNIExports.swift` - Add image processing exports

## Getting Started

1. Open this Starter project in Android Studio
2. Follow along with the lesson article
3. Reference the Final project if you get stuck
4. Test each feature as you build it

Happy coding! 📸
