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

import platform.Foundation.NSString
import platform.Foundation.precomposedStringWithCanonicalMapping

actual val SYSTEM_NORMALIZER = object : Normalizer {
  override fun normalizeNfc(string: String): String {
    // https://kotlinlang.org/api/latest/jvm/stdlib/kotlinx.cinterop/-create-n-s-string-from-k-string.html
    @Suppress("CAST_NEVER_SUCCEEDS")
    val nsString = string as NSString
    return nsString.precomposedStringWithCanonicalMapping()
  }
}
