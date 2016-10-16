package grails.plugin.springsession.store.hazelcast.config

/**
 * @author Jitendra Singh
 */
class HazelcastConfigProperties {
    String urlProtocol
    String urlHost
    String urlFile
    int urlPort
    String configurationFile
    Map properties
    String instanceName
    String groupName
    String groupPassword
    int networkPort
    int networkPortCount
    boolean portAutoIncrement
    boolean reuseAddress
    String publicAddress
    List<String> outboundPortDefinitions
    List<Integer> outboundPorts
    boolean interfaceEnabled
    Set<String> interfaceSet

    boolean joinMulticastEnabled
    String joinMulticastGroup
    int joinMulticastPort
    int joinMulticastTimeoutSeconds
    int joinMulticastTimeToLive
    Set<String> joinMulticastTrustedInterfaces
    boolean joinMulticastLoopbackModeEnabled

    int joinTcpIpConnectionTimeoutSeconds
    boolean joinTcpIpEnabled
    List<String> joinTcpIpMembers
    String joinTcpIpRequiredMember

    boolean joinAwsEnabled
    String joinAwsAccessKey
    String joinAwsSecretKey
    String joinAwsRegion
    String joinAwsSecurityGroupName
    String joinAwsTagKey
    String joinAwsTagValue
    String joinAwsHostHeader
    int joinAwsConnectionTimeoutSeconds

    boolean symmetricEncryptionEnabled
    String symmetricEncryptionSalt
    String symmetricEncryptionPassword
    int symmetricEncryptionIterationCount
    String symmetricEncryptionAlgorithm
    String symmetricEncryptionKey

    boolean socketInterceptorEnabled
    String socketInterceptorClassName
    Object socketInterceptorImplementation
    Map socketInterceptorProperties

    boolean sslEnabled
    String sslFactoryClassName
    Object sslFactoryImplementation
    Map sslProperties

    public HazelcastConfigProperties(ConfigObject config) {
        urlProtocol = config.configurationUrl.protocol ?: "http"
        urlHost = config.configurationUrl.host ?: "localhost"
        urlPort = config.configurationUrl.port ?: 5701
        urlFile = config.configurationUrl.file ?: ""
        configurationFile = config.configurationFile ?: ""
        properties = config.properties
        instanceName = config.instanceName ?: ""

        groupName = config.group.name ?: ""
        groupPassword = config.group.password ?: ""

        networkPort = config.network.port ?: 5701
        networkPortCount = config.network.portCount ?: 100
        portAutoIncrement = config.network.portAutoIncrement ?: true
        reuseAddress = config.network.reuseAddress
        publicAddress = config.network.publicAddress ?: ""
        outboundPortDefinitions = config.network.outboundPortDefinitions
        outboundPorts = config.network.outboundPorts

        interfaceEnabled = config.network.interfaces.enabled
        interfaceSet = config.network.interfaces.interfaceSet

        joinMulticastEnabled = config.network.join.multicast.enabled ?: false
        joinMulticastGroup = config.network.join.multicast.multicastGroup ?: "224.2.2.3"
        joinMulticastPort = config.network.join.multicast.multicastPort ?: 54327
        joinMulticastTimeoutSeconds = config.network.join.multicast.multicastTimeoutSeconds ?: 2
        joinMulticastTimeToLive = config.network.join.multicast.multicastTimeToLive ?: 32
        joinMulticastTrustedInterfaces = config.network.join.multicast.trustedInterfaces ?: []
        joinMulticastLoopbackModeEnabled = config.network.join.multicast.loopbackModeEnabled ?: false

        joinTcpIpEnabled = config.network.join.tcpIp.enabled ?: false
        joinTcpIpMembers = config.network.join.tcpIp.members ?: []
        joinTcpIpRequiredMember = config.network.join.tcpIp.members ?: ""

        joinAwsEnabled = config.network.join.aws.enabled ?: false
        joinAwsAccessKey = config.network.join.aws.accessKey ?: ""
        joinAwsSecretKey = config.network.join.aws.secretKey ?: ""
        joinAwsRegion = config.network.join.aws.region ?: "us-east-1"
        joinAwsSecurityGroupName = config.network.join.aws.securityGroupName ?: ""
        joinAwsTagKey = config.network.join.aws.tagKey ?: ""
        joinAwsTagValue = config.network.join.aws.tagValue ?: ""
        joinAwsHostHeader = config.network.join.aws.hostHeader ?: "ec2.amazonaws.com"
        joinAwsConnectionTimeoutSeconds = config.network.join.aws.connectionTimeoutSeconds ?: 5

        symmetricEncryptionEnabled = config.network.symmetricEncryption.enabled ?: false
        symmetricEncryptionSalt = config.network.symmetricEncryption.salt ?: ""
        symmetricEncryptionPassword = config.network.symmetricEncryption.password ?: ""
        symmetricEncryptionIterationCount = config.network.symmetricEncryption.iterationCount ?: 19
        symmetricEncryptionAlgorithm = config.network.symmetricEncryption.algorithm ?: ""
        symmetricEncryptionKey = config.network.symmetricEncryption.key ?: ""

        socketInterceptorEnabled = config.network.socketInterceptor.enabled ?: false
        socketInterceptorClassName = config.network.socketInterceptor.className ?: null
        socketInterceptorImplementation = config.network.socketInterceptor.implementation ?: null
        socketInterceptorProperties = config.network.socketInterceptor.properties ?: null

        sslEnabled = config.network.ssl.enabled ?: false
        sslFactoryClassName = config.network.ssl.factoryClassName ?: null
        sslFactoryImplementation = config.network.ssl.factoryImplementation ?: null
        sslProperties = config.network.ssl.properties ?: null
    }
}
