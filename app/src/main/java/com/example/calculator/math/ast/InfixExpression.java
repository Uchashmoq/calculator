package com.example.calculator.math.ast;

import androidx.annotation.NonNull;

import com.example.calculator.math.Token;

public class InfixExpression extends Expression{
    public Expression leftExpr;
    public Expression rightExpr;

    @NonNull
    @Override
    public String toString() {
        return "("+leftExpr+operator.literal+rightExpr+")";
    }

    public InfixExpression(Token operator , Expression leftExpr, Expression rightExpr) {
        super.operator=operator;
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
    }
}
