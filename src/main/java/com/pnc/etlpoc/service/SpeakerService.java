package com.pnc.etlpoc.service;

import com.pnc.etlpoc.response.SpeakerSummaryDTO;
import com.pnc.etlpoc.util.JobUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.pnc.etlpoc.util.JobUtil.Constants.*;

/**
 * Service to generate Speaker summary results.
 */
@Slf4j
@Service
public class SpeakerService {

    @Autowired
    Job speakerSummaryJob;
    @Autowired
    JobLauncher jobLauncher;

    private JobExecution jobExecution;

    @SneakyThrows
    public SpeakerSummaryDTO getSpeakersSummary(String url1, String url2) {
        Map<String, String> summaryMap = evaluateSpeakerSummary(url1, url2);
        return convertIntoDTO(summaryMap);
    }

    private Map evaluateSpeakerSummary(String resource1, String resource2) throws Throwable {
        log.info("!! Batch job triggered !!");
        JobParameters jobParameters = new JobParametersBuilder()
                .addString(JOB_PARAMETER_1, resource1)
                .addString(JOB_PARAMETER_2, resource2)
                .toJobParameters();
        this.jobExecution = this.jobLauncher.run(this.speakerSummaryJob, jobParameters);

        log.info("!! Job response handling !!");
        List<Throwable> failureExceptions = this.jobExecution.getAllFailureExceptions();
        String jobName = this.jobExecution.getJobInstance().getJobName();
        if (!this.jobExecution.getExitStatus().equals(ExitStatus.COMPLETED) && failureExceptions.stream().count() > 0) {
            JobUtil.handleJobFailures(jobName, failureExceptions);
        }
        return (Map) this.jobExecution.getExecutionContext().get(SPEAKER_SUMMARY_KEY);
    }

    private SpeakerSummaryDTO convertIntoDTO(Map<String, String> summaryMap) {
        SpeakerSummaryDTO speakerSummaryDTO = new SpeakerSummaryDTO();
        speakerSummaryDTO.setMostSpeeches(summaryMap.get(SUMMARY_MOST_SPEECHES_KEY));
        speakerSummaryDTO.setMostSecurity(summaryMap.get(SUMMARY_MOST_SECURITY_KEY));
        speakerSummaryDTO.setLeastWords(summaryMap.get(SUMMARY_LEAST_WORDS_KEY));
        return speakerSummaryDTO;
    }

}
