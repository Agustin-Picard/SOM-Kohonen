import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.21"
}

group = "agustinpicard"
version = "0.0.1"

repositories {
    mavenCentral()
    maven { url = uri("http://dl.bintray.com/tomasvolker/maven") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")

    implementation(group = "tomasvolker", name = "numeriko-core", version = "0.0.3")
    implementation(group = "tomasvolker", name = "kyplot", version = "0.0.1")
    implementation(group = "com.github.tomasvolker", name = "parallel-utils", version = "v1.0")
}

application { 
    mainClassName = "agustinpicard.demo.MainKt"
}

val fatJar = task<Jar>("fatJar") {
    baseName = "${project.name}-fat"
    manifest {
        attributes["Main-Class"] = "agustinpicard.demo.MainKt"
    }
    from(configurations.runtime.map { if (it.isDirectory) it else zipTree(it) })
    with(tasks["jar"] as CopySpec)
}

val buildExecutable = task<Copy>("buildExecutable") {
    from(fatJar)
    into("./output")
    rename(".*", "tp_kohonen.jar")
}

tasks {
    "build" {
        dependsOn(buildExecutable)
    }
}

/*configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}*/