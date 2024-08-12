package com.example.calculator.math.ast;

import com.example.calculator.math.ExpressionException;
import com.example.calculator.math.Lexer;
import com.example.calculator.math.Token;

public class Parser {
    private static final int LOWEST =1;
    private static final int PLUS_MINUS=2;
    private static final int MUL_DIV =3;
    private static final int PREFIX =4;
    private static final int POW =5;
    private static final int PARAM  =6;

    private static int getPrecedence(byte tokenType){
        switch (tokenType) {
            case Token.PLUS :
            case Token.MINUS :
                return PLUS_MINUS;
            case Token.MUL :
            case Token.DIV :
            case Token.PCT:
            case Token.SLASH:
                return MUL_DIV;
           case Token.LPARAM:
                return PARAM  ;
           case Token.POW:
                 return POW;
            default:
                return LOWEST;
        }
    }

    Lexer lexer;
    public Parser(String expr){
        lexer=new Lexer(expr);
    }

    public Expression parse(){
        return parseExpr(LOWEST);
    }

   private Expression parseExpr(int precedence){
        Expression expr = parsePrefix();
        while (precedence<getPrecedence(lexer.cur.type)){
            expr=parseInfix(expr);
        }
        return expr;
    }

    private Expression parseInfix(Expression expr) {
        Token last = lexer.cur;
        lexer.nextToken();
        Expression right = parseExpr(last.type);
        return new InfixExpression(last,expr,right);
    }

    private Expression parsePrefix() {
        switch (lexer.cur.type){
            case Token.MINUS:
                return parsePrefixExpr();
            case Token.NUM:
                return parseNum();
            case Token.LPARAM:
                return parseGroupedExpr();
            default:
                throw new ExpressionException("expected '"+lexer.cur.literal+"'");
        }
    }

    private Expression parseGroupedExpr() {
        lexer.nextToken();
        Expression expr = parseExpr(LOWEST);
        if(lexer.cur.type!=Token.RPARAM){
            throw new ExpressionException("expected ')'");
        }
        lexer.nextToken();
        return expr;
    }

    private Expression parseNum() {
        Num num =  new Num(lexer.cur.literal);
        lexer.nextToken();
        return num;
    }

    private Expression parsePrefixExpr() {
        Token op = lexer.cur;
        lexer.nextToken();
        Expression expr = parseExpr(PREFIX);
        return new PrefixExpression(op,expr);
    }
}
