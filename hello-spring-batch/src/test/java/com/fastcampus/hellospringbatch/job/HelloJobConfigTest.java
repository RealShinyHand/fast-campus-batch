package com.fastcampus.hellospringbatch.job;

import com.fastcampus.hellospringbatch.BatchTestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
//@Extends(SpringExtension.class) 강의에서는 하라고 하나 , 스프링 부트 테스트에 포함 되어있다. 버전 차이 때문인 듯
@ContextConfiguration(classes = {HelloJobConfig.class, BatchTestConfig.class})
public class HelloJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void success() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        Assertions.assertEquals(jobExecution.getExitStatus(), ExitStatus.COMPLETED);
    }

}
