package org.shan.reference_contributor;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.shan.SimpleFileType;
import org.shan.psi.SimpleFile;
import org.shan.psi.SimpleValue;

public class SimpleElementFactory {
    public static SimpleValue createProperty(Project project, String name) {
        final SimpleFile file = createFile(project, name);
        return (SimpleValue) file.getFirstChild();
    }

    public static SimpleFile createFile(Project project, String text) {
        String name = "dummy.simple";
        return (SimpleFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, SimpleFileType.INSTANCE, text);
    }
}