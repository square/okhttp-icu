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

import okio.Buffer
import okio.BufferedSource
import okio.Path.Companion.toPath

/**
 * The [Unicode Normalization Test Suite](https://www.unicode.org/Public/15.0.0/ucd/NormalizationTest.txt).
 *
 * Each test is a line of the file `NormalizationTest-15.0.0.txt`.
 */
class NormalizationTestData(
  val lineNumber: Int,
  val part: String?,
  val source: String,
  val nfc: String,
  val nfd: String,
  val nfkc: String,
  val nfkd: String,
  val comment: String?,
) {
  companion object {
    private val okHttpIcuRootDir = getEnv("OKHTTP_ICU_ROOT_DIR")!!.toPath()

    fun load(): List<NormalizationTestData> {
      val path = okHttpIcuRootDir / "okhttp-icu/src/commonTest/testdata/NormalizationTest.txt"
      return SYSTEM_FILE_SYSTEM.read(path) {
        readNormalizationTestData()
      }
    }

    private fun BufferedSource.readNormalizationTestData(): List<NormalizationTestData> {
      val result = mutableListOf<NormalizationTestData>()

      var nextLineNumber = 1
      var part: String? = null
      while (!exhausted()) {
        val lineNumber = nextLineNumber++
        val line = readUtf8LineStrict()

        if (line.startsWith("#")) {
          continue
        }

        if (line.startsWith("@")) {
          part = line
          continue
        }

        val columns = line.split(';', limit = 6)

        result += NormalizationTestData(
          lineNumber = lineNumber,
          part = part,
          source = columns[0].decodeHexCodePoints(),
          nfc = columns[1].decodeHexCodePoints(),
          nfd = columns[2].decodeHexCodePoints(),
          nfkc = columns[3].decodeHexCodePoints(),
          nfkd = columns[4].decodeHexCodePoints(),
          comment = columns.getOrNull(5)?.removePrefix(" # "),
        )
      }

      return result
    }

    private fun String.decodeHexCodePoints(): String {
      val buffer = Buffer()
      for (codePointHex in split(' ')) {
        buffer.writeUtf8CodePoint(codePointHex.toInt(radix = 16))
      }
      return buffer.readUtf8()
    }
  }
}
