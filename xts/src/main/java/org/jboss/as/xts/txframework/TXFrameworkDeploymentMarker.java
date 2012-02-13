package org.jboss.as.xts.txframework;

import org.jboss.as.server.deployment.AttachmentKey;
import org.jboss.as.server.deployment.DeploymentUnit;

/**
 * @author paul.robinson@redhat.com, 2012-02-13
 */
public class TXFrameworkDeploymentMarker {

    private static final AttachmentKey<TXFrameworkDeploymentMarker> MARKER = AttachmentKey.create(TXFrameworkDeploymentMarker.class);

    private TXFrameworkDeploymentMarker() {
    }

    public static void mark(DeploymentUnit unit) {
        unit.putAttachment(MARKER, new TXFrameworkDeploymentMarker());
    }

    public static boolean isTXFrameworkDeployment(DeploymentUnit unit) {
        return unit.hasAttachment(MARKER);
    }

}
