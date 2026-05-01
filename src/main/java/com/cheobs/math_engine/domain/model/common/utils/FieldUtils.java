package com.cheobs.math_engine.domain.model.common.utils;

import com.cheobs.math_engine.domain.model.field.FieldType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FieldUtils {

    public static BigDecimal setScale(BigDecimal value, FieldType fieldType) {
        return switch (fieldType) {
            case NUMBER -> value.setScale(0, RoundingMode.HALF_UP);
            case DECIMAL_2, DECIMAL_2_OPT, PERCENT, CURRENCY  -> value.setScale(2, RoundingMode.HALF_UP);
            case DECIMAL_4, DECIMAL_4_OPT, PERCENT_2 -> value.setScale(4, RoundingMode.HALF_UP);
            case PERCENT_4 -> value.setScale(6, RoundingMode.HALF_UP);
        };
    }


}
