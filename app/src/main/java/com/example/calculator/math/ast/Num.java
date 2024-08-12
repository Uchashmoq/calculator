package com.example.calculator.math.ast;

import androidx.annotation.NonNull;

import com.example.calculator.math.ExpressionException;

import java.math.BigDecimal;

public class Num extends Expression{
    public BigDecimal num;

    @NonNull
    @Override
    public String toString() {
        return num.toString();
    }

    public Num(String num){
        try {
            this.num=new BigDecimal(num);
        }catch (RuntimeException e){
            throw new ExpressionException(num+"is not a number");
        }
    }
}
