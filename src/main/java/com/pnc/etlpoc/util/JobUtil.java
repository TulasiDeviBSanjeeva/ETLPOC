package com.pnc.etlpoc.util;

import com.pnc.etlpoc.exception.FileParseException;
import com.pnc.etlpoc.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.batch.core.listener.StepListenerFailedException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global utility helper.
 */
public class JobUtil {

    public static void handleJobFailures(String jobName, List<Throwable> failureExceptions) {
        Throwable throwable = failureExceptions.stream().findFirst().get();
        if (throwable instanceof ResourceNotFoundException) {
            throw new ResourceNotFoundException(buildErrorMessage(jobName, throwable), throwable);
        } else if (throwable instanceof StepListenerFailedException) {
            throw new FileParseException(buildErrorMessage(jobName, throwable), throwable);
        } else {
            throw new RuntimeException(buildErrorMessage(jobName, throwable), throwable);
        }
    }

    private static String buildErrorMessage(String jobName, Throwable throwable) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Job[" + jobName + "] execution failed.");
        errorMessage.append("Reason : '" + throwable.getMessage() + "'");
        return errorMessage.toString();
    }

    public static <S, T> List<T> mapList(ModelMapper modelMapper, List<S> source, Class<T> targetClass) {
        return source
                .stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }

    /** Global Constants **/
    public static class Constants {

        //General
        public static final String DATE_PATTERN = "yyyy-MM-dd";

        //Job parameter keys
        public static final String JOB_PARAMETER_1 = "inputCSVFileResource1";
        public static final String JOB_PARAMETER_2 = "inputCSVFileResource2";

        //Resource keys
        public static final String RESOURCE_ROOT = "/data/";

        //Fields
        public static final String FIELD_ID = "id";
        public static final String FIELD_NAME = "name";
        public static final String FIELD_SUBJECT = "subject";
        public static final String FIELD_DATE = "date";
        public static final String FIELD_WORDS = "words";

        //Filter keys
        public static final String FILTER_BY_YEAR = "2012";
        public static final String FILTER_BY_SUBJECT = "Innere Sicherheit";
        public static final String ZERO = "zero";

        //Speaker summary key
        public static final String SPEAKER_SUMMARY_KEY = "SpeakersSummary";
        public static final String SUMMARY_MOST_SPEECHES_KEY = "mostSpeeches";
        public static final String SUMMARY_MOST_SECURITY_KEY = "mostSecurity";
        public static final String SUMMARY_LEAST_WORDS_KEY = "leastWords";

        //Error messages
        public static final String MISSING_RESOURCE_REQUEST_PARAMETER = "One or both resource request parameter is missing.";
        public static final String INVALID_FILE_EXTENSION = "One or both resource request parameter does not have the .csv file extension.";
        public static final String EXT = "csv";

    }

}
