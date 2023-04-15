import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithTests
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable

plugins {
  kotlin("multiplatform")
  id("org.jetbrains.dokka")
  id("com.vanniktech.maven.publish.base")
}

kotlin {
//  android {
//    publishLibraryVariants("release")
//  }
  jvm()

  js {
    nodejs()
  }

  iosArm64()
  iosSimulatorArm64()
  iosX64()
  linuxX64()
  macosArm64()
  macosX64()
  mingwX64()
  tvosArm64()
  tvosSimulatorArm64()
  tvosX64()
  watchosArm32()
  watchosArm64()
  watchosSimulatorArm64()
  watchosX64()
  watchosX86()

  sourceSets {
    val commonMain by getting {
      dependencies {
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
        api(libs.okio.core)
      }
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

    val appleMain by creating {
      dependsOn(commonMain)
    }
    val appleTest by creating {
      dependsOn(commonTest)
    }

    val linuxMain by creating {
      dependsOn(commonMain)
      dependencies {
        api(projects.okhttpIcu4c)
      }
    }
    val linuxTest by creating {
      dependsOn(commonTest)
    }

    val windowsMain by creating {
      dependsOn(commonMain)
    }
    val windowsTest by creating {
      dependsOn(commonTest)
    }

    for (platform in KotlinNativePlatform.values()) {
      val platformMain = get("${platform.lowerCamel}Main")
      platformMain.dependsOn(
        when (platform.osFamily) {
          OsFamily.Apple -> appleMain
          OsFamily.Linux -> linuxMain
          OsFamily.Windows -> windowsMain
        }
      )
      val platformTest = get("${platform.lowerCamel}Test")
      platformTest.dependsOn(
        when (platform.osFamily) {
          OsFamily.Apple -> appleTest
          OsFamily.Linux -> linuxTest
          OsFamily.Windows -> windowsTest
        }
      )
    }

    val jsTest by getting {
      dependencies {
        api(libs.okio.nodefilesystem)
      }
    }

    // Add the ICU4C tests for the build host platform.
    if (buildHostPlatform.supportsIcu4c) {
      val icu4cTest by creating {
        dependsOn(commonTest)
        dependencies {
          api(projects.okhttpIcu4c)
        }
      }
      val buildHostTest = get("${buildHostPlatform.lowerCamel}Test")
      buildHostTest.dependsOn(icu4cTest)
    }
  }
}

configure<MavenPublishBaseExtension> {
  configure(
    KotlinMultiplatform(
      javadocJar = JavadocJar.Dokka("dokkaGfm")
    )
  )
}
