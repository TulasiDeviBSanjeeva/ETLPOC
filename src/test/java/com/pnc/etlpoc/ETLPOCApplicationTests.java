package com.pnc.etlpoc;

import com.pnc.etlpoc.response.SpeakerInfoResponse;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class ETLPOCApplicationTests {

	Logger logger = Logger.getLogger(ETLPOCApplicationTests.class.getName());

	@LocalServerPort
	int randomServerPort;

	@Test
	public void testGetSpeakerInfo() throws URISyntaxException {
		RestTemplate restTemplate = new RestTemplate();
		String url1 = "https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data1.csv";
		String url2 = "https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data2.csv";

		final String buildUrl = "http://localhost:" + randomServerPort + "/speakers/info?url1=" + url1 + "&url2=" + url2;
		URI uri = new URI(buildUrl);

		SpeakerInfoResponse response  = restTemplate.getForObject(uri, SpeakerInfoResponse.class);
		System.out.println(response.toString());

		Assert.assertEquals("Zero", response.getMostSecurity());
		Assert.assertEquals("Alexander Abel", response.getMostSpeeches());
		Assert.assertEquals("Caesare Collins", response.getLeastWordy());

	}

/*	@Test
	public void testResourceNotExists() throws Exception {
		JobExecution jobExecution = launcher.run(job2, new JobParameters());
		Assert.isTrue(jobExecution.getExitStatus().getExitCode().equals("FAILED"), "The job exit status is not FAILED.");
		Assert.isTrue(jobExecution.getAllFailureExceptions().get(0).getMessage().contains("Failed to initialize the reader"), "The job failed for the wrong reason.");
	}*/

}
