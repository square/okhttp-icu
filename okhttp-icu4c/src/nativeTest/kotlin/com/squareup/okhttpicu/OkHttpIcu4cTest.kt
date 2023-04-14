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
import okio.ByteString.Companion.decodeHex

class OkHttpIcu4cTest {
  @Test
  fun normalize() {
    //         c  a  f  é    ␣  🍩
    val nfc = "43 61 66 c3a9 20 f09f8da9".replace(" ", "").decodeHex().utf8()

    //         c  a  f  e  ´    ␣  🍩
    val nfd = "43 61 66 65 cc81 20 f09f8da9".replace(" ", "").decodeHex().utf8()

    assertEquals(nfc, Icu4c.normalizeNfc(nfc))
    assertEquals(nfc, Icu4c.normalizeNfc(nfd))
  }
}
