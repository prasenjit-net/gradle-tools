plugins {
    `java-gradle-plugin`
    `maven-publish`
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("duplicatePlugin") {
            id = "net.prasenjit.tools.duplicateClass"
            implementationClass = "net.prasenjit.tools.gradle.duplicateclass.DuplicateClassPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/prasenjit-net/gradle-tools")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
}


dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}