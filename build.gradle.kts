plugins {
    id("java")
    id("application")
    id("org.javamodularity.moduleplugin") version "1.8.12"
    id("org.openjfx.javafxplugin") version "0.0.14"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        url = uri("https://mvnrepository.artifact.advaita.su/repository/advaita/")
    }
    mavenCentral()
}

dependencies {
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.24.1"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))

    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.1")
    implementation("info.debatty:java-string-similarity:2.0.0")

    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-core")

    // Используйте в случае если библиотеки отказываются подгружаться из репозитория
//    implementation(files("libs/log4j-core-2.24.1.jar"))
//    implementation(files("libs/log4j-api-2.24.1.jar"))
}

javafx {
    version = "25.0.1"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics", "javafx.base")
}

application {
    mainClass.set("org.Main.Main")
    applicationDefaultJvmArgs = listOf(
        "--enable-native-access=javafx.graphics",
    )
}

tasks.withType<Javadoc> {
    destinationDir = layout.buildDirectory.dir("docs/javadoc").get().asFile

    options {
        this as StandardJavadocDocletOptions
        addBooleanOption("Xdoclint:none", true) // Suppresses some warnings
        addStringOption("source", "25")
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }

    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveBaseName.set("swapi-client")
}

java {
    withJavadocJar()
}

tasks.test {
    useJUnitPlatform()
}