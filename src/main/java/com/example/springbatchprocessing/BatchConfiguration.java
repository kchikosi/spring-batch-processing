package com.example.springbatchprocessing;

import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
public class BatchConfiguration {
    /**
     * reader looks for a file called sample-data.csv and parses each line item with enough information to turn it into a Person
     * @return
     */
    @Bean
    public FlatFileItemReader<Person> reader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names("firstName","lastName")
                .targetType(Person.class)
                .build();
    }

    /**
     *  creates an instance of the PersonItemProcessor to convert the data to upper case.
     * @return
     */
    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

    /**
     * takes in a copy of the dataSource, executes the SQL statement needed to insert a single Person, for each record.
     * @param datasource
     * @return
     */
    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource datasource){
        return new  JdbcBatchItemWriterBuilder<Person>()
                .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(datasource)
                .beanMapped()
                .build();
    }
}
