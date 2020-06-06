package com.kigamba.ephraim.neatform.autocomplete;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.json.JsonLanguage;
import com.intellij.json.psi.JsonPsiUtil;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.kigamba.ephraim.neatform.JsonRulesFileChecker;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class NeatFormJsonCompletionContributor extends CompletionContributor {

    public NeatFormJsonCompletionContributor() {
        extend( CompletionType.BASIC,
                PlatformPatterns.psiElement(PsiElement.class).withLanguage(JsonLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {

                        PsiElement psiElement = parameters.getPosition();

                        if (isRulesFile(parameters)) {
                            if (psiElement.getParent() instanceof JsonStringLiteral && JsonPsiUtil.isPropertyValue(psiElement.getParent())) {
                                PsiElement propertyKey = psiElement.getParent().getParent().getFirstChild();
                                if (JsonPsiUtil.isPropertyKey(propertyKey) && propertyKey instanceof JsonStringLiteral) {
                                    String propertyName = ((JsonStringLiteral) propertyKey).getValue();
                                    if (propertyName.equals("name")) {
                                        for (HashMap<String, PsiElement> fieldNames: JsonRulesFileChecker.filesFieldNames.values()) {
                                            ArrayList<LookupElementBuilder> lookupElementBuilders = new ArrayList<>();

                                            for (String fieldName : fieldNames.keySet()) {
                                                lookupElementBuilders.add(LookupElementBuilder.create(fieldName));
                                            }

                                            resultSet.addAllElements(lookupElementBuilders);
                                        }
                                    }
                                }
                            }
                        } else if (psiElement.getParent() instanceof JsonStringLiteral && JsonPsiUtil.isPropertyValue(psiElement.getParent())) {
                            PsiElement propertyKey = psiElement.getParent().getParent().getFirstChild();
                            if (JsonPsiUtil.isPropertyKey(propertyKey) && propertyKey instanceof JsonStringLiteral) {
                                String propertyName = ((JsonStringLiteral) propertyKey).getValue();
                                if (propertyName.equals("subjects") || propertyName.equals("binding_fields")) {
                                    String filePath = psiElement.getContainingFile().getOriginalFile().getVirtualFile().getPath();
                                    HashMap<String, PsiElement> fieldNames = JsonRulesFileChecker.filesFieldNames.get(filePath);
                                    // resultSet.addElement(LookupElementBuilder.create("Hello"));
                                    ArrayList<LookupElementBuilder> lookupElementBuilders = new ArrayList<>();

                                    for (String fieldName: fieldNames.keySet()) {
                                        lookupElementBuilders.add(LookupElementBuilder.create(fieldName));
                                    }

                                    resultSet.addAllElements(lookupElementBuilders);
                                }
                            }
                        }
                    }
                }
        );
    }

    // Add autcomplete to json rules files
    public boolean isRulesFile(@NotNull CompletionParameters completionParameters) {
        PsiFile file = completionParameters.getOriginalFile();

        return !file.isDirectory() && file.getParent() != null && file.getParent().getName().contains("json") && file.getParent().getParent() != null && file.getParent().getParent().getName().contains("rules");
    }

    public void runJsnRulesFileCheckerOnOtherFiles() {
        //
    }

}
