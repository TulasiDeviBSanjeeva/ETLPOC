package com.pnc.etlpoc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ETLPOCApplicationTests {

    @LocalServerPort
    int randomServerPort;

    @Test
    public void testGetSpeakerInfo() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        String url1 = "https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data1.csv";
        String url2 = "https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data2.csv";

        final String buildUrl = "http://localhost:" + randomServerPort + "/api/speakers/info?url1=" + url1 + "&url2=" + url2;
        URI uri = new URI(buildUrl);
        //TODO
    }

}
