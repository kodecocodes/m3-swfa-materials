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
public class TaskValidator {
  // 2
  private static let minTitleLength = 3
  private static let maxTitleLength = 50
  
  // 3
  private static let minDescriptionLength = 10
  private static let maxDescriptionLength = 200
  
  // 4
  public static func validateTitle(_ title: String) -> Bool {
    // 5
    let trimmed = title.trimmingCharacters(in: .whitespacesAndNewlines)
    // 6
    return trimmed.count >= minTitleLength && trimmed.count <= maxTitleLength
  }
  
  // 7
  public static func validateDescription(_ description: String) -> Bool {
    // 8
    let trimmed = description.trimmingCharacters(in: .whitespacesAndNewlines)
    // 9
    return trimmed.count >= minDescriptionLength && trimmed.count <= maxDescriptionLength
  }
  
  // 10
  public static func validatePriority(_ priority: String) -> Bool {
    // 11
    return Task.Priority(rawValue: priority) != nil
  }
}
