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
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.windows.NormalizationKD
import platform.windows.NormalizeString
import platform.windows.WCHARVar
import platform.windows._NORM_FORM

fun String.normalize(winForm: _NORM_FORM): String {
  return memScoped {
    val size = NormalizeString(winForm, this@normalize, -1, null, 0)
    println("size=$size")
    val result = allocArray<WCHARVar>(size)
    NormalizeString(winForm, this@normalize, -1, result, size)
    result.toKString()
  }
}

class MaxExpansionWindowsTest {

  @Test
  fun maxExpansion() {
    val a = "a"
    val b = "(ﷺ)"
    val c = "(صلى الله عليه وسلم)"
    val a100 = a.repeat(100)
    val b100 = b.repeat(100)
    val c100 = c.repeat(100)
    val a2048 = a.repeat(2048)
    val b2048 = b.repeat(2048)
    val c2048 = c.repeat(2048)
    assertEquals(a100, a100.normalize(NormalizationKD))
    assertEquals(c100, b100.normalize(NormalizationKD))
    assertEquals(c100, c100.normalize(NormalizationKD))
    assertEquals(a100 + c, (a100 + c).normalize(NormalizationKD))
    assertEquals(a100 + c, (a100 + b).normalize(NormalizationKD))
    assertEquals(a100 + c100, (a100 + c100).normalize(NormalizationKD))
    assertEquals(a100 + c100, (a100 + b100).normalize(NormalizationKD))
    assertEquals(a2048 + c, (a2048 + c).normalize(NormalizationKD))
    assertEquals(a2048 + c, (a2048 + b).normalize(NormalizationKD))
    assertEquals(a2048 + c2048, (a2048 + c2048).normalize(NormalizationKD))
    assertEquals(a2048 + c2048, (a2048 + b2048).normalize(NormalizationKD))
  }
}
