package org.jboss.as.xts.txframework;

import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.jandex.AnnotationInstance;

/**
 * @author paul.robinson@redhat.com, 2012-02-06
 */
public class ATAnnotation {

    private static final String AT_ANNOTATION = "org.jboss.narayana.txframework.api.annotation.transaction.AT";

    private BridgeType bridgeType;

    private ATAnnotation(BridgeType bridgeType) {
        this.bridgeType = bridgeType;
    }

    public BridgeType getBridgeType() {
        return bridgeType;
    }

    public static ATAnnotation build(DeploymentUnit unit, String endpoint) throws TXFrameworkException {

        final AnnotationInstance annotationInstance = Helper.getAnnotation(unit, endpoint, AT_ANNOTATION);
        if (annotationInstance == null) {
            return null;
        }

        BridgeType bridgeType = BridgeType.build(annotationInstance);
        return new ATAnnotation(bridgeType);
    }
}
