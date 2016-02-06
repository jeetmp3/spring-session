package org.grails.plugins.springsession.data.redis.config;

import org.springframework.data.redis.connection.NamedNode;

/**
 * @author jitendra on 2/10/15.
 */
public class MasterNamedNode implements NamedNode {
    String name;

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name;
    }
}
