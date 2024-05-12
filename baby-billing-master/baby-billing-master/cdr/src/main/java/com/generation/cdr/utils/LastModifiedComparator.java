package com.generation.cdr.utils;

import io.minio.messages.Item;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class LastModifiedComparator implements Comparator<Item> {
    @Override
    public int compare(Item o1, Item o2) {
        return o1.lastModified().compareTo(o2.lastModified());
    }
}
