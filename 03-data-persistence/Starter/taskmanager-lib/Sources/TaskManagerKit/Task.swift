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

public enum Priority: String, Codable {
  case low = "Low"
  case medium = "Medium"
  case high = "High"
}

public struct Task: Codable {
  public let id: String
  public let title: String
  public let description: String
  public let priority: Priority
  public var isCompleted: Bool
  public let photoUri: String?
  
  // TODO: In this lesson, you'll rename photoUri to photoFilename
  // and implement persistent storage via PhotoStorage
  
  public init(id: String, title: String, description: String, priority: Priority, isCompleted: Bool = false, photoUri: String? = nil) {
    self.id = id
    self.title = title
    self.description = description
    self.priority = priority
    self.isCompleted = isCompleted
    self.photoUri = photoUri
  }
}
