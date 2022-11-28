package com.fastcampus.hellospringbatch.job;

import com.fastcampus.hellospringbatch.BatchTestConfig;
import com.fastcampus.hellospringbatch.core.domain.PlainText;
import com.fastcampus.hellospringbatch.core.repository.PlainTextRepository;
import com.fastcampus.hellospringbatch.core.repository.ResultTextRepository;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.stream.IntStream;

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
//@Extends(SpringExtension.class) 강의에서는 하라고 하나 , 스프링 부트 테스트에 포함 되어있다. 버전 차이 때문인 듯
@ContextConfiguration(classes = {PlainTextJobConfig.class, BatchTestConfig.class})
public class PlainTextConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private PlainTextRepository plainTextRepository;
    @Autowired
    private ResultTextRepository resultTextRepository;

    @AfterEach
    public void tearDown(){
        plainTextRepository.deleteAll();
        resultTextRepository.deleteAll();
    }

    @Test
    @DisplayName("PlaintText - Empty 일떄")
    public void givenNotingWhenScheduleThenSuccess() throws Exception {

        //given
        //no
        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        //then
        Assertions.assertEquals(jobExecution.getExitStatus(), ExitStatus.COMPLETED);
        Assertions.assertEquals(resultTextRepository.count(),0);
    }


    @Test
    @DisplayName("PlaintText - 정상 조건")
    public void givenPlainTextWhenScheduleThenSuccess() throws Exception {

        //given
        givenPlainText(10);
        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        //then
        Assertions.assertEquals(jobExecution.getExitStatus(), ExitStatus.COMPLETED);
        Assertions.assertEquals(resultTextRepository.count(),10);
    }

    private void givenPlainText(int count){
        IntStream.range(1,count+1)
                .forEach(
                        (item)->plainTextRepository.save(new PlainText(null,"text"+item))
                );
    }


}
