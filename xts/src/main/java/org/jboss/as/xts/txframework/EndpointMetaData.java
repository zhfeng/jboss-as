package org.jboss.as.xts.txframework;

import org.jboss.as.server.deployment.DeploymentUnit;

/**
 * @author paul.robinson@redhat.com, 2012-02-06
 */
public class EndpointMetaData {

    private WSATAnnotation wsatAnnotation;
    private WSBAAnnotation wsbaAnnotation;
    private WebServiceAnnotation webServiceAnnotation;

    private boolean isTXFrameworkEnabled;

    private EndpointMetaData(WSATAnnotation wsatAnnotation, WSBAAnnotation wsbaAnnotation, WebServiceAnnotation webServiceAnnotation, boolean isTXFrameworkEnabled) {
        this.wsatAnnotation = wsatAnnotation;
        this.wsbaAnnotation = wsbaAnnotation;
        this.webServiceAnnotation = webServiceAnnotation;
        this.isTXFrameworkEnabled = isTXFrameworkEnabled;
    }

    public static EndpointMetaData build(DeploymentUnit unit, String endpoint) throws TXFrameworkException {
        final WSATAnnotation wsatAnnotation = WSATAnnotation.build(unit, endpoint);
        final WSBAAnnotation wsbaAnnotation = WSBAAnnotation.build(unit, endpoint);
        final WebServiceAnnotation webServiceAnnotation = WebServiceAnnotation.build(unit, endpoint);
        final boolean isTXFrameworkEnabled = (wsatAnnotation != null || wsbaAnnotation != null);

        return new EndpointMetaData(wsatAnnotation, wsbaAnnotation, webServiceAnnotation, isTXFrameworkEnabled);
    }

    public WSATAnnotation getWsatAnnotation() {
        return wsatAnnotation;
    }

    public WSBAAnnotation getWsbaAnnotation() {
        return wsbaAnnotation;
    }

    public WebServiceAnnotation getWebServiceAnnotation() {
        return webServiceAnnotation;
    }

    public boolean isTXFrameworkEnabled() {
        return isTXFrameworkEnabled;
    }
}
