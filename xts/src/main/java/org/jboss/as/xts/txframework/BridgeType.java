package org.jboss.as.xts.txframework;

import org.jboss.jandex.AnnotationInstance;

/**
 * @author paul.robinson@redhat.com, 2012-02-06
 */
public enum BridgeType {

    DEFAULT, NONE, JTA;

    public static BridgeType build(AnnotationInstance annotationInstance) throws TXFrameworkException {

        if (annotationInstance.value("bridgeType") == null) {
            return DEFAULT;
        }

        String bridgeType = annotationInstance.value("bridgeType").asString();

        if (bridgeType.equals("DEFAULT")) {
            return DEFAULT;
        } else if (bridgeType.equals("NONE")) {
            return NONE;
        } else if (bridgeType.equals("JTA")) {
            return JTA;
        } else {
            throw new TXFrameworkException("Unexpected bridge type: '" + bridgeType + "'");
        }
    }
}
