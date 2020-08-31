package com.pnc.etlpoc;

import com.pnc.etlpoc.response.SpeakerSummaryDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Test Coverage :
 * 1. Success - input url1 & url2. Speaker Summary {Best speaker in 2012, Best speaker on subject 'internal security', Speaker who spoke fewest of all}
 * 2. Input Validation - input url1 but url2 is missing (empty or null).
 * 3. Input Validation - input urls file extension
 * 4. Input Validation - input url non-existent resource, should fail with 404 ResourceNotFoundException
 * 5. Input Validation - input url faulty data, should fail with 400 FlatFileParseException
 * 6. Functional - No answer possible, field should be 'zero'
 * 7. Functional - No unique answer possible, field should be zero
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ETLPOCApplicationTests {

    private static final String VALID_SPEAKER_DATA_1_CSV = "https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data1.csv";
    private static final String VALID_SPEAKER_DATA_2_CSV = "https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data2.csv";
    private static final String VALID_SPEAKER_DATA_3_CSV = "https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data3.csv";
    private static final String VALID_SPEAKER_DATA_4_CSV = "https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data4.csv";
    private static final String VALID_SPEAKER_DATA_5_CSV = "https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data5.csv";
    private static final String INVALID_ADDRESSES_CSV = "https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/addresses.csv";
    private static final String UNKNOWN_SPEAKER_DATA_6_CSV = "https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data6.csv";

    @LocalServerPort
    int randomServerPort;

    @Test
    @Sql(scripts = "/schema_test.sql")
    public void testRetrieveSpeakerSummarySuccess() throws URISyntaxException {
        String url1 = VALID_SPEAKER_DATA_1_CSV;
        String url2 = VALID_SPEAKER_DATA_2_CSV;

        RestTemplate restTemplate = new RestTemplate();
        final String buildUrl = "http://localhost:" + randomServerPort + "/api/speakers/info?url1=" + url1 + "&url2=" + url2;
        URI uri = new URI(buildUrl);

        ResponseEntity<SpeakerSummaryDTO> speakerSummaryResult = restTemplate.getForEntity(uri, SpeakerSummaryDTO.class);
        //Verify request succeed
        Assert.assertEquals(200, speakerSummaryResult.getStatusCodeValue());
        Assert.assertNotNull(speakerSummaryResult.getBody().getMostSpeeches());
        Assert.assertNotNull(speakerSummaryResult.getBody().getMostSecurity());
        Assert.assertNotNull(speakerSummaryResult.getBody().getLeastWords());

        Assert.assertEquals("Alexander Abel", speakerSummaryResult.getBody().getMostSpeeches());
        Assert.assertEquals("Alexander Abel", speakerSummaryResult.getBody().getMostSecurity());
        Assert.assertEquals("Caesare Collins", speakerSummaryResult.getBody().getLeastWords());
    }

    @Test
    @Sql(scripts = "/schema_test.sql")
    public void testRetrieveSpeakerSummaryFailure_MissingRequestParameters() throws URISyntaxException {
        String url1 = VALID_SPEAKER_DATA_1_CSV;
        String url2 = null;

        RestTemplate restTemplate = new RestTemplate();
        final String buildUrl = "http://localhost:" + randomServerPort + "/api/speakers/info?url1=" + url1 + "&url2=" + url2;
        URI uri = new URI(buildUrl);
        try {
            restTemplate.getForEntity(uri, SpeakerSummaryDTO.class);
            Assert.fail();
        } catch (HttpClientErrorException ex) {
            Assert.assertEquals(400, ex.getRawStatusCode());
            Assert.assertEquals(true, ex.getMessage().contains("One or both resource request parameter does not have the .csv file extension."));
            Assert.assertEquals(true, ex.getResponseBodyAsString().contains("JobParametersInvalidException"));
        }
    }

    @Test
    @Sql(scripts = "/schema_test.sql")
    public void testRetrieveSpeakerSummaryFailure_WithHttpStatus_404_ResourceNotFound() throws URISyntaxException {
        String url1 = VALID_SPEAKER_DATA_1_CSV;
        String url2 = UNKNOWN_SPEAKER_DATA_6_CSV;

        RestTemplate restTemplate = new RestTemplate();
        final String buildUrl = "http://localhost:" + randomServerPort + "/api/speakers/info?url1=" + url1 + "&url2=" + url2;
        URI uri = new URI(buildUrl);

        try {
            restTemplate.getForEntity(uri, SpeakerSummaryDTO.class);
            Assert.fail();
        } catch (HttpClientErrorException ex) {
            Assert.assertEquals(404, ex.getRawStatusCode());
            Assert.assertEquals(true, ex.getResponseBodyAsString().contains("ResourceNotFoundException"));
        }
    }

    @Test
    @Sql(scripts = "/schema_test.sql")
    public void testRetrieveSpeakerSummaryFailure_WithHttpStatus_400_BadRequest() throws URISyntaxException {
        String url1 = VALID_SPEAKER_DATA_1_CSV;
        String url2 = INVALID_ADDRESSES_CSV;

        RestTemplate restTemplate = new RestTemplate();
        final String buildUrl = "http://localhost:" + randomServerPort + "/api/speakers/info?url1=" + url1 + "&url2=" + url2;
        URI uri = new URI(buildUrl);
        try {
            restTemplate.getForEntity(uri, SpeakerSummaryDTO.class);
            Assert.fail();
        } catch (HttpClientErrorException ex) {
            Assert.assertEquals(400, ex.getRawStatusCode());
            Assert.assertEquals(true, ex.getResponseBodyAsString().contains("FlatFileParseException"));
        }
    }

    @Test
    @Sql(scripts = "/schema_test.sql")
    public void testRetrieveSpeakerSummarySuccess_BestSpeakerWithMostSpeechIn2012_NoAnswerPossible() throws URISyntaxException {
        String url1 = VALID_SPEAKER_DATA_3_CSV;
        String url2 = VALID_SPEAKER_DATA_4_CSV;

        RestTemplate restTemplate = new RestTemplate();
        final String buildUrl = "http://localhost:" + randomServerPort + "/api/speakers/info?url1=" + url1 + "&url2=" + url2;
        URI uri = new URI(buildUrl);

        ResponseEntity<SpeakerSummaryDTO> speakerSummaryResult = restTemplate.getForEntity(uri, SpeakerSummaryDTO.class);
        //Verify request succeed
        Assert.assertEquals(200, speakerSummaryResult.getStatusCodeValue());
        Assert.assertNotNull(speakerSummaryResult.getBody().getMostSpeeches());
        Assert.assertNotNull(speakerSummaryResult.getBody().getMostSecurity());
        Assert.assertNotNull(speakerSummaryResult.getBody().getLeastWords());

        Assert.assertEquals("zero", speakerSummaryResult.getBody().getMostSpeeches());
        Assert.assertEquals("Alexander Abel2", speakerSummaryResult.getBody().getMostSecurity());
        Assert.assertEquals("Caesare Collins2", speakerSummaryResult.getBody().getLeastWords());
    }

    @Test
    @Sql(scripts = "/schema_test.sql")
    public void testRetrieveSpeakerSummarySuccess_BestSpeakerWithMostSpeechIn2012_NoUniqueAnswerPossible() throws URISyntaxException {
        String url1 = VALID_SPEAKER_DATA_4_CSV;
        String url2 = VALID_SPEAKER_DATA_5_CSV;

        RestTemplate restTemplate = new RestTemplate();
        final String buildUrl = "http://localhost:" + randomServerPort + "/api/speakers/info?url1=" + url1 + "&url2=" + url2;
        URI uri = new URI(buildUrl);

        ResponseEntity<SpeakerSummaryDTO> speakerSummaryResult = restTemplate.getForEntity(uri, SpeakerSummaryDTO.class);
        //Verify request succeed
        Assert.assertEquals(200, speakerSummaryResult.getStatusCodeValue());
        Assert.assertNotNull(speakerSummaryResult.getBody().getMostSpeeches());
        Assert.assertNotNull(speakerSummaryResult.getBody().getMostSecurity());
        Assert.assertNotNull(speakerSummaryResult.getBody().getLeastWords());

        Assert.assertEquals("zero", speakerSummaryResult.getBody().getMostSpeeches());
        Assert.assertEquals("Alexander Abel2", speakerSummaryResult.getBody().getMostSecurity()); //Query picks Top 1 speaker
        Assert.assertEquals("Caesare Collins2", speakerSummaryResult.getBody().getLeastWords());
    }

}
