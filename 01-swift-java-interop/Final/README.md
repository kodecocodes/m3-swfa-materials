# Lesson 1: Swift-Java Interoperability - Final Project
## Using Real Swift SDK for Android

**Status:** Complete - Uses real Swift SDK for Android 6.2.3

---

## Overview

This Final project demonstrates **real Swift SDK for Android integration** using:

- ✅ **Swift 6.2.3** (stable release)
- ✅ **Swift Package Manager** for business logic
- ✅ **Custom Gradle task** to build Swift and copy `.so` files
- ✅ **JNI integration** (brief, focused on Swift SDK usage)
- ✅ **All architectures**: arm64-v8a, armeabi-v7a, x86_64, x86

---

## Prerequisites

**⚠️ REQUIRED: You MUST have Swift SDK for Android installed before building this project.**

This is not optional - the project will not build without Swift SDK for Android properly configured. The build will fail with a helpful error message directing you to installation instructions.

### Quick Installation Steps

1. **Install swiftly** (Swift toolchain manager):
   ```bash
   curl -L https://swift-server.github.io/swiftly/swiftly-install.sh | bash
   ```

2. **Install Swift 6.2.3**:
   ```bash
   swiftly install 6.2.3
   swiftly use 6.2.3
   ```

3. **Install Swift SDK for Android**:
   Visit [Swift.org Downloads](https://www.swift.org/install/) and follow instructions for Swift SDK for Android 6.2.3

4. **Configure Android NDK 27d**:
   ```bash
   # Download NDK
   mkdir ~/android-ndk && cd ~/android-ndk
   curl -fSLO https://dl.google.com/android/repository/android-ndk-r27d-$(uname -s).zip
   unzip -q android-ndk-r27d-*.zip
   export ANDROID_NDK_HOME=$PWD/android-ndk-r27d
   ```

5. **Link NDK to Swift SDK**:
   ```bash
   cd ~/Library/org.swift.swiftpm || cd ~/.swiftpm
   ./swift-sdks/[YOUR_SDK]/swift-android/scripts/setup-android-sdk.sh
   ```

6. **Verify installation**:
   ```bash
   swift sdk list
   # Should show Android SDKs
   ```

**Full instructions:** https://www.swift.org/documentation/articles/swift-sdk-for-android-getting-started.html

---

## Project Structure

```
Final/
├── app/                                    # Android application
│   ├── build.gradle                        # Custom buildSwift task
│   └── src/
│       ├── main/
│       │   ├── java/com/kodeco/.../       # Kotlin code
│       │   │   ├── MainActivity.kt        # Loads native library
│       │   │   ├── model/Task.kt
│       │   │   ├── repository/
│       │   │   │   └── TaskRepository.kt  # Calls Swift via JNI
│       │   │   └── ui/                    # Compose UI
│       │   └── jniLibs/                   # Compiled Swift .so files
│       │       ├── arm64-v8a/
│       │       │   └── libTaskManagerKit.so
│       │       ├── armeabi-v7a/
│       │       │   └── libTaskManagerKit.so
│       │       ├── x86_64/
│       │       │   └── libTaskManagerKit.so
│       │       └── x86/
│       │           └── libTaskManagerKit.so
│       └── res/
├── TaskManagerKit/                         # Swift Package
│   ├── Package.swift                       # SPM manifest
│   └── Sources/
│       └── TaskManagerKit/
│           ├── Task.swift                  # Swift Task model
│           ├── TaskValidator.swift         # Validation logic
│           └── TaskManager.swift           # Business logic manager
└── build.gradle                            # Root build file
```

---

## How It Works

### 1. Swift Business Logic

The `TaskManagerKit` Swift Package contains:

**Task.swift** - Task data model with validation
**TaskValidator.swift** - Business rules (title length, due date, etc.)
**TaskManager.swift** - Singleton managing task operations

All Swift code is platform-independent (no Darwin-only APIs).

### 2. Building Swift for Android

The custom `buildSwift` Gradle task:

```gradle
./gradlew buildSwift
```

This task:
1. Runs `swift build --swift-sdk <architecture>` for each target
2. Produces `libTaskManagerKit.so` files
3. Copies them to `app/src/main/jniLibs/<arch>/`

**Supported architectures:**
- `aarch64-unknown-linux-android28` → `arm64-v8a/` (64-bit ARM devices)
- `armv7-unknown-linux-android28` → `armeabi-v7a/` (32-bit ARM devices)
- `x86_64-unknown-linux-android28` → `x86_64/` (64-bit x86 emulator)
- `i686-unknown-linux-android28` → `x86/` (32-bit x86 emulator)

### 3. Automatic Build Integration

The `buildSwift` task runs automatically before Android builds:

```gradle
tasks.named('preBuild').configure {
    dependsOn 'buildSwift'
}
```

So running `./gradlew build` or clicking "Run" in Android Studio automatically compiles Swift!

### 4. JNI Integration (Brief)

**Loading the Library (MainActivity.kt):**
```kotlin
companion object {
    init {
        System.loadLibrary("TaskManagerKit")
    }
}
```

**Calling Swift (TaskRepository.kt):**
The repository uses JNI to call Swift functions. The actual JNI wrappers are generated or can be written manually.

> **Important Note about swift-java:** 
> 
> The [swift-java](https://github.com/swiftlang/swift-java) library can automatically generate JNI bindings between Swift and Kotlin/Java, significantly reducing boilerplate code and maintenance overhead. It's especially valuable for complex APIs with many functions and types.
> 
> This tutorial uses **manual JNI bindings** to teach the fundamental concepts of Swift-Android interoperability. Once you understand these foundations, you can leverage swift-java in production projects to streamline your workflow.

---

## Building the Project

### Method 1: Android Studio

1. Open project in Android Studio
2. Wait for Gradle sync
3. Click **Run** (or press `Shift+F10`)
4. Swift will build automatically, then Android

### Method 2: Command Line

```bash
# Build Swift and Android together
./gradlew build

# Or build Swift separately
./gradlew buildSwift

# Then build Android
./gradlew assembleDebug
```

### Method 3: Manual Swift Build

```bash
cd TaskManagerKit

# Build for ARM64 (physical devices)
swift build --swift-sdk aarch64-unknown-linux-android28 --configuration release

# Build for x86_64 (emulator)
swift build --swift-sdk x86_64-unknown-linux-android28 --configuration release

# Copy .so files manually
cp .build/aarch64-unknown-linux-android28/release/libTaskManagerKit.so \
   ../app/src/main/jniLibs/arm64-v8a/
```

---

## Testing the App

### On Physical Device (ARM64)

1. Connect Android device via USB
2. Enable USB debugging
3. Run from Android Studio
4. Swift validation should work:
   - Empty title → Error
   - Short title (< 3 chars) → Error
   - Past due date → Error
   - Valid data → Task created ✓

### On Emulator (x86_64)

1. Start Android emulator
2. Run from Android Studio
3. Same validation tests apply

---

## Current Features (Lesson 1)

### ✅ Implemented
- Swift SDK for Android integration
- Swift Package Manager structure
- Custom Gradle build task
- Task data model in Swift
- Validation logic in Swift (title, description, priority, due date)
- Singleton TaskManager in Swift
- Kotlin UI with Jetpack Compose
- JNI integration (basic)
- All architectures supported

### ⚠️ Limitations (By Design)
- **No persistence** - Tasks are in-memory only (addressed in Lesson 3)
- **Manual JNI** - Using basic JNI, not swift-java auto-generation (for clarity)
- **No photos** - Photo feature comes in Lesson 2
- **No testing** - Swift unit tests in Lesson 3

---

## Troubleshooting

### Error: "Swift SDK not found"

**Cause:** Swift SDK for Android not installed or not in PATH

**Solution:**
```bash
swift sdk list
# Should show Android SDKs
# If not, reinstall Swift SDK for Android
```

### Error: "Build failed for [architecture]"

**Cause:** Missing Swift SDK for specific architecture

**Solution:** 
The buildSwift task will skip unavailable architectures automatically. You need at least one working (arm64-v8a for devices OR x86_64 for emulator).

### Error: "Library not found: TaskManagerKit"

**Cause:** `.so` files not in `jniLibs/`

**Solution:**
```bash
./gradlew buildSwift
# Check that .so files appear in app/src/main/jniLibs/
```

### Error: "UnsatisfiedLinkError"

**Cause:** JNI method signature mismatch

**Solution:** Verify JNI wrapper signatures match Swift exports. Use `swift-java` for production.

---

## Key Differences from Starter

| Aspect | Starter | Final |
|--------|---------|-------|
| Swift Package | Empty stubs | Complete implementation |
| buildSwift task | Commented out | Fully functional |
| jniLibs/ | Empty | Contains .so files |
| JNI integration | Stubbed | Working |
| Validation | Client-side only | Swift backend |
| Task creation | Non-functional | Fully functional |

---

## Learning Objectives Achieved

By studying this Final project, students learn:

1. ✅ How to structure Swift Packages for Android
2. ✅ Cross-compilation with Swift SDK for Android
3. ✅ Building Swift libraries as `.so` files
4. ✅ Gradle integration with Swift builds
5. ✅ JNI basics for Swift-Kotlin communication
6. ✅ Real-world hybrid app architecture

---

## Next Steps (Lesson 2)

Lesson 2 will add:
- Camera integration
- Photo capture in Kotlin
- Image processing in Swift
- Bidirectional binary data flow (passing image bytes)
- Photo storage

---

## References

- **Official docs:** https://www.swift.org/documentation/articles/swift-sdk-for-android-getting-started.html
- **Swift Android examples:** https://github.com/swift-android/swift-android-examples
- **swift-java library:** https://github.com/swiftlang/swift-java
- **Swift forums:** https://forums.swift.org/c/development/android/

---

## Notes

- This project uses **Swift 6.2.3** (stable) instead of development snapshots
- All code follows **Kodeco style guide**
- Comments are numbered (`// 1`, `// 2`) matching article explanations
- Focus is on **Swift SDK usage**, not deep JNI details
- Project is **production-ready** pattern, scaled for education

---

**Ready to learn?** Open the article and follow along! 🚀
