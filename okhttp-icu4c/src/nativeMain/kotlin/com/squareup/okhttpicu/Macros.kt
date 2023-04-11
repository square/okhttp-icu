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

import com.squareup.okhttpicu.icu4c.UErrorCode
import com.squareup.okhttpicu.icu4c.U_ZERO_ERROR

// Macros manually ported from ICU.

/** From icu4c/source/common/unicode/utypes.h */
internal fun U_SUCCESS(x: UErrorCode) = (x <= U_ZERO_ERROR)

/** From icu4c/source/common/unicode/utypes.h */
internal fun U_FAILURE(x: UErrorCode) = (x > U_ZERO_ERROR)
