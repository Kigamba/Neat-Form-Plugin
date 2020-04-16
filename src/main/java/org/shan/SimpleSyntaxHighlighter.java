package org.shan;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.shan.lexer.SimpleLexerAdapter;
import org.shan.psi.SimpleElementType;
import org.shan.psi.SimpleTokenType;
import org.shan.psi.SimpleTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class SimpleSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey SEPARATOR =
            createTextAttributesKey("SIMPLE_SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey KEY =
            createTextAttributesKey("SIMPLE_KEY", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey VALUE =
            createTextAttributesKey("SIMPLE_VALUE", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("SIMPLE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("SIMPLE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);


    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] SEPARATOR_KEYS = new TextAttributesKey[]{SEPARATOR};
    private static final TextAttributesKey[] KEY_KEYS = new TextAttributesKey[]{KEY};
    private static final TextAttributesKey[] VALUE_KEYS = new TextAttributesKey[]{VALUE};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new SimpleLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        System.out.println("Trying token highlights : " + tokenType.toString());
        if (tokenType instanceof SimpleTokenType) {
            System.out.println("this is a Token Type");
        } else if (tokenType instanceof SimpleElementType) {
            System.out.println("This is an Element Type");
        }

        if (tokenType.equals(SimpleTypes.PROPERTY_KEY)) {
            return KEY_KEYS;
        } else if (tokenType.equals(SimpleTypes.LEFT_BRAC) || tokenType.equals(SimpleTypes.RIGHT_BRAC) || tokenType.equals(SimpleTypes.LEFT_CURLY) || tokenType.equals(SimpleTypes.RIGHT_CURLY)) {
            return SEPARATOR_KEYS;
        } else if (tokenType.equals(SimpleTypes.BOOLEAN) ) {
            return KEY_KEYS;
        } else if (tokenType.equals(SimpleTypes.STRING) || tokenType.equals(SimpleTypes.STRING_1) || tokenType.equals(SimpleTypes.STRING_2)) {
            return VALUE_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            System.out.println(tokenType.toString() + " IS AN EMPTY KEY");
            return EMPTY_KEYS;
        }
    }
}
