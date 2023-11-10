val mainClassName = "com.github.xpenatan.gdx.multiview.example.basic.Main"
val assetsDir = File("/assets")

dependencies {
    implementation(project(":examples:basic:core"))

    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl:${LibExt.gdxVersion}")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:${LibExt.gdxVersion}:natives-desktop")
}

tasks.register<JavaExec>("core-run-desktop") {
    dependsOn("classes")
    group = "examples-desktop"
    description = "Run basic example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = assetsDir
}