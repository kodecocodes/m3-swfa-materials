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
  
  // TODO: Add TaskStorage property for file persistence

  public static let shared = TaskManager()
  

  private init() {
    // TODO: Load tasks from file on initialization
  }
  

  @discardableResult
  public func addTask(_ task: Task) -> Bool {

    guard TaskValidator.validateTitle(task.title),
          TaskValidator.validateDescription(task.description) else {
      return false
    }
    

    tasks.append(task)
    
    // TODO: Save tasks to file after adding
    
    return true
  }
  

  public func getTasks() -> [Task] {
    return tasks
  }
  

  public func getTaskCount() -> Int {
    return tasks.count
  }
  

  public func clearTasks() {
    tasks.removeAll()
  }
  
  // TODO: Add updateTask method for editing existing tasks
  
  // TODO: Add deleteTask method for removing tasks
  
  // TODO: Add getTask(by:) method to find a specific task
}
