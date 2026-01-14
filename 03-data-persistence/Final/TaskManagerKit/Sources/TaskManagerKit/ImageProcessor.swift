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

public struct ImageProcessor {
  
  public static func applyGrayscaleFilter(
    imageData: Data,
    width: Int32,
    height: Int32
  ) -> Data? {
    guard width > 0, height > 0 else { return nil }
    
    var pixels = [UInt8](repeating: 0, count: Int(width * height * 4))
    imageData.copyBytes(to: &pixels, count: pixels.count)
    
    for i in stride(from: 0, to: pixels.count, by: 4) {
      let r = Float(pixels[i])
      let g = Float(pixels[i + 1])
      let b = Float(pixels[i + 2])
      
      let gray = UInt8(0.299 * r + 0.587 * g + 0.114 * b)
      
      pixels[i] = gray
      pixels[i + 1] = gray
      pixels[i + 2] = gray
    }
    
    return Data(pixels)
  }
  
  public static func applyBlurFilter(
    imageData: Data,
    width: Int32,
    height: Int32,
    radius: Int32 = 5
  ) -> Data? {
    guard width > 0, height > 0, radius > 0 else { return nil }
    
    // Limit radius to avoid excessive computation
    let safeRadius = min(radius, 10)
    
    var pixels = [UInt8](repeating: 0, count: Int(width * height * 4))
    imageData.copyBytes(to: &pixels, count: pixels.count)
    
    var output = [UInt8](repeating: 0, count: Int(width * height * 4))
    
    // Copy alpha channel directly (no blur)
    for i in stride(from: 0, to: pixels.count, by: 4) {
      output[i + 3] = pixels[i + 3]
    }
    
    // Simple box blur - process every nth pixel for speed
    let step = 1
    
    for y in stride(from: 0, to: Int(height), by: step) {
      for x in stride(from: 0, to: Int(width), by: step) {
        var r: Float = 0, g: Float = 0, b: Float = 0
        var count: Float = 0
        
        for ky in -Int(safeRadius)...Int(safeRadius) {
          for kx in -Int(safeRadius)...Int(safeRadius) {
            let px = x + kx
            let py = y + ky
            
            // Bounds check
            guard px >= 0, px < Int(width), py >= 0, py < Int(height) else { continue }
            
            let index = (py * Int(width) + px) * 4
            
            r += Float(pixels[index])
            g += Float(pixels[index + 1])
            b += Float(pixels[index + 2])
            count += 1
          }
        }
        
        let index = (y * Int(width) + x) * 4
        output[index] = UInt8(r / count)
        output[index + 1] = UInt8(g / count)
        output[index + 2] = UInt8(b / count)
      }
    }
    
    return Data(output)
  }
  
  public static func adjustBrightness(
    imageData: Data,
    width: Int32,
    height: Int32,
    amount: Float
  ) -> Data? {
    guard width > 0, height > 0 else { return nil }
    
    var pixels = [UInt8](repeating: 0, count: Int(width * height * 4))
    imageData.copyBytes(to: &pixels, count: pixels.count)
    
    for i in stride(from: 0, to: pixels.count, by: 4) {
      let r = Float(pixels[i]) + amount
      let g = Float(pixels[i + 1]) + amount
      let b = Float(pixels[i + 2]) + amount
      
      pixels[i] = UInt8(max(0, min(255, r)))
      pixels[i + 1] = UInt8(max(0, min(255, g)))
      pixels[i + 2] = UInt8(max(0, min(255, b)))
    }
    
    return Data(pixels)
  }
  
  public static func processImageFile(
    inputPath: String,
    outputPath: String,
    filterType: String,
    amount: Float = 0
  ) -> Bool {
    return false
  }
}
