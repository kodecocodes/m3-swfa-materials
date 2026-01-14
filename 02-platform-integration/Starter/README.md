# Lesson 2: Platform Integration - Starter Project

## Overview

This is the **Starter project** for Lesson 2. It's a fully functional Task Manager app with Swift-powered validation. In this lesson, you'll add camera integration and image processing capabilities.

## What's Included

### ✅ Complete Working App from Lesson 1
- **Swift validation** via JNI for task titles and descriptions
- **Task management** with creation, completion, and deletion
- **Material Design 3 UI** with Jetpack Compose
- **Hybrid architecture** - Swift for business logic, Kotlin for UI
- **Repository pattern** with StateFlow for reactive updates

### 📦 Swift Package (TaskManagerKit)
- **Task model** with Priority enum
- **TaskValidator** with business rules (3-50 char titles, 10-200 char descriptions)
- **TaskManager** singleton managing tasks
- **JNI exports** bridging Swift and Kotlin

### 🎯 What You'll Add in This Lesson

In Lesson 2, you'll integrate platform-specific features:

1. **Camera Integration:**
   - Request camera permissions
   - Capture photos using CameraX
   - Display photo preview

2. **Photo Storage:**
   - Store photo URIs with tasks
   - Display photos in task cards
   - Full-screen photo view

3. **Image Processing in Swift:**
   - Create image processing functions in Swift
   - Apply filters and transformations
   - Export image operations via JNI
   - Call Swift image processing from Kotlin

## Current Behavior

**Build and run** the app. You'll see:
- ✅ Task list with priority badges
- ✅ Create task dialog with validation
- ✅ Swift validation prevents invalid input
- ✅ Tasks can be toggled complete/incomplete
- ✅ Tasks can be deleted
- ❌ **No photos yet** - you'll add this!

Try creating tasks:
- **Valid:** Title "Buy groceries" (13 chars), Description "Get milk and eggs" (17 chars) → ✅ Success
- **Invalid:** Title "Hi" (2 chars) → ❌ "Title must be between 3 and 50 characters"

## Expected Behavior After Lesson 2

After completing the lesson, you'll be able to:
- ✅ Tap a camera button to capture photos
- ✅ Attach photos to tasks
- ✅ See task photos in the list
- ✅ Apply Swift-based image filters
- ✅ Process images using Swift via JNI

## Project Structure

```
02-platform-integration/Starter/
├── app/                                   # Android app
│   ├── src/main/
│   │   ├── java/.../taskmanager/
│   │   │   ├── model/                    # Task.kt, Priority enum
│   │   │   ├── repository/               # TaskRepository with Swift calls
│   │   │   ├── jni/                      # TaskManagerJNI bridge
│   │   │   ├── ui/                       # Compose UI components
│   │   │   └── MainActivity.kt
│   │   ├── jniLibs/                       # Swift .so libraries
│   │   └── res/                          # Android resources
│   └── build.gradle                       # Build config with buildSwift task
├── TaskManagerKit/                        # Swift Package
│   ├── Package.swift                     # SPM manifest
│   └── Sources/TaskManagerKit/
│       ├── Task.swift                    # Task model
│       ├── TaskValidator.swift           # Validation logic
│       ├── TaskManager.swift             # Singleton manager
│       └── JNIExports.swift              # JNI bridge functions
├── build.gradle                           # Root build config
└── README.md                             # This file
```

## Prerequisites

To build and run this project, you need:

### Swift SDK for Android
- **swiftly** (Swift toolchain manager)
- **Swift 6.3 snapshot**
- **Swift SDK for Android**
- **Android NDK 27**

If you completed Lesson 1, you already have these installed. If not, see the Lesson 1 Starter README for installation instructions.

## Building the Project

### From Android Studio:
1. Open this project in Android Studio
2. Sync Gradle (the `buildSwift` task runs automatically)
3. Build → Run

### From Terminal:
```bash
./gradlew buildSwift     # Compiles Swift for all architectures
./gradlew assembleDebug  # Builds the APK
```

The `buildSwift` Gradle task:
- Compiles Swift for arm64-v8a, armeabi-v7a, and x86_64
- Copies libTaskManagerKit.so to jniLibs
- Includes Swift runtime libraries (28 files)
- Includes NDK C++ library

## Key Technologies

**Android:**
- Kotlin 2.3.0
- Jetpack Compose with Material 3
- StateFlow for reactive UI
- JNI for native integration

**Swift:**
- Swift 6.3 (development snapshot)
- Swift Package Manager
- Foundation framework
- Manual JNI exports

**Integration:**
- Custom Gradle task (`buildSwift`)
- JNI function naming conventions
- Type marshaling across boundaries
- Memory management

## Learning Objectives

After Lesson 2, you'll understand:
- How to integrate Android platform APIs (Camera) with Swift
- How to pass complex data (images) between Kotlin and Swift
- How to process images in Swift and display in Android UI
- How to handle platform-specific features in hybrid apps
- Bidirectional data flow patterns

## Notes

### Clean Codebase
This Starter project has **no numbered comments**. The code represents production-quality Swift and Kotlin that you can read and understand. In the Final project, you'll add numbered comments to NEW features (camera and image processing).

### Swift Validation
All task validation happens in Swift:
- Title: 3-50 characters (trimmed)
- Description: 10-200 characters (trimmed)
- Priority: Must be valid enum value

### Architecture
The hybrid architecture continues:
- **Swift:** Business logic (validation, task management)
- **Kotlin:** UI (Compose), Android platform APIs
- **JNI:** Bridge between the two

In Lesson 2, you'll extend this pattern to include platform integration features!

## Next Steps

Ready to start Lesson 2? Open the lesson article and begin adding camera integration! 📸
