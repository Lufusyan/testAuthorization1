package com.generation.cdr.dto;

import com.generation.cdr.enums.CallParticipantType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDTO {

    private String callType;

    private String firstNumber;

    private String secondNumber;

    private CallParticipantType callParticipantType;

    private long startTime;

    private long endTime;
}
