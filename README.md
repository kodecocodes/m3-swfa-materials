# Swift SDK for Android: Materials

This repo contains all the downloadable materials and projects associated with the [Swift SDK for Android](https://www.kodeco.com/ios/paths/multiplatform-swift/53038276-swift-sdk-for-android) module from [Kodeco](https://www.kodeco.com).

## About This Module

Learn how to integrate Swift code into your Android applications using **Swift SDK for Android**! This 3-lesson module teaches you how to build a hybrid Task Manager app that leverages Swift for business logic while maintaining a native Kotlin UI with Jetpack Compose.

### What You'll Build

By the end of this module, you'll have created a fully functional **Task Manager** app that demonstrates real-world Swift-Android integration patterns.

---

## 🚀 Getting Started (For Reviewers)

### Prerequisites

Before building these projects, ensure you have:

- **macOS 13.0+** (Ventura or later) or **Ubuntu 20.04+** / **Debian 12+** 
- **Android Studio** (Iguana 2023.2.1 or later)
- **Android SDK** with API Level 34
- **Android NDK 27**
- **JDK 21** (for daily builds) and **JDK 25** (one-time setup for SwiftKitCore publishing)
- **Git** for cloning the repository

### Step 1: Install Swiftly (Swift Toolchain Manager)

Swiftly is the official Swift toolchain manager that makes it easy to install and manage Swift versions.

#### On macOS:

```bash
curl -L https://swift-server.github.io/swiftly/swiftly-install.sh | bash
```

#### On Linux:

```bash
curl -L https://swift-server.github.io/swiftly/swiftly-install.sh | bash
```

After installation, restart your terminal or run:

```bash
source ~/.local/share/swiftly/env.sh
```

Verify installation:

```bash
swiftly --version
```

### Step 2: Install Swift 6.3 Development Snapshot

Install the Swift 6.3 development snapshot (required for Swift SDK for Android):

```bash
swiftly install 6.3-snapshot
swiftly use 6.3-snapshot
```

Verify Swift installation:

```bash
swift --version
# Should show: Swift version 6.3-dev
```

### Step 3: Install Swift SDK for Android

Install the Swift SDK for Android:

```bash
swift sdk install https://download.swift.org/swift-6.3-branch/android-sdk/swift-6.3-DEVELOPMENT-SNAPSHOT-2026-01-16-a/swift-6.3-DEVELOPMENT-SNAPSHOT-2026-01-16-a_android.artifactbundle.tar.gz --checksum 080da5553cdd12d286f715d86527089e7c924093733f8f4e1195f2bd2137d45c
```

**Note:** Check [swift.org/install](https://www.swift.org/install/) for the latest snapshot URL and checksum if this is outdated.

Verify SDK installation:

```bash
swift sdk list
# Should show: swift-6.3-DEVELOPMENT-SNAPSHOT-2026-01-16-a_android (installed)
```

### Step 4: Install JDKs (for swift-java)

**swift-java requires two JDK versions:**

1. **JDK 21** - For all project builds (daily use)
2. **JDK 25** - For one-time SwiftKitCore publishing

#### Install JDK 21 (Primary)

First install `sdkman` using homebrew

```bash
brew tap sdkman/tap
brew install sdkman-cli
```

After successful installation add the following lines to the end of your `.bash_profile` or `.zshrc`

```bash
export SDKMAN_DIR=$(brew --prefix sdkman-cli)/libexec
[[ -s "${SDKMAN_DIR}/bin/sdkman-init.sh" ]] && source "${SDKMAN_DIR}/bin/sdkman-init.sh"
```

Open a new terminal and type

```bash
sdk version
```

The output should look similar to this

```
SDKMAN!
script: 5.19.0
native: 0.7.4 (macos aarch64)
```

Next, install JDK 21 with sdkman

```bash
# Using sdkman (recommended)
sdk install java 21.0.5-tem
sdk use java 21.0.5-tem

# Set as default
sdk default java 21.0.5-tem

# Verify
java -version  # Should show: openjdk version "21.0.5"
```

#### Install JDK 25 (One-Time Setup)

```bash
# Using sdkman
sdk install java 25.0.1-tem

# Or download from: https://jdk.java.net/25/
```

### Step 5: Set Up Android Studio

1. **Open Android Studio** and install:
   - Android SDK Platform 34
   - Android NDK 27.2.12479018 (via SDK Manager → SDK Tools → NDK)
   - Android SDK Build-Tools 34.0.0

2. **Configure NDK Path:**
   - Open **Preferences**, and search for **Android SDK**
   - Go to **SDK Tools** tab
   - Check **NDK (Side by side)**
   - Note the NDK path (typically `~/Library/Android/sdk/ndk/27.2.12479018`)

3. **Set Environment Variables** (add to `~/.zshrc` or `~/.bash_profile`):

```bash
export ANDROID_HOME=$HOME/Library/Android/sdk
export ANDROID_NDK_HOME=$ANDROID_HOME/ndk/27.2.12479018
export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"
export PATH=$JAVA_HOME/bin:$PATH
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
```

Then reload your shell:

```bash
source ~/.zshrc  # or source ~/.bash_profile
```

### Step 6: Publish SwiftKitCore to Maven (One-Time Setup)

**Required for swift-java integration** - this only needs to be done once per machine:

```bash
# Switch to JDK 25 (required for SwiftKitCore compilation)
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home"
java -version  # Verify: openjdk version "25.0.1"

# Clone swift-java repository
cd ~
git clone https://github.com/swiftlang/swift-java.git
cd swift-java

# Publish SwiftKitCore to local Maven
./gradlew :SwiftKitCore:publishToMavenLocal

# Verify publication
ls ~/.m2/repository/org/swift/swiftkit/swiftkit-core/
# Should show: 1.0-SNAPSHOT/

# Switch back to JDK 21 for all other builds
export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"
java -version  # Verify: openjdk version "21.0.5"
```

**What this does:** Publishes the swift-java core library (SwiftKitCore) to your local Maven repository at `~/.m2/repository`. This is required for JExtractSwiftPlugin to generate Java bindings from Swift code.

**Why JDK 25?** SwiftKitCore requires JDK 25 to compile. After this one-time setup, you'll use JDK 21 for all daily project builds.

### Step 7: Link NDK to Swift SDK

**Critical:** After installing both the Swift SDK and Android NDK, run the setup script to link them:

```bash
cd ~/Library/org.swift.swiftpm || cd ~/.swiftpm
./swift-sdks/swift-6.3-DEVELOPMENT-SNAPSHOT-2026-01-16-a_android.artifactbundle/swift-android/scripts/setup-android-sdk.sh
```

You should see this success message:
```
setup-android-sdk.sh: success: ndk-sysroot linked to Android NDK at android-ndk-r27d/toolchains/llvm/prebuilt
```

**Why this is needed:** The Swift SDK expects the NDK at a specific symlink path. Without this step, builds will fail with "ndk-sysroot not found" or "semaphore.h not found" errors.

### Step 8: Clone and Build the Projects

1. **Clone the repository:**

```bash
git clone https://github.com/kodecocodes/m3-swfa-materials.git
cd m3-swfa-materials
```

2. **Navigate to a project** (e.g., Lesson 1 Final):

```bash
cd 01-swift-java-interop/Final
```

3. **Build Swift code:**

```bash
# Build the Swift library module
./gradlew :taskmanager-lib:buildSwiftAll
```

This task:
- Compiles Swift for 3 architectures (arm64-v8a, armeabi-v7a, x86_64)
- **Auto-generates Java wrapper classes** via JExtractSwiftPlugin
- Generates libTaskManagerKit.so for each architecture
- Copies all libraries to `app/src/main/jniLibs/`
- Includes Swift runtime libraries (~28 files per architecture)

**Generated Java classes location:**
```
taskmanager-lib/.build/plugins/outputs/taskmanagerkit/TaskManagerKit/JExtractSwiftPlugin/
└── src/generated/java/
    ├── Task.java
    ├── Priority.java
    ├── TaskValidator.java
    ├── TaskManager.java
    └── SwiftArena.java
```

These classes are **automatically** included in your Android build - no manual imports needed!

4. **Build the Android app:**

```bash
./gradlew assembleDebug
```

Or open the project in Android Studio and click **Run** ▶️

### Troubleshooting

**Issue:** `Swift SDK not found`
- **Solution:** Run `swift sdk list` to verify SDKs are installed
- Ensure you're using Swift 6.3 snapshot: `swiftly use 6.3-snapshot`

**Issue:** `NDK not found`
- **Solution:** Set `ANDROID_NDK_HOME` environment variable
- Verify NDK is installed: `ls $ANDROID_HOME/ndk/`

**Issue:** `Could not find org.swift:swiftkitcore:1.0-SNAPSHOT`
- **Solution:** Publish SwiftKitCore to Maven (Step 6)
- Verify publication: `ls ~/.m2/repository/org/swift/swiftkitcore/`
- Make sure you used JDK 25 for publishing

**Issue:** `Kotlin compiler requires JDK 21` or version errors
- **Solution:** Ensure you're using JDK 21 for builds:
  ```bash
  export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"
  java -version  # Should show 21.0.5
  ```
- JDK 25 is only for publishing SwiftKitCore (one-time)

**Issue:** `buildSwiftAll task fails`
- **Solution:** Check Swift installation: `swift --version`
- Ensure Swift SDK for Android is installed: `swift sdk list`
- Verify JDK 21 is active: `java -version`

**Issue:** `JExtractSwiftPlugin not generating classes`
- **Solution:** Verify `swift-java.config` exists in Swift Sources directory
- Check Package.swift includes swift-java dependency
- Clean and rebuild: `./gradlew clean :taskmanager-lib:buildSwiftAll`

**Issue:** Build succeeds but app crashes on launch
- **Solution:** Verify correct libraries are in `jniLibs/` folders
- Check that Swift runtime libraries were copied (should be ~29 files per architecture)
- Verify generated Java classes exist in `.build/plugins/outputs/`

### Testing on Device/Emulator

1. **For Emulator:** Use x86_64 image (API 28+)
2. **For Physical Device:** Use ARM64 device (most modern Android phones)
3. **Grant Permissions:** Camera permission required for Lessons 2 & 3

---

## 📦 Project Structure

```
m3-swfa-materials/
├── 01-swift-java-interop/
│   ├── Starter/
│   │   ├── taskmanager-lib/        # Swift library module
│   │   │   ├── build.gradle.kts    # Swift build + JExtract config
│   │   │   ├── Package.swift       # Swift dependencies (includes swift-java)
│   │   │   ├── gradle.properties
│   │   │   └── Sources/TaskManagerKit/
│   │   │       ├── Task.swift
│   │   │       ├── TaskManager.swift
│   │   │       ├── TaskValidator.swift
│   │   │       └── swift-java.config  # JExtract configuration
│   │   ├── app/                    # Android app module
│   │   │   ├── build.gradle.kts    # Depends on :taskmanager-lib
│   │   │   └── src/main/
│   │   │       ├── java/           # Kotlin UI code
│   │   │       └── jniLibs/        # Generated .so files
│   │   ├── build.gradle.kts        # Root build config
│   │   └── settings.gradle.kts     # Multi-module declaration
│   └── Final/                      # (same structure)
├── 02-platform-integration/        # (same structure + image processing)
├── 03-data-persistence/            # (same structure + persistence)
├── images/                         # Screenshots and assets
├── scratch/                        # Development documentation
└── README.md                       # This file
```

**Multi-Module Architecture:**

Each project uses a **multi-module Gradle structure**:

- **taskmanager-lib/** - Swift library module
  - Compiles Swift code for Android
  - Uses **JExtractSwiftPlugin** to auto-generate Java bindings
  - Produces `.so` libraries and Java wrapper classes
  - No manual JNI exports needed!

- **app/** - Android app module
  - Kotlin/Compose UI
  - Imports auto-generated Java classes from `taskmanager-lib`
  - Uses type-safe Swift APIs through generated wrappers

**Key Files:**

- **Package.swift** - Swift dependencies (includes swift-java package)
- **swift-java.config** - Configures Java package name and JNI mode
- **build.gradle.kts** - Gradle build with JExtractSwiftPlugin
- **settings.gradle.kts** - Declares both modules (`app`, `taskmanager-lib`)

---

## Final App Demo

After completing all three lessons, your Task Manager app will feature:

![Task Manager App](images/final-screenshot.png)

**Lesson 1: Swift-Java Interoperability**
- Swift-based validation for task titles (3-50 characters) and descriptions (10-200 characters)
- **swift-java auto-generated bindings** for type-safe Swift-Kotlin interop
- **Zero manual JNI code** - JExtractSwiftPlugin generates Java wrappers automatically
- **SwiftArena memory management** for proper object lifecycle

**Lesson 2: Platform Integration**
- Camera capture using CameraX
- Real-time image processing with Swift filters:
  - Grayscale conversion
  - Blur effects
  - Brightness adjustments
- Photo attachment to tasks

**Lesson 3: Data Persistence & Testing**
- JSON-based task persistence (tasks survive app restarts)
- Photo persistence
- Full CRUD operations (Create, Read, Update, Delete)
- Material Design 3 UI with priority badges

### App Highlights:

- **Hybrid Architecture**: Swift handles business logic and validation, Kotlin handles UI
- **swift-java Integration**: Auto-generated type-safe bindings, zero manual JNI
- **Multi-Module Design**: Clean separation between Swift library and Android app
- **Production Patterns**: Repository pattern, singleton managers, proper error handling
- **Modern UI**: Material Design 3 with Jetpack Compose
- **Real-World Integration**: Demonstrates practical Swift SDK for Android usage

--- 

Each version has its own branch, named `versions/[VERSION]`. The default branch for this repo is for the most recent version.

## Release History

| Branch                                                                                  | Version | Release Date |
| --------------------------------------------------------------------------------------- |:-------:|:------------:|
| [versions/1.0](https://github.com/kodecocodes/m3-swfa-materials/tree/versions/1.0) | 1.0     | 2026-05-31   |
