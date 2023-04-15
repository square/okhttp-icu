import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import org.jetbrains.kotlin.gradle.tasks.KotlinTest

buildscript {
  repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
  }
  dependencies {
    classpath(libs.mavenPublish.gradle.plugin)
    classpath(libs.kotlin.gradle.plugin)
    classpath(libs.dokka.gradle.plugin)
    classpath(libs.cklib.gradle.plugin)
  }
}

apply(plugin = "com.vanniktech.maven.publish.base")

allprojects {
  group = "com.squareup.okhttpicu"
  version = project.property("VERSION_NAME") as String

  repositories {
    mavenCentral()
    google()
  }
}

subprojects {
//  plugins.withId("com.android.library") {
//    extensions.configure<BaseExtension> {
//      lintOptions {
//        textReport = true
//        textOutput("stdout")
//        lintConfig = rootProject.file("lint.xml")
//
//        isCheckDependencies = true
//        isCheckTestSources = false // TODO true https://issuetracker.google.com/issues/138247523
//        isExplainIssues = false
//
//        // We run a full lint analysis as build part in CI, so skip vital checks for assemble task.
//        isCheckReleaseBuilds = false
//      }
//    }
//  }
}

allprojects {
  tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets.configureEach {
      reportUndocumented.set(false)
      skipDeprecated.set(true)
      jdkVersion.set(8)
    }
    if (name == "dokkaGfm") {
      outputDirectory.set(project.file("${project.rootDir}/docs/0.x"))
    }
  }

  // Don't attempt to sign anything if we don't have an in-memory key. Otherwise, the 'build' task
  // triggers 'signJsPublication' even when we aren't publishing (and so don't have signing keys).
  tasks.withType<Sign>().configureEach {
    enabled = project.findProperty("signingInMemoryKey") != null
  }

  tasks.withType<KotlinTest>().configureEach {
    testLogging {
      events(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
      exceptionFormat = TestExceptionFormat.FULL
      showStandardStreams = true
      showStackTraces = true
    }
  }

  tasks.withType<KotlinNativeTest>().configureEach {
    // https://stackoverflow.com/a/53604237/40013
    environment("SIMCTL_CHILD_OKHTTP_ICU_ROOT_DIR", rootDir)
    environment("OKHTTP_ICU_ROOT_DIR", rootDir)
  }

  plugins.withId("org.jetbrains.kotlin.multiplatform") {
    configure<KotlinMultiplatformExtension> {
      jvmToolchain(11)
    }
  }

  plugins.withId("org.jetbrains.kotlin.jvm") {
    configure<KotlinJvmProjectExtension> {
      jvmToolchain(11)
    }
  }

  plugins.withId("com.vanniktech.maven.publish.base") {
    configure<MavenPublishBaseExtension> {
      publishToMavenCentral(SonatypeHost.S01, automaticRelease = true)
      signAllPublications()
      pom {
        description.set("A minimal subset of ICU required by OkHttp.")
        name.set(project.name)
        url.set("https://github.com/square/okhttp-icu/")
        licenses {
          license {
            name.set("The Apache Software License, Version 2.0")
            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            distribution.set("repo")
          }
        }
        developers {
          developer {
            id.set("square")
            name.set("Square, Inc.")
          }
        }
        scm {
          url.set("https://github.com/square/okhttp-icu/")
          connection.set("scm:git:https://github.com/square/okhttp-icu.git")
          developerConnection.set("scm:git:ssh://git@github.com/square/okhttp-icu.git")
        }
      }
    }
  }

  // Our CI builds run on either 'ubuntu-latest', 'macOS-latest', or 'windows-latest'. Run each
  // Kotlin/Native task on exactly one of these. (We include non-matching platforms in our Gradle
  // configuration to get full set of platforms listed in the published .module file.)
  tasks.all {
    val kotlinNativePlatform = this.kotlinNativePlatform ?: return@all
    onlyIf { kotlinNativePlatform.osFamily == buildHostPlatform.osFamily }
  }
}
