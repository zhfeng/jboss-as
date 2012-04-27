package org.jboss.as.xts.txframework;

import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.jandex.AnnotationInstance;

/**
 * @author paul.robinson@redhat.com, 2012-02-06
 */
public class BAAnnotation {

    private static final String BA_ANNOTATION = "org.jboss.narayana.txframework.api.annotation.transaction.BA";

    private BAAnnotation() {
    }


    public static BAAnnotation build(DeploymentUnit unit, String endpoint) throws TXFrameworkException {

        final AnnotationInstance annotationInstance = Helper.getAnnotation(unit, endpoint, BA_ANNOTATION);
        if (annotationInstance == null) {
            return null;
        }

        return new BAAnnotation();
    }
}
