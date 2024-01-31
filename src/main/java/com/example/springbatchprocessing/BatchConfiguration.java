package com.example.springbatchprocessing;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfiguration {
    /**
     * reader looks for a file called sample-data.csv and parses each line item with enough information to turn it into a Person
     *
     * @return
     */
    @Bean
    public FlatFileItemReader<Person> reader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names("firstName", "lastName")
                .targetType(Person.class)
                .build();
    }

    /**
     * creates an instance of the PersonItemProcessor to convert the data to upper case.
     *
     * @return
     */
    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

    /**
     * takes in a copy of the dataSource, executes the SQL statement needed to insert a single Person, for each record.
     *
     * @param datasource
     * @return
     */
    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource datasource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(datasource)
                .beanMapped()
                .build();
    }

    /**
     * define the job
     * this job has only one step
     *
     * @param jobRepository
     * @param step
     * @param listener
     * @return
     */
    @Bean
    public Job importPersonJob(JobRepository jobRepository, Step step, JobCompletionNotificationListener listener) {
        return new JobBuilder("importPersonJob", jobRepository)
                .listener(listener)
                .start(step)
                .build();
    }

    /**
     * define the steps
     * in the step definition, you can define how much data to write at a time.
     * in this case, it writes up to three records at a time.
     *
     * @param jobRepository
     * @param transactionManager
     * @param reader
     * @param processor
     * @param writer
     * @return
     */
    @Bean
    public Step step(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                     FlatFileItemReader<Person> reader, PersonItemProcessor processor, JdbcBatchItemWriter<Person> writer) {
        return new StepBuilder("step", jobRepository)
                .<Person, Person>chunk(3, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
