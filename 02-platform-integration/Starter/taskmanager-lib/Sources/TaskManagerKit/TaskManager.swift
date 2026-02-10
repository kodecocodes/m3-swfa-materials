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
  
  public static let shared = TaskManager()
  
  private init() {}
  
  @discardableResult
  public func addTask(_ task: Task) -> Bool {
    guard TaskValidator.validateTitle(task.title),
          TaskValidator.validateDescription(task.description) else {
      return false
    }
    
    tasks.append(task)
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
  }
}
