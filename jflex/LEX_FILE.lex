/***************************/
/* FILE NAME: LEX_FILE.lex */
/***************************/

/*************/
/* USER CODE */
/*************/

import java_cup.runtime.*;

/******************************/
/* DOLLAR DOLLAR - DON'T TOUCH! */
/******************************/

%%

/************************************/
/* OPTIONS AND DECLARATIONS SECTION */
/************************************/
   
/*****************************************************/ 
/* Lexer is the name of the class JFlex will create. */
/* The code will be written to the file Lexer.java.  */
/*****************************************************/ 
%class Lexer

/********************************************************************/
/* The current line number can be accessed with the variable yyline */
/* and the current column number with the variable yycolumn.        */
/********************************************************************/
%line
%column

/*******************************************************************************/
/* Note that this has to be the EXACT same name of the class the CUP generates */
/*******************************************************************************/
%cupsym TokenNames

/******************************************************************/
/* CUP compatibility mode interfaces with a CUP generated parser. */
/******************************************************************/
%cup

/******************************************************************/
/* CUP compatibility mode interfaces with a CUP generated parser. */
/******************************************************************/

/****************/
/* DECLARATIONS */
/****************/
/*****************************************************************************/   
/* Code between %{ and %}, both of which must be at the beginning of a line, */
/* will be copied verbatim (letter to letter) into the Lexer class code.     */
/* Here you declare member variables and functions that are used inside the  */
/* scanner actions.                                                          */  
/*****************************************************************************/   
%{
	/*********************************************************************************/
	/* Create a new java_cup.runtime.Symbol with information about the current token */
	/*********************************************************************************/
	private Symbol symbol(int type)               {return new Symbol(type, yyline, yycolumn);}
	private Symbol symbol(int type, Object value) {return new Symbol(type, yyline, yycolumn, value);}

	/*******************************************/
	/* Enable line number extraction from main */
	/*******************************************/
	public int getLine() { return yyline + 1; } 

	/**********************************************/
	/* Enable token position extraction from main */
	/**********************************************/
	public int getTokenStartPosition() { return yycolumn + 1; } 
%}

/***********************/
/* MACRO DECLARATIONS */
/***********************/
LineTerminator	= \r|\n|\r\n
WhiteSpace		= {LineTerminator} | [ \t\f]
INTEGER			= 0 | [1-9][0-9]*
ID				= [a-zA-Z][0-9a-zA-Z]*
CLASS           = "class"
NIL             = "nil"
ARRAY           = "array"
WHILE           = "while"
INT             = "int"
VOID            = "void"
EXTENDS         = "extends"
RETURN          = "return"
NEW             = "new"
IF              = "if"
ELSE            = "else"
STRING          = "string"
ERROR           = 0[0-9]*
QUOTE           = "\""
LETTERS         = [a-zA-z]*
COMMENT         = [ [ \t\f] | INTEGER | LETTERS | "{" | "}" | "." | ";" | "/" | "+" | "-" | "?" | "!" | "(" | ")" | "[" | "]" ]*

/******************************/
/* DOLLAR DOLLAR - DON'T TOUCH! */
/******************************/
%state YYSTRING
$state YYCOMMENT1
$state YYCOMMENT2
%%

/************************************************************/
/* LEXER matches regular expressions to actions (Java code) */
/************************************************************/

/**************************************************************/
/* YYINITIAL is the state at which the lexer begins scanning. */
/* So these regular expressions will only be matched if the   */
/* scanner is in the start state YYINITIAL.                   */
/**************************************************************/

<YYINITIAL> {

"+"					{ return symbol(TokenNames.PLUS);}
"-"					{ return symbol(TokenNames.MINUS);}
"PPP"				{ return symbol(TokenNames.TIMES);}
"/"					{ return symbol(TokenNames.DIVIDE);}
"("					{ return symbol(TokenNames.LPAREN);}
")"					{ return symbol(TokenNames.RPAREN);}
"//"                { yybegin(YYCOMMENT1); }
"/*"                { yybegin(YYCOMMENT2); }
QUOTE               { yybegin(YYSTRING); }
{INTEGER}			{ return symbol(TokenNames.NUMBER, Integer.valueOf(yytext())) <= 2**15-1 ? symbol(TokenNames.NUMBER, Integer.valueOf(yytext())) : throw new Error();}
{ID}				{ return symbol(TokenNames.ID, yytext());}
{WhiteSpace}		{ return symbol(TokenNames.WHITESPACE, yytext()) } /* todo: check if need to return yytext */
<<EOF>>				{ return symbol(TokenNames.EOF);}
}

<YYSTRING> {
QUOTE               { return symbol(TokenNames.STRING, yytext()); }
{LETTERS}           { }
{^LETTERS}          { throw new Error(); }
<<EOF>>             { throw new Error(); }
}

<YYCOMMENT1>{
LineTerminator      { return; } /* todo: check if how to return correctly */
{COMMENT | "*" }    {}

<<EOF>>             { throw new Error(); }
}

<YYCOMMENT2>{
"*/"                { return; } /* todo: check if how to return correctly */
{COMMENT}           {}
"*"                 {}
<<EOF>>             { throw new Error(); }
}