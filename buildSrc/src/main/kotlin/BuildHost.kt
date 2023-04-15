enum class OsFamily {
  Apple,
  Linux,
  Windows
}

val buildHostOsFamily: OsFamily = run {
  val osName = System.getProperty("os.name")
  when {
    osName == "Mac OS X" -> OsFamily.Apple
    osName == "Linux" -> OsFamily.Linux
    osName.startsWith("Windows", ignoreCase = true) -> OsFamily.Windows
    else -> error("Unexpected build host: $osName")
  }
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

  fun matchesTask(taskName: String) = name in taskName || lowerCamel in taskName

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
