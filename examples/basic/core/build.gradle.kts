plugins {
    id("java")
    id("java-library")
}

dependencies {
    implementation(project(":multiview:core"))

    implementation("com.badlogicgames.box2dlights:box2dlights:1.4")
    implementation("com.badlogicgames.gdx:gdx-box2d:${LibExt.gdxVersion}")
}