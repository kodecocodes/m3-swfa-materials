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
public enum StorageError: Error {
  case fileNotFound
  case corruptedData
  case encodingFailed
  case decodingFailed
  case writeFailed(Error)
  case readFailed(Error)
}

// 2
public class TaskStorage {
  
  // 3
  private let fileURL: URL
  
  // 4
  public init(filename: String = "tasks.json") {
    #if os(Android)
    // Android: Use app's files directory
    let documentsPath = "/data/data/com.kodeco.android.swiftsdkforandroid.taskmanager/files"
    self.fileURL = URL(fileURLWithPath: documentsPath).appendingPathComponent(filename)
    #else
    // iOS/macOS: Use documents directory
    let documentsDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
    self.fileURL = documentsDirectory.appendingPathComponent(filename)
    #endif
  }
  
  // 5
  public func saveTasks(_ tasks: [Task]) -> Result<Void, StorageError> {
    do {
      let encoder = JSONEncoder()
      encoder.outputFormatting = .prettyPrinted
      let data = try encoder.encode(tasks)
      
      try data.write(to: fileURL, options: [.atomic])
      
      return .success(())
    } catch let error as EncodingError {
      return .failure(.encodingFailed)
    } catch {
      return .failure(.writeFailed(error))
    }
  }
  
  // 6
  public func loadTasks() -> Result<[Task], StorageError> {
    guard FileManager.default.fileExists(atPath: fileURL.path) else {
      return .failure(.fileNotFound)
    }
    
    do {
      let data = try Data(contentsOf: fileURL)
      
      guard !data.isEmpty else {
        return .success([])
      }
      
      let decoder = JSONDecoder()
      let tasks = try decoder.decode([Task].self, from: data)
      
      return .success(tasks)
    } catch let error as DecodingError {
      return .failure(.corruptedData)
    } catch {
      return .failure(.readFailed(error))
    }
  }
  
  // 7
  public func deleteFile() -> Bool {
    guard FileManager.default.fileExists(atPath: fileURL.path) else {
      return true
    }
    
    do {
      try FileManager.default.removeItem(at: fileURL)
      return true
    } catch {
      return false
    }
  }
}
