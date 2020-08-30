package com.pnc.etlpoc.reader;

import com.pnc.etlpoc.config.BeanWrapperFieldSetMapperCustom;
import com.pnc.etlpoc.model.Speaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

/**
 * Read the csv file, use FlatFileItemReader here, and convert to Person output.
 */
@Slf4j
public class MultipleCSVResourceItemReader extends MultiResourceItemReader<Speaker> {

    public static final String RESOURCE_ROOT = "/data/";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String SUBJECT = "subject";
    public static final String DATE = "date";
    public static final String WORDS = "words";

    public MultipleCSVResourceItemReader(String inputResources) {
        this.setName("MultipleCSVFileReader");
        this.setResources(new ClassPathResource[]{new ClassPathResource(RESOURCE_ROOT + inputResources.split(",")[0]),
                new ClassPathResource(RESOURCE_ROOT + inputResources.split(",")[1])});
        this.setDelegate(delegate());
    }

    @Bean
    public FlatFileItemReader<Speaker> delegate() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(new String[]{ID, NAME, SUBJECT, DATE, WORDS});
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(true);

        BeanWrapperFieldSetMapperCustom<Speaker> fieldSetMapper = new BeanWrapperFieldSetMapperCustom<>();
        fieldSetMapper.setTargetType(Speaker.class);

        DefaultLineMapper<Speaker> customerLineMapper = new DefaultLineMapper<>();
        customerLineMapper.setLineTokenizer(lineTokenizer);
        customerLineMapper.setFieldSetMapper(fieldSetMapper);
        customerLineMapper.afterPropertiesSet();
        FlatFileItemReader<Speaker> reader = new FlatFileItemReader<Speaker>();
        reader.setLineMapper(customerLineMapper);
        return reader;
    }

}
