package com.fastcampus.hellospringbatch.job;

import com.fastcampus.hellospringbatch.core.domain.PlainText;
import com.fastcampus.hellospringbatch.core.repository.PlainTextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
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
public class PlainTextJobConfig {

    private final JobBuilderFactory builderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private  final PlainTextRepository plainTextRepository;

    @Bean("plainTextJob")
    public Job plainTextJob(@Qualifier("plainTextStep") Step step){
        return builderFactory.get("plainTextJob")
                .incrementer(new RunIdIncrementer())
                //자동으로 실행횟수 증가시켜줌
                .start(step)
                .build();
}

    @Bean("plainTextStep")
    @JobScope //job이 실행되는 동안에만 살아 있음
    public Step helloStep(
            ItemReader plainTextItemReader,
            ItemProcessor plainTextProcessor,
            ItemWriter plaintTextWriter){
        return stepBuilderFactory.get("plainTextStep")
                .<PlainText,String>chunk(5)
                .reader(plainTextItemReader)
                .processor(plainTextProcessor)
                .writer(plaintTextWriter)
                .build();
    }

    @Bean("helloReader")
    @StepScope
    public RepositoryItemReader<PlainText> plainTextReader(){
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
    public ItemProcessor<PlainText,String> plainTextProcessor(){
        return (item)->{
            return "processed" + item.getText();
        };
    }

    @StepScope
    @Bean("plaintTextWriter")
    public ItemWriter<String> plaintTextWriter(){
        return items->{
            items.forEach(System.out::println);
            System.out.println("=== chunk is finished");
        };
    }
}
