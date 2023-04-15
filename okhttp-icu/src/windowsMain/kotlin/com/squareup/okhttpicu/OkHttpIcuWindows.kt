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

import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKStringFromUtf16
import platform.windows.NormalizationC
import platform.windows.NormalizeString
import platform.windows.WCHARVar

actual val SYSTEM_NORMALIZER = object : Normalizer {
  override fun normalizeNfc(string: String): String {
    if (string.isEmpty()) return ""

    memScoped {
      // https://www.unicode.org/reports/tr15/tr15-53.html#Detecting_Normalization_Forms
      // ‘no string when decomposed with NFC expands to more than 3× in length’
      // (plus 1 for the trailing \0, since toKStringFromUtf16 needs that)
      val maxResultLength = string.length * 3
      val result = allocArray<WCHARVar>(maxResultLength + 1)
      val resultLength = NormalizeString(
        NormalizationC,
        string,
        string.length,
        result,
        maxResultLength,
      )

      // https://learn.microsoft.com/en-us/windows/win32/api/winnls/nf-winnls-normalizestring
      require(resultLength > 0)

      return result.toKStringFromUtf16()
    }
  }
}
