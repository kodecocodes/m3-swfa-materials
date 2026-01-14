# Lesson 3: Data Persistence & Testing - Starter Project

## Overview

This is the **Starter project** for Lesson 3. It's a fully functional Task Manager app with Swift validation, camera capture, and image processing. In this lesson, you'll add data persistence so tasks survive app restarts.

## What's Included

### ✅ Complete Working App from Lessons 1 & 2

**From Lesson 1:**
- **Swift validation** via JNI for task titles and descriptions
- **Task management** with creation, completion, and deletion
- **Material Design 3 UI** with Jetpack Compose
- **Hybrid architecture** - Swift for business logic, Kotlin for UI
- **Repository pattern** with StateFlow for reactive updates

**From Lesson 2:**
- **Camera integration** with CameraX for photo capture
- **Photo attachment** to tasks with display in task cards
- **Swift image processing** with 5 filters (Original, Grayscale, Blur, Brighter, Darker)
- **JNI byte array marshaling** for image data
- **Background threading** for smooth UI during processing

### 📦 Swift Package (TaskManagerKit)
- **Task model** with Priority enum
- **TaskValidator** with business rules (3-50 char titles, 10-200 char descriptions)
- **TaskManager** singleton managing tasks (in-memory only)
- **ImageProcessor** with filter algorithms (grayscale, blur, brightness)
- **JNI exports** for validation and image processing

### 🎯 What You'll Add in This Lesson

In Lesson 3, you'll implement production-ready data persistence:

1. **File-Based Persistence:**
   - Swift file I/O with FileManager
   - JSON serialization with Codable
   - Save tasks to app documents directory
   - Load tasks on app launch

2. **Full CRUD Operations:**
   - Create tasks (already working)
   - Read tasks from file
   - Update/Edit existing tasks
   - Delete tasks with file sync

3. **Edit Task UI:**
   - Add edit button to task cards
   - Dual-mode dialog (create vs edit)
   - Pre-populate fields for editing
   - Validation during edits

4. **Photo Persistence:**
   - Save photos to disk
   - Persist photo paths across restarts
   - Clean up photos when tasks deleted

## Current Behavior

**Build and run** the app. You'll see:
- ✅ Task list with priority badges
- ✅ Create task dialog with Swift validation
- ✅ Camera capture with permission handling
- ✅ Photo attachment to tasks
- ✅ 5 Swift image filters (Grayscale, Blur, Brighter, Darker, Original)
- ✅ Photos display in task cards
- ✅ Tasks can be toggled complete/incomplete
- ✅ Tasks can be deleted
- ❌ **Tasks lost on app restart** - you'll fix this!

Try the current features:
- **Create task:** Add "Buy groceries" with a photo, apply a filter → ✅ Works
- **Close app:** Swipe away from recents
- **Reopen app:** → ❌ Tasks are gone (in-memory only)

## Expected Behavior After Lesson 3

After completing the lesson, you'll have a production-ready app:
- ✅ Tasks persist across app restarts (JSON file storage)
- ✅ Photos persist across restarts
- ✅ Edit existing tasks (title, description, priority, photo)
- ✅ Full CRUD operations via Swift file I/O
- ✅ Graceful error handling for file operations

## Project Structure

```
03-data-persistence/Starter/
├── app/                                   # Android app
│   ├── src/main/
│   │   ├── java/.../taskmanager/
│   │   │   ├── model/                    # Task.kt (with photoUri), Priority enum
│   │   │   ├── repository/               # TaskRepository, ImageProcessingRepository
│   │   │   ├── jni/                      # TaskManagerJNI, ImageProcessorJNI
│   │   │   ├── ui/                       # Compose UI (list, card, dialog, camera)
│   │   │   └── MainActivity.kt
│   │   ├── jniLibs/                       # Swift .so libraries (all architectures)
│   │   └── res/                          # Android resources
│   └── build.gradle                       # Build config with CameraX, Coil dependencies
├── TaskManagerKit/                        # Swift Package
│   ├── Package.swift                     # SPM manifest
│   └── Sources/TaskManagerKit/
│       ├── Task.swift                    # Task model (in-memory only)
│       ├── TaskValidator.swift           # Validation logic
│       ├── TaskManager.swift             # Singleton manager (in-memory)
│       ├── ImageProcessor.swift          # Image filters
│       └── JNIExports.swift              # JNI bridge functions
├── build.gradle                           # Root build config
├── TODO_LESSON_3.md                      # What you'll build in this lesson
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
- CameraX 1.3.1 for camera capture
- Coil 2.5.0 for async image loading
- Accompanist 0.34.0 for permissions
- StateFlow for reactive UI
- JNI for native integration

**Swift:**
- Swift 6.3 (development snapshot)
- Swift Package Manager
- Foundation framework
- Manual JNI exports
- Pixel manipulation algorithms

**Integration:**
- Custom Gradle task (`buildSwift`)
- JNI string and byte array marshaling
- Type marshaling across boundaries
- Memory management

## Learning Objectives

After Lesson 3, you'll understand:
- How to implement file-based persistence with Swift on Android
- How to use Swift's Codable protocol for JSON serialization
- How to implement full CRUD operations across JNI boundaries
- Cross-platform file path handling (iOS vs Android)
- Production patterns: auto-save, graceful error handling, edit UI flows

## Notes

### Clean Codebase
This Starter project has **no numbered comments**. The code represents production-quality Swift and Kotlin that you can read and understand. In the Final project, you'll add numbered comments ONLY to NEW features (persistence and CRUD operations).

### In-Memory Only (For Now)
Currently, all tasks are stored in-memory:
- Tasks stored in `TaskManager.shared.tasks` array
- Lost when app closes
- No file persistence
- **This is what Lesson 3 will fix!**

### Swift Validation (Working)
All task validation happens in Swift:
- Title: 3-50 characters (trimmed)
- Description: 10-200 characters (trimmed)
- Priority: Must be valid enum value

### Image Processing (Working)
All image filters implemented in Swift:
- Grayscale: RGB to luminance conversion
- Blur: Box blur algorithm (5px radius)
- Brightness: Add/subtract values (±30)
- Processed via JNI byte array marshaling

### Architecture
The hybrid architecture continues:
- **Swift:** Business logic (validation, task management, image processing)
- **Kotlin:** UI (Compose), Android platform APIs (Camera, Storage)
- **JNI:** Bridge for strings, byte arrays, and primitives

In Lesson 3, you'll add file persistence to complete the production-ready app!

### Important: Cross-Platform File Paths
You'll learn a critical lesson: Swift's `FileManager.SearchPathDirectory.documentDirectory` doesn't work on Android! You'll learn to pass platform-specific paths from Kotlin to Swift.

## Next Steps

Ready to start Lesson 3? Check out `TODO_LESSON_3.md` for a complete checklist, then open the lesson article and begin adding persistence! 💾
