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

import cnames.structs.UNormalizer2
import com.squareup.okhttpicu.icu4c.UCharVar
import com.squareup.okhttpicu.icu4c.UErrorCodeVar
import com.squareup.okhttpicu.icu4c.unorm2_getNFCInstance_73
import com.squareup.okhttpicu.icu4c.unorm2_normalize_73
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.UShortVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKStringFromUtf16
import kotlinx.cinterop.utf16

object OkHttpIcu4c {
  private val nfc: CPointer<UNormalizer2> = memScoped {
    val errorCodeVar = alloc<UErrorCodeVar>()
    unorm2_getNFCInstance_73(errorCodeVar.ptr)
      .also {
        checkSuccess(errorCodeVar) { "getNFCInstance failed" }
      }!!
  }

  fun normalizeNfc(string: String): String {
    memScoped {
      val errorCodeVar = alloc<UErrorCodeVar>()

      val sourceUtf16 = string.utf16

      // ‘no string when decomposed with NFC expands to more than 3× in length’
      // https://www.unicode.org/reports/tr15/#Detecting_Normalization_Forms
      val capacity = sourceUtf16.size * 3
      val resultUtf16 = allocArray<UShortVar>(capacity)

      unorm2_normalize_73(
        norm2 = nfc,
        src = sourceUtf16 as CValuesRef<UCharVar>,
        length = sourceUtf16.size,
        dest = resultUtf16,
        capacity = capacity,
        pErrorCode = errorCodeVar.ptr,
      ).also {
        checkSuccess(errorCodeVar) { "normalize failed" }
      }


      return resultUtf16.toKStringFromUtf16()
    }
  }
}
