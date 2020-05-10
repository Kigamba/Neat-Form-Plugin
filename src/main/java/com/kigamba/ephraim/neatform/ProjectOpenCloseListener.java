package com.kigamba.ephraim.neatform;


import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.jetbrains.jsonSchema.JsonMappingKind;
import com.jetbrains.jsonSchema.JsonSchemaMappingsProjectConfiguration;
import com.jetbrains.jsonSchema.UserDefinedJsonSchemaConfiguration;
import com.jetbrains.jsonSchema.impl.JsonSchemaVersion;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Listener to detect project open and close.
 * Depends on org.intellij.sdk.maxOpenProjects.ProjectCountingService
 */
public class ProjectOpenCloseListener implements ProjectManagerListener {

    public static final String NEAT_JSON_FORM_SCHEMA_NAME = "Neat Form Plugin-Defined Schema";

    /**
     * Invoked on project open.
     *
     * @param project opening project
     */
    @Override
    public void projectOpened(@NotNull Project project) {
        // Ensure this isn't part of testing
        if (ApplicationManager.getApplication().isUnitTestMode()) return;


        try {

            if (!isNeatFormSchemaRegistered(project)) {
                addJsonSchema(project);
            } else {
                print("The user-defined schema already exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isNeatFormSchemaRegistered(@NotNull Project project) {
        JsonSchemaMappingsProjectConfiguration projectCountingService = project.getService(JsonSchemaMappingsProjectConfiguration.class);
        return projectCountingService.getStateMap().containsKey(NEAT_JSON_FORM_SCHEMA_NAME);
    }

    private void addJsonSchema(@NotNull Project project) throws Exception {
        JsonSchemaMappingsProjectConfiguration projectCountingService = project.getService(JsonSchemaMappingsProjectConfiguration.class);
        String projectFilePath = project.getProjectFilePath().replace(".idea/misc.xml", "");
        String jsonSchemasFolderName = "json-schemas";
        String schemaFolderPath = projectFilePath + File.separator + jsonSchemasFolderName;
        String pluginVersion = PluginManager.getPlugin(PluginId.getId("org.shan")).getVersion();
        String fileName = "neat-form-json-schema-v" + pluginVersion + ".json";
        String jsonSchemaLocation = schemaFolderPath + File.separator + fileName;
        File jsonSchemaFile = new File(jsonSchemaLocation);

        if (!jsonSchemaFile.exists()) {
            if ((new File(schemaFolderPath)).mkdirs()) {
                ExportResource("/schema-files/neat_form_schema_validator-v3.json", jsonSchemaLocation);
            } else {
                print("Failed to create some directories in the project");
            }
        }


        // Increment the project count
        ArrayList<UserDefinedJsonSchemaConfiguration.Item> neatFormPatterns = new ArrayList<>();
        neatFormPatterns.add(new UserDefinedJsonSchemaConfiguration.Item("*.neat.json", JsonMappingKind.Pattern));
        UserDefinedJsonSchemaConfiguration userDefinedJsonSchemaConfiguration = new UserDefinedJsonSchemaConfiguration(NEAT_JSON_FORM_SCHEMA_NAME, JsonSchemaVersion.SCHEMA_7, jsonSchemasFolderName + File.separator + fileName, true, neatFormPatterns);

        projectCountingService.addConfiguration(userDefinedJsonSchemaConfiguration);
    }

    private void print(@NotNull String toPrint) {
        System.out.println("Neat Form Plugin: " + toPrint);
    }

    /**
     * Invoked on project close.
     *
     * @param project closing project
     */
    @Override
    public void projectClosed(@NotNull Project project) {
        // Ensure this isn't part of testing
        if (ApplicationManager.getApplication().isUnitTestMode()) return;
        // Get the counting service
        /*ProjectCountingService projectCountingService = ServiceManager.getService(ProjectCountingService.class);
        // Decrement the count because a project just closed
        projectCountingService.decrProjectCount();*/
    }

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

}