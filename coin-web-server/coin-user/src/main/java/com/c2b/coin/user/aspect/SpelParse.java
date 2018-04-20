package com.c2b.coin.user.aspect;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

public class SpelParse {
  private  LocalVariableTableParameterNameDiscoverer discoverer ;
  private ExpressionParser parser;
  public SpelParse(){
    discoverer = new LocalVariableTableParameterNameDiscoverer();
    parser = new SpelExpressionParser();
  }
  public String getStringValue(Method method, Object[] args, String spel){

    EvaluationContext context = new StandardEvaluationContext();
    String[] params = discoverer.getParameterNames(method);
    for (int len = 0; len < params.length; len++) {
      context.setVariable(params[len], args[len]);
    }
    Expression expression = parser.parseExpression(spel);
    return expression.getValue(context, String.class);
  }
}
