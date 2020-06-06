package com.kigamba.ephraim.neatform;


import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.kigamba.ephraim.neatform.jsonschema.NeatFormJsonSchemaProviderFactory;
import org.jetbrains.annotations.NotNull;

/**
 * This listener has been been deprecated for adding the Neat Form JSON Schema in favour of {@link NeatFormJsonSchemaProviderFactory}
 * which is a direct plugin extension for JSON Schema to provide schemas programmatically.
 *
 * This listener depends on a higher version of Intellij IDEA not currently on Android studio 3.6
 *
 * Listener to detect project open and close.
 * Depends on org.intellij.sdk.maxOpenProjects.ProjectCountingService
 */
@Deprecated
public class ProjectOpenCloseListener implements ProjectManagerListener {

    /**
     * Invoked on project open.
     *
     * @param project opening project
     */
    @Override
    public void projectOpened(@NotNull Project project) {
        // Ensure this isn't part of testing
        /*if (ApplicationManager.getApplication().isUnitTestMode()) return;


        try {

            if (!isNeatFormSchemaRegistered(project)) {
                addJsonSchema(project);
            } else {
                JsonPluginUtils.print("The user-defined schema already exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /*private boolean isNeatFormSchemaRegistered(@NotNull Project project) {
        JsonSchemaMappingsProjectConfiguration projectCountingService = project.getService(JsonSchemaMappingsProjectConfiguration.class);
        return projectCountingService.getStateMap().containsKey(JsonPluginUtils.NEAT_JSON_FORM_SCHEMA_NAME);
    }

    private void addJsonSchema(@NotNull Project project) throws Exception {
        JsonSchemaMappingsProjectConfiguration projectCountingService = project.getService(JsonSchemaMappingsProjectConfiguration.class);
        JsonPluginUtils.SampleClass sampleClass = new JsonPluginUtils.SampleClass(project).saveJsonSchemaInProject();
        String jsonSchemasFolderName = sampleClass.getJsonSchemasFolderName();
        String fileName = sampleClass.getFileName();


        // Increment the project count
        ArrayList<UserDefinedJsonSchemaConfiguration.Item> neatFormPatterns = new ArrayList<>();
        neatFormPatterns.add(new UserDefinedJsonSchemaConfiguration.Item("*.neat.json", JsonMappingKind.Pattern));
        UserDefinedJsonSchemaConfiguration userDefinedJsonSchemaConfiguration = new UserDefinedJsonSchemaConfiguration(JsonPluginUtils.NEAT_JSON_FORM_SCHEMA_NAME, JsonSchemaVersion.SCHEMA_7, jsonSchemasFolderName + File.separator + fileName, true, neatFormPatterns);

        projectCountingService.addConfiguration(userDefinedJsonSchemaConfiguration);
    }*/

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

}