package com.pnc.etlpoc.writer;

import com.pnc.etlpoc.model.Speaker;
import com.pnc.etlpoc.repository.SpeakerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Writes all speaker objects to the database
 */
@Slf4j
public class ResourceItemWriter implements ItemWriter<Speaker> {

    @Autowired
    private SpeakerRepository speakerRepository;

    @Override
    public void write(List<? extends Speaker> speakers) throws Exception {
        log.info("Writing " + speakers.size() + " speakers to database.");
        speakerRepository.saveAll(speakers);
        log.info("Writing speakers to database completed.");
    }

}
