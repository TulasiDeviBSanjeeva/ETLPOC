package com.pnc.etlpoc.controller;

import com.pnc.etlpoc.exception.FileParseException;
import com.pnc.etlpoc.exception.ResourceNotFoundException;
import com.pnc.etlpoc.repository.SpeakerRepository;
import com.pnc.etlpoc.response.SpeakerInfoResponse;
import com.pnc.etlpoc.util.HttpDownloadUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.listener.StepListenerFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Execute job via web request
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class SpeakerController {

    public static final String FILTER_BY_YEAR = "2012";
    public static final String FILTER_BY_SUBJECT = "Innere Sicherheit";
    public static final String ZERO = "zero";

    @Autowired
    JobLauncher jobLauncher;
    @Autowired
    Job importCSVJob;
    @Autowired
    SpeakerRepository speakerRepo;

    private JobExecution jobExecution;

    /**
     * Retrieve speaker information on
     * 1. who was best speaker in a year 2012?
     * 2. who was best speaker on subject 'Internal Security'
     * 3. which speaker spoke the least
     *
     * @param url1 source url
     * @param url2 source url
     * @return Returns a map with the query results
     * @throws Exception
     */
    @GetMapping(value = "/speakers/info", produces = "application/json")
    public ResponseEntity<SpeakerInfoResponse> getSpeakerInfo(@RequestParam("url1") String url1,
                                                              @RequestParam("url2") String url2) throws Throwable {
        SpeakerInfoResponse response = new SpeakerInfoResponse();
        runImportBatchJob(url1, url2);

        Optional<String> mostSpeeches = speakerRepo.findBestSpeakerByYear(FILTER_BY_YEAR);
        Optional<String> mostSecurity = speakerRepo.findBestSpeakerBySubject(FILTER_BY_SUBJECT);
        Optional<String> leastWords = speakerRepo.findSpeakerWithMinWordsOverAll();

        response.setMostSpeeches(mostSpeeches.isPresent() ? mostSpeeches.get() : ZERO);
        response.setMostSecurity(mostSecurity.isPresent() ? mostSecurity.get() : ZERO);
        response.setLeastWordy(leastWords.isPresent() ? leastWords.get() : ZERO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void runImportBatchJob(String resource1, String resource2) throws Throwable {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("job.inputCSVFileResource1", resource1)
                .addString("job.inputCSVFileResource2", resource2)
                .toJobParameters();

        jobExecution = jobLauncher.run(importCSVJob, jobParameters);
        List<Throwable> failureExceptions = jobExecution.getAllFailureExceptions();
        if (!jobExecution.getExitStatus().equals(ExitStatus.COMPLETED) && failureExceptions.stream().count() > 0) {
            Throwable throwable = failureExceptions.stream().findFirst().get();
            if (throwable instanceof ResourceNotFoundException) {
                throw new ResourceNotFoundException(String.format("JobName[%s] Job execution failed. Reason : %s",
                        jobExecution.getJobInstance().getJobName(), throwable), throwable);
            } else if (throwable instanceof StepListenerFailedException) {
                throw new FileParseException(String.format("JobName[%s] Job execution failed. Reason : %s",
                        jobExecution.getJobInstance().getJobName(), throwable.getCause()), throwable);
            } else {
                throw new RuntimeException(String.format("JobName[%s] Job execution failed. Reason : %s",
                        jobExecution.getJobInstance().getJobName(), throwable.getCause()), throwable);
            }
        }
    }

    // TODO : Is there a better way to implement?
    private String validateResourcetype(String url) {
        return HttpDownloadUtility.getResourceAtUrl(url)
                .orElseThrow(() -> new ResourceNotFoundException("Resource at url (" + url + ") NOT found or corrupted."));
    }

}
