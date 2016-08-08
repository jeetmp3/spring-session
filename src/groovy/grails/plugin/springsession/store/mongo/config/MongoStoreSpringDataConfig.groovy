package grails.plugin.springsession.store.mongo.config

import com.mongodb.*
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoConfiguration

/**
 * @author Jitendra Singh
 */
@Configuration
class MongoStoreSpringDataConfig extends AbstractMongoConfiguration {

    private MongoStoreConfigProperties mongoProperties

    public MongoStoreSpringDataConfig(ConfigObject configObject) {
        mongoProperties = new MongoStoreConfigProperties(configObject)
    }

    @Override
    protected String getDatabaseName() {
        return mongoProperties.database
    }

    @Override
    Mongo mongo() throws Exception {
        List<ServerAddress> addresses
        List<MongoCredential> credentials = null
        if (mongoProperties.replicaSets && mongoProperties.replicaSets.first().hostName) {
            addresses = mongoProperties.replicaSets.collect { new ServerAddress(it.hostName, it.port) }
        } else {
            addresses = [new ServerAddress(mongoProperties.hostName, mongoProperties.port)]
        }

        if(mongoProperties.username && mongoProperties.password) {
            credentials = [MongoCredential.createCredential(mongoProperties.username, getDatabaseName(), mongoProperties.password.toCharArray())]
        }
        return new MongoClient(addresses, credentials, MongoClientOptions.builder().build())
    }
}
