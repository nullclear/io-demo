package dev.yxy.nio.server;

import java.util.regex.Pattern;

/**
 * Created by atom on 2021/2/13
 */
public class Calculator {

    public static Integer cal(String expression) {
        if (expression == null || expression.isBlank()) {
            throw new RuntimeException("表达式异常");
        } else {
            String pattern = "^[0-9]+[+\\-*/][0-9]+$";
            if (Pattern.matches(pattern, expression)) {
                if (expression.contains("+")) {
                    int i = expression.indexOf("+");
                    int a = Integer.parseInt(expression.substring(0, i));
                    int b = Integer.parseInt(expression.substring(i + 1));
                    return a + b;
                } else if (expression.contains("-")) {
                    int i = expression.indexOf("-");
                    int a = Integer.parseInt(expression.substring(0, i));
                    int b = Integer.parseInt(expression.substring(i + 1));
                    return a - b;
                } else if (expression.contains("*")) {
                    int i = expression.indexOf("*");
                    int a = Integer.parseInt(expression.substring(0, i));
                    int b = Integer.parseInt(expression.substring(i + 1));
                    return a * b;
                } else if (expression.contains("/")) {
                    int i = expression.indexOf("/");
                    int a = Integer.parseInt(expression.substring(0, i));
                    int b = Integer.parseInt(expression.substring(i + 1));
                    if (b == 0) {
                        throw new ArithmeticException("除数不得为0");
                    }
                    return a / b;
                } else {
                    throw new RuntimeException("操作符异常");
                }
            } else {
                throw new RuntimeException("表达式异常");
            }
        }
    }
}
