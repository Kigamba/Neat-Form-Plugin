package org.shan;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.shan.psi.SimpleFile;
import org.shan.psi.SimpleValue;

import java.util.*;

public class SimpleUtil {

    // Searches the entire project for Simple language files with instances of the Simple property with the given key
    public static List<SimpleValue> findProperties(Project project, String key) {
        List<SimpleValue> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(SimpleFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            SimpleFile simpleFile = (SimpleFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (simpleFile != null) {
                SimpleValue[] properties = PsiTreeUtil.getChildrenOfType(simpleFile, SimpleValue.class);
                if (properties != null) {
                    for (SimpleValue property : properties) {
                        if (key.equals(property.getText())) {
                            result.add(property);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static List<SimpleValue> findProperties(Project project) {
        List<SimpleValue> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(SimpleFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            SimpleFile simpleFile = (SimpleFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (simpleFile != null) {
                SimpleValue[] properties = PsiTreeUtil.getChildrenOfType(simpleFile, SimpleValue.class);
                if (properties != null) {
                    Collections.addAll(result, properties);
                }
            }
        }
        return result;
    }
}