package org.kigamba.neatform;


import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.json.psi.JsonValue;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.kigamba.simple.SimpleSyntaxHighlighter;

import java.io.File;
import java.net.URL;

public class JsonRulesFileChecker implements Annotator {

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        // Ensure the Psi Element is an expression
        if (!(element instanceof JsonStringLiteral)) return;

        JsonStringLiteral literalExpression = (JsonStringLiteral) element;
        String rulesFilePath = literalExpression.getValue();

        // Check if the rules file exists
        if (literalExpression.getParent() != null) {
            PsiElement propertyName = literalExpression.getParent().getFirstChild();
            if (propertyName != literalExpression && propertyName instanceof JsonStringLiteral && ((JsonStringLiteral) propertyName).isPropertyName() && ((JsonStringLiteral) propertyName).getValue().equals("rules_file")) {

                if (!isRulesFileExist(element, rulesFilePath)) {
                    // No well-formed property found following the key-separator
                    Annotation badProperty = holder.createErrorAnnotation(element.getTextRange(), "The rules file cannot be found. Correct the path to the rules file");
                    //badProperty.setTextAttributes(SimpleSyntaxHighlighter.BAD_CHARACTER);
                    badProperty.setHighlightType(ProblemHighlightType.ERROR);
                    // ** Tutorial step 18.3 - Add a quick fix for the string containing possible properties
                    //badProperty.registerFix(new SimpleCreatePropertyQuickFix(possibleProperties));
                }
            }
        }
    }

    private boolean isRulesFileExist(PsiElement element, String rulesFilePath) {
        String basePath = element.getContainingFile().getOriginalFile().getProject().getBasePath();

        PsiDirectory parentDirectory = element.getContainingFile().getParent();

        while (parentDirectory != null && !parentDirectory.getVirtualFile().getPath().equals(basePath) && !parentDirectory.getName().equals("assets")) {
            parentDirectory = parentDirectory.getParentDirectory();
        }

        String finalResourcePath = "";
        if (parentDirectory != null && !parentDirectory.getVirtualFile().getPath().equals(basePath) && parentDirectory.getName().equals("assets")) {
            finalResourcePath = parentDirectory.getVirtualFile().getPath() + File.separator + rulesFilePath;

            File file = new File(finalResourcePath);
            return file.exists();
        }

        return false;
    }

}