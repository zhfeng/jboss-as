package org.jboss.as.xts.txframework;

import org.jboss.as.server.deployment.DeploymentUnit;

/**
 * @author paul.robinson@redhat.com, 2012-02-06
 */
public class EndpointMetaData {

    private RESTATAnnotation restatAnnotation;
    private TransactionalAnnotation transactionalAnnotation;
    private CompensatableAnnotation compensatableAnnotation;
    private WebServiceAnnotation webServiceAnnotation;
    private StatelessAnnotation statelessAnnotation;

    private boolean isTXFrameworkEnabled;

    private EndpointMetaData(StatelessAnnotation statelessAnnotation, RESTATAnnotation restatAnnotation, TransactionalAnnotation transactionalAnnotation, CompensatableAnnotation compensatableAnnotation, WebServiceAnnotation webServiceAnnotation, boolean isTXFrameworkEnabled) {
        this.statelessAnnotation = statelessAnnotation;
        this.restatAnnotation = restatAnnotation;
        this.transactionalAnnotation = transactionalAnnotation;
        this.compensatableAnnotation = compensatableAnnotation;
        this.webServiceAnnotation = webServiceAnnotation;
        this.isTXFrameworkEnabled = isTXFrameworkEnabled;
    }

    public static EndpointMetaData build(DeploymentUnit unit, String endpoint) throws TXFrameworkException {
        final StatelessAnnotation statelessAnnotation = StatelessAnnotation.build(unit, endpoint);
        final RESTATAnnotation restatAnnotation = RESTATAnnotation.build(unit, endpoint);
        final TransactionalAnnotation transactionalAnnotation = TransactionalAnnotation.build(unit, endpoint);
        final CompensatableAnnotation compensatableAnnotation = CompensatableAnnotation.build(unit, endpoint);
        final WebServiceAnnotation webServiceAnnotation = WebServiceAnnotation.build(unit, endpoint);
        final boolean isTXFrameworkEnabled = (transactionalAnnotation != null || compensatableAnnotation != null || restatAnnotation != null);

        return new EndpointMetaData(statelessAnnotation, restatAnnotation, transactionalAnnotation, compensatableAnnotation, webServiceAnnotation, isTXFrameworkEnabled);
    }

    public TransactionalAnnotation getTransactionalAnnotation() {
        return transactionalAnnotation;
    }

    public CompensatableAnnotation getCompensatableAnnotation() {
        return compensatableAnnotation;
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

    public boolean isEJB() {
        return statelessAnnotation != null;
    }
}
