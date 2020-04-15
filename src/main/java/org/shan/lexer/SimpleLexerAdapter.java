package org.shan.lexer;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.FlexLexer;
import org.jetbrains.annotations.NotNull;
import org.shan.grammar.SimpleLexer;

import java.io.Reader;

public class SimpleLexerAdapter extends FlexAdapter {

    public SimpleLexerAdapter(@NotNull FlexLexer flex) {
        super(flex);
    }

    public SimpleLexerAdapter() {
        super(new SimpleLexer((Reader) null));
    }
}
