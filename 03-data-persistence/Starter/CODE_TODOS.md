# Code TODO Reference - Lesson 3

This document lists all the TODO comments placed throughout the codebase to guide you in implementing persistence and CRUD operations. Each TODO marks a specific location where you'll add code as you follow the lesson.

## 📦 Swift Files

### 1. TaskStorage.swift (NEW FILE TO CREATE)

**Location:** `TaskManagerKit/Sources/TaskManagerKit/TaskStorage.swift`

**What to create:** A new Swift file with:
- `saveTasks()` method - Write tasks to JSON file
- `loadTasks()` method - Read tasks from JSON file  
- File URL management using FileManager
- Error handling with do-catch blocks

**Key Learning:** Swift FileManager on Android has limitations - the lesson will teach you the correct cross-platform approach.

---

### 2. TaskManager.swift

**Location:** `TaskManagerKit/Sources/TaskManagerKit/TaskManager.swift`

#### TODOs:

**Line ~20:** Add TaskStorage property for file persistence

**Line ~26:** Load tasks from file on initialization  
- Call `storage.loadTasks()` in the init method

**Line ~41:** Save tasks to file after adding  
- Call `storage.saveTasks(tasks)` after appending task

**Line ~58:** Add updateTask method for editing existing tasks  
- Find task by ID, validate, replace in array, save to file

**Line ~68:** Add deleteTask method for removing tasks  
- Find task by ID, remove from array, save to file

**Line ~76:** Add getTask(by:) method to find a specific task  
- Use `first(where:)` to find task by ID

---

### 3. JNIExports.swift

**Location:** `TaskManagerKit/Sources/TaskManagerKit/JNIExports.swift`

#### TODOs:

**Line ~95:** Add updateTask JNI export to allow editing existing tasks from Kotlin  
- Function name: `Java_com_kodeco_android_swiftsdkforandroid_taskmanager_jni_TaskManagerJNI_updateTask`
- Parameters: env, obj, jId, jTitle, jDescription, jPriority
- Returns: Bool

**Line ~112:** Add deleteTask JNI export to allow deleting tasks from Kotlin  
- Function name: `Java_com_kodeco_android_swiftsdkforandroid_taskmanager_jni_TaskManagerJNI_deleteTask`
- Parameters: env, obj, jId
- Returns: Bool

---

## 📱 Kotlin Files

### 4. TaskManagerJNI.kt

**Location:** `app/src/main/java/.../jni/TaskManagerJNI.kt`

#### TODOs:

**Line ~41:** Add external function for updating tasks  
- Signature: `external fun updateTask(id: String, title: String, description: String, priority: String): Boolean`

**Line ~48:** Add external function for deleting tasks  
- Signature: `external fun deleteTask(id: String): Boolean`

---

### 5. TaskRepository.kt

**Location:** `app/src/main/java/.../repository/TaskRepository.kt`

#### TODOs:

**Line ~8:** Add org.json imports for parsing JSON from Swift  
- `import org.json.JSONArray`
- `import org.json.JSONObject`

**Line ~15:** Add init block to load tasks on startup  
- Call `loadTasks()` when repository is initialized

**Line ~87:** Call Swift to delete task from file  
- Add `TaskManagerJNI.deleteTask(taskId)` before updating state

**Line ~95:** Add loadTasks method to restore persisted tasks from JSON file  
- Call `TaskManagerJNI.getAllTasks()` to get JSON string
- Parse JSON with `JSONArray` and `JSONObject`
- Map to Task objects and update StateFlow
- Handle errors gracefully (return empty list)

**Line ~125:** Add updateTask method for editing existing tasks  
- Validate title and description
- Call `TaskManagerJNI.updateTask()` 
- Update local state with new task data
- Return Result<Unit> for success/failure

---

### 6. TaskCard.kt

**Location:** `app/src/main/java/.../ui/TaskCard.kt`

#### TODOs:

**Line ~5:** Add Icons import for edit button  
- `import androidx.compose.material.icons.Icons`
- `import androidx.compose.material.icons.filled.Edit`

**Line ~11:** Add Alignment import for edit button layout  
- `import androidx.compose.ui.Alignment`

**Line ~17:** Add onEdit callback parameter  
- `onEdit: (Task) -> Unit = {}`

**Line ~91:** Add Row with priority badge and edit button  
- Create Row with `verticalAlignment = Alignment.CenterVertically`
- PriorityBadge on left
- Spacer with `Modifier.weight(1f)` to push right
- IconButton with `Icons.Default.Edit` that calls `onEdit(task)`

---

### 7. TaskListScreen.kt

**Location:** `app/src/main/java/.../ui/TaskListScreen.kt`

#### TODOs:

**Line ~60:** Add state for edit mode  
- `var taskToEdit by remember { mutableStateOf<Task?>(null) }`

**Line ~107:** Add onEdit callback to TaskCard  
- Pass `onEdit = { taskToEdit = it }` to TaskCard

**Line ~121:** Add edit dialog when task is selected for editing  
- Use `taskToEdit?.let { }` to conditionally show CreateTaskDialog
- Pass `editTask = task` parameter
- Reset `taskToEdit = null` in onDismiss

---

### 8. CreateTaskDialog.kt

**Location:** `app/src/main/java/.../ui/CreateTaskDialog.kt`

#### TODOs:

**Line ~68:** Add editTask parameter for edit mode  
- `editTask: Task? = null`

**Line ~71:** Detect edit mode  
- `val isEditMode = editTask != null`

**Line ~73:** Pre-populate fields in edit mode  
- Use `editTask?.title ?: ""` for initial title value
- Use `editTask?.description ?: ""` for description
- Use `editTask?.priority ?: Task.Priority.Medium` for priority

**Line ~82:** Load existing photo in edit mode  
- `var photoUri by remember { mutableStateOf(editTask?.photoUri?.let { Uri.parse(it) }) }`

**Line ~88:** Load existing photo bitmap in edit mode  
- Use `editTask?.photoUri` to load existing photo with `loadBitmapFromUri()`
- Set both originalBitmap and displayedBitmap

**Line ~125:** Make title dynamic based on edit mode  
- Show "Edit Task" when editing, "Add Task" when creating

**Line ~337:** Handle both create and edit modes  
- Use if/else to call `updateTask()` when editing or `addTask()` when creating

---

## 📋 Implementation Order

Follow this recommended order:

### Phase 1: File Persistence (Swift)
1. ✅ Create `TaskStorage.swift` with save/load methods
2. ✅ Add `TaskStorage` property to `TaskManager`
3. ✅ Call `loadTasks()` in `TaskManager.init`
4. ✅ Call `saveTasks()` after `addTask()`
5. ✅ Test: Tasks should persist across app restarts

### Phase 2: Update & Delete Methods (Swift)
1. ✅ Add `updateTask()` to `TaskManager`
2. ✅ Add `deleteTask()` to `TaskManager`
3. ✅ Add `getTask(by:)` to `TaskManager`
4. ✅ Add JNI exports for update and delete in `JNIExports.swift`

### Phase 3: Kotlin Integration
1. ✅ Add `org.json` imports to `TaskRepository`
2. ✅ Add external declarations to `TaskManagerJNI`
3. ✅ Add `init` block with `loadTasks()` to `TaskRepository`
4. ✅ Add `updateTask()` method to `TaskRepository`
5. ✅ Update `deleteTask()` to call Swift
6. ✅ Test: CRUD operations work via Swift

### Phase 4: Edit UI
1. ✅ Add `onEdit` parameter to `TaskCard`
2. ✅ Add edit button UI to `TaskCard`
3. ✅ Add `taskToEdit` state to `TaskListScreen`
4. ✅ Pass `onEdit` callback to `TaskCard`
5. ✅ Add `editTask` parameter to `CreateTaskDialog`
6. ✅ Pre-populate fields in edit mode
7. ✅ Handle both create and update in save button
8. ✅ Show edit dialog when task selected
9. ✅ Test: Edit flow works end-to-end

---

## 🎯 Testing Checklist

After implementing all TODOs:

- [ ] **Persistence Test:** Create tasks → Close app → Reopen → Tasks appear
- [ ] **Edit Test:** Tap edit button → Modify task → Save → Changes persist
- [ ] **Delete Test:** Delete task → Close app → Reopen → Task is gone
- [ ] **Validation Test:** Edit task with invalid data → Error shown
- [ ] **Photo Test:** Create task with photo → Close app → Reopen → Photo displays

---

## 💡 Tips

- **Follow the order:** Implement TODOs in the recommended phase order
- **Build frequently:** After each phase, build and test
- **Reference Final:** If stuck, check the Final project for comparison
- **Read comments:** Each TODO has context about what it does
- **Cross-platform files:** Remember Swift FileManager works differently on Android!

---

Happy coding! 💾
