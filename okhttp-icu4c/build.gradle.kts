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
//  iosArm64()
//  iosX64()
//  iosSimulatorArm64()
//  tvosArm64()
//  tvosSimulatorArm64()
//  tvosX64()

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

// Avoid cross-compiling ICU; we haven't done the work to support that.
val osArch = System.getProperty("os.arch")
val isX64 = osArch == "x86_64" || osArch == "amd64"
val isAarch64 = osArch == "aarch64"
val osName = System.getProperty("os.name")
val isMac = osName == "Mac OS X"
val isLinux = osName == "Linux"

val buildIcu4cMacOSX = tasks.register<BuildIcu4c>("buildIcu4cMacOSX") {
  platform.set("MacOSX")
  onlyIf { isMac }
}

val buildIcu4cLinux = tasks.register<BuildIcu4c>("buildIcu4cLinux") {
  platform.set("Linux")
  onlyIf { isLinux }
}

tasks.all {
  when (name) {
    "cinteropIcu4cMacosX64" -> {
      dependsOn(buildIcu4cMacOSX)
      onlyIf { isMac && isX64 }
    }
    "cinteropIcu4cMacosArm64" -> {
      dependsOn(buildIcu4cMacOSX)
      onlyIf { isMac && isAarch64 }
    }
    "cinteropIcu4cLinuxX64" -> {
      dependsOn(buildIcu4cLinux)
      onlyIf { isLinux && isX64 }
    }
  }

  when (name) {
    "compileKotlinMacosX64",
    "compileTestKotlinMacosX64",
    "publishMacosX64PublicationToMavenCentralRepository" -> {
      onlyIf { isMac && isX64 }
    }

    "compileKotlinMacosArm64",
    "compileTestKotlinMacosArm64",
    "publishMacosArm64PublicationToMavenCentralRepository" -> {
      onlyIf { isMac && isAarch64 }
    }

    "compileKotlinLinuxX64",
    "compileTestKotlinLinuxX64",
    "publishLinuxX64PublicationToMavenCentralRepository" -> {
      onlyIf { isLinux && isX64 }
    }
  }

  when (name) {
    "clean" -> dependsOn(cleanIcu4c)
  }
}


configure<MavenPublishBaseExtension> {
  configure(
    KotlinMultiplatform(
      javadocJar = JavadocJar.Dokka("dokkaGfm")
    )
  )
}
