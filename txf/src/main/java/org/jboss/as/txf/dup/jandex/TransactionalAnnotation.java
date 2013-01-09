package org.jboss.as.txf.dup.jandex;

import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.txf.dup.Helper;
import org.jboss.as.txf.dup.TXFrameworkException;
import org.jboss.jandex.AnnotationInstance;

/**
 * @author paul.robinson@redhat.com, 2012-02-06
 */
public class TransactionalAnnotation {

    public static final String TRANSACTIONAL_ANNOTATION = "org.jboss.narayana.txframework.api.annotation.transaction.Transactional";

    private BridgeType bridgeType;

    private TransactionalAnnotation(BridgeType bridgeType) {
        this.bridgeType = bridgeType;
    }

    public BridgeType getBridgeType() {
        return bridgeType;
    }

    public static TransactionalAnnotation build(DeploymentUnit unit, String endpoint) throws TXFrameworkException {

        final AnnotationInstance annotationInstance = Helper.getAnnotation(unit, endpoint, TRANSACTIONAL_ANNOTATION);
        if (annotationInstance == null) {
            return null;
        }

        BridgeType bridgeType = BridgeType.build(annotationInstance);
        return new TransactionalAnnotation(bridgeType);
    }
}
