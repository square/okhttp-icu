OkHttp-ICU
==========

This is a [Kotlin/Multiplatform] API to the subset of [ICU] required by [OkHttp].

This project builds upon the following implementations:

 * [ICU4C]: for Kotlin/Native on LinuxX64. (This project also builds ICU4C for Mac, but only uses
   that implementation in tests.)
 * [Core Foundation APIs]: for all Apple platforms where Kotlin/Multiplatform runs.
 * [Java APIs]: for JVM platforms.


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
 |       |-- jsMain        Implementation that uses ICU features built into JavaScript
 |       |-- jvmMain       Implementation that uses ICU features built into Java
 |       |-- appleMain     Implementation that uses ICU features built into macOS / iOS
 |       '-- linuxMain     Implementation that uses okhttp-icu4c
 |-- okhttp-icu4c          Kotlin/Native module that packages the code in submodules/icu/icu4c
 |-- README.md
 '-- ...
```

Developing
----------

You'll need [Git LFS] for ICU.


[Core Foundation APIs]: https://developer.apple.com/documentation/corefoundation/cfstringnormalizationform
[Git LFS]: https://git-lfs.com/
[Git Submodules]: https://git-scm.com/book/en/v2/Git-Tools-Submodules
[ICU4C]: https://unicode-org.github.io/icu/userguide/icu4c/
[ICU]: https://icu.unicode.org/
[Java APIs]: https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/text/Normalizer.html
[Kotlin/Multiplatform]: https://kotlinlang.org/docs/multiplatform.html
[OkHttp]: https://github.com/square/okhttp
