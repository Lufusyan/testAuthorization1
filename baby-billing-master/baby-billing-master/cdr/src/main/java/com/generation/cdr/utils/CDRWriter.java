package com.generation.cdr.utils;

import com.generation.cdr.dto.TransactionDTO;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


@Component
public class CDRWriter {

    public byte[] writeCDRFile(List<TransactionDTO> cdrRecords) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (TransactionDTO record : cdrRecords) {
            String data = record.getCallType() + "," + record.getFirstNumber() + ","
                    + record.getSecondNumber() + "," + record.getStartTime() + ","
                    + record.getEndTime() + "\n";
            try {
                outputStream.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outputStream.toByteArray();
    }
}
