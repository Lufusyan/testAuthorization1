package com.generation.cdr.generating;

import java.time.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class CallTimeGenerator {
    private final Lock lock = new ReentrantLock();
    private final Condition available = lock.newCondition();
    private final AtomicInteger month;
    private final AtomicInteger year;
    private final List<Boolean> timeSlots;

    private static final int MINUTES_IN_HOUR = 60;
    private static final int HOURS_IN_DAY = 24;

    public CallTimeGenerator(AtomicInteger month, AtomicInteger year) {
        this.month = month;
        this.year = year;
        int daysInMonth = YearMonth.of(year.intValue(), month.intValue()).lengthOfMonth();
        this.timeSlots = Collections.synchronizedList(new ArrayList<>(daysInMonth * HOURS_IN_DAY * MINUTES_IN_HOUR));
        for (int i = 0; i < daysInMonth * HOURS_IN_DAY * MINUTES_IN_HOUR; i++) {
            timeSlots.add(true);
        }
    }

    public Instant requestCallStartTime() {
        lock.lock();
        try {
            int startIndex = ThreadLocalRandom.current().nextInt(timeSlots.size());
            for (int i = startIndex; i < timeSlots.size(); i++) {
                if (timeSlots.get(i)) {
                    timeSlots.set(i, false);
                    return getInstantFromIndex(i);
                }
            }
            for (int i = 0; i < startIndex; i++) {
                if (timeSlots.get(i)) {
                    timeSlots.set(i, false);
                    return getInstantFromIndex(i);
                }
            }
            available.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return requestCallStartTime();
    }

    public void releaseCallTime(Instant time) {
        lock.lock();
        try {
            int index = getIndexFromInstant(time);
            timeSlots.set(index, true);
            available.signal();
        } finally {
            lock.unlock();
        }
    }

    public Instant requestCallEndTime(Instant startTime) {
        lock.lock();
        try {
            int startIndex = getIndexFromInstant(startTime);
            int endIndex = generateRandomEndTime(startIndex);
            return getInstantFromIndex(endIndex);
        } finally {
            lock.unlock();
        }
    }

    private int generateRandomEndTime(int startIndex) {
        Random random = ThreadLocalRandom.current();
        int offset = random.nextInt(1, MINUTES_IN_HOUR + 1);
        int endIndex = startIndex + offset;
        int daysInMonth = getDaysInMonth(month);
        if (endIndex >= daysInMonth * HOURS_IN_DAY * MINUTES_IN_HOUR) {
            endIndex -= daysInMonth * HOURS_IN_DAY * MINUTES_IN_HOUR;
            month.incrementAndGet();
            if (month.intValue() > Month.valueOf("DECEMBER").getValue()) {
                month.set(Month.valueOf("JANUARY").getValue());
                year.incrementAndGet();
            }
        }
        return endIndex;
    }

    private int getDaysInMonth(AtomicInteger month) {
        YearMonth yearMonth = YearMonth.of(year.intValue(), month.intValue());
        return yearMonth.lengthOfMonth();
    }

    private Instant getInstantFromIndex(int index) {
        LocalDateTime dateTime = LocalDateTime.of(year.intValue(), month.intValue(), 1, 0, 0)
                .plusMinutes(index);
        return dateTime.toInstant(ZoneOffset.UTC);
    }

    private int getIndexFromInstant(Instant time) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(time, ZoneOffset.UTC);
        int daysOffset = dateTime.getDayOfMonth() - 1;
        int minutesOffset = dateTime.getHour() * MINUTES_IN_HOUR + dateTime.getMinute();
        return daysOffset * HOURS_IN_DAY * MINUTES_IN_HOUR + minutesOffset;
    }
}
