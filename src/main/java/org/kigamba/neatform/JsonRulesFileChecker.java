package org.kigamba.neatform;


import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

public class JsonRulesFileChecker implements Annotator, PsiTreeChangeListener {

    public static HashMap<String, HashMap<String, PsiElement>> filesFieldNames = new HashMap<>();
    public static HashMap<PsiElement, String> fieldNameReferences = new HashMap<>();
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
            PsiManager.getInstance(element.getProject()).addPsiTreeChangeListener(this);
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
                            HashMap<String, PsiElement> fields = filesFieldNames.computeIfAbsent(jsonFilePath, k -> new HashMap<>());

                            String jsonStringLiteralValue = jsonStringLiteral.getValue();


                            // Check that the field-name does not already exist
                            PsiElement fieldNameElement = fields.get(jsonStringLiteralValue);
                            if (fieldNameElement != null && element != fieldNameElement) {
                                Annotation badProperty = holder.createErrorAnnotation(element.getTextRange(), "This field-name already exists");
                                badProperty.setHighlightType(ProblemHighlightType.ERROR);
                            } else {
                                fields.put(jsonStringLiteralValue, element);
                                fieldNameReferences.put(element, jsonStringLiteralValue);
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

                    elementsToTraverse.clear();
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

                if (propertyName.equals("subjects") || propertyName.equals("binding_fields")) {
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

                                HashMap<String, PsiElement> fileFields = filesFieldNames.get(jsonFilePath);
                                PsiElement fieldNameElement = fileFields.get(fieldName);
                                if (!(fileFields != null && fieldNameElement != null)) {
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

    @Override
    public void beforeChildAddition(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildRemoval(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildReplacement(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildMovement(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildrenChange(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforePropertyChange(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {
        PsiElement changedElement = event.getParent();

        if (!fieldNameReferences.containsKey(changedElement)) return;

        String filePath = changedElement.getContainingFile().getVirtualFile().getPath();

        HashMap<String, PsiElement> fieldNames = filesFieldNames.get(filePath);
        if (fieldNames != null) {
            String key = fieldNameReferences.remove(changedElement);
            fieldNames.remove(key);

            if (changedElement instanceof JsonStringLiteral) {
                JsonStringLiteral jsonStringLiteral = (JsonStringLiteral) changedElement;
                String newStringValue = jsonStringLiteral.getValue();

                if (!fieldNames.containsKey(newStringValue)) {
                    fieldNameReferences.put(changedElement, newStringValue);
                    fieldNames.put(newStringValue, changedElement
                    );
                }
            }
        }
    }

    @Override
    public void childrenChanged(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void propertyChanged(@NotNull PsiTreeChangeEvent event) {

    }


    /**
     *

     How to perform automation

     1. Actions for collecting data -> These are done before the whole file is parsed
     2. Actions performed after data is collected

     -> Add a queue for all the elements after we are done

     */
}