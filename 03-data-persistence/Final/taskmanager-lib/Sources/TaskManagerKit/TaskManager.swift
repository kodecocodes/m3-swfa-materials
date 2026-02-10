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

public class TaskManager: @unchecked Sendable {
  private var tasks: [Task] = []
  private let storage = TaskStorage()
  
  public static let shared = TaskManager()
  
  // Accessor for Java/Kotlin (swift-java doesn't expose static properties directly)
  public static func getShared() -> TaskManager {
    return shared
  }
  
  private init() {
    if case .success(let loadedTasks) = storage.loadTasks() {
      self.tasks = loadedTasks
    }
  }
  

  @discardableResult
  public func addTask(_ task: Task) -> Bool {
    guard TaskValidator.validateTitle(task.title),
          TaskValidator.validateDescription(task.description) else {
      return false
    }
    
    tasks.append(task)
    
    _ = storage.saveTasks(tasks)
    return true
  }
  
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
    
    _ = storage.saveTasks(tasks)
    return true
  }
  
  @discardableResult
  public func deleteTask(id: String) -> Bool {
    guard let index = tasks.firstIndex(where: { $0.id == id }) else {
      return false
    }
    
    let task = tasks[index]
    
    if let photoFilename = task.photoFilename {
      _ = PhotoStorage.deletePhoto(filename: photoFilename)
    }
    
    tasks.remove(at: index)
    
    _ = storage.saveTasks(tasks)
    return true
  }
  
  public func getTasks() -> [Task] {
    return tasks
  }
  
  public func getTaskCount() -> Int64 {
    return Int64(tasks.count)
  }
  
  public func clearTasks() {
    tasks.removeAll()
    _ = storage.saveTasks(tasks)
  }
  
  public func getTask(by id: String) -> Task? {
    return tasks.first(where: { $0.id == id })
  }
  
  public func getPhotoPath(for taskId: String, documentsPath: String) -> String? {
    guard let task = tasks.first(where: { $0.id == taskId }),
          let filename = task.photoFilename else {
      return nil
    }
    
    return PhotoStorage.photoPath(for: filename, documentsPath: documentsPath)
  }
  
  public static func getAllTasksJSON() -> String {
    let encoder = JSONEncoder()
    guard let jsonData = try? encoder.encode(shared.tasks),
          let jsonString = String(data: jsonData, encoding: .utf8) else {
      return "[]"
    }
    return jsonString
  }
  
  public static func savePhotoFromPath(taskId: String, sourcePath: String, documentsPath: String) -> String? {
    let fileURL = URL(fileURLWithPath: sourcePath)
    
    guard let photoData = try? Data(contentsOf: fileURL) else {
      print("Failed to read photo from path: \(sourcePath)")
      return nil
    }
    
    let filename = "\(taskId).jpg"
    
    switch PhotoStorage.savePhoto(data: photoData, withFilename: filename, documentsPath: documentsPath) {
    case .success(let path):
      return filename
    case .failure(let error):
      print("Failed to save photo: \(error)")
      return nil
    }
  }
}
