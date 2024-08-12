package com.example.calculator.math;

public class ExpressionException extends RuntimeException {

    // 无参构造函数
    public ExpressionException() {
        super();
    }

    // 带消息的构造函数
    public ExpressionException(String message) {
        super(message);
    }

    // 带消息和原因的构造函数
    public ExpressionException(String message, Throwable cause) {
        super(message, cause);
    }

    // 带原因的构造函数
    public ExpressionException(Throwable cause) {
        super(cause);
    }
}
