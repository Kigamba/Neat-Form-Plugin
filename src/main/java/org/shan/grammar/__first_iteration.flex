package org.shan.lexer; // Package which will be the lexer generation location.

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER; // Pre-defined bad character token.
import static com.intellij.psi.TokenType.WHITE_SPACE; // Pre-defined whitespace character token.
import static org.shan.psi.SimpleTypes.*; // Note that is the class which is specified as `elementTypeHolderClass`
// in bnf grammar file. This will contain all other tokens which we will use.

%%

%public
%class SimpleLexer // Name of the lexer class which will be generated.
%implements FlexLexer // Name of the super class.
%function advance
%type IElementType
%unicode

// We define various Lexer rules as regular expressions first.

// If some character sequence is matched to this regex, it will be treated as an IDENTIFIER.
// IDENTIFIER=[a-zA-Z_][a-zA-Z0-9_]*

// If some character sequence is matched to this regex, it will be treated as a WHITE_SPACE.
WHITE_SPACE=[ \t\n\x0B\f\r]+
space=[ \t\n\x0B\f\r]+
STRING_1="[^\"]*"
STRING_2='[^\']*'
NUMBER_=(\+|\-)?\p{Digit}*
LEFT_CURLY = {
RIGHT_CURLY = }
LEFT_BRAC = \[
RIGHT_BRAC = \]
COMMA = ,
COLON = :
TRUE = true
FALSE = false

// Initial state. We can specify mutiple states for more complex grammars. This corresponds to `modes` in ANTLR grammar.
%%
<YYINITIAL> {
  // In here, we match keywords. So if a keyword is found, this returns a token which corresponds to that keyword.
  // These tokens are generated using the `Ballerina.bnf` file and located in the SimpleTypes `class`.
  // These tokens are Parser uses these return values to match token squence to a parser rule.

  // In here, we check for character sequences which matches regular expressions which we defined above.
  {WHITE_SPACE}      { return WHITE_SPACE; } // This indicates that a character sequence which matches to the rule
                                             // whitespace is encountered.
  {STRING_1}      { return STRING_1; }

  {STRING_2}      { return STRING_2; }

  {NUMBER_}      { return NUMBER_; }

      {LEFT_CURLY}  { return LEFT_CURLY;}

      {RIGHT_CURLY} {return RIGHT_CURLY;}

      {LEFT_BRAC}   {return LEFT_BRAC;}

      {RIGHT_BRAC}  {return RIGHT_BRAC;}

      {COMMA}   {return COMMA;}

      {COLON}   {return COLON;}

      {TRUE}    {return TRUE;}

      {FALSE}   {return FALSE;}
}

// If the character sequence does not match any of the above rules, we return BAD_CHARACTER which indicates that
// there is an error in the character sequence. This is used to highlight errors.
[^] { return BAD_CHARACTER; }
