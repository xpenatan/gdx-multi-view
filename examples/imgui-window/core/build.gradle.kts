dependencies {
//    implementation(project(":examples:basic:base"))
    implementation(project(":extensions:imgui-window"))
    implementation(project(":multiview:core"))

    // Required
    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:${LibExt.gdxVersion}")

    implementation("com.github.xpenatan.gdx-imgui:gdx-impl:${LibExt.gdxImguiVersion}")
    implementation("com.github.xpenatan.gdx-imgui:imgui-ext-core:${LibExt.gdxImguiVersion}")
}