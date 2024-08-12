package com.example.calculator.math;

public class Lexer {
    private char[] expr;
    int i;
    public Token cur;
    public Token next;
    public Lexer(String expression){
        expr=expression.toCharArray();
        nextToken();
        nextToken();
    }

    public boolean isEOF(){
        return cur.type==Token.EOF;
    }

    public Token nextToken(){
        Token token;
        if(!readable()){
            token= new Token(Token.EOF,"EOF");
            cur=next;
            next=token;
            return token;
        }
        char c = expr[i];
          if ('0'<=c && c<='9') {
            token=readNum();
        }else{
            byte type = Token.toTokenType(c);
            token=new Token(type,c+"");
            i++;
        }
        cur=next;
        next=token;
        return token;
    }

    private boolean readable(){
        return !(expr.length==0 || i==expr.length);
    }

    private Token readNum() {
        String num="";
        while (readable() && ('0'<=expr[i] && expr[i]<='9' || expr[i]=='.')){
            num+=expr[i];
            i++;
        }
        return new Token(Token.NUM,num);
    }
}
