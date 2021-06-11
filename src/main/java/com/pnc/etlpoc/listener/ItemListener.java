package com.pnc.etlpoc.listener;

import com.pnc.etlpoc.model.Speaker;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.item.file.FlatFileParseException;

import java.util.List;

@Slf4j
public class ItemListener extends ItemListenerSupport<Speaker, Speaker> {

    @SneakyThrows
    @Override
    public void onReadError(Exception ex) {
        log.error("!!! On ReadError !!! Exception occurred while reading.", ex);

        if (ex instanceof FlatFileParseException) {
            FlatFileParseException ffpe = (FlatFileParseException) ex;
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("An error occured while processing the " + ffpe.getLineNumber() + " line of the file. "
                    + "Below was the faulty input." + ffpe.getInput());
            log.error(errorMessage.toString(), ffpe);
            throw ffpe;
        } else {
            log.error("An error has occurred", ex);
            throw ex;
        }
    }

    @SneakyThrows
    @Override
    public void onWriteError(Exception ex, List<? extends Speaker> items) {
        log.error("!!! On WriteError !!! Exception occurred while writing items.", ex, items);
        throw ex;
    }
}
