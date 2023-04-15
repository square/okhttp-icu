import org.gradle.api.Task

enum class OsFamily {
  Apple,
  Linux,
  Windows,
}

enum class CpuArchitecture {
  Arm64,
  X64,
}

enum class KotlinNativePlatform(
  val osFamily: OsFamily,
) {
  IosArm64(OsFamily.Apple),
  IosSimulatorArm64(OsFamily.Apple),
  IosX64(OsFamily.Apple),
  LinuxX64(OsFamily.Linux),
  MacosArm64(OsFamily.Apple),
  MacosX64(OsFamily.Apple),
  MingwX64(OsFamily.Windows),
  TvosArm64(OsFamily.Apple),
  TvosSimulatorArm64(OsFamily.Apple),
  TvosX64(OsFamily.Apple),
  WatchosArm32(OsFamily.Apple),
  WatchosArm64(OsFamily.Apple),
  WatchosSimulatorArm64(OsFamily.Apple),
  WatchosX64(OsFamily.Apple),
  WatchosX86(OsFamily.Apple);

  val lowerCamel: String = name.substring(0, 1).lowercase() + name.substring(1)

  val supportsIcu4c: Boolean
    get() = this == LinuxX64 || this == MacosX64 || this == MacosArm64

  internal fun matchesTask(taskName: String): Boolean {
    val result = name in taskName || lowerCamel in taskName

    if (result) {
      check(taskName in knownTasks) {
        "unexpected task name $taskName contains a Kotlin/Native platform name: update knownTasks?"
      }
    }

    return result
  }

  /**
   * These are the Kotlin/Native task names we know about. We keep this allowlist to avoid
   * accidentally skipping a task that we don't want to skip.
   */
  val knownTasks = setOf(
    "${lowerCamel}Binaries",
    "${lowerCamel}MainBinaries",
    "${lowerCamel}MainKlibrary",
    "${lowerCamel}MetadataJar",
    "${lowerCamel}ProcessResources",
    "${lowerCamel}ReleaseTest",
    "${lowerCamel}SourcesJar",
    "${lowerCamel}TestProcessResources",
    "${lowerCamel}TestKlibrary",
    "${lowerCamel}TestBinaries",
    "${lowerCamel}Test",
    "cinteropIcu4c${this}",
    "clean${this}ReleaseTest",
    "clean${this}Test",
    "compileKotlin${this}",
    "compileTestKotlin${this}",
    "copyCinteropIcu4c${this}",
    "generateMetadataFileFor${this}Publication",
    "generatePomFileFor${this}Publication",
    "link${this}",
    "linkDebugTest${this}",
    "linkReleaseTest${this}",
    "publish${this}PublicationToMavenCentralRepository",
    "publish${this}PublicationToMavenLocal",
    "sign${this}Publication",
  )
}

val Task.kotlinNativePlatform: KotlinNativePlatform?
  get() = KotlinNativePlatform.values().firstOrNull { it.matchesTask(name) }

val buildHostOsFamily: OsFamily = run {
  val osName = System.getProperty("os.name")
  when {
    osName == "Mac OS X" -> OsFamily.Apple
    osName == "Linux" -> OsFamily.Linux
    osName.startsWith("Windows", ignoreCase = true) -> OsFamily.Windows
    else -> error("Unexpected build host: $osName")
  }
}

val buildHostCpuArchitecture: CpuArchitecture = when (val osArch = System.getProperty("os.arch")) {
  "x86_64", "amd64" -> CpuArchitecture.X64
  "aarch64" -> CpuArchitecture.Arm64
  else -> error("Unexpected build architecture: $osArch")
}

val buildHostPlatform: KotlinNativePlatform = when (buildHostOsFamily) {
  OsFamily.Apple -> when (buildHostCpuArchitecture) {
    CpuArchitecture.Arm64 -> KotlinNativePlatform.MacosArm64
    CpuArchitecture.X64 -> KotlinNativePlatform.MacosX64
    else -> error("unexpected build host: $buildHostCpuArchitecture")
  }
  OsFamily.Linux -> KotlinNativePlatform.LinuxX64
  OsFamily.Windows -> KotlinNativePlatform.MingwX64
}
