package com.fastcampus.hellospringbatch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HelloJobConfig {

    private final JobBuilderFactory builderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean("helloJob")
    public Job helloJob(Step step){
        return builderFactory.get("helloJob")
                .incrementer(new RunIdIncrementer())
                //자동으로 실행횟수 증가시켜줌
                .start(step)
                .build();
    }

    @Bean("helloStep")
    @JobScope //job이 실행되는 동안에만 살아 있음
    public Step helloStep(Tasklet tasklet){
        return stepBuilderFactory.get("helloStep")
                .tasklet(tasklet)
                .build();
    }

    @Bean("helloTasklet")
    @StepScope
    public Tasklet tasklet(){
        return (contribution,chunkContext)->{
            System.out.println("Hello Spring Batch");
            return RepeatStatus.FINISHED;
        };
    }
}
