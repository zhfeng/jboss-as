package org.jboss.as.xts.txframework;

import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.jandex.AnnotationInstance;

/**
 * @author paul.robinson@redhat.com, 2012-02-06
 */
public class WSATAnnotation {

    private static final String WSAT_ANNOTATION = "org.jboss.narayana.txframework.api.annotation.transaction.WSAT";

    private BridgeType bridgeType;

    private WSATAnnotation(BridgeType bridgeType) {
        this.bridgeType = bridgeType;
    }

    public BridgeType getBridgeType() {
        return bridgeType;
    }

    public static WSATAnnotation build(DeploymentUnit unit, String endpoint) throws TXFrameworkException {

        final AnnotationInstance annotationInstance = Helper.getAnnotation(unit, endpoint, WSAT_ANNOTATION);
        if (annotationInstance == null) {
            return null;
        }

        BridgeType bridgeType = BridgeType.build(annotationInstance);
        return new WSATAnnotation(bridgeType);
    }
}
