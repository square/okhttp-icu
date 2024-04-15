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
  abstract val normalizer: Normalizer

  open fun isKnownFailure(test: NormalizationTestData): Boolean = false

  @Test
  fun normalize() {
    val lines = NormalizationTestData.load()
    for (line in lines) {
      try {
        assertEquals(
          line.nfc,
          normalizer.normalizeNfc(line.source),
          "${line.part} ${line.lineNumber} ${line.source} ${line.comment}",
        )
      } catch (e: AssertionError) {
        if (!isKnownFailure(line)) throw e
      }
    }
  }

  /** https://unicode.org/faq/normalization.html#12 */
  @Test
  fun maximumExpansionNfcUtf8() {
    val s = "\uD834\uDD60"
    val sNfc = "\uD834\uDD58\uD834\uDD65\uD834\uDD6E"
    assertEquals(sNfc, normalizer.normalizeNfc(s))
    assertEquals(sNfc.repeat(1024), normalizer.normalizeNfc(s.repeat(1024)))
  }

  @Test
  fun maximumExpansionNfcUtf16() {
    val s = "\uFB2C"
    val sNfc = "שּׁ"
    assertEquals(sNfc, normalizer.normalizeNfc(s))
    assertEquals(sNfc.repeat(1024), normalizer.normalizeNfc(s.repeat(1024)))
  }

  @Test
  fun maximumExpansionNfdUtf8() {
    val s = "ΐ"
    val sNfc = "\u0390"
    assertEquals(sNfc, normalizer.normalizeNfc(s))
    assertEquals(sNfc.repeat(1024), normalizer.normalizeNfc(s.repeat(1024)))
  }

  @Test
  fun maximumExpansionNfdUtf16() {
    val s = "ᾂ"
    val sNfc = "\u1F82"
    assertEquals(sNfc, normalizer.normalizeNfc(s))
    assertEquals(sNfc.repeat(1024), normalizer.normalizeNfc(s.repeat(1024)))
  }
}
