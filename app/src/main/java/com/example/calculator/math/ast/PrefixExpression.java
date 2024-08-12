package com.example.calculator.math.ast;

import androidx.annotation.NonNull;

import com.example.calculator.math.Token;

public class PrefixExpression extends Expression{
    public Expression expr;

    public PrefixExpression(Token operator, Expression expr) {
        super.operator=operator;
        this.expr = expr;
    }

    @NonNull
    @Override
    public String toString() {
        return operator.literal+"("+expr+")";
    }
}
