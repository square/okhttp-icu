import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithTests
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable

plugins {
  kotlin("multiplatform")
  id("org.jetbrains.dokka")
  id("com.vanniktech.maven.publish.base")
}

kotlin {
  linuxX64()
  macosX64()
  macosArm64()

  sourceSets {
    val commonMain by getting {
      dependencies {
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }

    val nativeMain by creating {
      dependencies {
        implementation(libs.okio.core)
      }
    }
    val nativeTest by creating {
    }

    targets.withType<KotlinNativeTarget> {
      val main by compilations.getting
      main.defaultSourceSet.dependsOn(nativeMain)

      main.cinterops {
        create("icu4c") {
          includeDirs("$rootDir/submodules/icu/icu4c/source/common")
          defFile("src/nativeInterop/cinterop/icu4c.def")
          packageName("com.squareup.okhttpicu.icu4c")
        }
      }

      val test by compilations.getting
      test.defaultSourceSet.dependsOn(nativeTest)
    }

    targets.withType<KotlinNativeTargetWithTests<*>> {
      binaries {
        // Configure a separate test where code is compiled in release mode.
        test(setOf(NativeBuildType.RELEASE))
      }
      testRuns {
        create("release") {
          setExecutionSourceFrom(binaries.getByName("releaseTest") as TestExecutable)
        }
      }
    }
  }
}

val cleanIcu4c by tasks.creating {
  description = "clean up after buildIcu4c by resetting git"

  doLast {
    exec {
      commandLine("git", "clean", "-fdx")
    }
  }
}

val buildIcu4cMacOSX = tasks.register<BuildIcu4c>("buildIcu4cMacOSX") {
  platform.set("MacOSX")
  onlyIf { buildHostOsFamily == OsFamily.Apple }
}

val buildIcu4cLinux = tasks.register<BuildIcu4c>("buildIcu4cLinux") {
  platform.set("Linux")
  onlyIf { buildHostOsFamily == OsFamily.Linux }
}

tasks.all {
  when (name) {
    "cinteropIcu4c${KotlinNativePlatform.MacosX64}",
    "cinteropIcu4c${KotlinNativePlatform.MacosArm64}" -> {
      dependsOn(buildIcu4cMacOSX)
    }

    "cinteropIcu4c${KotlinNativePlatform.LinuxX64}" -> {
      dependsOn(buildIcu4cLinux)
    }
  }

  when (name) {
    "clean" -> dependsOn(cleanIcu4c)
  }
}

// Only build ICU4C when the host platform exactly matches the target platform. We haven't done the
// work to support cross-compiling across CPU architectures.
tasks.all {
  val kotlinNativePlatform = this.kotlinNativePlatform ?: return@all
  if (kotlinNativePlatform.supportsIcu4c) {
    onlyIf { kotlinNativePlatform == buildHostPlatform }
  }
}

configure<MavenPublishBaseExtension> {
  configure(
    KotlinMultiplatform(
      javadocJar = JavadocJar.Dokka("dokkaGfm")
    )
  )
}
