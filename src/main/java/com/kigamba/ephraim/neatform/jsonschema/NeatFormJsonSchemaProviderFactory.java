package com.kigamba.ephraim.neatform.jsonschema;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ResourceUtil;
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider;
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory;
import com.jetbrains.jsonSchema.extension.SchemaType;
import com.jetbrains.jsonSchema.impl.JsonSchemaVersion;
import com.kigamba.ephraim.neatform.JsonPluginUtils;
import com.kigamba.ephraim.neatform.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NeatFormJsonSchemaProviderFactory implements JsonSchemaProviderFactory {

    private String schemaFilePath;

    @Override
    public @NotNull List<JsonSchemaFileProvider> getProviders(@NotNull Project project) {
        try {
            //schemaFilePath = JsonPluginUtils.saveJsonSchemaInProject(project);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<JsonSchemaFileProvider> providers = new ArrayList<>();
        providers.add(new NeatFormJsonSchemaProvider(schemaFilePath));
        return providers;
    }

    static class NeatFormJsonSchemaProvider implements JsonSchemaFileProvider {

        private String schemaFilePath;

        public NeatFormJsonSchemaProvider(@Nullable String schemaFilePath) {
            this.schemaFilePath = schemaFilePath;
        }

        @Override
        public boolean isAvailable(@NotNull VirtualFile file) {
            boolean validateFile = file.getName().endsWith(Constants.NEAT_FORM_FILE_EXTENSION) && !file.isDirectory() && file.isValid();
            return validateFile;
        }

        @Override
        public @NotNull String getName() {
            return JsonPluginUtils.NEAT_JSON_FORM_SCHEMA_NAME;
        }

        @Override
        public @Nullable VirtualFile getSchemaFile() {
            /*VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(schemaFilePath);
            return virtualFile;*/
            URL url = ResourceUtil.getResource(getClass().getClassLoader(), "schema-files", "neat_form_schema_validator-v3.json");
            VirtualFile virtualFile = VfsUtil.findFileByURL(url);
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
