package org.kigamba.neatform;


import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class JsonRulesFileChecker implements Annotator {

    private static HashMap<String, HashSet<String>> filesFieldNames = new HashMap<>();
    private HashMap<String, AnnotationHolder> annotationHolders = new HashMap<>();
    private LinkedList<PsiElement> elementsToTraverse = new LinkedList<>();


    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        String jsonFilePath = element.getContainingFile().getVirtualFile().getPath();

        if (!annotationHolders.containsKey(jsonFilePath)) {
            annotationHolders.put(jsonFilePath, holder);
        }

        if (!elementsToTraverse.contains(element)) {
            elementsToTraverse.add(element);
        }

        // Ensure the Psi Element is an expression
        if (element instanceof JsonStringLiteral) {

            JsonStringLiteral jsonStringLiteral = (JsonStringLiteral) element;
            String rulesFilePath = jsonStringLiteral.getValue();

            boolean isPropertyValue = isPropertyValue(jsonStringLiteral);

            // Check if the rules file exists
            if (isPropertyValue && ((JsonStringLiteral) jsonStringLiteral.getParent().getFirstChild()).getValue().equals("rules_file")) {
                if (!isRulesFileExist(element, rulesFilePath)) {
                    Annotation badProperty = holder.createErrorAnnotation(element.getTextRange(), "The rules file cannot be found. Correct the path to the rules file");
                    badProperty.setHighlightType(ProblemHighlightType.ERROR);
                }
            }

            // If a field name then add it to the hash-map
            if (isPropertyValue) {
                PsiElement propertyNameElement = jsonStringLiteral.getParent().getFirstChild();
                if (propertyNameElement instanceof JsonStringLiteral) {
                    String propertyName = ((JsonStringLiteral) propertyNameElement).getValue();

                    if (propertyName.equals("name")) {
                        PsiElement psiElement1 = element.getParent().getParent().getParent();
                        PsiElement psiElement2 = psiElement1.getParent();
                        if (psiElement1 instanceof JsonArray && psiElement2 instanceof JsonProperty &&
                                psiElement2.getFirstChild() instanceof JsonStringLiteral &&
                                ((JsonStringLiteral) psiElement2.getFirstChild()).getValue().equals("fields")) {
                            HashSet<String> fields = filesFieldNames.computeIfAbsent(jsonFilePath, k -> new HashSet<>());

                            String jsonStringLiteralValue = jsonStringLiteral.getValue();


                            // Check that the field-name does not already exist
                            if (fields.contains(jsonStringLiteralValue)) {
                                Annotation badProperty = holder.createErrorAnnotation(element.getTextRange(), "This field-name already exists");
                                badProperty.setHighlightType(ProblemHighlightType.ERROR);
                            } else {
                                fields.add(jsonStringLiteral.getValue());
                            }
                        }
                    }
                }
            }
        } else {
            // We have reached the end of the file and can traverse it again
            if ((element.getTextOffset() + element.getTextLength()) == element.getContainingFile().getTextLength()) {
                AnnotationHolder fileAnnotationHolder = annotationHolders.get(jsonFilePath);
                if (elementsToTraverse.size() > 0 && fileAnnotationHolder != null) {
                    for (PsiElement element1: elementsToTraverse) {
                        annotateAfterFullTraversal(element1, fileAnnotationHolder);
                    }
                }
            }
        }
    }

    protected void annotateAfterFullTraversal(@NotNull PsiElement element, AnnotationHolder holder) {
        if (!(element instanceof JsonStringLiteral)) return;

        String jsonFilePath = element.getContainingFile().getVirtualFile().getPath();

        JsonStringLiteral jsonStringLiteral = (JsonStringLiteral) element;
        String rulesFilePath = jsonStringLiteral.getValue();

        boolean isPropertyValue = isPropertyValue(jsonStringLiteral);

        // Make sure that the field exists in the subjects property
        if (isPropertyValue) {
            PsiElement propertyNameElement = jsonStringLiteral.getParent().getFirstChild();
            if (propertyNameElement instanceof JsonStringLiteral) {
                String propertyName = ((JsonStringLiteral) propertyNameElement).getValue();

                if (propertyName.equals("subjects")) {
                    PsiElement psiElement1 = element.getParent().getParent().getParent();
                    PsiElement psiElement2 = psiElement1.getParent();
                    if (psiElement1 instanceof JsonArray && psiElement2 instanceof JsonProperty &&
                            psiElement2.getFirstChild() instanceof JsonStringLiteral &&
                            ((JsonStringLiteral) psiElement2.getFirstChild()).getValue().equals("fields")) {
                        String jsonStringLiteralValue = jsonStringLiteral.getValue();
                        String[] fields = jsonStringLiteralValue.replace(" ", "").split(",");

                        for (String field : fields) {
                            String[] fieldParts = field.split(":");
                            if (fieldParts.length > 1) {
                                String fieldName = fieldParts[0];
                                String fieldType = fieldParts[1];

                                HashSet<String> fileFields = filesFieldNames.get(jsonFilePath);
                                if (!(fileFields != null && fileFields.contains(fieldName))) {
                                    Annotation badProperty = holder.createErrorAnnotation(element.getTextRange(), "This field does not exist");
                                    badProperty.setHighlightType(ProblemHighlightType.ERROR);
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    private boolean isPropertyValue(JsonStringLiteral jsonStringLiteral) {
        if (jsonStringLiteral.getParent() != null) {
            PsiElement propertyName = jsonStringLiteral.getParent().getFirstChild();
            if (propertyName != jsonStringLiteral && propertyName instanceof JsonStringLiteral && ((JsonStringLiteral) propertyName).isPropertyName()) {
                return true;
            }
        }

        return false;
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


    /**
     *

     How to perform automation

     1. Actions for collecting data -> These are done before the whole file is parsed
     2. Actions performed after data is collected

     -> Add a queue for all the elements after we are done

     */
}