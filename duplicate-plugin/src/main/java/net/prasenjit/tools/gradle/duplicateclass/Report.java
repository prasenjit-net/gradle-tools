package net.prasenjit.tools.gradle.duplicateclass;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Report {
    private final Map<Set<String>, Set<String>> data = new HashMap<>();

    public void add(Map.Entry<String, Set<String>> entry){
        data.computeIfAbsent(entry.getValue(), k->new HashSet<>()).add(entry.getKey());
    }

    public void combine(Report r){
        this.data.putAll(r.data);
    }

    public void print(PrintWriter out){
        if (data.isEmpty()){
            out.println("======Duplicate Report======");
            out.println("           PASSED");
            out.println("============================");
        } else {
            out.println("======Duplicate Report======");
            out.println("           FAILED");
            data.forEach((k,v)->{
                out.println("=====================================");
                out.println("Module: ");
                k.forEach(out::println);
                out.println("Classes: ");
                v.forEach(out::println);
                out.println("=====================================");
            });
            out.println("============================");
        }
        out.flush();
    }

    public void write(File outputFile) {
        try (PrintWriter writer = new PrintWriter(outputFile)){
            print(writer);
        } catch (Exception e){
            throw new RuntimeException("Error writing report to file", e);
        }
    }
}
