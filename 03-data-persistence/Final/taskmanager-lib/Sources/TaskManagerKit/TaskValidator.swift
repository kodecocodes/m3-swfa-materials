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


public class TaskValidator {

  private static let minTitleLength = 3
  private static let maxTitleLength = 50
  

  private static let minDescriptionLength = 10
  private static let maxDescriptionLength = 200
  

  public static func validateTitle(_ title: String) -> Bool {

    let trimmed = title.trimmingCharacters(in: .whitespacesAndNewlines)

    return trimmed.count >= minTitleLength && trimmed.count <= maxTitleLength
  }
  

  public static func validateDescription(_ description: String) -> Bool {

    let trimmed = description.trimmingCharacters(in: .whitespacesAndNewlines)

    return trimmed.count >= minDescriptionLength && trimmed.count <= maxDescriptionLength
  }
  

  public static func validatePriority(_ priority: String) -> Bool {

    return Priority(rawValue: priority) != nil
  }
}
