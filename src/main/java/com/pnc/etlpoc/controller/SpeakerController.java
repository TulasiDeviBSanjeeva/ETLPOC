package com.pnc.etlpoc.controller;

import com.pnc.etlpoc.response.SpeakerSummaryDTO;
import com.pnc.etlpoc.service.SpeakerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class SpeakerController {

    @Autowired
    SpeakerService service;

    /**
     * Retrieves speaker information on
     * 1. The best speaker in a year 2012.
     * 2. The best speaker on subject 'Internal Security'.
     * 3. The speaker who spoke the least.
     *
     * @param url1 source url
     * @param url2 source url
     * @return Returns a map with the query results
     * @throws Exception
     */
    @GetMapping(value = "/speakers/info", produces = "application/json")
    public ResponseEntity<SpeakerSummaryDTO> retrieveSpeakerSummary(@RequestParam("url1") String url1,
                                                 @RequestParam("url2") String url2) {
        SpeakerSummaryDTO speakerSummaryDTO = service.getSpeakersSummary(url1, url2);
        return new ResponseEntity<>(speakerSummaryDTO, HttpStatus.OK);
    }

}
