package org.jboss.as.xts.jandex;

import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.webservices.util.ASHelper;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;

import java.util.List;


/**
 * @author paul.robinson@redhat.com, 2012-02-06
 */
public class JandexHelper {

    public static AnnotationInstance getAnnotation(DeploymentUnit unit, String endpoint, String annotationClassName) {

        final List<AnnotationInstance> annotations = ASHelper.getAnnotations(unit, DotName.createSimple(annotationClassName));
        for (AnnotationInstance annotationInstance : annotations) {
            final ClassInfo classInfo = (ClassInfo) annotationInstance.target();
            final String endpointClass = classInfo.name().toString();

            if (endpointClass.equals(endpoint)) {
                return annotationInstance;
            }
        }

        return null;
    }
}
