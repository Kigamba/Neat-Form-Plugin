package org.shan.grammar;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static org.shan.psi.SimpleTypes.*;

%%

%{
  public SimpleLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class SimpleLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

WHITE_SPACE=[ \t\n\x0B\f\r]+
SPACE=[ \t\n\x0B\f\r]+
STRING_1=\"[^\"]*\"
STRING_2='[^']*'
NUMBER_=(\+|\-)?[:digit:]*

%%
<YYINITIAL> {
  {WHITE_SPACE}      { return WHITE_SPACE; }

  "{"                { return LEFT_CURLY; }
  "}"                { return RIGHT_CURLY; }
  "["                { return LEFT_BRAC; }
  "]"                { return RIGHT_BRAC; }
  ","                { return COMMA; }
  ":"                { return COLON; }
  "true"             { return TRUE; }
  "false"            { return FALSE; }
  "null"             { return NULL; }

  {WHITE_SPACE}      { return WHITE_SPACE; }
  {SPACE}            { return SPACE; }
  {STRING_1}         { return STRING_1; }
  {STRING_2}         { return STRING_2; }
  {NUMBER_}          { return NUMBER_; }

}

[^] { return BAD_CHARACTER; }
