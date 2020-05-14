package com.kigamba.ephraim.neatform;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class JsonPluginUtils {

    public static final String NEAT_JSON_FORM_SCHEMA_NAME = "Neat Form Plugin-Defined Schema";

    /**
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param resourceName ie.: "/SmartLibrary.dll"
     * @return The path to the exported resource
     * @throws Exception
     */
    static public String ExportResource(String resourceName, String finalPath) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        try {
            stream = ProjectOpenCloseListener.class.getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if (stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            //jarFolder = new File(ProjectOpenCloseListener.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');
            resStreamOut = new FileOutputStream(finalPath);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }

        return finalPath;
    }

    public static void print(@NotNull String toPrint) {
        System.out.println("Neat Form Plugin: " + toPrint);
    }

    public static String saveJsonSchemaInProject(@NotNull Project project) throws Exception {
        String projectFilePath = project.getProjectFilePath().replace(".idea/misc.xml", "");
        String jsonSchemasFolderName = "json-schemas";
        String schemaFolderPath = projectFilePath + File.separator + jsonSchemasFolderName;
        String pluginVersion = PluginManager.getPlugin(PluginId.getId("com.kigamba.ephraim.neatform")).getVersion();
        String fileName = "neat-form-json-schema-v" + pluginVersion + ".json";
        String jsonSchemaFilePath = schemaFolderPath + File.separator + fileName;
        File jsonSchemaFile = new File(jsonSchemaFilePath);

        if (!jsonSchemaFile.exists()) {
            if ((new File(schemaFolderPath)).mkdirs()) {
                ExportResource("/schema-files/neat_form_schema_validator-v3.json", jsonSchemaFilePath);
            } else {
                print("Failed to create some directories in the project");
            }
        }
        return jsonSchemaFilePath;
    }
}
