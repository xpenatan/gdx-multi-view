plugins {
    id("java")
    id("maven-publish")
    id("signing")
}

subprojects {
    apply {
        plugin("java")
    }

    java.sourceCompatibility = JavaVersion.VERSION_11
    java.targetCompatibility = JavaVersion.VERSION_11

    repositories {
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/releases/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/releases/") }
        maven {
            url = uri("http://teavm.org/maven/repository/")
            isAllowInsecureProtocol = true
        }
        maven { url = uri("https://jitpack.io") }
    }

//    configurations.configureEach {
//        // Check for updates every sync
//        resolutionStrategy.cacheChangingModulesFor(0, "seconds")
//    }
}

var libProjects = mutableSetOf(
    project(":multiview:core"),
    project(":extensions:imgui-window"),
)

configure(libProjects) {
    apply(plugin = "signing")
    apply(plugin = "maven-publish")

    group = LibExt.groupId
    version = LibExt.libVersion

    publishing {
        repositories {
            maven {
                url = uri {
                    val ver = project.version.toString()
                    val isSnapshot = ver.uppercase().contains("SNAPSHOT")
                    val repoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
                    val repoUrlSnapshot = "https://oss.sonatype.org/content/repositories/snapshots/"
                    if(isSnapshot) repoUrlSnapshot else repoUrl
                }
                credentials {
                    username = System.getenv("USER")
                    password = System.getenv("PASSWORD")
                }
            }
        }
    }

    tasks.withType<Javadoc> {
        options.encoding = "UTF-8"
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    publishing.publications.configureEach {
        if (this is MavenPublication) {
            pom {
                name.set("gdx-multi-view")
                description.set("Render libgdx application in the framebuffer")
                url.set("https://github.com/xpenatan/gdx-multi-view")
                developers {
                    developer {
                        id.set("Xpe")
                        name.set("Natan")
                    }
                }
                scm {
                    connection.set("scm:git:git://https://github.com/xpenatan/gdx-multi-view.git")
                    developerConnection.set("scm:git:ssh://https://github.com/xpenatan/gdx-multi-view.git")
                    url.set("http://https://github.com/xpenatan/gdx-multi-view/tree/master")
                }
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
            }
        }
    }

    val signingKey = System.getenv("SIGNING_KEY")
    val signingPassword = System.getenv("SIGNING_PASSWORD")
    if(signingKey != null && signingPassword != null) {
        signing {
            useInMemoryPgpKeys(signingKey, signingPassword)
            publishing.publications.configureEach {
                sign(this)
            }
        }
    }
}