val moduleName = "imgui-window"

dependencies {
    implementation(project(":multiview:core"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation("com.github.xpenatan.xImGui:imgui-ext-core:${LibExt.xImguiVersion}")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}