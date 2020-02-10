package org.saigon.striker.config

import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.index.IndexResolver
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.util.TypeInformation
import org.springframework.stereotype.Component

@Component
class MongoIndexCreator(val mongoTemplate: ReactiveMongoTemplate, val mongoMappingContext: MongoMappingContext) {

    @EventListener(ApplicationReadyEvent::class)
    fun createIndices() {
        mongoMappingContext.managedTypes.forEach(::createIndexForEntity)
    }

    private fun createIndexForEntity(entityType: TypeInformation<*>) {
        val indexOps = mongoTemplate.indexOps(entityType.type)
        val resolver = IndexResolver.create(mongoMappingContext)

        runBlocking {
            resolver.resolveIndexFor(entityType).forEach { indexOps.ensureIndex(it).awaitSingle() }
        }
    }
}
