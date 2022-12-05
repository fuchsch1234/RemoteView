import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("multiplatform") version "1.7.21"
    kotlin("plugin.serialization") version "1.7.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
}

group = "de.fuchsch"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

val ktorVersion = "2.0.2"
val logbackVersion = "1.4.5"
val testContainersVersion = "1.17.6"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
        compilations {
            val main by getting

            val e2eTests by registering {
                defaultSourceSet {
                    kotlin
                    dependencies {
                        implementation(main.compileDependencyFiles + main.output.allOutputs)
                    }
                }

                tasks.register<Test>("e2eTest") {
                    group = "verification"

                    useJUnitPlatform()

                    systemProperty("junit.jupiter.extensions.autodetection.enabled", true)

                    classpath = compileDependencyFiles + runtimeDependencyFiles + output.allOutputs
                    testClassesDirs = output.classesDirs
                }
            }
        }
    }
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-server-html-builder-jvm:$ktorVersion")
                implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")

                implementation("ch.qos.logback:logback-classic:$logbackVersion")
            }
        }
        val jvmTest by getting
        val jvmE2eTests by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.hamcrest:hamcrest:2.2")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

                implementation("org.testcontainers:testcontainers-bom:$testContainersVersion")
                implementation("org.testcontainers:testcontainers:$testContainersVersion")
                implementation("org.testcontainers:junit-jupiter:$testContainersVersion")

                implementation("org.seleniumhq.selenium:selenium-java:4.6.0")
                implementation("io.github.bonigarcia:webdrivermanager:5.3.1")

                implementation("ch.qos.logback:logback-classic:$logbackVersion")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:18.2.0-pre.346")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:18.2.0-pre.346")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion:11.9.3-pre.346")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-redux:4.1.2-pre.346")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-redux:7.2.6-pre.346")
            }
        }
        val jsTest by getting
    }
}

application {
    mainClass.set("de.fuchsch.application.ServerKt")
}

tasks.named<Copy>("jvmProcessResources") {
    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
    from(jsBrowserDistribution)
}

tasks.withType<ShadowJar> {
    manifest {
        attributes("Main-Class" to "de.fuchsch.application.ServerKt")
    }
    archiveClassifier.set("all")
    val main by kotlin.jvm().compilations
    from(main.output)
    configurations += main.compileDependencyFiles as Configuration
    configurations += main.runtimeDependencyFiles as Configuration
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<ShadowJar>("shadowJar"))
    classpath(tasks.named<ShadowJar>("shadowJar"))
}
