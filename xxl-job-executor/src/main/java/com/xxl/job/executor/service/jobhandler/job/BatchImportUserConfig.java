package com.xxl.job.executor.service.jobhandler.job;

import com.xxl.job.core.context.XxlJobHelper;
import groovy.util.logging.Slf4j;
import lombok.*;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @author yuxiang
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class BatchImportUserConfig {

    public final JobBuilderFactory jobBuilderFactory;
    public final StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<Person> reader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names(new String[]{"firstName", "lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                    setTargetType(Person.class);
                }})
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public ItemProcessor<Person, Person> itemProcessor() {
        return person -> {
            final String firstName = person.getFirstName().toUpperCase();
            final String lastName = person.getLastName().toUpperCase();
            final Person transformedPerson = new Person(firstName, lastName);
            XxlJobHelper.log("Converting ({}) into ({})", person, transformedPerson);
            return transformedPerson;
        };
    }

    @Bean
    public JobExecutionListenerSupport jobExecutionListenerSupport(JdbcTemplate jdbcTemplate) {
        return new JobExecutionListenerSupport() {
            @Override
            public void afterJob(JobExecution jobExecution) {
                if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                    XxlJobHelper.log("!!! JOB FINISHED! Time to verify the results");
                    jdbcTemplate.query("SELECT first_name, last_name FROM people",
                            (rs, row) -> new Person(
                                    rs.getString(1),
                                    rs.getString(2))
                    ).forEach(person -> XxlJobHelper.log("Found <" + person + "> in the database."));
                }
            }
        };
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<Person> writer, ItemProcessor<Person, Person> itemProcessor, FlatFileItemReader<Person> reader) {
        return stepBuilderFactory.get("step1")
                .<Person, Person>chunk(10)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job importUserJob(JobExecutionListenerSupport jobExecutionListenerSupport, Step step1) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobExecutionListenerSupport)
                .flow(step1)
                .end()
                .build();
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Person {
        private String lastName;
        private String firstName;
    }
}