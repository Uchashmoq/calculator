package com.example.calculator.math;

public class Token {
    public static final byte PLUS = 1;    // +
    public static final byte MINUS = 2;   // -
    public static final byte MUL = 3;     // *
    public static final byte DIV = 4;     // ÷
    public static final byte SLASH = 5;   // /
    public static final byte PCT = 6;     // %
    public static final byte POW = 7;     // ^
    public static final byte LPARAM = 8;  // (
    public static final byte RPARAM = 9;  // )
    public static final byte NUM = 10;    // 123  123.0  123.5
    public static final byte EOF = 11;    // End of file



    public static byte toTokenType(char c) throws ExpressionException{
        switch (c) {
            case '+':
                return PLUS;
            case '-':
                return MINUS;
            case '*':
                return MUL;
            case '÷':
                return DIV;
            case '/':
                return SLASH;
            case '%':
                return PCT;
            case '^':
                return POW;
            case '(':
                return LPARAM;
            case ')':
                return RPARAM;
            default:
                throw new ExpressionException("Unknown character: " + c);
        }
    }
    public byte type;
    public String literal;
    public Token(byte type, String literal) {
        this.type = type;
        this.literal = literal;
    }

    @Override
    public String toString() {
        // 根据类型返回类型名
        String typeName;
        switch (type) {
            case PLUS: typeName = "PLUS"; break;
            case MINUS: typeName = "MINUS"; break;
            case MUL: typeName = "MUL"; break;
            case DIV: typeName = "DIV"; break;
            case SLASH: typeName = "SLASH"; break;
            case PCT: typeName = "PCT"; break;
            case POW: typeName = "POW"; break;
            case LPARAM: typeName = "LPARAM"; break;
            case RPARAM: typeName = "RPARAM"; break;
            case NUM: typeName = "NUM"; break;
            case EOF: typeName = "EOF"; break;
            default: typeName = "UNKNOWN"; break;
        }

        return "{" + typeName + " , " + literal + "}";
    }

}
