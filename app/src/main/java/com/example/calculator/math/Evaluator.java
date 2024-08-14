package com.example.calculator.math;

import com.example.calculator.math.ast.Expression;
import com.example.calculator.math.ast.InfixExpression;
import com.example.calculator.math.ast.Num;
import com.example.calculator.math.ast.Parser;
import com.example.calculator.math.ast.PrefixExpression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Evaluator {

    private static final BigDecimal ep = BigDecimal.valueOf(1e-12);
    public static final MathContext mc = new MathContext(10000, RoundingMode.HALF_UP);

    public static BigInteger eval(String expr,BigInteger M) throws ExpressionException{
        if (expr==null || expr.isEmpty()) throw new ExpressionException("empty expression");
        Parser parser = new Parser(expr);
        Expression expression = parser.parse();
        return evalExpression(expression,M);
    }

    private static BigInteger evalExpression(Expression expression,BigInteger M){
        if (expression instanceof Num){
            Num num = (Num) expression;
            return toBigInteger(num.num).mod(M);
        }

        if (expression instanceof PrefixExpression){
            PrefixExpression prefixExpression = (PrefixExpression) expression;
            switch (prefixExpression.operator.type){
                case Token.MINUS:
                    return evalExpression(prefixExpression.expr,M).negate().mod(M);
                default:
                    throw new ExpressionException("unknown prefix operator");
            }
        }

        if (expression instanceof InfixExpression){
            InfixExpression infixExpression = (InfixExpression) expression;
            BigInteger left = evalExpression(infixExpression.leftExpr,M);
            BigInteger right = evalExpression(infixExpression.rightExpr,M);
            try {
                switch (infixExpression.operator.type){
                    case Token.PLUS:
                        return left.add(right).mod(M);
                    case Token.MINUS:
                        return left.subtract(right).mod(M);
                    case Token.MUL:
                        return left.multiply(right).mod(M);
                    case Token.SLASH:
                    case Token.DIV:
                        return left.multiply(right.modInverse(M));
                    case Token.PCT:
                        return left.mod(right);
                    case Token.POW:
                        return left.modPow(right,M);
                    default:
                        throw new ExpressionException("unknown infix operator");
                }
            }catch (ArithmeticException arithmeticException){
                throw new ExpressionException(arithmeticException);
            }
        }
        return null;
    }



    public static BigDecimal eval(String expr) throws ExpressionException{
        if (expr==null || expr.isEmpty()) throw new ExpressionException("empty expression");
        Parser parser = new Parser(expr);
        Expression expression = parser.parse();
        return evalExpression(expression);
    }


    private static BigDecimal evalExpression(Expression expression) {
        if (expression instanceof Num){
            Num num = (Num) expression;
            return num.num;
        }

        if (expression instanceof PrefixExpression){
            PrefixExpression prefixExpression = (PrefixExpression) expression;
            switch (prefixExpression.operator.type){
                case Token.MINUS:
                    BigDecimal num = evalExpression(prefixExpression.expr);
                    return num.negate();
                default:
                    throw new ExpressionException("unknown prefix operator");
            }
        }

        if (expression instanceof InfixExpression){
            InfixExpression infixExpression = (InfixExpression) expression;
            BigDecimal left = evalExpression(infixExpression.leftExpr);
            BigDecimal right = evalExpression(infixExpression.rightExpr);
            try {
                switch (infixExpression.operator.type){
                    case Token.PLUS:
                        return left.add(right);
                    case Token.MINUS:
                        return left.subtract(right);
                    case Token.MUL:
                        return left.multiply(right);
                    case Token.SLASH:
                        if(isInteger(left)&& isInteger(right)){
                            return new BigDecimal(toBigInteger(left).divide(toBigInteger(right)));
                        }else{
                            return left.divide(right,mc);
                        }
                    case Token.DIV:
                        return left.divide(right,mc);
                    case Token.PCT:
                        return new BigDecimal(toBigInteger(left).mod(toBigInteger(right)));
                    case Token.POW:
                        return pow(left,right);
                    default:
                        throw new ExpressionException("unknown infix operator");
                }
            }catch (ArithmeticException arithmeticException){
                throw new ExpressionException(arithmeticException);
            }
        }
        return null;
    }

    public static boolean isInteger(BigDecimal number) {
        return number.stripTrailingZeros().scale() <= 0;
    }

    public static BigInteger toBigInteger(BigDecimal b){
        if(!isInteger(b)) throw new ExpressionException(b.toString()+" is not a integer");
        return b.toBigInteger();
    }

    public static BigDecimal pow(BigDecimal base, BigDecimal exponent) {
        if (isInteger(exponent)){
            BigInteger exp = toBigInteger(exponent);
            return base.pow(exp.intValue(),mc);
        }
        BigDecimal logBase = BigDecimal.valueOf(Math.log(base.doubleValue()));
        BigDecimal expLogBase = exponent.multiply(logBase, mc);
        return integerApproximation(Math.exp(expLogBase.doubleValue()));
    }

    public static BigDecimal integerApproximation(double d){
        BigDecimal decimal = BigDecimal.valueOf(d);
        if(Evaluator.isInteger(decimal)){
            return decimal.stripTrailingZeros();
        }
        BigDecimal floor = new BigDecimal(decimal.toBigInteger());
        if(decimal.subtract(floor).abs().compareTo(ep)<0){
            return floor;
        }
        BigDecimal ceil = floor.add(BigDecimal.ONE);
        if(decimal.subtract(ceil).abs().compareTo(ep)<0){
            return ceil;
        }
       return decimal.stripTrailingZeros();
    }

    private static BigInteger BIGINTEGER_TWO = BigInteger.valueOf(2);
    private static BigInteger modPow(BigInteger base, BigInteger exponent, BigInteger mod) {
        return base.modPow(exponent, mod);
    }

    public static List<BigInteger> findQuadraticResidue(BigInteger n, BigInteger m){
        if(m.compareTo(BigInteger.valueOf(100000))>0 && m.isProbablePrime(15)){
            List<BigInteger> results = new ArrayList<>();
            results.add(findQuadraticResidueTonelliShanks(n,m));
        }
        List<BigInteger> results = findQuadraticResidueNormal(n, m);
        if(results.isEmpty()) throw new ExpressionException("No square root exists");
        return results;
    }

    public static List<BigInteger> findQuadraticResidueNormal(BigInteger a, BigInteger m) {
        Set<BigInteger> solutions = new TreeSet<>();
        BigInteger halfM = m.divide(BIGINTEGER_TWO);
        for (BigInteger x = BigInteger.ZERO; x.compareTo(m) < 0; x = x.add(BigInteger.ONE)) {
            BigInteger xSquared = x.modPow(BIGINTEGER_TWO, m);
            if (xSquared.equals(a.mod(m))) {
                solutions.add(x);
                // 考虑到 x 和 m - x 是同一个二次剩余的解
                if (!x.equals(halfM)) {
                    solutions.add(m.subtract(x));
                }
            }
        }
        return new ArrayList<>(solutions);
    }
    public static String convertToBase(BigDecimal value, int base,int scale) {
        BigInteger integerPart = value.toBigInteger();
        BigDecimal fractionalPart = value.subtract(new BigDecimal(integerPart));

        // Convert integer part
        String integerString = integerPart.toString(base).toUpperCase();

        // Convert fractional part
        StringBuilder fractionalStringBuilder = new StringBuilder();

        // Limit to 5 decimal places
        for (int i = 0; i < scale; i++) {
            fractionalPart = fractionalPart.multiply(BigDecimal.valueOf(base));
            BigInteger digit = fractionalPart.toBigInteger();
            fractionalStringBuilder.append(digit.toString(base).toUpperCase());
            fractionalPart = fractionalPart.subtract(new BigDecimal(digit));
        }

        String frac = fractionalStringBuilder.toString();
        int k=frac.length()-1;
        for (;k>=0;k--){
            if(frac.charAt(k)!='0') break;
        }
        if(k<0){
            return integerString;
        }
        return integerString+"."+frac.substring(0,k+1);
    }

    public static List<String> factorize(BigInteger number) {
        List<String> factorList = new ArrayList<>();
        BigInteger two = BigInteger.valueOf(2);

        if(number.compareTo(BigInteger.ONE)==0 || number.compareTo(BigInteger.ZERO)==0){
            return factorList;
        }

        if(number.compareTo(BigInteger.valueOf(500000))>0 &&  number.isProbablePrime(15)){
            factorList.add(number.toString());
            return factorList;
        }

        // 分解偶数因子
        int count = 0;
        while (number.mod(two).equals(BigInteger.ZERO)) {
            number = number.divide(two);
            count++;
        }
        if (count > 0) {
            factorList.add("2" + (count > 1 ? "^" + count : ""));
        }

        // 分解奇数因子
        BigInteger factor = BigInteger.valueOf(3);
        while (factor.multiply(factor).compareTo(number) <= 0) {
            count = 0;
            while (number.mod(factor).equals(BigInteger.ZERO)) {
                number = number.divide(factor);
                count++;
            }
            if (count > 0) {
                factorList.add(factor.toString() + (count > 1 ? "^" + count : ""));
            }
            factor = factor.add(two);
        }

        // 如果剩下的数字大于 1，它也是一个质数
        if (number.compareTo(BigInteger.ONE) > 0) {
            factorList.add(number.toString());
        }

        return factorList;
    }

    public static BigInteger findQuadraticResidueTonelliShanks(BigInteger n, BigInteger p) {
        // 检查 n 是否是二次剩余
        if (modPow(n, p.subtract(BigInteger.ONE).divide(BIGINTEGER_TWO), p).compareTo(BigInteger.ONE) != 0) {
            throw new ExpressionException("No square root exists");
        }

        // 特殊情况：p ≡ 3 (mod 4)
        if (p.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))) {
            return modPow(n, p.add(BigInteger.ONE).divide(BigInteger.valueOf(4)), p);
        }

        // 寻找 q 和 s，使得 p - 1 = q * 2^s，且 q 是奇数
        BigInteger q = p.subtract(BigInteger.ONE);
        BigInteger s = BigInteger.ZERO;
        while (q.mod(BIGINTEGER_TWO).equals(BigInteger.ZERO)) {
            q = q.divide(BIGINTEGER_TWO);
            s = s.add(BigInteger.ONE);
        }

        // 寻找非二次剩余 z
        BigInteger z = BIGINTEGER_TWO;
        while (modPow(z, p.subtract(BigInteger.ONE).divide(BIGINTEGER_TWO), p).equals(BigInteger.ONE)) {
            z = z.add(BigInteger.ONE);
        }

        BigInteger m = s;
        BigInteger c = modPow(z, q, p);
        BigInteger t = modPow(n, q, p);
        BigInteger r = modPow(n, q.add(BigInteger.ONE).divide(BIGINTEGER_TWO), p);

        while (t.compareTo(BigInteger.ONE) != 0) {
            BigInteger tt = t;
            BigInteger i = BigInteger.ZERO;
            while (tt.compareTo(BigInteger.ONE) != 0 && i.compareTo(m) < 0) {
                tt = tt.multiply(tt).mod(p);
                i = i.add(BigInteger.ONE);
            }

            BigInteger b = modPow(c, BIGINTEGER_TWO.pow(m.subtract(i).subtract(BigInteger.ONE).intValue()), p);
            m = i;
            c = b.multiply(b).mod(p);
            t = t.multiply(c).mod(p);
            r = r.multiply(b).mod(p);
        }
        return r;
    }

    public static String decimalStrFormat(BigDecimal decimal){
        if(Evaluator.isInteger(decimal)){
            return decimal.stripTrailingZeros().toPlainString();
        }
        BigInteger floor = decimal.toBigInteger();
        if(decimal.subtract(new BigDecimal(floor)).abs().compareTo(ep)<0){
            return floor.toString();
        }
        BigInteger ceil = floor.add(BigInteger.ONE);
        if(decimal.subtract(new BigDecimal(ceil)).abs().compareTo(ep)<0){
            return ceil.toString();
        }
        BigDecimal strip =  decimal.stripTrailingZeros();
        if(strip.scale()<50){
            return strip.toPlainString();
        }
        return strip.setScale(20,BigDecimal.ROUND_HALF_DOWN).toPlainString();
    }

}
