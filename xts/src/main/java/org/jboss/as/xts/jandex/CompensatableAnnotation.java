package org.jboss.as.xts.jandex;

import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.xts.XTSException;
import org.jboss.jandex.AnnotationInstance;

/**
 * @author paul.robinson@redhat.com, 2012-02-06
 */
public class CompensatableAnnotation {

    public static final String COMPENSATABLE_ANNOTATION = "org.jboss.narayana.txframework.api.annotation.transaction.Compensatable";

    private CompensatableAnnotation() {
    }


    public static CompensatableAnnotation build(DeploymentUnit unit, String endpoint) throws XTSException {

        final AnnotationInstance annotationInstance = JandexHelper.getAnnotation(unit, endpoint, COMPENSATABLE_ANNOTATION);
        if (annotationInstance == null) {
            return null;
        }

        return new CompensatableAnnotation();
    }
}
