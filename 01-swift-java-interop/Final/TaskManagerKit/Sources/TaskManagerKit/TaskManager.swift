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

// 1
public class TaskManager: @unchecked Sendable {
  // 2
  private var tasks: [Task] = []
  
  // 3
  public static let shared = TaskManager()
  
  // 4
  private init() {}
  
  // 5
  @discardableResult
  public func addTask(_ task: Task) -> Bool {
    // 6
    guard TaskValidator.validateTitle(task.title),
          TaskValidator.validateDescription(task.description) else {
      return false
    }
    
    // 7
    tasks.append(task)
    return true
  }
  
  // 8
  public func getTasks() -> [Task] {
    return tasks
  }
  
  // 9
  public func getTaskCount() -> Int {
    return tasks.count
  }
  
  // 10
  public func clearTasks() {
    tasks.removeAll()
  }
}
