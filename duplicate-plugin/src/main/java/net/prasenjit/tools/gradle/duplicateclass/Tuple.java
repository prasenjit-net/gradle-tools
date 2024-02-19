package net.prasenjit.tools.gradle.duplicateclass;

import org.gradle.api.artifacts.ResolvedArtifact;

import java.io.File;

/**
 * A tuple class to hold two generic values
 * Created by prasenjit on 5/6/2020.
 * prasenjit.net
 */
public class Tuple<T1, T2> {
    private final T1 t1;
    private final T2 t2;

    public Tuple(T1 t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public static Tuple<String, File> fromResolvedArtifact(ResolvedArtifact resolvedArtifact) {
        return new Tuple<>(resolvedArtifact.getId().getComponentIdentifier().getDisplayName(), resolvedArtifact.getFile());
    }

    public T1 getT1() {
        return t1;
    }

    public T2 getT2() {
        return t2;
    }
}
