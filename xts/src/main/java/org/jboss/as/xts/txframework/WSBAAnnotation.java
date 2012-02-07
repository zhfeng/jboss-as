package org.jboss.as.xts.txframework;

import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.jandex.AnnotationInstance;

/**
 * @author paul.robinson@redhat.com, 2012-02-06
 */
public class WSBAAnnotation {

    private static final String WSBA_ANNOTATION = "org.jboss.narayana.txframework.api.annotation.transaction.WSBA";

    private WSBAAnnotation() {
    }


    public static WSBAAnnotation build(DeploymentUnit unit, String endpoint) throws TXFrameworkException {

        final AnnotationInstance annotationInstance = Helper.getAnnotation(unit, endpoint, WSBA_ANNOTATION);
        if (annotationInstance == null) {
            return null;
        }

        return new WSBAAnnotation();
    }
}
