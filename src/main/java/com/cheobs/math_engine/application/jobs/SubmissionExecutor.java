package com.cheobs.math_engine.application.jobs;

import com.cheobs.math_engine.application.service.SubmissionEvaluatorService;
import com.cheobs.math_engine.domain.model.submission.Submission;
import com.cheobs.math_engine.domain.port.output.SubmissionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class SubmissionExecutor {

    private final SubmissionPort submissionPort;
    private final SubmissionEvaluatorService submissionEvaluatorService;

    private final Logger logger = LoggerFactory.getLogger(SubmissionExecutor.class);

    public SubmissionExecutor(SubmissionPort submissionPort, SubmissionEvaluatorService submissionEvaluatorService) {
        this.submissionPort = submissionPort;
        this.submissionEvaluatorService = submissionEvaluatorService;
    }

    @Scheduled(fixedDelay = 10000)
    public void execute() {

        logger.info("Starting submission execution job");

        var submissions = submissionPort.pullPendingSubmisions(5);

        for (Submission sub : submissions) {

            var submissionId = sub.getId();
            logger.info("Processing submission {}", submissionId);

            try {
                sub.markAsProcessing();
                submissionPort.save(sub);
                submissionEvaluatorService.evaluateSubmission(sub);
                sub.completeProcessing();
                logger.info("Successfully processed submission {}", submissionId);

            } catch (Exception e) {
                sub.failProcessing();
                logger.error("Error processing submission {}", submissionId, e);
            }

            submissionPort.save(sub);
        }
    }

}
