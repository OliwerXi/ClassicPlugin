plugins {
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "6.0.0"
    `maven-publish`
}

group = "club.rarlab"
version = "0.0.1"

repositories {
    mavenCentral()
    mavenLocal()

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    // Shaded Dependencies
    implementation(kotlin("stdlib-jdk8"))

    // Provided Dependencies
    compileOnly("org.spigotmc:spigot:1.16.1-R0.1-SNAPSHOT")
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")

    from(sourceSets["main"].allSource)
    dependsOn(JavaPlugin.CLASSES_TASK_NAME)
}

val javadocJar by tasks.creating(Jar::class) {
    archiveClassifier.set("javadoc")

    from(tasks.javadoc)
    dependsOn(JavaPlugin.JAVADOC_TASK_NAME)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(sourcesJar)
            artifact(javadocJar)

            from(components["java"])
        }
    }

    repositories {
        mavenLocal()
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}