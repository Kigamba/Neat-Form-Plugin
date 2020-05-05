package org.kigamba.simple;


import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotator;

import com.intellij.lang.annotation.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;

public class SimpleAnnotator implements Annotator {
    // Define strings for the Simple language prefix - used for annotations, line markers, etc.
    public static final String SIMPLE_PREFIX_STR = "simple";
    public static final String SIMPLE_SEPARATOR_STR = ":";

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        // Ensure the Psi Element is an expression
        if (!(element instanceof PsiLiteralExpression)) return;

        // Ensure the Psi element contains a string that starts with the key and separator
        PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
        String value = literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;
        if ((value == null) || !value.startsWith(SIMPLE_PREFIX_STR + SIMPLE_SEPARATOR_STR)) return;

        // Define the text ranges (start is inclusive, end is exclusive)
        // "simple:key"
        //  01234567890
        TextRange prefixRange = TextRange.from(element.getTextRange().getStartOffset(), SIMPLE_PREFIX_STR.length() + 1);
        TextRange separatorRange = TextRange.from(prefixRange.getEndOffset(), SIMPLE_SEPARATOR_STR.length());
        TextRange keyRange = new TextRange(separatorRange.getEndOffset(), element.getTextRange().getEndOffset() - 1);

        // Set the annotations using the text ranges.
        Annotation keyAnnotation = holder.createInfoAnnotation(prefixRange, null);
        keyAnnotation.setTextAttributes(DefaultLanguageHighlighterColors.KEYWORD);
        Annotation separatorAnnotation = holder.createInfoAnnotation(separatorRange, null);
        separatorAnnotation.setTextAttributes(SimpleSyntaxHighlighter.SEPARATOR);

        String key = value.substring(SIMPLE_PREFIX_STR.length() + SIMPLE_SEPARATOR_STR.length());

        if (!key.equals("daily-goal-review")) {
            // No well-formed property found following the key-separator
            Annotation badProperty = holder.createErrorAnnotation(keyRange, "Unresolved property - Property should be daily-goal-review");
            //badProperty.setTextAttributes(SimpleSyntaxHighlighter.BAD_CHARACTER);
            badProperty.setHighlightType(ProblemHighlightType.ERROR);
            // ** Tutorial step 18.3 - Add a quick fix for the string containing possible properties
            //badProperty.registerFix(new SimpleCreatePropertyQuickFix(possibleProperties));
        } else {
            // Found at least one property
            Annotation annotation = holder.createInfoAnnotation(keyRange, null);
            annotation.setTextAttributes(SimpleSyntaxHighlighter.VALUE);
        }
    }

}