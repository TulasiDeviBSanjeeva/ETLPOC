package com.pnc.etlpoc.reader;

import com.pnc.etlpoc.batchConfig.BeanWrapperFieldSetMapperCustom;
import com.pnc.etlpoc.model.Speaker;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import static com.pnc.etlpoc.util.JobUtil.Constants.*;

/**
 * Reader to read the CSV file.
 */
public class MultipleCSVResourceItemReader extends MultiResourceItemReader<Speaker> {

    private static final String READER_NAME = "MultipleCSVFileReader";

    public MultipleCSVResourceItemReader(String inputResourceUrl1, String inputResourceUrl2) {
        ClassPathResource inputResource1 = getClasspathResource(inputResourceUrl1);
        ClassPathResource inputResource2 = getClasspathResource(inputResourceUrl2);
        this.setName(READER_NAME);
        this.setResources(new ClassPathResource[]{inputResource1, inputResource2});
        this.setDelegate(delegate());
    }

    @Bean
    public FlatFileItemReader<Speaker> delegate() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(new String[]{FIELD_ID, FIELD_NAME, FIELD_SUBJECT, FIELD_DATE, FIELD_WORDS});
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(true);

        BeanWrapperFieldSetMapperCustom<Speaker> fieldSetMapper = new BeanWrapperFieldSetMapperCustom<>();
        fieldSetMapper.setTargetType(Speaker.class);

        DefaultLineMapper<Speaker> customerLineMapper = new DefaultLineMapper<>();
        customerLineMapper.setLineTokenizer(lineTokenizer);
        customerLineMapper.setFieldSetMapper(fieldSetMapper);
        customerLineMapper.afterPropertiesSet();
        FlatFileItemReader<Speaker> reader = new FlatFileItemReader<>();
        reader.setLineMapper(customerLineMapper);
        return reader;
    }

    private ClassPathResource getClasspathResource(String inputResourceUrl1) {
        return new ClassPathResource(RESOURCE_ROOT + inputResourceUrl1.substring(inputResourceUrl1.lastIndexOf("/") + 1));
    }

}
