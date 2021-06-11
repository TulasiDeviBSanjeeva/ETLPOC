package com.pnc.etlpoc.batchConfig;

import com.pnc.etlpoc.repository.SpeakerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.pnc.etlpoc.util.JobUtil.Constants.*;

@Slf4j
public class SpeakersSummaryTasklet implements Tasklet {

    private SpeakerRepository speakerRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("!! Evaluating Speaker Summary !!");
        Optional<String> mostSpeeches = this.speakerRepository.findBestSpeakerByYear(FILTER_BY_YEAR);
        Optional<String> mostSecurity = this.speakerRepository.findBestSpeakerBySubject(FILTER_BY_SUBJECT);
        Optional<String> leastWords = this.speakerRepository.findSpeakerWithMinWordsOverAll();

        Map<String, String> speakersSummary = new HashMap<>();
        speakersSummary.put(SUMMARY_MOST_SPEECHES_KEY, mostSpeeches.isPresent() ? mostSpeeches.get() : ZERO);
        speakersSummary.put(SUMMARY_MOST_SECURITY_KEY, mostSecurity.isPresent() ? mostSecurity.get() : ZERO);
        speakersSummary.put(SUMMARY_LEAST_WORDS_KEY, leastWords.isPresent() ? leastWords.get() : ZERO);
        chunkContext.getStepContext().getStepExecution().getJobExecution()
                .getExecutionContext().put(SPEAKER_SUMMARY_KEY, speakersSummary);
        return RepeatStatus.FINISHED;
    }

    public void setSpeakerRepository(SpeakerRepository speakerRepository) {
        this.speakerRepository = speakerRepository;
    }

}
