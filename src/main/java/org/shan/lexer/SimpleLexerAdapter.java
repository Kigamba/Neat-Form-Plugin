package org.shan.lexer;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.FlexLexer;
import org.antlr.v4.runtime.CharStream;
import org.jetbrains.annotations.NotNull;
import org.shan.grammar.SimpleLexer;

public class SimpleLexerAdapter extends FlexAdapter {

    public SimpleLexerAdapter(@NotNull FlexLexer flex) {
        super(flex);
    }

    public SimpleLexerAdapter() {
        super(new SimpleLexer(null));
    }
}
