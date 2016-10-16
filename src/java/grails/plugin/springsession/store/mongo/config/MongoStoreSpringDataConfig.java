package grails.plugin.springsession.store.mongo.config;

import com.mongodb.*;
import groovy.util.ConfigObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static grails.plugin.springsession.utils.Objects.isEmpty;

/**
 * @author Jitendra Singh
 */
@Configuration
class MongoStoreSpringDataConfig extends AbstractMongoConfiguration {

    private MongoStoreConfigProperties mongoProperties;

    public MongoStoreSpringDataConfig(ConfigObject configObject) {
        mongoProperties = new MongoStoreConfigProperties(configObject);
    }

    @Override
    protected String getDatabaseName() {
        return mongoProperties.getDatabase();
    }

    @Override
    public Mongo mongo() throws Exception {
        List<ServerAddress> addresses;
        List<MongoCredential> credentials = null;
        if (!isEmpty(mongoProperties.getReplicaSets())
                && !isEmpty(mongoProperties.getReplicaSets().get(0).getHostName())) {
            addresses = new ArrayList<ServerAddress>(mongoProperties.getReplicaSets().size());
            for(MongoStoreConfigProperties.ReplicaSet replicaSet : mongoProperties.getReplicaSets()) {
                addresses.add(new ServerAddress(replicaSet.getHostName(), replicaSet.getPort()));
            }
        } else {
            addresses = Collections.singletonList(
                    new ServerAddress(mongoProperties.getHostName(), mongoProperties.getPort())
            );
        }

        if(!isEmpty(mongoProperties.getUsername()) && !isEmpty(mongoProperties.getPassword())) {
            credentials = Collections.singletonList(
                    MongoCredential.createCredential(
                            mongoProperties.getUsername(),
                            getDatabaseName(),
                            mongoProperties.getPassword().toCharArray()
                    ));
        }
        return new MongoClient(addresses, credentials, MongoClientOptions.builder().build());
    }
}
