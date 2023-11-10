object LibExt {
    const val groupId = "com.github.xpenatan.gdx-multiview"
    val libVersion: String = getVersion("1.0.0", "b1")
    const val gdxVersion = "1.12.1"
}

private fun getVersion(releaseVersion: String, suffix: String = ""): String {
    val isRelease = System.getenv("RELEASE")
    var libVersion = "${releaseVersion}-SNAPSHOT"
    if(isRelease != null && isRelease.toBoolean()) {
        libVersion = releaseVersion + if(suffix.isNotEmpty()) "-${suffix}" else ""
    }
    println("gdx-multi-view Version: $libVersion")
    return libVersion
}