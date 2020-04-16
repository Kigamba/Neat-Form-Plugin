package org.shan.completion_contributor;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.shan.SimpleLanguage;
import org.shan.psi.SimpleTypes;

public class SimpleCompletionContributor extends CompletionContributor {
    public SimpleCompletionContributor() {
        extend(CompletionType.BASIC,
                //PlatformPatterns.psiElement(SimpleTypes.STRING_1).withTreeParent(PlatformPatterns.psiElement(SimpleTypes.PROPERTY_KEY)).
                PlatformPatterns.psiElement().
                        withLanguage(SimpleLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {

                        resultSet.addElement(LookupElementBuilder.create("Hello"));
                    }
                }
        );
    }
}