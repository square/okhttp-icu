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

  linuxX64()
  macosX64()
  macosArm64()
//  iosArm64()
//  iosX64()
//  iosSimulatorArm64()
//  tvosArm64()
//  tvosSimulatorArm64()
//  tvosX64()
  mingwX64()

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

    val icu4cTest by creating {
      dependsOn(commonTest)
      dependencies {
        api(projects.okhttpIcu4c)
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

    val linuxX64Main by getting {
      dependsOn(commonMain)
      dependencies {
        api(projects.okhttpIcu4c)
      }
    }
    val linuxX64Test by getting {
      dependsOn(icu4cTest)
    }

    val appleMain by creating {
      dependsOn(commonMain)
    }
    val appleTest by creating {
      dependsOn(commonTest)
      dependsOn(icu4cTest)
    }
    val macosArm64Main by getting {
      dependsOn(appleMain)
    }
    val macosArm64Test by getting {
      dependsOn(appleTest)
    }

    val macosX64Main by getting {
      dependsOn(appleMain)
    }
    val macosX64Test by getting {
      dependsOn(appleTest)
    }

    val jsTest by getting {
      dependencies {
        api(libs.okio.nodefilesystem)
      }
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
