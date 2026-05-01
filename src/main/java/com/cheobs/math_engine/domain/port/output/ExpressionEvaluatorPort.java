package com.cheobs.math_engine.domain.port.output;

import java.math.BigDecimal;
import java.util.Map;

public interface ExpressionEvaluatorPort {

    BigDecimal evaluate(String expression, Map<String, BigDecimal> variables);

}
