import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class BuildIcu4c : DefaultTask() {

  /** See submodules/icu/icu4c/source/runConfigureICU for platform names & descriptions. */
  @get:Input
  abstract val platform: Property<String>

  init {
    description = "Run icu4c's configure and make to build a binary for the host platform"
  }

  @TaskAction
  fun build() {
    project.exec {
      workingDir("${project.rootDir}/submodules/icu/icu4c/source")

      commandLine(
        "./runConfigureICU",

        platform.get(),

        // Produce static libraries (.a files) which work nicely with Kotlin/Native.
        "--enable-static",
        "--disable-shared",
      )
    }

    project.exec {
      workingDir("${project.rootDir}/submodules/icu/icu4c/source")

      // Assume gnu-make.
      commandLine("make")
    }
  }
}
