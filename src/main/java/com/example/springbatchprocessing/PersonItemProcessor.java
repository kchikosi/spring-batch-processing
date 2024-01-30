package com.example.springbatchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * simple processor that converts the names to uppercase.
 */
public class PersonItemProcessor implements ItemProcessor<Person, Person> {
    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

    @Override
    public Person process(Person person) throws Exception {

        String firstName = person.firstName().toLowerCase();
        String lastName = person.lastName().toLowerCase();
        final Person updatedPerson = new Person(firstName, lastName);

        log.info("Converting (" + person + ") to (" + updatedPerson + ")");
        return updatedPerson;
    }
}
