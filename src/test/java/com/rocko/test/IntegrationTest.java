package com.rocko.test;

import static org.junit.Assert.assertEquals;

import java.net.UnknownHostException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TestExecutionListeners(listeners = MongoDataTestExecutionListener.class, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
public class IntegrationTest extends AbstractEmbeddedMongoTest {
    
    @Autowired
    FooRepository fooRepository;

    @Test
    @MongoData("/test-data/foo.json")
    public void test() {
        assertEquals(1, fooRepository.count());
    }

    @Configuration
    @EnableMongoRepositories(considerNestedRepositories = true)
    static class FooConfig extends AbstractMongoConfiguration {

        @Override
        public MongoClient mongo() throws UnknownHostException {
            return getMongoClient();
        }

        @Override
        protected String getDatabaseName() {
            return "test";
        }

        @Bean
        public ObjectMapper objectMapper() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper;
        }
    }

    public interface FooRepository extends PagingAndSortingRepository<Foo, String> {

    }

    @Document
    public static class Foo {
        @Id
        private String id;
        private String bar;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBar() {
            return bar;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }

    }

}
