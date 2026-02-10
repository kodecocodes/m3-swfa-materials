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
 * 
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 * 
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.kodeco.android.swiftsdkforandroid.taskmanager.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kodeco.android.swiftsdkforandroid.taskmanager.R
import com.kodeco.android.taskmanagerkit.Priority
import com.kodeco.android.swiftsdkforandroid.taskmanager.repository.TaskRepository
import org.swift.swiftkit.core.SwiftArena


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskDialog(
  onDismiss: () -> Unit
) {
  val arena = remember { SwiftArena.ofConfined() }

  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var priority by remember { mutableStateOf(Priority.medium(arena)) }
  var expanded by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  
  val priorities = remember(arena) {
    listOf(
      Priority.low(arena),
      Priority.medium(arena),
      Priority.high(arena)
    )
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {

      Text(text = stringResource(R.string.add_task))
    },
    text = {

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text(stringResource(R.string.task_title)) },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text(stringResource(R.string.task_description)) },
          modifier = Modifier.fillMaxWidth(),
          minLines = 3,
          maxLines = 5
        )
        

        ExposedDropdownMenuBox(
          expanded = expanded,
          onExpandedChange = { expanded = !expanded }
        ) {

          OutlinedTextField(
            value = priority.rawValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.task_priority)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
              .menuAnchor(MenuAnchorType.PrimaryNotEditable)
              .fillMaxWidth()
          )
          

          ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
          ) {
            priorities.forEach { option ->

              DropdownMenuItem(
                text = { Text(option.rawValue) },
                onClick = {
                  priority = option
                  expanded = false
                }
              )
            }
          }
        }
        

        errorMessage?.let { error ->
          Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
          )
        }
      }
    },
    confirmButton = {

      Button(
        onClick = {

          val result = TaskRepository.addTask(
            title = title,
            description = description,
            priority = priority
          )
          

          result.onSuccess {

            onDismiss()
          }.onFailure { error ->
            errorMessage = error.message ?: "Validation failed"
          }
        }
      ) {
        Text(stringResource(R.string.save))
      }
    },
    dismissButton = {

      TextButton(onClick = onDismiss) {
        Text(stringResource(R.string.cancel))
      }
    }
  )
}
