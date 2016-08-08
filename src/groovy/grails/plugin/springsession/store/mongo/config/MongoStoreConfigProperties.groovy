package grails.plugin.springsession.store.mongo.config

import org.springframework.session.data.mongo.MongoOperationsSessionRepository

/**
 * @author Jitendra Singh
 */
class MongoStoreConfigProperties {

    String hostName
    int port
    String username
    String password
    String database
    String collectionName
    List<ReplicaSet> replicaSets
    List<String> jacksonModules

    public MongoStoreConfigProperties(ConfigObject conf) {
        init(conf)
    }

    private void init(ConfigObject config) {
        hostName = config.mongo.hostName ?: "localhost"
        port = config.mongo.port ?: 27017
        database = config.mongo.database ?: "test"
        username = config.mongo.username ?: ""
        password = config.mongo.password ?: ""
        collectionName = config.mongo.collectionName ?: MongoOperationsSessionRepository.DEFAULT_COLLECTION_NAME
        if(config.mongo.jackson.modules && config.mongo.jackson.modules instanceof List)
            jacksonModules = config.mongo.jackson.modules
        if (config.mongo.replicaSet instanceof List)
            replicaSets = config.mongo.replicaSet as List<ReplicaSet>
    }

    public class ReplicaSet {
        String hostName
        int port
    }
}
