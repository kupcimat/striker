package org.saigon.striker.config

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.IndexResolver
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.util.TypeInformation
import org.springframework.stereotype.Component

@Component
class MongoIndexCreator(val mongoTemplate: MongoTemplate, val mongoMappingContext: MongoMappingContext) {

    @EventListener(ApplicationReadyEvent::class)
    fun createIndices() {
        mongoMappingContext.managedTypes.forEach(::createIndexForEntity)
    }

    private fun createIndexForEntity(entityType: TypeInformation<*>) {
        val indexOps = mongoTemplate.indexOps(entityType.type)
        val resolver = IndexResolver.create(mongoMappingContext)

        resolver.resolveIndexFor(entityType).forEach { indexOps.ensureIndex(it) }
    }
}
