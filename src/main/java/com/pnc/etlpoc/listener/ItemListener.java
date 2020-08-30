package com.pnc.etlpoc.listener;

import com.pnc.etlpoc.model.Speaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.item.file.FlatFileParseException;

import java.util.List;

@Slf4j
public class ItemListener extends ItemListenerSupport<Speaker, Speaker> {

    @Override
    public void onReadError(Exception ex) {
        log.error("!!! On ReadError !!! Exception occurred while reading.", ex);
        if (ex instanceof FlatFileParseException) {
            FlatFileParseException ffpe = (FlatFileParseException) ex;
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("An error occured while processing the " +
                    ffpe.getLineNumber() +
                    " line of the file.  Below was the faulty " +
                    "input.\n");
            errorMessage.append(ffpe.getInput() + "\n");
            log.error(errorMessage.toString(), ffpe);
            throw new IllegalStateException(errorMessage.toString(), ffpe);
        } else {
            log.error("An error has occurred", ex);
        }
    }

    @Override
    public void onWriteError(Exception ex, List<? extends Speaker> items) {
        log.error("!!! On WriteError !!! Exception occurred while writing items.", ex, items);
    }
}
