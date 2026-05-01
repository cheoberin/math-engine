package com.cheobs.math_engine.application.service;

import com.cheobs.math_engine.domain.model.field.FieldSource;
import com.cheobs.math_engine.domain.model.submission.Submission;
import com.cheobs.math_engine.domain.model.submission.SubmissionField;
import com.cheobs.math_engine.domain.port.output.ExpressionEvaluatorPort;
import com.cheobs.math_engine.domain.port.output.FieldPort;
import com.cheobs.math_engine.domain.port.output.SubmissionPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public void evaluateSubmission(Submission submission) {

        var formulaFields = fieldPort.getByLayoutIdAndFieldType(submission.getLayout().getId(), FieldSource.CALCULATION);
        var submissionFields = submissionPort.getSubmissionFieldsBySubmission(submission.getId());
        Map<String, BigDecimal> fieldMap = submissionFields
                .stream()
                .collect(Collectors.toMap(
                        f -> f.getField().getExternalKey(),
                        SubmissionField::getValue
                ));


        formulaFields.forEach(field -> {
            var result = expressionEvaluatorPort.evaluate(field.getFormula(), fieldMap);
            fieldMap.put(field.getExternalKey(), result);
        });


    }


}
