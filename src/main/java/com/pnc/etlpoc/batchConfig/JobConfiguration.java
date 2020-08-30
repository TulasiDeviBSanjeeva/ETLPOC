package com.pnc.etlpoc.batchConfig;

import com.pnc.etlpoc.listener.DownloadingJobExecutionListener;
import com.pnc.etlpoc.listener.ItemListener;
import com.pnc.etlpoc.listener.JobSkipPolicy;
import com.pnc.etlpoc.model.Speaker;
import com.pnc.etlpoc.reader.MultipleCSVResourceItemReader;
import com.pnc.etlpoc.repository.SpeakerRepository;
import com.pnc.etlpoc.validator.ParameterValidator;
import com.pnc.etlpoc.writer.ResourceItemWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Configure Batch Job [1. Read the CSV file, 2. Write to DB and 3. Evaluate summary results].
 */
@Component
@Slf4j
public class JobConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job(Step stepOne, Step stepTwo) {
        return this.jobBuilderFactory.get("ETL")
                .incrementer(new RunIdIncrementer())
                .listener(new DownloadingJobExecutionListener())
                .start(stepOne)
                .validator(validator())
                .next(stepTwo)
                .build();
    }

    @Bean
    public Step stepOne(ItemReader<Speaker> reader, ItemWriter<Speaker> writer, ItemReadListener<Speaker> itemListener) {
        return this.stepBuilderFactory.get("readCSV-writeToDb")
                .<Speaker, Speaker>chunk(20)
                .reader(reader)
                .writer(writer)
                //.faultTolerant().skipPolicy(skipPolicy()) // TODO : requirement to be clarified on how failures are expected to be handled
                .listener(itemListener)
                .build();
    }

    @Bean
    public Step stepTwo(SpeakerRepository speakerRepository) {
        return stepBuilderFactory.get("evaluate-speakerSummary")
                .tasklet(speakersSummaryTasklet(speakerRepository))
                .build();
    }

    @Bean
    public JobParametersValidator validator() {
        return new ParameterValidator();
    }

    @Bean
    public DownloadingJobExecutionListener downloadingStepExecutionListener() {
        return new DownloadingJobExecutionListener();
    }

    @Bean
    @StepScope
    public MultiResourceItemReader<Speaker> reader(@Value("#{jobParameters['inputCSVFileResource1']}") String inputResource1,
                                                   @Value("#{jobParameters['inputCSVFileResource2']}") String inputResource2) {
        return new MultipleCSVResourceItemReader(inputResource1,inputResource2);
    }

    @Bean
    public ResourceItemWriter writer() {
        return new ResourceItemWriter();
    }

    @Bean
    public ItemListener customItemReadListener() {
        return new ItemListener();
    }

    @Bean
    public JobSkipPolicy skipPolicy() {
        return new JobSkipPolicy();
    }

    @Bean
    public SpeakersSummaryTasklet speakersSummaryTasklet(SpeakerRepository speakerRepository) {
        SpeakersSummaryTasklet speakersSummaryTasklet = new SpeakersSummaryTasklet();
        speakersSummaryTasklet.setSpeakerRepository(speakerRepository);
        return speakersSummaryTasklet;
    }

}