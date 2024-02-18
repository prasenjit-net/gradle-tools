package net.prasenjit.tools.gradle.duplicateclass;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateClassPluginTest {

    @TempDir
    File testProjectDir;
    private File settingsFile;
    private File buildFile;

    @BeforeEach
    public void setup() {
        settingsFile = new File(testProjectDir, "settings.gradle");
        buildFile = new File(testProjectDir, "build.gradle");
    }

    @Test
    void apply() {
        Project project = ProjectBuilder.builder()
                .withName("test")
                .build();
        project.getPlugins().apply("java");
        project.getPlugins().apply(DuplicateClassPlugin.PLUGIN_ID);
    }

    @Test
    void test() throws IOException {
        Files.writeString(settingsFile.toPath(), "rootProject.name = \"test-plugin\"");
        String buildConfig = """
                plugins {
                    id 'java'
                    id 'net.prasenjit.tools.duplicateClass'
                }
                
                repositories {
                    mavenCentral()
                }
                
                dependencies {
                    implementation 'com.sun.xml.bind:jaxb:2.1.9'
                    implementation 'org.glassfish.jaxb:jaxb-runtime:4.0.4'
                    // commons lang3
                    implementation 'org.apache.commons:commons-lang3:3.12.0'
                }
                """;
        Files.writeString(buildFile.toPath(), buildConfig);

        BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withPluginClasspath()
                .withDebug(true)
                .forwardOutput()
                .withArguments("check")
                .build();

        System.out.println(result);
    }
}