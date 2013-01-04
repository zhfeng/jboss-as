package org.jboss.as.xts.txframework;

import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.jandex.AnnotationInstance;

/**
 * @author paul.robinson@redhat.com, 2012-02-06
 */
public class StatelessAnnotation {

    private static final String STATELESS_ANNOTATION = "javax.ejb.Stateless";


    public static StatelessAnnotation build(DeploymentUnit unit, String endpoint) throws TXFrameworkException {

        final AnnotationInstance annotationInstance = Helper.getAnnotation(unit, endpoint, STATELESS_ANNOTATION);
        if (annotationInstance == null) {
            return null;
        }

        return new StatelessAnnotation();
    }
}
