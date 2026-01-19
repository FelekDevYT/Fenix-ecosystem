package me.felek.fenix.compiler;

import javax.crypto.Mac;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Preprocessor {
    private static final class Macros {
        String name;
        List<String> parameters = new ArrayList<>();
        StringBuilder body = new StringBuilder();
    }

    public final Map<String, String> defines = new HashMap<>();
    public final Map<String, Macros> macros = new HashMap<>();

    public String preprocess(String text) {
        StringBuilder code = new StringBuilder();
        String[] lines = text.split("\n");
        List<String> filesToInclude = new ArrayList<>();

        boolean isMacroDefines = false;
        Macros current = null;

        for (String line : lines) {
            String trimmed = line.trim();

            if (isMacroDefines) {
                if (trimmed.equalsIgnoreCase("%endmacro")) {
                    isMacroDefines = false;
                    macros.put(current.name.toUpperCase(), current);
                } else {
                    current.body.append(line).append("\n");
                }

                continue;
            }

            if (trimmed.toUpperCase().startsWith("%DEFINE")) {
                String[] parts = line.split("\\s+", 3);
                if (parts.length == 3) {
                    defines.put(parts[1], parts[2]);
                }

                continue;
            }

            if (trimmed.toUpperCase().startsWith("%MACRO")) {
                String[] parts = line.split("\\s+", 3);
                isMacroDefines = true;
                current = new Macros();
                current.name = parts[1];
                for (int i = 2; i < parts.length; i++) {
                    current.parameters.add(parts[i]);
                }

                continue;
            }

            if (trimmed.toUpperCase().startsWith("%INCLUDE")) {
                String[] parts = trimmed.split("\\s+", 2);
                if (parts.length == 2) {
                    String filePath = parts[1].replaceAll("\"", "");
                    filesToInclude.add(filePath);
                }
                continue;
            }

            String[] parts = trimmed.split("\\s+", 2);
            String command = parts[0].toUpperCase();
            if (macros.containsKey(command)) {
                Macros mac = macros.get(command);
                String[] args = (parts.length > 1) ? parts[1].split(",\\s*") : new String[0];

                if (args.length != mac.parameters.size()) {
                    code.append("; ERROR: Macro '").append(current.name).append("' called with wrong number of arguments.\n");
                    continue;
                }

                String expandedBody = current.body.toString();
                for (int i = 0; i < args.length; i++) {
                    expandedBody = expandedBody.replace("%" + (i + 1), args[i]);
                }
                code.append(expandedBody);
                continue;
            }

            String processedLine = line;
            for (Map.Entry<String, String> entry : defines.entrySet()) {
                processedLine = processedLine.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
            }
            code.append(processedLine).append("\n");
        }
        for (String path : filesToInclude) {
            String fileContent = readFileContent(path);
            code.append("\n; --- Included from file: ").append(path).append(" ---\n");
            code.append(fileContent).append("\n");
        }

        System.out.println(code.toString());

        return code.toString();
    }

    private String readFileContent(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            return "; ERROR: Could not include file '" + path + "'. Reason: " + e.getMessage();
        }
    }
}
