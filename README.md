OkHttp-ICU
==========

This builds a minimal subset of [ICU] required by [OkHttp].


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


git@github.com:unicode-org/icu.git

[Git LFS]: https://git-lfs.com/
[Git Submodules]: https://git-scm.com/book/en/v2/Git-Tools-Submodules
[ICU]: https://icu.unicode.org/
[OkHttp]: https://github.com/square/okhttp
