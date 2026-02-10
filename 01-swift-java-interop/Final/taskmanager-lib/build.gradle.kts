import java.nio.file.Files

plugins {
    id("com.android.library")
}

android {
    namespace = "com.kodeco.android.taskmanagerkit"
    compileSdk = 35

    defaultConfig {
        minSdk = 28
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("org.swift.swiftkit:swiftkit-core:1.0-SNAPSHOT")
}

// Helper function to get swiftly executable path
fun getSwiftlyPath(): String {
    val fromConfig = project.findProperty("swiftly.path")?.toString() 
        ?: System.getenv("SWIFTLY_PATH")
    if (fromConfig != null) {
        return fromConfig
    }

    // Try to find swiftly in common locations
    val homeDir = System.getProperty("user.home")
    val possiblePaths = listOf(
        "$homeDir/.swiftly/bin/swiftly",
        "$homeDir/.local/share/swiftly/bin/swiftly",
        "$homeDir/.local/bin/swiftly",
        "/usr/local/bin/swiftly",
        "/opt/homebrew/bin/swiftly"
    )

    for (path in possiblePaths) {
        if (file(path).exists()) {
            return path
        }
    }

    throw GradleException("swiftly not found. Please install swiftly or set SWIFTLY_PATH.")
}

fun getSwiftSDKPath(): File {
    val fromConfig = project.findProperty("swift.sdk.path")?.toString()
        ?: System.getenv("SWIFT_SDK_PATH")
    if (fromConfig != null) {
        return file(fromConfig)
    }

    val homeDir = System.getProperty("user.home")
    val possiblePaths = listOf(
        "$homeDir/Library/org.swift.swiftpm/swift-sdks/",
        "$homeDir/.config/swiftpm/swift-sdks/",
        "$homeDir/.swiftpm/swift-sdks/"
    )

    for (path in possiblePaths) {
        if (file(path).exists()) {
            return file(path)
        }
    }

    throw GradleException("Swift SDK path not found. Set swift.sdk.path in gradle.properties.")
}

// List of Swift runtime libraries
val swiftRuntimeLibs = listOf(
    "swiftCore",
    "swift_Concurrency",
    "swift_StringProcessing",
    "swift_RegexParser",
    "swift_Builtin_float",
    "swift_math",
    "swiftAndroid",
    "dispatch",
    "BlocksRuntime",
    "swiftSwiftOnoneSupport",
    "swiftDispatch",
    "Foundation",
    "FoundationEssentials",
    "FoundationInternationalization",
    "_FoundationICU",
    "swiftSynchronization"
)

val sdkName = "swift-6.3-DEVELOPMENT-SNAPSHOT-2026-01-16-a_android.artifactbundle"
val swiftVersion = "6.3-snapshot-2026-01-16"
val minSdk = android.defaultConfig.minSdk ?: 28

// Android ABIs and their Swift triple mappings
val abis = mapOf(
    "arm64-v8a" to mapOf(
        "triple" to "aarch64-unknown-linux-android$minSdk",
        "androidSdkLibDirectory" to "swift-aarch64",
        "ndkDirectory" to "aarch64-linux-android"
    ),
    "armeabi-v7a" to mapOf(
        "triple" to "armv7-unknown-linux-android$minSdk",
        "androidSdkLibDirectory" to "swift-armv7",
        "ndkDirectory" to "arm-linux-android"
    ),
    "x86_64" to mapOf(
        "triple" to "x86_64-unknown-linux-android$minSdk",
        "androidSdkLibDirectory" to "swift-x86_64",
        "ndkDirectory" to "x86_64-linux-android"
    )
)

val generatedJniLibsDir = layout.buildDirectory.dir("generated/jniLibs")
val swiftSdkPath = "${getSwiftSDKPath().absolutePath}/$sdkName"

val buildSwiftAll = tasks.register("buildSwiftAll") {
    group = "build"
    description = "Builds the Swift code for all Android ABIs."

    inputs.file(file("Package.swift"))
    inputs.dir(file("Sources/TaskManagerKit"))

    outputs.dir(layout.buildDirectory.dir("../.build/plugins/outputs/${projectDir.name.toLowerCase()}"))

    val baseSwiftPluginOutputsDir = layout.buildDirectory.dir("../.build/plugins/outputs/").get().asFile
    if (!baseSwiftPluginOutputsDir.exists()) {
        baseSwiftPluginOutputsDir.mkdirs()
    }

    Files.walk(layout.buildDirectory.dir("../.build/plugins/outputs/").get().asFile.toPath()).forEach {
        if (it.toString().endsWith("JExtractSwiftPlugin/src/generated/java")) {
            outputs.dir(it)
        }
    }
}

// Create a build task for each ABI
abis.forEach { (abi, info) ->
    val capitalizedAbi = abi.split("-").joinToString("") { 
        it.replaceFirstChar { char -> char.uppercase() } 
    }
    
    tasks.register<Exec>("buildSwift$capitalizedAbi") {
        group = "build"
        description = "Builds the Swift code for the $abi ABI."

        doFirst {
            println("Building Swift for $abi (${info["triple"]})...")
        }

        outputs.dir(layout.projectDirectory.dir(".build/${info["triple"]}/debug"))

        workingDir = layout.projectDirectory.asFile
        executable = getSwiftlyPath()
        args("run", "swift", "build", "+$swiftVersion", "--swift-sdk", info["triple"]!!)
    }

    buildSwiftAll.configure {
        dependsOn("buildSwift$capitalizedAbi")
    }
}

val copyJniLibs = tasks.register<Copy>("copyJniLibs") {
    dependsOn(buildSwiftAll)

    abis.forEach { (abi, info) ->
        // Copy the built .so files
        from(layout.projectDirectory.dir(".build/${info["triple"]}/debug")) {
            include("*.so")
            into(abi)
        }

        // Copy libc++_shared.so from NDK
        from(file("$swiftSdkPath/swift-android/ndk-sysroot/usr/lib/${info["ndkDirectory"]}/libc++_shared.so")) {
            into(abi)
        }

        doFirst {
            println("Copying Swift runtime libraries for $abi...")
        }

        // Copy the Swift runtime libraries
        swiftRuntimeLibs.forEach { libName ->
            from("$swiftSdkPath/swift-android/swift-resources/usr/lib/${info["androidSdkLibDirectory"]}/android/lib$libName.so") {
                into(abi)
            }
        }
    }

    into(generatedJniLibsDir)
}

// Add the java-swift generated Java sources
android {
    sourceSets {
        getByName("main") {
            java {
                srcDir(buildSwiftAll)
            }

            jniLibs {
                srcDir(generatedJniLibsDir)
            }
        }
    }
}

// Make sure we run our tasks before build
tasks.named("preBuild") {
    dependsOn(copyJniLibs)
}

// Extend clean task to also remove Swift build directory
tasks.named("clean") {
    doLast {
        val buildDir = layout.projectDirectory.dir(".build").asFile
        if (buildDir.exists()) {
            println("Cleaning Swift build directory: ${buildDir.absolutePath}")
            buildDir.deleteRecursively()
        }
    }
}
