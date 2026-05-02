package com.cheobs.math_engine.adapter.output.lib.implementation;

import com.cheobs.math_engine.domain.port.output.ExpressionEvaluatorPort;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.operator.Operator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExpressionEvaluatorAdapter implements ExpressionEvaluatorPort {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\[([A-Za-z0-9]+)]");
    private static final Pattern ABSOLUTE_VALUE_PATTERN = Pattern.compile("\\|([^|]+)\\|");
    private static final Operator SAFE_DIVISION = new Operator("/", 2, true, Operator.PRECEDENCE_DIVISION) {
        @Override
        public double apply(double... args) {
            return Double.compare(args[1], 0d) == 0 ? 0d : args[0] / args[1];
        }
    };

    @Override
    public BigDecimal evaluate(String expression, Map<String, BigDecimal> variables) {
        if (expression == null || expression.isBlank()) {
            return BigDecimal.ZERO;
        }

        var aliasByVariable = new LinkedHashMap<String, String>();
        String normalizedExpression = toExp4jAbsoluteValueExpression(expression);
        String parsedExpression = toExp4jExpression(normalizedExpression, aliasByVariable);

        var builder = new ExpressionBuilder(parsedExpression)
                .operator(SAFE_DIVISION);

        Set<String> aliases = Set.copyOf(aliasByVariable.values());
        if (!aliases.isEmpty()) {
            builder.variables(aliases);
        }

        var exp4jExpression = builder.build();
        for (var entry : aliasByVariable.entrySet()) {
            BigDecimal value = variables == null ? null : variables.get(entry.getKey());
            exp4jExpression.setVariable(entry.getValue(), value == null ? 0d : value.doubleValue());
        }

        double result = exp4jExpression.evaluate();
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(result);
    }

    private String toExp4jAbsoluteValueExpression(String expression) {
        String parsedExpression = expression;
        Matcher matcher = ABSOLUTE_VALUE_PATTERN.matcher(parsedExpression);
        while (matcher.find()) {
            String replacement = "abs(" + matcher.group(1) + ")";
            parsedExpression = matcher.replaceFirst(Matcher.quoteReplacement(replacement));
            matcher = ABSOLUTE_VALUE_PATTERN.matcher(parsedExpression);
        }
        return parsedExpression;
    }

    private String toExp4jExpression(String expression, Map<String, String> aliasByVariable) {
        Matcher matcher = VARIABLE_PATTERN.matcher(expression);
        StringBuilder parsedExpression = new StringBuilder();
        int lastMatchEnd = 0;

        while (matcher.find()) {
            parsedExpression.append(expression, lastMatchEnd, matcher.start());

            String variable = matcher.group(1).toUpperCase(Locale.ROOT);
            String alias = aliasByVariable.computeIfAbsent(variable, key -> "v" + aliasByVariable.size());
            parsedExpression.append(alias);

            lastMatchEnd = matcher.end();
        }

        parsedExpression.append(expression.substring(lastMatchEnd));
        return parsedExpression.toString();
    }
}
