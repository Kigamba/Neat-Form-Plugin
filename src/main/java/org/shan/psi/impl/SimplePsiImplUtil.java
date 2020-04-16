package org.shan.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.shan.psi.SimpleTypes;
import org.shan.psi.SimpleValue;
import org.shan.reference_contributor.SimpleElementFactory;

public class SimplePsiImplUtil {
    public static String getKey(SimpleValue element) {
        if (element.equals(SimpleTypes.KEY_VALUE_PAIR)) {
            return element.getFirstChild().getText();
        }

        ASTNode keyNode = element.getNode().findChildByType(SimpleTypes.VALUE);
        if (keyNode != null) {
            // IMPORTANT: Convert embedded escaped spaces to simple spaces
            return keyNode.getText().replaceAll("\\\\ ", " ");
        } else {
            return null;
        }
    }

    public static String getValue(SimpleValue element) {
        if (element.equals(SimpleTypes.KEY_VALUE_PAIR)) {
            return element.getLastChild().getText();
        }

        ASTNode valueNode = element.getNode().findChildByType(SimpleTypes.VALUE);
        if (valueNode != null) {
            return valueNode.getText();
        } else {
            return null;
        }
    }

    public static String getName(SimpleValue element) {
        return getKey(element);
    }

    public static PsiElement setName(SimpleValue element, String newName) {
        ASTNode keyNode = element.getNode().findChildByType(SimpleTypes.STRING_1);
        if (keyNode != null) {

            SimpleValue property = SimpleElementFactory.createProperty(element.getProject(), newName);
            ASTNode newKeyNode = property.getFirstChild().getNode();
            element.getNode().replaceChild(keyNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(SimpleValue element) {
        ASTNode keyNode = element.getNode().findChildByType(SimpleTypes.STRING_1);
        if (keyNode != null) {
            return keyNode.getPsi();
        } else {
            return null;
        }
    }
}
