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
    val a = "(ﷺ)"
    val b = "(صلى الله عليه وسلم)"
    for (i in 0 until 100) {
      println(i)
      assertEquals(b.repeat(i), a.repeat(i).normalize(NormalizationKD))
    }
  }

  @Test
  fun substrings() {
    val a = "0123456789".repeat(1000)
    for (i in a.indices) {
      println(i)
      val substring = a.substring(0, i)
      assertEquals(substring, substring.normalize(NormalizationKD))
    }
  }
}
