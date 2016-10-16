package grails.plugin.springsession.store.hazelcast.config;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import grails.plugin.springsession.config.SpringSessionConfigProperties;
import groovy.util.ConfigObject;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.hazelcast.config.annotation.web.http.HazelcastHttpSessionConfiguration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import static grails.plugin.springsession.utils.ApplicationUtils.notEmpty;
import static grails.plugin.springsession.utils.ApplicationUtils.toByteArray;

/**
 * @author Jitendra Singh.
 */
@Configuration
public class HazelcastStoreSessionConfig extends HazelcastHttpSessionConfiguration {

    private final GrailsApplication grailsApplication;
    private final SpringSessionConfigProperties configProperties;
    private final HazelcastConfigProperties hazelcastProperties;

    public HazelcastStoreSessionConfig(GrailsApplication grailsApplication, ConfigObject config) {
        this.grailsApplication = grailsApplication;
        this.configProperties = SpringSessionConfigProperties.getInstance(config);
        this.hazelcastProperties = new HazelcastConfigProperties(config);
        this.setMaxInactiveIntervalInSeconds(configProperties.getMaxInactiveInterval());
    }

    @Bean
    public HazelcastInstance hazelcastInstance() {
        return Hazelcast.newHazelcastInstance(hazelcastConfig());
    }

    @Bean
    public Config hazelcastConfig() {
        Config config = new Config();
        if(notEmpty(hazelcastProperties.getUrlHost()) && notEmpty(hazelcastProperties.getUrlProtocol())) {
            try {
                config.setConfigurationUrl(
                        new URL(hazelcastProperties.getUrlProtocol(), hazelcastProperties.getUrlHost(), hazelcastProperties.getUrlPort(), hazelcastProperties.getUrlFile())
                );
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        if (notEmpty(hazelcastProperties.getConfigurationFile()))
            config.setConfigurationFile(new File(hazelcastProperties.getConfigurationFile()));
        Properties properties = new Properties();
        properties.putAll(hazelcastProperties.getProperties());
        config.setProperties(properties);
        config.setInstanceName(hazelcastProperties.getInstanceName());
        config.setGroupConfig(new GroupConfig(hazelcastProperties.getGroupName(), hazelcastProperties.getGroupPassword()));
        config.setNetworkConfig(buildNetworkConfig());
        return new Config();
    }

    private NetworkConfig buildNetworkConfig() {
        NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.setPort(hazelcastProperties.getNetworkPort())
                .setPortAutoIncrement(hazelcastProperties.getPortAutoIncrement())
                .setReuseAddress(hazelcastProperties.getReuseAddress())
                .setPublicAddress(hazelcastProperties.getPublicAddress())
                .setOutboundPortDefinitions(hazelcastProperties.getOutboundPortDefinitions())
                .setOutboundPorts(hazelcastProperties.getOutboundPorts())
                .setInterfaces(
                        new InterfacesConfig().setEnabled(hazelcastProperties.getInterfaceEnabled())
                                .setInterfaces(hazelcastProperties.getInterfaceSet())
                )
                .setJoin(
                        new JoinConfig().setMulticastConfig(
                                new MulticastConfig().setMulticastGroup(hazelcastProperties.getJoinMulticastGroup())
                                        .setMulticastPort(hazelcastProperties.getJoinMulticastPort())
                                        .setMulticastTimeoutSeconds(hazelcastProperties.getJoinMulticastTimeoutSeconds())
                                        .setMulticastTimeToLive(hazelcastProperties.getJoinMulticastTimeToLive())
                                        .setLoopbackModeEnabled(hazelcastProperties.getJoinMulticastLoopbackModeEnabled())
                                        .setTrustedInterfaces(hazelcastProperties.getJoinMulticastTrustedInterfaces())
                                        .setEnabled(hazelcastProperties.getJoinMulticastEnabled())
                        ).setAwsConfig(
                                new AwsConfig().setEnabled(hazelcastProperties.getJoinAwsEnabled())
                                        .setAccessKey(hazelcastProperties.getJoinAwsAccessKey())
                                        .setSecretKey(hazelcastProperties.getJoinAwsSecretKey())
                                        .setConnectionTimeoutSeconds(hazelcastProperties.getJoinAwsConnectionTimeoutSeconds())
                                        .setHostHeader(hazelcastProperties.getJoinAwsHostHeader())
                                        .setRegion(hazelcastProperties.getJoinAwsRegion())
                                        .setSecurityGroupName(hazelcastProperties.getJoinAwsSecurityGroupName())
                                        .setTagKey(hazelcastProperties.getJoinAwsTagKey())
                                        .setTagValue(hazelcastProperties.getJoinAwsTagValue())
                        ).setTcpIpConfig(
                                new TcpIpConfig().setEnabled(hazelcastProperties.getJoinTcpIpEnabled())
                                        .setConnectionTimeoutSeconds(hazelcastProperties.getJoinTcpIpConnectionTimeoutSeconds())
                                        .setMembers(hazelcastProperties.getJoinTcpIpMembers())
                                        .setRequiredMember(hazelcastProperties.getJoinTcpIpRequiredMember())
                        )
                )
                .setSSLConfig(
                        new SSLConfig().setEnabled(hazelcastProperties.getSslEnabled())
                                .setFactoryClassName(hazelcastProperties.getSslFactoryClassName())
                                .setFactoryImplementation(hazelcastProperties.getSslFactoryImplementation())
                                .setProperties(buildProperties(hazelcastProperties.getSslProperties()))
                )
                .setSocketInterceptorConfig(
                        new SocketInterceptorConfig().setEnabled(hazelcastProperties.getSocketInterceptorEnabled())
                                .setClassName(hazelcastProperties.getSocketInterceptorClassName())
                                .setImplementation(hazelcastProperties.getSocketInterceptorImplementation())
                                .setProperties(buildProperties(hazelcastProperties.getSocketInterceptorProperties()))
                )
                .setSymmetricEncryptionConfig(
                        new SymmetricEncryptionConfig().setEnabled(hazelcastProperties.getSymmetricEncryptionEnabled())
                                .setAlgorithm(hazelcastProperties.getSymmetricEncryptionAlgorithm())
                                .setIterationCount(hazelcastProperties.getSymmetricEncryptionIterationCount())
                                .setSalt(hazelcastProperties.getSymmetricEncryptionSalt())
                                .setPassword(hazelcastProperties.getSymmetricEncryptionPassword())
                                .setKey(toByteArray(hazelcastProperties.getSymmetricEncryptionKey()))
                )
                .setPortCount(hazelcastProperties.getNetworkPortCount());
        return networkConfig;
    }

    private Properties buildProperties(Map data) {
        Properties properties = new Properties();
        if (data != null && data.size() > 0)
            properties.putAll(data);
        return properties;
    }
}
