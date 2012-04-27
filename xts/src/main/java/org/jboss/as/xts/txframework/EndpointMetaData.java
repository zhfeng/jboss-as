package org.jboss.as.xts.txframework;

import org.jboss.as.server.deployment.DeploymentUnit;

/**
 * @author paul.robinson@redhat.com, 2012-02-06
 */
public class EndpointMetaData {

    private RESTATAnnotation restatAnnotation;
    private ATAnnotation atAnnotation;
    private BAAnnotation baAnnotation;
    private WebServiceAnnotation webServiceAnnotation;

    private boolean isTXFrameworkEnabled;

    private EndpointMetaData(RESTATAnnotation restatAnnotation, ATAnnotation atAnnotation, BAAnnotation baAnnotation, WebServiceAnnotation webServiceAnnotation, boolean isTXFrameworkEnabled) {
        this.restatAnnotation = restatAnnotation;
        this.atAnnotation = atAnnotation;
        this.baAnnotation = baAnnotation;
        this.webServiceAnnotation = webServiceAnnotation;
        this.isTXFrameworkEnabled = isTXFrameworkEnabled;
    }

    public static EndpointMetaData build(DeploymentUnit unit, String endpoint) throws TXFrameworkException {
        final RESTATAnnotation restatAnnotation = RESTATAnnotation.build(unit, endpoint);
        final ATAnnotation atAnnotation = ATAnnotation.build(unit, endpoint);
        final BAAnnotation baAnnotation = BAAnnotation.build(unit, endpoint);
        final WebServiceAnnotation webServiceAnnotation = WebServiceAnnotation.build(unit, endpoint);
        final boolean isTXFrameworkEnabled = (atAnnotation != null || baAnnotation != null || restatAnnotation != null);

        return new EndpointMetaData(restatAnnotation, atAnnotation, baAnnotation, webServiceAnnotation, isTXFrameworkEnabled);
    }

    public ATAnnotation getAtAnnotation() {
        return atAnnotation;
    }

    public BAAnnotation getBaAnnotation() {
        return baAnnotation;
    }

    public RESTATAnnotation getRestatAnnotation() {
        return restatAnnotation;
    }

    public WebServiceAnnotation getWebServiceAnnotation() {
        return webServiceAnnotation;
    }

    public boolean isTXFrameworkEnabled() {
        return isTXFrameworkEnabled;
    }

    public boolean isWebservice() {
        return webServiceAnnotation != null;
    }
}
