package com.fastcampus.hellospringbatch.job.validator;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@AllArgsConstructor
public class LocalDateParameterValidator implements JobParametersValidator {
    private String paramterName;
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        //메모리 관리에 필요하지 않은 것
        String localDate = parameters.getString(paramterName);
        if(!StringUtils.hasText(localDate)){
            throw new JobParametersInvalidException(paramterName + "is not compatible");
        }
        try{
        LocalDate.parse(localDate);
      }catch(DateTimeParseException e ) {
            throw new JobParametersInvalidException(paramterName + "format not compatible");
        }
    }
}
