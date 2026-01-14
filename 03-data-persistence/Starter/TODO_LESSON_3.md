# Lesson 3: Data Persistence & Testing - What You'll Build

## Overview
In this lesson, you'll add complete data persistence to your Task Manager app. You'll learn how to store tasks in JSON format using Swift's file I/O capabilities and implement full CRUD operations across the JNI boundary.

## Features You'll Implement

### 1. File-Based Persistence in Swift
- [ ] Create `TaskStorage.swift` with file I/O operations:
  - `saveTasks()` - Write task array to JSON file
  - `loadTasks()` - Read tasks from JSON file
  - Custom error types for debugging
- [ ] Implement Swift Codable for JSON serialization
- [ ] Use FileManager for file operations in app documents directory

### 2. Enhanced TaskManager
- [ ] Update `TaskManager.swift` to add persistence:
  - Auto-load tasks on initialization
  - Auto-save after every change (add, update, delete)
  - `updateTask()` method for editing existing tasks
  - `deleteTask()` method with file sync
  - Graceful error handling
- [ ] Make TaskManager `Sendable` for Swift 6 concurrency

### 3. CRUD JNI Exports
- [ ] Add new JNI exports in `JNIExports.swift`:
  - `updateTask()` - Edit existing task (7 parameters)
  - `deleteTask()` - Remove task by ID (2 parameters)
  - `getAllTasks()` - Return all tasks as JSON string
- [ ] Implement proper JNI string marshaling

### 4. Kotlin Integration
- [ ] Update `TaskManagerJNI.kt` with external declarations:
  - `external fun updateTask()`
  - `external fun deleteTask()`
  - `external fun getAllTasks()`
- [ ] Enhance `TaskRepository.kt`:
  - `loadTasks()` - Parse JSON and update StateFlow
  - `updateTask()` - Validate and call Swift
  - Complete `deleteTask()` - Call Swift and update state
  - Init block to auto-load on startup

### 5. Edit Task UI
- [ ] Update `CreateTaskDialog.kt` for dual mode:
  - Add optional `editTask` parameter
  - Auto-detect create vs edit mode
  - Dynamic dialog title ("Create Task" vs "Edit Task")
  - Pre-populate fields in edit mode
  - Smart save (calls addTask or updateTask)
  - Load existing photo if present
- [ ] Update `TaskCard.kt`:
  - Add edit button with Icons.Default.Edit
  - Edit callback to open dialog
- [ ] Update `TaskListScreen.kt`:
  - Add `taskToEdit` state variable
  - Pass edit callback to TaskCard
  - Show edit dialog when task selected

### 6. Photo Persistence (Bonus!)
- [ ] Implement cross-platform photo storage:
  - Pass documents path from Kotlin to Swift
  - Save photos as `{taskId}.jpg` in photos directory
  - Load photos by constructing path on app launch
  - Delete photos when tasks are deleted
- [ ] Use byte array approach to avoid file path issues

## Key Learning Goals

- **File Persistence**: Master Swift file I/O with FileManager and Codable
- **JSON Serialization**: Understand encoding/decoding with Swift's Codable protocol
- **Complete CRUD**: Implement full Create, Read, Update, Delete operations
- **Cross-Platform Paths**: Learn iOS vs Android file system differences
- **Production Patterns**: Auto-save on changes, graceful error handling

## Files You'll Create

**New Swift Files:**
- `TaskStorage.swift` - File persistence infrastructure

**Files You'll Modify:**
- `TaskManager.swift` - Add persistence and new CRUD methods
- `JNIExports.swift` - Add updateTask, deleteTask, getAllTasks exports
- `TaskManagerJNI.kt` - Add external function declarations
- `TaskRepository.kt` - Add loadTasks, updateTask, enhanced deleteTask
- `CreateTaskDialog.kt` - Add edit mode support
- `TaskCard.kt` - Add edit button
- `TaskListScreen.kt` - Add edit flow

## Data Architecture

### Storage Format
**tasks.json:**
```json
[
  {
    "id": "uuid-string",
    "title": "Buy groceries",
    "description": "Get milk, eggs, and bread",
    "priority": "Medium",
    "isCompleted": false
  }
]
```

**Location:** `{APP_DOCUMENTS}/tasks.json`

### Data Flow

**App Launch:**
```
App Start → TaskRepository.init → loadTasks()
→ Swift getAllTasks() → TaskStorage.loadTasks()
→ Read JSON → Return to Kotlin → Update UI
```

**Create Task:**
```
User creates → Repository validates
→ Swift addTask() → Append to array
→ TaskStorage.saveTasks() → Write JSON
```

**Edit Task:**
```
User taps edit → Dialog opens (edit mode)
→ Pre-populate fields → User modifies → Save
→ Swift updateTask() → Find by ID → Validate
→ Replace in array → TaskStorage.saveTasks()
```

**Delete Task:**
```
User deletes → Swift deleteTask()
→ Find by ID → Remove from array
→ TaskStorage.saveTasks() → Photo cleanup
```

## Testing Scenarios

### Basic Persistence
1. ✅ Create 3 tasks with different priorities
2. ✅ Close app completely (swipe away from recents)
3. ✅ Reopen app → All 3 tasks should appear

### Edit Functionality
1. ✅ Create task "Buy milk"
2. ✅ Tap edit button
3. ✅ Form pre-filled with existing data
4. ✅ Change title to "Buy almond milk"
5. ✅ Save → Task updated in list

### Validation During Edit
1. ✅ Edit task title to "Ab" (2 chars)
2. ✅ Error: "Title must be between 3 and 50 characters"
3. ✅ Edit to valid title → Success

### Photo Persistence
1. ✅ Create task with photo
2. ✅ Close app completely
3. ✅ Reopen app → Photo should display
4. ✅ Edit task and change photo → New photo persists
5. ✅ Delete task → Photo file cleaned up

## Key Technical Concepts

### Cross-Platform File Paths
**Important:** Swift's `FileManager.SearchPathDirectory.documentDirectory` doesn't work on Android!
- ❌ **Wrong:** Swift tries to determine Android paths
- ✅ **Right:** Kotlin passes `context.filesDir.absolutePath` to Swift
- **Why:** Swift's directory search APIs are iOS-specific
- **Lesson:** Always pass platform-specific paths from Kotlin to Swift

### Auto-Save Pattern
Save on **every change** for simplicity:
- After `addTask()` → save
- After `updateTask()` → save
- After `deleteTask()` → save

Load **once** on app initialization.

### Error Handling
Graceful degradation:
- If load fails → return empty array
- If save fails → print warning, continue
- No crashes, app remains functional

## 📍 Code TODOs

**Throughout the codebase, you'll find TODO comments** marking exactly where to add code. See **`CODE_TODOS.md`** for a complete reference of all TODOs organized by file and phase.

### Quick Reference:
- **Swift files:** TaskStorage.swift (new), TaskManager.swift, JNIExports.swift
- **Kotlin JNI:** TaskManagerJNI.kt
- **Kotlin Repository:** TaskRepository.kt
- **UI files:** CreateTaskDialog.kt, TaskCard.kt, TaskListScreen.kt

Each TODO includes:
- ✅ What needs to be added
- ✅ Why it's needed
- ✅ Often includes example code (commented out)

## Getting Started

1. Open this Starter project in Android Studio
2. Verify the app runs (tasks won't persist yet)
3. **Read `CODE_TODOS.md`** for a complete guide to all TODOs
4. Follow along with the lesson article
5. Look for TODO comments in the code as you work
6. Test persistence after each major section
7. Reference the Final project if you get stuck

## Expected Result

After completing this lesson, your Task Manager app will be **production-ready** with:
- ✅ Full data persistence (survives app restarts)
- ✅ Complete CRUD operations
- ✅ Edit task functionality
- ✅ Photo persistence
- ✅ Clean architecture with proper error handling

Happy coding! 💾
