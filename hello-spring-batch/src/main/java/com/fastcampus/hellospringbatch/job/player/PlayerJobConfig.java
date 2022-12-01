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
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.List;

@Configuration
@AllArgsConstructor
public class PlayerJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flatFileJob(
            @Qualifier("flatFileStep") Step step){
        return jobBuilderFactory.get("flatFileJob")
                .start(step)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @JobScope
    @Bean("flatFileStep")
    public Step flatFileStep(
          @Qualifier("flatFileItemReader")  FlatFileItemReader<PlayerDto> flatFileItemReader,
          PlayerSalaryService playerSalaryService,
          ItemProcessorAdapter itemProcessorAdapter
    ){
        return stepBuilderFactory.get("flatFileStep")
                .<PlayerDto, PlayerSalaryDto>chunk(5)
                .reader(flatFileItemReader)

               .processor(itemProcessorAdapter)
                .writer(
                        new ItemWriter<PlayerSalaryDto>() {
                            @Override
                            public void write(List<? extends PlayerSalaryDto> items) throws Exception {
                                items.forEach(System.out::println);
                            }
                        }
                )
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessorAdapter<PlayerDto,PlayerSalaryDto> playerSalaryDtoItemProcessorAdapter(PlayerSalaryService playerSalaryService){
        ItemProcessorAdapter<PlayerDto,PlayerSalaryDto> adapter = new ItemProcessorAdapter<>();
        adapter.setTargetObject(playerSalaryService);
        adapter.setTargetMethod("calcSalary");
        return adapter;
    }


    @StepScope
    @Bean("flatFileItemReader")
    public FlatFileItemReader<PlayerDto> flatFileItemReader() throws IOException {
       System.out.println("resourceLoader"+resourceLoader.getResource("player-list.txt").getFilename());
        System.out.println("classPath"+new ClassPathResource("player-list.txt").getFilename());
        return new FlatFileItemReaderBuilder<PlayerDto>()
                .name("flatFileItemReader")
                .lineTokenizer(new DelimitedLineTokenizer())
                .linesToSkip(1)
                .fieldSetMapper(new PlayerFieldSetMapper())
                .resource(new FileSystemResource("player-list.txt"))
                .build();
    }


    @Autowired
    ResourceLoader resourceLoader;
}
