package org.jboss.as.xts;

import org.jboss.as.server.deployment.AttachmentKey;
import org.jboss.as.server.deployment.DeploymentUnit;

/**
 * @author paul.robinson@redhat.com, 2012-02-13
 */
public class CDIDeploymentMarker {

    private static final AttachmentKey<CDIDeploymentMarker> MARKER = AttachmentKey.create(CDIDeploymentMarker.class);

    private CDIDeploymentMarker() {
    }

    public static void mark(DeploymentUnit unit) {
        unit.putAttachment(MARKER, new CDIDeploymentMarker());
    }

    public static boolean isXTSAnnotationDeployment(DeploymentUnit unit) {
        return unit.hasAttachment(MARKER);
    }

}
