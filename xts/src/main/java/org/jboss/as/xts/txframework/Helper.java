package org.jboss.as.xts.txframework;

import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.jboss.as.webservices.util.ASHelper.getAnnotations;

/**
 * @author paul.robinson@redhat.com, 2012-02-06
 */
public class Helper {

    public static AnnotationInstance getAnnotation(DeploymentUnit unit, String endpoint, String annotationClassName) {

        final List<AnnotationInstance> annotations = getAnnotations(unit, DotName.createSimple(annotationClassName));
        for (AnnotationInstance annotationInstance : annotations) {
            final ClassInfo classInfo = (ClassInfo) annotationInstance.target();
            final String endpointClass = classInfo.name().toString();

            if (endpointClass.equals(endpoint)) {
                return annotationInstance;
            }
        }

        return null;
    }

    //todo: find a way to return all deployment classes.
    public static Set<String> getDeploymentClasses(DeploymentUnit unit) {

        final List<AnnotationInstance> annotations = getAnnotations(unit, DotName.createSimple("javax.jws.WebService"));
        final Set<String> endpoints = new HashSet<String>();
        for (AnnotationInstance annotationInstance : annotations) {
            final ClassInfo classInfo = (ClassInfo) annotationInstance.target();
            final String endpointClass = classInfo.name().toString();
            endpoints.add(endpointClass);
        }
        return endpoints;
    }

    public static String getStringVaue(AnnotationInstance annotationInstance, String key) {
        AnnotationValue value = annotationInstance.value(key);
        if (value == null) {
            return null;
        }
        return value.asString();
    }
}
