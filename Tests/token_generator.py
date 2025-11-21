#!/usr/bin/env python3
"""
token_generator.py

Usage:
    python token_generator.py <program_output_path> <tokens_output_path> <num_tokens>

Generates a random program text file and a token-location file according to the
specification provided by the user.

- Tokens list is fixed to the provided list.
- Special tokens: INT, STRING, ID (variable length 1-6)
- Tokens produced in groups of 5:
    - between tokens inside a group: 1..7 whitespace chars chosen from {space, \t, \f}
    - after each full group (every 5 tokens): append 1..3 newline sequences chosen from {\n, \r, \r\n}
- Token locations recorded as: TOKENNAME(lexeme)[line,column]  (lexeme shown for INT/STRING/ID)
"""

import sys
import random
import string

TOKENS = [
    "PLUS", "MINUS", "TIMES", "DIVIDE", "LPAREN", "RPAREN",
    "LBRACK", "RBRACK", "LBRACE", "RBRACE", "ASSIGN", "EQ", "LT", "GT",
    "COMMA", "DOT", "SEMICOLON", "ARRAY", "CLASS", "RETURN", "WHILE",
    "IF", "ELSE", "NEW", "EXTENDS", "NIL", "TYPE_INT", "TYPE_STRING",
    "TYPE_VOID", "INT", "STRING", "ID"
]

SPECIAL_TOKENS = {"INT", "STRING", "ID"}

# Map token names to the literal lexeme used in the program text for non-special tokens:
TOKEN_LEXEME = {
    "PLUS": "+", "MINUS": "-", "TIMES": "*", "DIVIDE": "/",
    "LPAREN": "(", "RPAREN": ")",
    "LBRACK": "[", "RBRACK": "]", "LBRACE": "{", "RBRACE": "}",
    "ASSIGN": ":=", "EQ": "=", "LT": "<", "GT": ">",
    "COMMA": ",", "DOT": ".", "SEMICOLON": ";",
    # keywords / named tokens:
    "ARRAY": "array", "CLASS": "class", "RETURN": "return",
    "WHILE": "while", "IF": "if", "ELSE": "else", "NEW": "new",
    "EXTENDS": "extends", "NIL": "nil",
    "TYPE_INT": "int", "TYPE_STRING": "string", "TYPE_VOID": "void"
}

WHITESPACE_CHOICES = [' ', '\t']
NEWLINE_CHOICES = ['\n']

# helpers for generating special token lexemes
def gen_int():
    return str(random.randint(0, 2**15 - 1))

def gen_id():
    length = random.randint(1, 6)
    letters = string.ascii_letters
    first = random.choice(string.ascii_letters)
    rest_choices = string.ascii_letters + string.digits
    rest = ''.join(random.choice(rest_choices) for _ in range(length - 1))
    return first + rest

def gen_string():
    length = random.randint(0, 6)
    # keep printable simple characters inside string (no quotes)
    choices = string.ascii_letters
    content = ''.join(random.choice(choices) for _ in range(length))
    return '"' + content + '"'

def token_to_text(token_name):
    """Return the text that should be written into the generated program for this token."""
    if token_name in SPECIAL_TOKENS:
        if token_name == "INT":
            return gen_int()
        if token_name == "ID":
            return gen_id()
        if token_name == "STRING":
            return gen_string()
    else:
        # fallback for non-special tokens: use TOKEN_LEXEME mapping or the token name itself (lowercased)
        return TOKEN_LEXEME.get(token_name, token_name.lower())

def record_entry(token_name, lexeme, line, col):
    """Format the entry for the tokens file."""
    if token_name in SPECIAL_TOKENS:
        # For STRING keep lexeme as quoted string already (gen_string returns quoted)
        return f"{token_name}({lexeme})[{line},{col}]"
    else:
        return f"{token_name}[{line},{col}]"

def main():
    if len(sys.argv) != 4:
        print("Usage: python token_generator.py <program_output_path> <tokens_output_path> <num_tokens>")
        sys.exit(1)

    prog_path = sys.argv[1]
    tokens_path = sys.argv[2]
    try:
        n = int(sys.argv[3])
        if n <= 0:
            raise ValueError()
    except ValueError:
        print("num_tokens must be a positive integer")
        sys.exit(1)

    random.seed()  # system time seed; you can set a fixed seed here for reproducibility

    out_chars = []  # accumulate program characters as list for performance
    token_entries = []

    # position counters (1-based)
    line = 1
    col = 1

    def write_text(s):
        nonlocal line, col
        i = 0
        while i < len(s):
            ch = s[i]
            if ch == '\r' or ch == '\n':
                # Handle \r\n as a single newline
                if ch == '\r' and i + 1 < len(s) and s[i+1] == '\n':
                    out_chars.append('\r')
                    out_chars.append('\n')
                    i += 2
                else:
                    out_chars.append(ch)
                    i += 1
                # Treat any newline style as one line break
                line += 1
                col = 1
            else:
                out_chars.append(ch)
                i += 1
                col += 1



    # generate tokens
    for idx in range(n):
        token_name = random.choice(TOKENS)
        token_text = token_to_text(token_name)

        # Record the start position BEFORE writing token_text
        # (line and col represent position of first character of token)
        entry_line = line
        entry_col = col

        # Save token entry (lexeme for special tokens)
        lexeme_for_entry = token_text if token_name in SPECIAL_TOKENS else None
        entry_str = record_entry(token_name, lexeme_for_entry, entry_line, entry_col)
        token_entries.append(entry_str)

        # write token text into program
        write_text(token_text)

        # decide spacing:
        # Determine position within its group of 5: pos = idx % 5
        pos_in_group = idx % 5

        # If this is not the last token in a group (i.e., pos_in_group != 4) and not the last overall token,
        # we must insert whitespace of length 1..7 between tokens
        is_last_token_overall = (idx == n - 1)
        if pos_in_group != 4 and not is_last_token_overall:
            ws_len = random.randint(1, 7)
            ws_chars = ''.join(random.choice(WHITESPACE_CHOICES) for _ in range(ws_len))
            write_text(ws_chars)
        # If this is the last token of a group (pos_in_group == 4), then after it we must insert 1..3 newline sequences
        # unless it is the last overall token (spec said after every 5 tokens)
        if pos_in_group == 4 and not is_last_token_overall:
            newline_count = random.randint(1, 3)
            for _ in range(newline_count):
                nl = random.choice(NEWLINE_CHOICES)
                write_text(nl)

    # done generating; write files
    program_text = ''.join(out_chars)
    # Write program file
    with open(prog_path, 'w', newline='') as fprog:
        # write the text exactly (note: newline handling preserved because we included \r and \n explicitly)
        fprog.write(program_text)

    # Write tokens file
    with open(tokens_path, 'w', newline='\n') as ftok:
        for entry in token_entries:
            ftok.write(entry + '\n')

    print(f"Generated program -> {prog_path}")
    print(f"Generated tokens file -> {tokens_path}")
    print(f"Tokens generated: {n}")

if __name__ == "__main__":
    main()
