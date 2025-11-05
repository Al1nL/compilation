/***************************/
/* FILE NAME: LEX_FILE.lex */
/***************************/

/*************/
/* USER CODE */
/*************/

import java_cup.runtime.*;
import java.lang.Math;
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
	public String getPos(){
		return "["+getLine()+","+getTokenStartPosition()+"]";
	}
%}

/***********************/
/* MACRO DECLARATIONS */
/***********************/
LineTerminator	= \r|\n|\r\n
WhiteSpace		= {LineTerminator} | [ \t]
INTEGER			= 0 | [1-9][0-9]*
ID				= [a-zA-Z][0-9a-zA-Z]*
ERROR           = 0[0-9]*
QUOTE           = "\""
LETTERS         = [a-zA-Z]
COMMENT         = [ \t\f0-9a-zA-Z{}.;/+\-?!()\[\]]*

/******************************/
/* DOLLAR DOLLAR - DON'T TOUCH! */
/******************************/
%state YYCOMMENT2
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

"+"						{ return symbol(TokenNames.PLUS);}
"-"						{ return symbol(TokenNames.MINUS);}
"*"						{ return symbol(TokenNames.TIMES);}
"/"						{ return symbol(TokenNames.DIVIDE);}
"("						{ return symbol(TokenNames.LPAREN);}
")"						{ return symbol(TokenNames.RPAREN);}
"["                     { return symbol(TokenNames.LBRACK); }
"]"                     { return symbol(TokenNames.RBRACK); }
"{"                     { return symbol(TokenNames.LBRACE); }
"}"                     { return symbol(TokenNames.RBRACE); }

":="                    { return symbol(TokenNames.ASSIGN); }
"="                     { return symbol(TokenNames.EQ); }
"<"                     { return symbol(TokenNames.LT); }
">"                     { return symbol(TokenNames.GT); }

"*"                     { return symbol(TokenNames.TIMES); }
"/"                     { return symbol(TokenNames.DIVIDE); }

","                     { return symbol(TokenNames.COMMA); }
"."                     { return symbol(TokenNames.DOT); }
";"                     { return symbol(TokenNames.SEMICOLON); }

"//"([*]|{COMMENT})*{LineTerminator}    { }                
"/*"					{ yybegin(YYCOMMENT2);}

"array"                 { return symbol(TokenNames.ARRAY); }
"class"                 { return symbol(TokenNames.CLASS); }
"return"                { return symbol(TokenNames.RETURN); }
"while"                 { return symbol(TokenNames.WHILE); }
"if"                    { return symbol(TokenNames.IF); }
"else"                  { return symbol(TokenNames.ELSE); }
"new"                   { return symbol(TokenNames.NEW); }
"extends"               { return symbol(TokenNames.EXTENDS); }
"nil"                   { return symbol(TokenNames.NIL); }

"int"                   { return symbol(TokenNames.TYPE_INT); }
"string"                { return symbol(TokenNames.TYPE_STRING); }
"void"                  { return symbol(TokenNames.TYPE_VOID); }

{ID}					{return symbol(TokenNames.ID,yytext());}

{QUOTE}{LETTERS}*{QUOTE} {return symbol(TokenNames.STRING,yytext());}
{INTEGER}				{ try {
								int val = Integer.parseInt(yytext());
								
								if (val <= Math.pow(2,15)-1 && val>=0)
									return symbol(TokenNames.INT, Integer.valueOf(yytext()));
								else
									throw new Error("Lex Illegal integer at "+getPos());
						} 
						catch (NumberFormatException e) {
							throw new Error("Java Illegal integer at "+getPos());}
						}}
						
{WhiteSpace}			{  } 
<<EOF>>					{ return symbol(TokenNames.EOF);}

.						{ throw new Error("Did not match any rule at "+getPos()); }


<YYCOMMENT2> {
"*/"					{ yybegin(YYINITIAL); }   
({COMMENT}|{LineTerminator})+				{ }      
"*"         			{ }                  
<<EOF>>     			{ throw new Error("Unterminated comment at "+getPos()); }
}