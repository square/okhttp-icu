package com.squareup.okhttpicu

import java.text.Normalizer
import kotlin.test.Test
import kotlin.test.assertEquals

class MaxExpansionJvmTest {
  val NormalizationKD = 1
  fun String.normalize(x: Int): String? {
    return Normalizer.normalize(this, Normalizer.Form.NFKD)
  }

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
