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

// 45
enum PhotoStorageError: Error {
  case invalidPath
  case saveFailed(Error)
  case copyFailed(Error)
  case deleteFailed(Error)
}

// 46
class PhotoStorage {
  
  // 47
  private static func photosDirectory() -> URL? {
    guard let documentsDir = FileManager.default.urls(
      for: .documentDirectory,
      in: .userDomainMask
    ).first else {
      return nil
    }
    
    let photosDir = documentsDir.appendingPathComponent("photos")
    
    if !FileManager.default.fileExists(atPath: photosDir.path) {
      try? FileManager.default.createDirectory(
        at: photosDir,
        withIntermediateDirectories: true
      )
    }
    
    return photosDir
  }
  
  // 48
  public static func savePhoto(
    data: Data,
    withFilename filename: String,
    documentsPath: String
  ) -> Result<String, PhotoStorageError> {
    // Create photos directory using the provided documents path
    let photosPath = (documentsPath as NSString).appendingPathComponent("photos")
    let photosURL = URL(fileURLWithPath: photosPath)
    
    // Create directory if it doesn't exist
    do {
      try FileManager.default.createDirectory(
        at: photosURL,
        withIntermediateDirectories: true,
        attributes: nil
      )
    } catch {
      return .failure(.saveFailed(error))
    }
    
    let destinationURL = photosURL.appendingPathComponent(filename)
    
    do {
      try data.write(to: destinationURL)
      return .success(filename)
    } catch {
      return .failure(.saveFailed(error))
    }
  }
  
  // 48b
  public static func savePhoto(
    fromPath sourcePath: String,
    withFilename filename: String
  ) -> Result<String, PhotoStorageError> {
    guard let photosDir = photosDirectory() else {
      return .failure(.invalidPath)
    }
    
    let sourceURL = URL(fileURLWithPath: sourcePath)
    let destURL = photosDir.appendingPathComponent(filename)
    
    do {
      if FileManager.default.fileExists(atPath: destURL.path) {
        try FileManager.default.removeItem(at: destURL)
      }
      
      try FileManager.default.copyItem(at: sourceURL, to: destURL)
      return .success(filename)
    } catch {
      return .failure(.copyFailed(error))
    }
  }
  
  // 49
  public static func photoPath(for filename: String, documentsPath: String) -> String? {
    // Use provided documentsPath instead of FileManager (which doesn't work on Android)
    let photosDir = documentsPath + "/photos"
    let photoPath = photosDir + "/" + filename
    return photoPath
  }
  
  // 50
  public static func deletePhoto(filename: String) -> Result<Void, PhotoStorageError> {
    guard let photosDir = photosDirectory() else {
      return .failure(.invalidPath)
    }
    
    let photoURL = photosDir.appendingPathComponent(filename)
    
    do {
      if FileManager.default.fileExists(atPath: photoURL.path) {
        try FileManager.default.removeItem(at: photoURL)
      }
      return .success(())
    } catch {
      return .failure(.deleteFailed(error))
    }
  }
  
  // 51
  public static func photoExists(filename: String) -> Bool {
    guard let photosDir = photosDirectory() else {
      return false
    }
    
    let photoURL = photosDir.appendingPathComponent(filename)
    return FileManager.default.fileExists(atPath: photoURL.path)
  }
}
