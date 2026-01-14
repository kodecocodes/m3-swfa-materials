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

// 9
public class TaskManager: @unchecked Sendable {
  // 10
  private var tasks: [Task] = []
  private let storage = TaskStorage()
  
  // 11
  public static let shared = TaskManager()
  
  // 12
  private init() {
    // Load persisted tasks on initialization
    if case .success(let loadedTasks) = storage.loadTasks() {
      self.tasks = loadedTasks
    }
  }
  

  // 13
  @discardableResult
  public func addTask(_ task: Task) -> Bool {
    guard TaskValidator.validateTitle(task.title),
          TaskValidator.validateDescription(task.description) else {
      return false
    }
    
    tasks.append(task)
    
    // Auto-save after adding
    _ = storage.saveTasks(tasks)
    return true
  }
  
  // 14
  @discardableResult
  public func updateTask(_ updatedTask: Task) -> Bool {
    guard let index = tasks.firstIndex(where: { $0.id == updatedTask.id }) else {
      return false
    }
    
    guard TaskValidator.validateTitle(updatedTask.title),
          TaskValidator.validateDescription(updatedTask.description) else {
      return false
    }
    
    tasks[index] = updatedTask
    
    // Auto-save after updating
    _ = storage.saveTasks(tasks)
    return true
  }
  
  // 15
  @discardableResult
  public func deleteTask(id: String) -> Bool {
    guard let index = tasks.firstIndex(where: { $0.id == id }) else {
      return false
    }
    
    let task = tasks[index]
    
    // 54
    if let photoFilename = task.photoFilename {
      _ = PhotoStorage.deletePhoto(filename: photoFilename)
    }
    
    tasks.remove(at: index)
    
    // Auto-save after deleting
    _ = storage.saveTasks(tasks)
    return true
  }
  
  // 16
  public func getTasks() -> [Task] {
    return tasks
  }
  
  // 17
  public func getTaskCount() -> Int {
    return tasks.count
  }
  
  // 18
  public func clearTasks() {
    tasks.removeAll()
    _ = storage.saveTasks(tasks)
  }
  
  // 55
  public func getPhotoPath(for taskId: String) -> String? {
    guard let task = tasks.first(where: { $0.id == taskId }),
          let filename = task.photoFilename else {
      return nil
    }
    
    return PhotoStorage.photoPath(for: filename)
  }
}
