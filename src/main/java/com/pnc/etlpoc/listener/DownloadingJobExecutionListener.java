package com.pnc.etlpoc.listener;

import com.pnc.etlpoc.exception.ResourceNotFoundException;
import com.pnc.etlpoc.util.JobUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static com.pnc.etlpoc.util.JobUtil.Constants.RESOURCE_ROOT;

@Slf4j
public class DownloadingJobExecutionListener extends JobExecutionListenerSupport {

    private static final String TARGET_DIR = DownloadingJobExecutionListener.class.getClassLoader()
            .getResource(".").getFile() + RESOURCE_ROOT;
    private String resourceUrl1;
    private String resourceUrl2;
    private long startTime;
    private long endTime;

    @SneakyThrows
    @Override
    public void beforeJob(JobExecution jobExecution) {
        this.resourceUrl1 = jobExecution.getJobParameters().getString(JobUtil.Constants.JOB_PARAMETER_1);
        this.resourceUrl2 = jobExecution.getJobParameters().getString(JobUtil.Constants.JOB_PARAMETER_2);

        startTime = System.currentTimeMillis();
        log.info("Job started. [JobName:{}][jobExecutionId:{}]",
                jobExecution.getJobInstance().getJobName(), jobExecution.getId());
        try {
            String[] resourceUrls = new String[]{resourceUrl1, resourceUrl2};
            File targetDirectoryAsFile = new File(TARGET_DIR);
            if (!targetDirectoryAsFile.exists()) {
                FileUtils.forceMkdir(targetDirectoryAsFile);
            }
            StringBuilder resourceNames = new StringBuilder();
            for (String resourceUrl : resourceUrls) {
                String targetFileName = resourceUrl.substring(resourceUrl.lastIndexOf("/") + 1);
                File targetFile = new File(TARGET_DIR, targetFileName);
                copyResourceFromUrlToFile(resourceUrl, targetFile);
                resourceNames.append(targetFileName + ",");
                log.info(" Downloaded File : " + targetFile.getAbsolutePath());
            }
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        endTime = System.currentTimeMillis();
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("job finished.[JobName:{}][jobExecutionId:{}][ExitStatus:{}]",
                    jobExecution.getJobInstance().getJobName(),
                    jobExecution.getId(), jobExecution.getExitStatus().getExitCode());
            log.info("!!! JOB FINISHED! Time consumed :" + (endTime - startTime) + "ms");
        }
    }

    private boolean copyResourceFromUrlToFile(String resourceUrl, File target) {
        try {
            URL inputUrlResource = new URL(resourceUrl);
            // Copy bytes from the URL to the destination file.
            FileUtils.copyURLToFile(inputUrlResource, target);
        } catch (IOException e) {
            log.error("URL Resource : " + resourceUrl + " NOT found or corrupted." + e);
            throw new ResourceNotFoundException("Resource at url (" + resourceUrl + ") NOT found or corrupted.");
        }
        return true;
    }


}
