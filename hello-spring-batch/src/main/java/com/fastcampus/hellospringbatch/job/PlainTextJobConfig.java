package com.fastcampus.hellospringbatch.job;

import com.fastcampus.hellospringbatch.core.domain.PlainText;
import com.fastcampus.hellospringbatch.core.domain.ResultText;
import com.fastcampus.hellospringbatch.core.repository.PlainTextRepository;
import com.fastcampus.hellospringbatch.core.repository.ResultTextRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class PlainTextJobConfig {

    private final JobBuilderFactory builderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final PlainTextRepository plainTextRepository;
    private final ResultTextRepository resultTextRepository;

    @Bean("plainTextJob")
    public Job plainTextJob(@Qualifier("plainTextStep") Step step,JobExecutionListener jobExecutionListener) {
        return builderFactory.get("plainTextJob")
                .incrementer(new RunIdIncrementer())
                //자동으로 실행횟수 증가시켜줌
                .listener(jobExecutionListener)
                .start(step)
                .build();
    }

    @Bean("plainTextJobListener")
    @JobScope
    public JobExecutionListener jobExecutionListener(){
        return new JobExecutionListener() {
            public void beforeJob(JobExecution jobExecution) {
                log.info(String.format("%s start : %s : %s",
                        jobExecution.getJobInstance().getJobName(),
                        jobExecution.getStatus(),
                        jobExecution.getExitStatus()));
            }
            public void afterJob(JobExecution jobExecution) {
                log.info(String.format("%s start : %s",jobExecution.getJobInstance().getJobName(),jobExecution.getExitStatus()));
            }
        };
    }

    @Bean("plainTextStep")
    @JobScope //job이 실행되는 동안에만 살아 있음
    public Step helloStep(
           @Qualifier("plainTextReader") ItemReader plainTextItemReader,
          @Qualifier("plainTextProcessor")  ItemProcessor plainTextProcessor,
          @Qualifier("plaintTextWriter")  ItemWriter plaintTextWriter) {
        return stepBuilderFactory.get("plainTextStep")
                .<PlainText, String>chunk(5)
                .reader(plainTextItemReader)
                .processor(plainTextProcessor)
                .writer(plaintTextWriter)
                .build();
    }

    @Bean("plainTextReader")
    @StepScope
    public RepositoryItemReader<PlainText> plainTextReader() {
        return new RepositoryItemReaderBuilder<PlainText>()
                .name("plainTextReader")
                .repository(plainTextRepository)
                .methodName("findBy")
                .pageSize(5)
                .arguments(List.of())
                .sorts(Collections.singletonMap("id", Sort.Direction.DESC))
                .build();
    }

    @StepScope
    @Bean("plainTextProcessor")
    public ItemProcessor<PlainText, String> plainTextProcessor() {
        return (item) -> {
            return "processed" + item.getText();
        };
    }

    @StepScope
    @Bean("plaintTextWriter")
    public ItemWriter<String> plaintTextWriter() {
        return items -> {
            items.forEach(
                    (item) -> {
                        resultTextRepository.save(
                                new ResultText(null, item)
                        );
                    }
            );
            System.out.println("=== chunk is finished");
        };
    }
}
