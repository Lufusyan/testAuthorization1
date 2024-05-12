package com.generation.cdr.generating;

import com.generation.cdr.dto.TransactionDTO;
import com.generation.cdr.enums.CallParticipantType;
import com.generation.cdr.enums.CallType;
import com.generation.cdr.minio.Minio;
import com.generation.cdr.services.TransactionService;
import com.generation.cdr.store.repositories.TransactionRepository;
import com.generation.cdr.utils.CDRWriter;
import com.generation.cdr.utils.StartDateComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CDRGenerator {

    private final Minio minio;
    private final NumberGenerator generatorNumber;
    private final StartDateComparator startDateComparator;
    private final CDRWriter cdrWriter;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    @Value("${billing_period_in_months}")
    private int billingPeriodInMonths;

    @Value("${billable_year}")
    private String billableYear;

    @Value("${bucket}")
    private String bucket;

    @Value("${records_per_file}")
    private int recordsPerFile;

    @Value(("${file_format}"))
    private String fileFormat;

    @Value(("${max_number_of_calls_per_month}"))
    private int maxNumberOfCallsPerMonth;
    private final int numberOfThreads = Runtime.getRuntime().availableProcessors();
    private final ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
    private final List<TransactionDTO> bufferTransactions = new ArrayList<>();
    private int fileCounter = 1;

    @Autowired
    public CDRGenerator(Minio minio, NumberGenerator generatorNumber,
                        StartDateComparator startDateComparator, CDRWriter cdrWriter,
                        TransactionRepository transactionRepository, TransactionService transactionService) {
        this.minio = minio;
        this.generatorNumber = generatorNumber;
        this.startDateComparator = startDateComparator;
        this.cdrWriter = cdrWriter;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    public void generateCDRFiles() {
        if (!minio.bucketExists(bucket)) {
            minio.makeBucket(bucket);
        }
        else {
            minio.clearBucket(bucket);
        }
        transactionRepository.deleteAll();

        Random random = new Random();

        for (int month = 1; month <= billingPeriodInMonths; month++) {
            generateMonthlyTransactions(month, random);
            sendFilesToMinio();
        }

        if (!bufferTransactions.isEmpty()) {
            sendRemainingFilesToMinio();
        }

        transactionService.sortByStartDate();
        executor.shutdown();
    }

    private void generateMonthlyTransactions(int month, Random random) {
        int numberOfTransactions = random.nextInt(1, maxNumberOfCallsPerMonth);

        try {
            for (int i = 1; i <= numberOfTransactions; i++) {
                AtomicInteger monthNumber = new AtomicInteger(month);
                Future<TransactionDTO> transactionDTOFuture = executor.submit(() -> generateTransactionDTO(monthNumber));
                TransactionDTO transactionDTO = transactionDTOFuture.get();
                bufferTransactions.add(transactionDTO);
                transactionRepository.save(transactionService.toTransactionEntity(transactionDTO));

                if (transactionDTO.getCallParticipantType().equals(CallParticipantType.INTERNAL_AND_INTERNAL)) {
                    Future<TransactionDTO> mirrorTransactionDTOFuture = executor.submit(() -> generateMirrorTransactionDTO(transactionDTO));
                    TransactionDTO mirrorTransactionDTO = mirrorTransactionDTOFuture.get();
                    bufferTransactions.add(mirrorTransactionDTO);
                    transactionRepository.save(transactionService.toTransactionEntity(mirrorTransactionDTO));
                }
            }
            bufferTransactions.sort(startDateComparator);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void sendFilesToMinio() {
        int countOfFiles = bufferTransactions.size() / recordsPerFile;
        for (int i = 0; i < countOfFiles; i++) {
            String filename = String.format("%d" + fileFormat, fileCounter++);
            byte[] data = cdrWriter.writeCDRFile(bufferTransactions.subList(0, recordsPerFile));
            bufferTransactions.subList(0, recordsPerFile).clear();

            minio.uploadObject(bucket, filename, data);
        }
    }

    private void sendRemainingFilesToMinio() {
        String filename = String.format("%d" + fileFormat, fileCounter);
        byte[] data = cdrWriter.writeCDRFile(bufferTransactions.subList(0, bufferTransactions.size()));
        bufferTransactions.clear();

        minio.uploadObject(bucket, filename, data);
    }

    private TransactionDTO generateTransactionDTO(AtomicInteger month) {
        TransactionDTO transactionDTO = new TransactionDTO();

        Random random = ThreadLocalRandom.current();
        int numberOfCallTypes = CallType.values().length;
        CallType callType = CallType.values()[random.nextInt(numberOfCallTypes)];
        transactionDTO.setCallType(callType.getCode());

        CallParticipantType callParticipantType = generatorNumber.generateCallParticipantType(random);
        transactionDTO.setCallParticipantType(callParticipantType);

        String[] call = generatorNumber.generateNumbers(callParticipantType, random);
        transactionDTO.setFirstNumber(call[0]);
        transactionDTO.setSecondNumber(call[1]);

        AtomicInteger atomicBillableYear = new AtomicInteger(Integer.parseInt(billableYear));
        CallTimeGenerator callTimeGenerator = new CallTimeGenerator(month, atomicBillableYear);

        Instant startTime = callTimeGenerator.requestCallStartTime();
        Instant endTime = callTimeGenerator.requestCallEndTime(startTime);
        transactionDTO.setStartTime(startTime.getEpochSecond());
        transactionDTO.setEndTime(endTime.getEpochSecond());

        callTimeGenerator.releaseCallTime(startTime);

        return transactionDTO;
    }

    private TransactionDTO generateMirrorTransactionDTO(TransactionDTO transactionDTO) {
        TransactionDTO mirrorTransactionDTO = new TransactionDTO();

        if (transactionDTO.getCallType().equals(CallType.OUTGOING.getCode())) {
            mirrorTransactionDTO.setCallType(CallType.INCOMING.getCode());
        } else {
            mirrorTransactionDTO.setCallType(CallType.OUTGOING.getCode());
        }

        mirrorTransactionDTO.setFirstNumber(transactionDTO.getSecondNumber());
        mirrorTransactionDTO.setSecondNumber(transactionDTO.getFirstNumber());

        mirrorTransactionDTO.setStartTime(transactionDTO.getStartTime());
        mirrorTransactionDTO.setEndTime(transactionDTO.getEndTime());

        return mirrorTransactionDTO;
    }
}
