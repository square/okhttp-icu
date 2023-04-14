/*
 * Copyright (C) 2023 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.okhttpicu

import kotlin.test.Test
import kotlin.test.assertEquals

abstract class AbstractNormalizationTest {
  abstract val okhttpIcu: OkHttpIcu

  open val knownFailureLineNumbers: Set<Int>
    get() = setOf()

  @Test
  fun normalize() {
    val lines = NormalizationTestData.load()
    for (line in lines) {
      try {
        assertEquals(
          line.nfc,
          okhttpIcu.normalizeNfc(line.source),
          "${line.part} ${line.lineNumber} ${line.source} ${line.comment}",
        )
        require(line.lineNumber !in knownFailureLineNumbers) {
          "expected failure for ${line.lineNumber}"
        }
      } catch (e: AssertionError) {
        if (line.lineNumber !in knownFailureLineNumbers) throw e
      }
    }
  }
}
