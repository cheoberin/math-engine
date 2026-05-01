package com.cheobs.math_engine.application.service;

import com.cheobs.math_engine.domain.model.common.utils.FieldUtils;
import com.cheobs.math_engine.domain.model.field.Field;
import com.cheobs.math_engine.domain.model.field.FieldSource;
import com.cheobs.math_engine.domain.model.submission.ProcessedField;
import com.cheobs.math_engine.domain.model.submission.Submission;
import com.cheobs.math_engine.domain.model.submission.SubmissionField;
import com.cheobs.math_engine.domain.port.output.ExpressionEvaluatorPort;
import com.cheobs.math_engine.domain.port.output.FieldPort;
import com.cheobs.math_engine.domain.port.output.SubmissionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SubmissionEvaluatorService {

    private final FieldPort fieldPort;
    private final SubmissionPort submissionPort;
    private final ExpressionEvaluatorPort expressionEvaluatorPort;

    public SubmissionEvaluatorService(
            FieldPort fieldPort,
            SubmissionPort submissionPort,
            ExpressionEvaluatorPort expressionEvaluatorPort
    ) {
        this.fieldPort = fieldPort;
        this.submissionPort = submissionPort;
        this.expressionEvaluatorPort = expressionEvaluatorPort;
    }

    @Transactional
    public void evaluateSubmission(Submission submission) {

        var formulaFields = fieldPort
                .getByLayoutIdAndFieldType(submission.getLayout().getId(), FieldSource.CALCULATION)
                .stream()
                .sorted(Comparator.comparing(Field::getCalculationOrder))
                .toList();

        var fieldMap = submissionPort.getSubmissionFieldsBySubmission(submission.getId());

        Map<String, BigDecimal> submissionFieldsMapMap = fieldMap
                .stream()
                .collect(Collectors.toMap(
                        f -> f.getField().getExternalKey(),
                        SubmissionField::getValue
                ));

        formulaFields.forEach(field -> {
            var result = expressionEvaluatorPort.evaluate(field.getFormula(), submissionFieldsMapMap);
            submissionFieldsMapMap.put(field.getExternalKey(), result);
        });

        var allFields = fieldPort.getByLayoutIdAndSearch(submission.getLayout().getId(), null);

        var processedFields = allFields.stream().map(field -> {
            var value = submissionFieldsMapMap.get(field.getExternalKey());
            var treatedValue = FieldUtils.setScale(value, field.getFieldType());
            return new ProcessedField(submission, field, treatedValue);
        }).toList();

        submissionPort.saveProcessedFields(processedFields);

    }


}
