grammar Simple;

//*************************
//****** Lexer rules ******
//*************************
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

LEFT_CURLY
    : '{'
    ;

RIGHT_CURLY
    :   '}'
    ;

LEFT_BRAC
    : '['
    ;

RIGHT_BRAC
    : ']'
    ;

COMMA
    : ','
    ;

COLON
    : ':'
    ;

TRUE
    : 'true'
    ;

FALSE
    : 'false'
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