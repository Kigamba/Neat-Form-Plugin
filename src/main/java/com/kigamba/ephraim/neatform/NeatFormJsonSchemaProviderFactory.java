package com.kigamba.ephraim.neatform;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider;
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory;
import com.jetbrains.jsonSchema.extension.SchemaType;
import com.jetbrains.jsonSchema.impl.JsonSchemaVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NeatFormJsonSchemaProviderFactory implements JsonSchemaProviderFactory {

    private String schemaFilePath;

    @Override
    public @NotNull List<JsonSchemaFileProvider> getProviders(@NotNull Project project) {
        try {
            schemaFilePath = JsonPluginUtils.saveJsonSchemaInProject(project);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<JsonSchemaFileProvider> providers = new ArrayList<>();
        providers.add(new NeatFormJsonSchemaProvider(schemaFilePath));
        return providers;
    }

    static class NeatFormJsonSchemaProvider implements JsonSchemaFileProvider {

        private String schemaFilePath;

        public NeatFormJsonSchemaProvider(@NotNull String schemaFilePath) {
            this.schemaFilePath = schemaFilePath;
        }

        @Override
        public boolean isAvailable(@NotNull VirtualFile file) {
            boolean validateFile = file.getName().endsWith(".neat.json") && !file.isDirectory() && file.isValid();
            return validateFile;
        }

        @Override
        public @NotNull String getName() {
            return JsonPluginUtils.NEAT_JSON_FORM_SCHEMA_NAME;
        }

        @Override
        public @Nullable VirtualFile getSchemaFile() {
            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(schemaFilePath);
            return virtualFile;
        }

        @Override
        public @NotNull SchemaType getSchemaType() {
            return SchemaType.userSchema;
        }

        @Override
        public JsonSchemaVersion getSchemaVersion() {
            return JsonSchemaVersion.SCHEMA_7;
        }
    }
}