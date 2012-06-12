package org.jboss.as.xts.txframework;

import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.jandex.AnnotationInstance;

/**
 * @author paul.robinson@redhat.com, 2012-02-06
 */
public class CompensatableAnnotation {

    public static final String COMPENSATABLE_ANNOTATION = "org.jboss.narayana.txframework.api.annotation.transaction.Compensatable";

    private CompensatableAnnotation() {
    }


    public static CompensatableAnnotation build(DeploymentUnit unit, String endpoint) throws TXFrameworkException {

        final AnnotationInstance annotationInstance = Helper.getAnnotation(unit, endpoint, COMPENSATABLE_ANNOTATION);
        if (annotationInstance == null) {
            return null;
        }

        return new CompensatableAnnotation();
    }
}
