OkHttp-ICU
==========

This is a [Kotlin/Multiplatform] API to the subset of [ICU] required by [OkHttp].

This project builds upon the following implementations:

 * [Normalizer]: on JVM platforms.
 * [String.normalize] on JavaScript.
 * [precomposedStringWithCanonicalMapping]: on all Apple platforms.
 * [normalizeString] on Windows.
 * [ICU4C]: on Kotlin/Native for LinuxX64. (This project also builds ICU4C for Mac, but it only uses
   that implementation in tests.)

Directory Structure
-------------------

The main branch uses [Git Submodules] to place this repo's own ICU branches into the main branch's
directory tree:

```
 |-- submodules
 |   '-- icu               Submodule of the ICU project's main branch
 |-- okhttp-icu            Top-level Kotlin/Multiplatform module API
 |   '-- src
 |       |-- commonMain    Interfaces for ICU features
 |       |-- jvmMain       Implementation that uses Java's built-in ICU APIs.
 |       |-- jsMain        Implementation that uses JavaScript's built-in ICU APIs.
 |       |-- appleMain     Implementation that uses Apple's built-in ICU APIs.
 |       |-- windowsMain   Implementation that uses Windows' built-in ICU APIs.
 |       '-- linuxMain     Implementation that uses okhttp-icu4c.
 |-- okhttp-icu4c          Kotlin/Native module that packages the code in submodules/icu/icu4c
 |-- README.md
 '-- ...
```

Developing
----------

You'll need [Git LFS] for ICU.


[Git LFS]: https://git-lfs.com/
[Git Submodules]: https://git-scm.com/book/en/v2/Git-Tools-Submodules
[ICU4C]: https://unicode-org.github.io/icu/userguide/icu4c/
[ICU]: https://icu.unicode.org/
[Kotlin/Multiplatform]: https://kotlinlang.org/docs/multiplatform.html
[Normalizer]: https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/text/Normalizer.html
[OkHttp]: https://github.com/square/okhttp
[String.normalize]: https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/normalize
[normalizeString]: https://learn.microsoft.com/en-us/windows/win32/api/winnls/nf-winnls-normalizestring
[precomposedStringWithCanonicalMapping]: https://developer.apple.com/documentation/foundation/nsstring/1412645-precomposedstringwithcanonicalma
