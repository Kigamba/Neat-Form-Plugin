package com.kigamba.ephraim.neatform.live_template;

import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.json.liveTemplates.JsonInPropertyKeysContextType;
import com.intellij.openapi.util.VolatileNullableLazyValue;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NeatFormJsonPropertyKeysContextType extends JsonInPropertyKeysContextType {

    private TemplateContextType templateContextType;

    @Override
    public boolean isInContext(@NotNull PsiFile file, int offset) {
        return LiveTemplateUtils.isInContext(file, offset) && super.isInContext(file, offset);
    }

    @Nullable
    public TemplateContextType getBaseContextType() {
        if (templateContextType == null) {
            Class<? extends TemplateContextType> baseContextType = NeatFormJsonContext.class;
            templateContextType = VolatileNullableLazyValue.createValue(() -> EP_NAME.findExtension(baseContextType)).getValue();
        }

        return templateContextType;
    }

    @NotNull
    public String getPresentableName() {
        return NeatFormJsonContext.NEAT_JSON_FORM + " Property Keys";
    }

    @NotNull
    public String getContextId() {
        return NeatFormJsonContext.NEAT_JSON_FORM_ID + "_PROPERTY_KEYS";
    }
}
