package com.pnc.etlpoc.config;

import com.pnc.etlpoc.listener.ItemListener;
import com.pnc.etlpoc.listener.DownloadingJobExecutionListener;
import com.pnc.etlpoc.listener.JobSkipPolicy;
import com.pnc.etlpoc.model.Speaker;
import com.pnc.etlpoc.reader.MultipleCSVResourceItemReader;
import com.pnc.etlpoc.writer.ResourceItemWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.Job;
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
 * Configure Job
 */
@Component
@Slf4j
public class JobConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job(Step stepOne) {
        return this.jobBuilderFactory.get("ETL")
                .incrementer(new RunIdIncrementer())
                .listener(new DownloadingJobExecutionListener())
                .start(stepOne)
                .build();
    }

    @Bean
    public Step stepOne(ItemReader<Speaker> reader, ItemWriter<Speaker> writer, ItemReadListener<Speaker> itemListener) {
        return this.stepBuilderFactory.get("readCSV-writeToDb")
                .<Speaker, Speaker>chunk(20)
                .reader(reader)
                .writer(writer)
                .faultTolerant().skipPolicy(skipPolicy())
                .listener(itemListener)
                .build();
    }

    @Bean
    public DownloadingJobExecutionListener downloadingStepExecutionListener() {
        return new DownloadingJobExecutionListener();
    }

    @Bean
    @StepScope
    public MultiResourceItemReader<Speaker> reader(@Value("#{jobExecutionContext['inputResources']}") String inputResources) {
        return new MultipleCSVResourceItemReader(inputResources);
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

}