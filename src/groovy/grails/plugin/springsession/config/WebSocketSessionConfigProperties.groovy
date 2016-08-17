package grails.plugin.springsession.config

/**
 * @author Jitendra Singh.
 */
class WebSocketSessionConfigProperties {

    List<String> stompEndpoints
    List<String> appDestinationPrefix
    List<String> simpleBrokers

    public WebSocketSessionConfigProperties(ConfigObject config) {
        stompEndpoints = config.websocket.stompEndpoints ?: []
        appDestinationPrefix = config.websocket.appDestinationPrefix ?: []
        simpleBrokers = config.websocket.simpleBrokers ?: []
    }
}
