dependencies {
    implementation(project(":examples:imgui-window:core"))
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:${LibExt.gdxVersion}")
    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation("com.github.xpenatan.xImGui:imgui-ext-desktop:${LibExt.xImguiVersion}")
}

val mainClassName = "com.github.xpenatan.imgui.example.viewport.Main"
val assetsDir = File("../assets");

tasks.register<JavaExec>("viewport-run-desktop") {
    group = "example-desktop"
    description = "Run desktop app"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = assetsDir
}