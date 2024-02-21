package net.prasenjit.tools.gradle.duplicateclass;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

import java.util.List;


public class DuplicateClassExtension {
    private final Property<Boolean> enabled;
    private final ListProperty<String> excludes;

    public DuplicateClassExtension(ObjectFactory objects) {
        enabled = objects.property(Boolean.class).convention(true);
        excludes = objects.listProperty(String.class).convention(getDefaultExcludes());
    }

    private List<String> getDefaultExcludes() {
        return List.of("META-INF/**", "module-info.class", "package-info.class");
    }

    public Property<Boolean> getEnabled() {
        return enabled;
    }

    public ListProperty<String> getExcludes() {
        return excludes;
    }
}
