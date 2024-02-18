package net.prasenjit.tools.gradle.duplicateclass;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class DuplicateClassPlugin implements Plugin<Project> {
    public static final String PLUGIN_ID = "net.prasenjit.tools.duplicateClass";
    private static void registerDuplicateCheckTask(@NotNull Project project) {
        DuplicateClassTask checkDependency = project.getTasks()
                .create("checkDuplicateClass", DuplicateClassTask.class, project);
        checkDependency.mustRunAfter(JavaPlugin.CLASSES_TASK_NAME);
        project.getTasks().getByName("check").dependsOn(checkDependency);
    }

    @Override
    public void apply(@NotNull Project project) {
        if (project.getPlugins().hasPlugin(JavaPlugin.class)) {
            DuplicateClassExtension extension = project.getExtensions()
                    .create("duplicateCheck", DuplicateClassExtension.class);
            if (extension.getEnabled().get()) {
                registerDuplicateCheckTask(project);
            }
        }
    }
}
