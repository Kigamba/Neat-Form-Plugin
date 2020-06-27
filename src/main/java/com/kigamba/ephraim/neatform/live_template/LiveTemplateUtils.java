package com.kigamba.ephraim.neatform.live_template;

import com.intellij.json.psi.JsonFile;
import com.intellij.psi.PsiFile;
import com.kigamba.ephraim.neatform.util.Constants;
import org.jetbrains.annotations.NotNull;

public class LiveTemplateUtils {

    public static boolean isInContext(@NotNull PsiFile file, int offset) {
        return file instanceof JsonFile && file.getName().endsWith(Constants.NEAT_FORM_FILE_EXTENSION) && !file.isDirectory() && file.isValid();
    }
}
