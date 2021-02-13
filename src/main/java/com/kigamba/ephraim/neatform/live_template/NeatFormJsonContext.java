package com.kigamba.ephraim.neatform.live_template;

import com.intellij.codeInsight.template.FileTypeBasedContextType;
import com.intellij.json.JsonFileType;
import com.intellij.json.psi.JsonFile;
import com.intellij.psi.PsiFile;
import com.kigamba.ephraim.neatform.util.Constants;
import org.jetbrains.annotations.NotNull;

public class NeatFormJsonContext extends FileTypeBasedContextType {

    public static final String NEAT_JSON_FORM = "Neat Form JSON";
    public static final String NEAT_JSON_FORM_ID = "NEAT_FORM_JSON";

    public NeatFormJsonContext() {
        super(NEAT_JSON_FORM_ID, NEAT_JSON_FORM, JsonFileType.INSTANCE);
    }

    @Override
    public boolean isInContext(@NotNull PsiFile file, int offset) {
        return file instanceof JsonFile && file.getName().endsWith(Constants.NEAT_FORM_FILE_EXTENSION) && !file.isDirectory() && file.isValid();
    }
}
