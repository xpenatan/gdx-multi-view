val moduleName = "imgui-window"

dependencies {
    implementation(project(":multiview:core"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation("com.github.xpenatan.gdx-imgui:gdx:${LibExt.gdxImguiVersion}")
    implementation("com.github.xpenatan.gdx-imgui:core:${LibExt.gdxImguiVersion}")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}