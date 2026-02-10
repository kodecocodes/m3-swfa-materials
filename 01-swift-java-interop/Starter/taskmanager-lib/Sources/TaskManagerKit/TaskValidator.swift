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
public struct TaskValidator {
  // 2
  public static func validateTitle(_ title: String) -> Bool {
    // TODO: Implement title validation (3-50 characters)
    return true
  }
  
  // 3
  public static func validateDescription(_ description: String) -> Bool {
    // TODO: Implement description validation (10-200 characters)
    return true
  }
  
  // 4
  public static func validatePriority(_ priority: String) -> Bool {
    // TODO: Implement priority validation
    return true
  }
}
