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
//  android {
//    publishLibraryVariants("release")
//  }
  jvm()

//  js {
//    nodejs()
//  }

//  linuxX64()
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
        api(libs.okio.core)
      }
    }

    val nativeMain by creating {
      dependsOn(commonMain)
    }
    val nativeTest by creating {
      dependsOn(commonTest)
      dependencies {
        api(projects.okhttpIcu4c)
      }
    }

    targets.withType<KotlinNativeTarget> {
      val main by compilations.getting
      main.defaultSourceSet.dependsOn(nativeMain)

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

    val appleMain by creating {
      dependsOn(nativeMain)
    }
    val appleTest by creating {
      dependsOn(nativeTest)
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
  }
}

configure<MavenPublishBaseExtension> {
  configure(
    KotlinMultiplatform(
      javadocJar = JavadocJar.Dokka("dokkaGfm")
    )
  )
}
