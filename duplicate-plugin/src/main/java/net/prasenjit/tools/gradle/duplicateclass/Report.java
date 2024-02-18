package net.prasenjit.tools.gradle.duplicateclass;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Report {
    private final Map<Set<String>, Set<String>> data = new HashMap<>();

    public Map<Set<String>, Set<String>> getData() {
        return data;
    }

    public void add(Map.Entry<String, Set<String>> entry){
        data.computeIfAbsent(entry.getValue(), k->new HashSet<>()).add(entry.getKey());
    }

    public void combine(Report r){
        this.data.putAll(r.data);
    }

    public void print(PrintStream out){
        if (data.isEmpty()){
            out.println("======Duplicate Report======");
            out.println("           PASSED");
            out.println("============================");
        } else {
            out.println("======Duplicate Report======");
            out.println("           FAILED");
            out.println("============================");
        }
    }
}
