package org.jboss.as.txn.txframework.jandex;

import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.txf.dup.Helper;
import org.jboss.as.txf.dup.TXFrameworkException;
import org.jboss.jandex.AnnotationInstance;

import javax.xml.namespace.QName;

/**
 * @author paul.robinson@redhat.com, 2012-02-06
 */
public class WebServiceAnnotation {

    private static final String WEBSERVICE_ANNOTATION = "javax.jws.WebService";

    private String portName;

    private String serviceName;

    private String name;

    private String targetNamespace;

    private WebServiceAnnotation(String portName, String serviceName, String name, String targetNamespace) {
        this.portName = portName;
        this.serviceName = serviceName;
        this.name = name;
        this.targetNamespace = targetNamespace;
    }

    public static WebServiceAnnotation build(DeploymentUnit unit, String endpoint) throws TXFrameworkException {
        AnnotationInstance annotationInstance = Helper.getAnnotation(unit, endpoint, WEBSERVICE_ANNOTATION);

        if (annotationInstance == null) {
            return null;
        }

        final String portName = Helper.getStringVaue(annotationInstance, "portName");
        final String serviceName = Helper.getStringVaue(annotationInstance, "serviceName");
        final String name = Helper.getStringVaue(annotationInstance, "name");
        final String targetNamespace = Helper.getStringVaue(annotationInstance, "targetNamespace");

        return new WebServiceAnnotation(portName, serviceName, name, targetNamespace);
    }

    public QName buildPortQName() {
        return new QName(targetNamespace, portName);
    }

    public String getPortName() {
        return portName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getName() {
        return name;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }
}