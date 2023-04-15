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
import okio.FileSystem
import okio.Path
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
    fun load(): List<NormalizationTestData> {
      val fileSystem = SYSTEM_FILE_SYSTEM
      val root = fileSystem.projectRoot()
      val path = root / "okhttp-icu" / "src" / "commonTest" / "testdata" / "NormalizationTest.txt"
      return fileSystem.read(path) {
        readNormalizationTestData()
      }
    }

    /**
     * Returns the path to the root of the okhttp-icu project. This assumes that the current working
     * directory is a child of a directory of that name, and the project root is the topmost
     * directory with that name.
     *
     * This will return the wrong answer if the project is checked out into a root directory like
     * `/Users/jesse/okhttp-icu/versions/current/okhttp-icu`.
     */
    private fun FileSystem.projectRoot(): Path {
      val cwd = ".".toPath()
      val projectName = "okhttp-icu"
      return canonicalize(cwd).dropSegmentsAfterFirst(projectName)
    }

    /**
     * Given a path like `/Users/jesse/okhttp-icu/build/js/packages/okhttp-icu-root` or
     * `/Users/jesse/okhttp-icu/okhttp-icu4c`, this returns a path like `/Users/jesse/okhttp-icu/`.
     */
    private fun Path.dropSegmentsAfterFirst(name: String): Path {
      val segments = this.segments
      val segmentsToDrop = segments.size - segments.indexOf(name) - 1
      var result = this
      for (i in 0 until segmentsToDrop) {
        result = result.parent!!
      }
      return result
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
