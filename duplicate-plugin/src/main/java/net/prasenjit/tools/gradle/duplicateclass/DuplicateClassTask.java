package net.prasenjit.tools.gradle.duplicateclass;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.file.RegularFile;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class DuplicateClassTask extends DefaultTask {

    private final Project project;
    @OutputFile
    private File outputFile;

    @Inject
    public DuplicateClassTask(Project project) {
        this.project = project;
        Provider<RegularFile> provider = project.getLayout().getBuildDirectory().file("dependency-report.txt");
        outputFile = provider.map(RegularFile::getAsFile).get();
    }

    @TaskAction
    public void examine() {
        System.out.println("Duplicate check task executed");
        Map<String, File> dependenciesMap = new HashMap<>();
        project.getConfigurations().forEach(configuration -> {
            if (configuration.isCanBeResolved()) {
                Set<ResolvedArtifact> resolvedArtifacts = configuration.getResolvedConfiguration().getResolvedArtifacts();
                Map<String, File> collect = resolvedArtifacts.stream()
                        .collect(Collectors.toMap(ResolvedArtifact::getName, ResolvedArtifact::getFile));
                dependenciesMap.putAll(collect);
            }
        });

        JavaPluginExtension javaPlugin = project.getExtensions().getByType(JavaPluginExtension.class);
        SourceSet mainSourceSet = javaPlugin.getSourceSets().getByName("main");

        int i = 1;
        for (File classesDir : mainSourceSet.getOutput().getClassesDirs()) {
            dependenciesMap.put("mainSource-" + i, classesDir);
        }

        Report report = generateReport(dependenciesMap);
        report.print(System.out);
    }

    private Report generateReport(Map<String, File> dependency) {
        Map<String, Set<String>> interMediate = new HashMap<>();
        dependency.forEach((name, file) -> {
            if (file.isDirectory()) {
                try {
                    Files.walkFileTree(file.toPath(), new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                            pushEntry(interMediate, file.toString(), name);
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try (JarFile jarFile = new JarFile(file);) {
                    jarFile.stream().forEach(jarEntry -> {
                        if (jarEntry.getName().endsWith(".class")) {
                            pushEntry(interMediate, jarEntry.getName(), name);
                        }
                    });
                } catch (IOException e) {
                    System.err.println("Error reading jar file: " + file.getName());
                }
            }
        });
        return interMediate.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .collect(Report::new, Report::add, Report::combine);
    }

    private void pushEntry(Map<String, Set<String>> interMediate, String classFile, String name) {
        interMediate.computeIfAbsent(classFile, e -> new HashSet<>()).add(name);
    }

    public File getOutputFile() {
        return outputFile;
    }
}
