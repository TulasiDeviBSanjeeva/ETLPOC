package com.pnc.etlpoc.response;

import lombok.Data;

/**
 * DTO to map model objects to response object.
 */
@Data
public class SpeakerSummaryDTO {

    String mostSpeeches;
    String mostSecurity;
    String leastWords;

}
