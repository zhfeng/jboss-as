package org.jboss.as.xts;

import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.as.server.deployment.Phase;

public class TXFrameworkDeploymentActivator {

    static void activate(final DeploymentProcessorTarget processorTarget) {
        processorTarget.addDeploymentProcessor(Phase.PARSE, Phase.PARSE_TXFRAMEWORK_HANDLERS, new TXFrameworkDeploymentProcessor());
    }
}
