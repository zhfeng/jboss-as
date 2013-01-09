package org.jboss.as.txf.dup;

import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.as.server.deployment.Phase;

public class TXFrameworkDeploymentActivator {

    public static void activate(final DeploymentProcessorTarget processorTarget) {
        processorTarget.addDeploymentProcessor(Phase.PARSE, Phase.PARSE_TXFRAMEWORK_HANDLERS, new TXFrameworkDeploymentProcessor());
        processorTarget.addDeploymentProcessor(Phase.POST_MODULE, Phase.POST_MODULE_WELD_PORTABLE_EXTENSIONS + 10, new TXFrameworkCDIExtensionProcessor());
    }
}
