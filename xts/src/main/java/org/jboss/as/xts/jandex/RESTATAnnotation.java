package org.jboss.as.xts.jandex;

import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.xts.XTSException;
import org.jboss.jandex.AnnotationInstance;

/**
 * @author paul.robinson@redhat.com, 2012-02-06
 */
public class RESTATAnnotation {

    private static final String RESTAT_ANNOTATION = "org.jboss.narayana.txframework.api.annotation.transaction.RESTAT";


    public static RESTATAnnotation build(DeploymentUnit unit, String endpoint) throws XTSException {

        final AnnotationInstance annotationInstance = JandexHelper.getAnnotation(unit, endpoint, RESTAT_ANNOTATION);
        if (annotationInstance == null) {
            return null;
        }

        return new RESTATAnnotation();
    }
}
