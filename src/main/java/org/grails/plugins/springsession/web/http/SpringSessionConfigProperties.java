package org.grails.plugins.springsession.web.http;

import grails.util.Holders;
import groovy.util.ConfigObject;

class SpringSessionConfigProperties{
    int maxInactiveInterval;
    String mapName;
    protected Boolean allowPersistMutable;
    public void setValues(int maxInactiveInterval, String mapName, Boolean allowPersistMutable) {
        this.maxInactiveInterval = maxInactiveInterval;
        this.mapName = mapName;
        this.allowPersistMutable = allowPersistMutable;
    }

}