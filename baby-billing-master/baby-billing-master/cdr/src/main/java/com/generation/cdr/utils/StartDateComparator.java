package com.generation.cdr.utils;

import com.generation.cdr.dto.TransactionDTO;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class StartDateComparator implements Comparator<TransactionDTO> {

    @Override
    public int compare(TransactionDTO o1, TransactionDTO o2) {
        return Long.compare(o1.getStartTime(), o2.getStartTime());
    }
}
