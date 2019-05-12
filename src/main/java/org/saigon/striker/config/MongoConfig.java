package org.saigon.striker.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.util.TypeInformation;

@Configuration
public class MongoConfig {

    private final MongoTemplate mongoTemplate;
    private final MongoMappingContext mongoMappingContext;

    public MongoConfig(MongoTemplate mongoTemplate, MongoMappingContext mongoMappingContext) {
        this.mongoTemplate = mongoTemplate;
        this.mongoMappingContext = mongoMappingContext;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createIndices() {
        mongoMappingContext.getManagedTypes().forEach(this::createIndexForEntity);
    }

    private void createIndexForEntity(TypeInformation<?> entityType) {
        final IndexOperations indexOps = mongoTemplate.indexOps(entityType.getType());
        final IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);

        resolver.resolveIndexFor(entityType).forEach(indexOps::ensureIndex);
    }
}
