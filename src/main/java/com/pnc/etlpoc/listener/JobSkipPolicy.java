package com.pnc.etlpoc.listener;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

/**
 * Fault tolerance handler.
 */
public class JobSkipPolicy implements SkipPolicy {

    @Override
    public boolean shouldSkip(Throwable throwable, int failedCount) throws SkipLimitExceededException {
        return (failedCount >= 1) ? false : true;
    }

}