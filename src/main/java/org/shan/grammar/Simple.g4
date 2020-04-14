grammar Simple;

//*************************
//****** Lexer rules ******
//*************************
IDENTIFIER
    :   [A-Za-z] [A-Za-z0-9]*
    ;

SPACE
    :   [ \n] -> skip
    ;

STRING_1
    // WORKING
    //: '"' ~('"')* '"'
    : '"' ~('"')* '"'
    ;

STRING_2
    : '\'' ~('\'')* '\''
    ;

NUMBER_
    :   ('+' '-')? ([0-9]+) ('.' [0-9]+)?
    ;

WHITESPACE
    :   [ \t\r\n] -> skip
    ;

//*************************
//***** Parser rules ******
//*************************
json
    :   value*
    ;

value
    :   object | array | string | number | bool | 'null'
    ;

string
    :   STRING_1 | STRING_2
    ;

number
    :   NUMBER_
    ;

bool
    :   'true' | 'false'
    ;

array
    :   '[' value (',' value)* ']'
    | '[' ']'
    ;

object
    :   '{' keyValuePair (',' keyValuePair)* '}'
    | '{' '}'
    ;

keyValuePair
    :   string ':' value
    ;