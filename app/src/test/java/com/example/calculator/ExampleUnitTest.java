package com.example.calculator;

import org.junit.Test;

import static org.junit.Assert.*;

import android.util.Log;

import com.example.calculator.math.Evaluator;
import com.example.calculator.math.Lexer;
import com.example.calculator.math.Token;
import com.example.calculator.math.ast.Expression;
import com.example.calculator.math.ast.Parser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void testTon(){
        BigInteger n = BigInteger.valueOf(13213);
        BigInteger m = BigInteger.probablePrime(30,new Random());
        System.out.printf("n: %s , m: %s\n",n,m);
        System.out.println(Evaluator.findQuadraticResidueTonelliShanks(n,m));
    }

    @Test
    public void testPow(){
        BigDecimal r = Evaluator.pow(new BigDecimal(7), new BigDecimal(20));
        System.out.println(r.toPlainString());
    }
    @Test
    public void testF(){
        List<String> factorize = Evaluator.factorize(new BigInteger("736132175"));
        System.out.println(factorize.toString());

    }
    @Test
    public void testBase(){
        System.out.println(Evaluator.convertToBase(
                BigDecimal.valueOf(27.6),15,5
        ));
    }

    @Test
    public void testEval(){
        BigDecimal decimal = Evaluator.eval("(3+3)÷4");
        System.out.println("result:  "+decimal);
    }

    @Test
    public void testParser(){
        Parser parser = new Parser("-2^3");
        Expression expr = parser.parse();
        System.out.println(expr);
    }
    @Test
    public void addition_isCorrect() {
        Lexer lexer = new Lexer("0.1234÷99*(1+2)");
        while (lexer.cur.type!= Token.EOF){
            System.out.println(lexer.cur);
            lexer.nextToken();
        }
    }
}