package net.prasenjit.tools.gradle.duplicateclass;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;


public class DuplicateClassExtension {
    private final Property<Boolean> enabled;

    public DuplicateClassExtension(ObjectFactory objects) {
        enabled = objects.property(Boolean.class).convention(true);
    }

    public Property<Boolean> getEnabled() {
        return enabled;
    }
}
