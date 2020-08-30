package com.pnc.etlpoc.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

import static com.pnc.etlpoc.util.JobUtil.Constants.*;

public class ParameterValidator implements JobParametersValidator {

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String resourceUrl1 = parameters.getString(JOB_PARAMETER_1);
        String resourceUrl2 = parameters.getString(JOB_PARAMETER_2);

        if(!StringUtils.hasText(resourceUrl1) || !StringUtils.hasText(resourceUrl2)) {
            throw new JobParametersInvalidException(MISSING_RESOURCE_REQUEST_PARAMETER);
        }
        else if(!StringUtils.endsWithIgnoreCase(resourceUrl1, EXT) ||
                !StringUtils.endsWithIgnoreCase(resourceUrl2, EXT)) {
            throw new JobParametersInvalidException(INVALID_FILE_EXTENSION);
        }
    }
}
