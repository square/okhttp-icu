object OkHttpIcuBuild {
  val osArch = System.getProperty("os.arch")
  val isX64 = osArch == "x86_64" || osArch == "amd64"
  val isAarch64 = osArch == "aarch64"
  val osName = System.getProperty("os.name")
  val isMac = osName == "Mac OS X"
  val isLinux = osName == "Linux"
  val isWindows = osName.startsWith("Windows", ignoreCase = true)
}

/** This lists the platforms we know how to build. */
enum class KotlinNativePlatform {
  LinuxX64,
  MacosArm64,
  MacosX64,
  MingwX64;

  val lowerCamel: String
    get() = when (this) {
      LinuxX64 -> "linuxX64"
      MacosArm64 -> "macosArm64"
      MacosX64 -> "macosX64"
      MingwX64 -> "mingwX64"
    }

  fun matchesBuildHost(): Boolean = when (this) {
    LinuxX64 -> OkHttpIcuBuild.isLinux && OkHttpIcuBuild.isX64
    MacosArm64 -> OkHttpIcuBuild.isMac && OkHttpIcuBuild.isAarch64
    MacosX64 -> OkHttpIcuBuild.isMac && OkHttpIcuBuild.isX64
    MingwX64 -> OkHttpIcuBuild.isWindows && OkHttpIcuBuild.isX64
  }
}
