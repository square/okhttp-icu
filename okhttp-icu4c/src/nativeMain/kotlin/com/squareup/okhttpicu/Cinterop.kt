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

import com.squareup.okhttpicu.icu4c.UErrorCodeVar
import com.squareup.okhttpicu.icu4c.u_errorName_73
import kotlinx.cinterop.ByteVarOf
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.get
import kotlinx.cinterop.value
import okio.Buffer

internal fun Buffer.writeNullTerminated(bytes: CPointer<ByteVarOf<Byte>>): Buffer = apply {
  var pos = 0
  while (true) {
    val byte = bytes[pos++].toInt()
    if (byte == 0) {
      break
    } else {
      writeByte(byte)
    }
  }
}


internal fun checkSuccess(errorCodeVar: UErrorCodeVar, message: () -> String) {
  check(U_SUCCESS(errorCodeVar.value)) {
    val errorMessage = Buffer()
      .writeUtf8(message())
      .writeUtf8(", errorCode=")
      .writeDecimalLong(errorCodeVar.value.toLong())

    val errorName = u_errorName_73(errorCodeVar.value)
    if (errorName != null) {
      errorMessage.writeUtf8(", errorName=")
        .writeNullTerminated(errorName)
    }

    errorMessage.readUtf8()
  }
}
