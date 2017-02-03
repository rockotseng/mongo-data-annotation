package com.rocko.test;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.repository.init.Jackson2ResourceReader;
import org.springframework.data.repository.init.ResourceReaderRepositoryPopulator;
import org.springframework.data.repository.support.Repositories;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.context.util.TestContextResourceUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MongoDataTestExecutionListener extends AbstractTestExecutionListener {

    final static Logger logger = LoggerFactory.getLogger(MongoDataTestExecutionListener.class);

    @Override
    public final int getOrder() {
        return 5000;
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {

        Set<MongoData> annotations = getAnnotations(testContext);

        for (MongoData mongoData : annotations) {
            populateRepository(mongoData, testContext);
        }
    }

    private Set<MongoData> getAnnotations(TestContext testContext) {
        Set<MongoData> annotations = AnnotatedElementUtils.getMergedRepeatableAnnotations(testContext.getTestMethod(),
                MongoData.class, MongoDataGroup.class);
        if (annotations.isEmpty()) {
            annotations = AnnotatedElementUtils.getMergedRepeatableAnnotations(testContext.getTestClass(), MongoData.class,
                    MongoDataGroup.class);
        }
        return annotations;
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        MongoTemplate mongoTemplate = TestContextBeanUtils.retrieveBean(testContext, MongoTemplate.class);
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            mongoTemplate.dropCollection(collectionName);
        }
    }

    private void populateRepository(MongoData mongoData, TestContext testContext) throws Exception {

        ObjectMapper objectMapper = TestContextBeanUtils.retrieveBean(testContext, mongoData.mapper(), ObjectMapper.class);
        Jackson2ResourceReader resourceReader = new Jackson2ResourceReader(objectMapper);
        final ResourceReaderRepositoryPopulator populator = new ResourceReaderRepositoryPopulator(resourceReader);

        List<Resource> resources = getResources(testContext, mongoData);
        populator.setResources(resources.toArray(new Resource[resources.size()]).clone());

        Repositories repositories = new Repositories(testContext.getApplicationContext());
        populator.populate(repositories);
    }

    private List<Resource> getResources(TestContext testContext, MongoData mongoData) {
        String[] locations =
                TestContextResourceUtils.convertToClasspathResourcePaths(testContext.getTestClass(), mongoData.locations());
        List<Resource> resources = TestContextResourceUtils.convertToResourceList(testContext.getApplicationContext(), locations);
        return resources;
    }

}
