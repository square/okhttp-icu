import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

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

println("osArch=${System.getProperty("os.arch")}")
println("osName=${System.getProperty("os.name")}")

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

  // Disable every task that doesn't match the build host. We haven't done the work to support
  // either cross-compiling ICU, or testing cross-compiled ICU.
  //
  // We still include non-matching platforms in our Gradle configuration because we want the full
  // set of platforms to be listed in the Kotlin Multiplatform .module file.
  tasks.all {
    for (kotlinNativePlatform in KotlinNativePlatform.values()) {
      if (kotlinNativePlatform.name !in name && kotlinNativePlatform.lowerCamel !in name) {
        continue
      }

      onlyIf { kotlinNativePlatform.matchesBuildHost() }
      check(
        name == "${kotlinNativePlatform.lowerCamel}Binaries" ||
        name == "${kotlinNativePlatform.lowerCamel}MainBinaries" ||
        name == "${kotlinNativePlatform.lowerCamel}MainKlibrary" ||
        name == "${kotlinNativePlatform.lowerCamel}MetadataJar" ||
        name == "${kotlinNativePlatform.lowerCamel}ProcessResources" ||
        name == "${kotlinNativePlatform.lowerCamel}ReleaseTest" ||
        name == "${kotlinNativePlatform.lowerCamel}SourcesJar" ||
        name == "${kotlinNativePlatform.lowerCamel}Test" ||
        name == "${kotlinNativePlatform.lowerCamel}TestBinaries" ||
        name == "${kotlinNativePlatform.lowerCamel}TestKlibrary" ||
        name == "${kotlinNativePlatform.lowerCamel}TestProcessResources" ||
        name == "cinteropIcu4c${kotlinNativePlatform}" ||
        name == "clean${kotlinNativePlatform}ReleaseTest" ||
        name == "clean${kotlinNativePlatform}Test" ||
        name == "compileKotlin${kotlinNativePlatform}" ||
        name == "compileTestKotlin${kotlinNativePlatform}" ||
        name == "copyCinteropIcu4c${kotlinNativePlatform}" ||
        name == "generateMetadataFileFor${kotlinNativePlatform}Publication" ||
        name == "generatePomFileFor${kotlinNativePlatform}Publication" ||
        name == "link${kotlinNativePlatform}" ||
        name == "linkDebugTest${kotlinNativePlatform}" ||
        name == "linkReleaseTest${kotlinNativePlatform}" ||
        name == "publish${kotlinNativePlatform}PublicationToMavenCentralRepository" ||
        name == "publish${kotlinNativePlatform}PublicationToMavenLocal" ||
        name == "sign${kotlinNativePlatform}Publication"
      ) {
        "unexpected task name $name contains a Kotlin/Native platform name: update this allowlist?"
      }
    }
  }
}
