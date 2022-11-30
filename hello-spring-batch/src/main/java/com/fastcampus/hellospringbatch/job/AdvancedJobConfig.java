package com.fastcampus.hellospringbatch.job;

import com.fastcampus.hellospringbatch.job.validator.LocalDateParameterValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
@Slf4j
public class AdvancedJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean("advancedJob")
    public Job advancedJob(Step advancedStep){
        return jobBuilderFactory.get("advancedJob")
                .incrementer(new RunIdIncrementer())
                .validator(new LocalDateParameterValidator("targetDate"))
                .start(advancedStep).build();
    }

    @JobScope
    @Bean("advancedStep")
    public Step advancedStep(
            @Qualifier("advancedReader") ItemReader advancedReader,
            @Qualifier("advancedProcessor") ItemProcessor advancedProcessor,
            @Qualifier("advancedWriter") ItemWriter advancedWriter
    ){
        return stepBuilderFactory.get("advancedStep")
                .<String,String>chunk(5)
                .reader(advancedReader)
                .processor(advancedProcessor)
                .writer(advancedWriter)
                .build();
    }

    @StepScope
    @Bean("advancedReader")
    public ItemReader<String> advancedReader(){
        return new ItemStreamReader<String>() {
            private  int countParam = 1;
            @Override
            public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                if(countParam > 1){
                    return null;
                }
                countParam++;
                return "just test";
            }

            @Override
            public void open(ExecutionContext executionContext) throws ItemStreamException {

            }

            @Override
            public void update(ExecutionContext executionContext) throws ItemStreamException {

            }

            @Override
            public void close() throws ItemStreamException {

            }
        };
    }

    @StepScope
    @Bean("advancedProcessor")
    public ItemProcessor<String,String> advancedProcessor(){
        return (String in)->{
            return in.concat(in);
        };
    }


    @StepScope
    @Bean("advancedWriter")
    public ItemWriter<String> advancedWriter(
            @Value("#{jobParameters[\"targetDate\"]}")
            String dateParam
    ){

        return (in)->{
            in.forEach((i)->log.warn(dateParam + "###" + i));
        };
    }
}
