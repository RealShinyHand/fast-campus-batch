package com.fastcampus.hellospringbatch.job.player;

import com.fastcampus.hellospringbatch.core.dto.PlayerDto;
import com.fastcampus.hellospringbatch.core.dto.PlayerSalaryDto;
import com.fastcampus.hellospringbatch.core.service.PlayerSalaryService;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemProcessorAdapter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Configuration
@AllArgsConstructor
public class PlayerJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flatFileJob(
            @Qualifier("flatFileStep") Step step) {
        return jobBuilderFactory.get("flatFileJob")
                .start(step)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @JobScope
    @Bean("flatFileStep")
    public Step flatFileStep(
            @Qualifier("flatFileItemReader") FlatFileItemReader<PlayerDto> flatFileItemReader,
            PlayerSalaryService playerSalaryService,
            FlatFileItemWriter flatFileItemWriter,
            ItemProcessorAdapter itemProcessorAdapter
    ) {
        return stepBuilderFactory.get("flatFileStep")
                .<PlayerDto, PlayerSalaryDto>chunk(5)
                .reader(flatFileItemReader)
                .processor(itemProcessorAdapter)
                .writer(
                        flatFileItemWriter
                )
                .build();
    }

    @StepScope
    @Bean("flatFileItemWriter")
    public FlatFileItemWriter<PlayerSalaryDto> playerSalaryDtoFlatFileItemWriter() throws IOException {

        BeanWrapperFieldExtractor<PlayerSalaryDto> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"ID","firstName","lastName","salary"});
        //파일에 쓰는 필드 지정 및 순서 정하기
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator<PlayerSalaryDto> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter("\t");
        //필드값마다 탭으로 구분

        lineAggregator.setFieldExtractor(fieldExtractor);

        new File("player-salary-list.txt").createNewFile();
        //파일 존재여부 확인없이 무조건 만듬

        return new FlatFileItemWriterBuilder<PlayerSalaryDto>().name("flatFileItemWriter")
                .resource(new FileSystemResource("player-salary-list.txt"))
                .lineAggregator(lineAggregator)
                .build();
    }

    @StepScope
    @Bean("flatFileItemReader")
    public FlatFileItemReader<PlayerDto> flatFileItemReader() throws IOException {
        return new FlatFileItemReaderBuilder<PlayerDto>()
                .name("flatFileItemReader")
                .lineTokenizer(new DelimitedLineTokenizer())
                .linesToSkip(1)
                .fieldSetMapper(new PlayerFieldSetMapper())
                .resource(new FileSystemResource("player-list.txt"))
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessorAdapter<PlayerDto, PlayerSalaryDto> playerSalaryDtoItemProcessorAdapter(PlayerSalaryService playerSalaryService) {
        ItemProcessorAdapter<PlayerDto, PlayerSalaryDto> adapter = new ItemProcessorAdapter<>();
        adapter.setTargetObject(playerSalaryService);
        adapter.setTargetMethod("calcSalary");
        return adapter;
    }


}
