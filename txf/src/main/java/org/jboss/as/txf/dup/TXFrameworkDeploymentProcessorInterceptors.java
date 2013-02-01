package org.jboss.as.txf.dup;

import org.jboss.as.ee.component.Attachments;
import org.jboss.as.ee.component.ComponentConfiguration;
import org.jboss.as.ee.component.ComponentConfigurator;
import org.jboss.as.ee.component.ComponentDescription;
import org.jboss.as.ee.component.EEModuleDescription;
import org.jboss.as.ee.component.ViewConfiguration;
import org.jboss.as.ee.component.ViewConfigurator;
import org.jboss.as.ee.component.ViewDescription;
import org.jboss.as.ee.component.interceptors.InterceptorOrder;
import org.jboss.as.ejb3.component.session.SessionBeanComponentDescription;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.txf.TXFEJBInterceptor;
import org.jboss.as.txf.TXFPOJOInterceptor;
import org.jboss.as.webservices.injection.WSComponentDescription;


public class TXFrameworkDeploymentProcessorInterceptors implements DeploymentUnitProcessor {


    public void deploy(final DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {

        final DeploymentUnit unit = phaseContext.getDeploymentUnit();

        final EEModuleDescription moduleDescription = unit.getAttachment(Attachments.EE_MODULE_DESCRIPTION);
        for (ComponentDescription component : moduleDescription.getComponentDescriptions()) {
            if (component instanceof SessionBeanComponentDescription) {
                registerSessionBeanInterceptors((SessionBeanComponentDescription) component);
            }
            if (component instanceof WSComponentDescription) {
                registerWSPOJOInterceptors((WSComponentDescription) component);
            }
        }
    }

    private void registerSessionBeanInterceptors(SessionBeanComponentDescription componentDescription) {
        if (componentDescription.isStateless()) {

            componentDescription.getConfigurators().addFirst(new ComponentConfigurator() {
                @Override
                public void configure(DeploymentPhaseContext context, ComponentDescription description, ComponentConfiguration configuration) throws
                        DeploymentUnitProcessingException {
                    configuration.addComponentInterceptor(TXFEJBInterceptor.FACTORY, InterceptorOrder.Component.TXF_HANDLERS_INTERCEPTOR, false);

                    configuration.getInterceptorContextKeys().add(TXFEJBInterceptor.CONTEXT_KEY);
                }
            });
        }

    }

    private void registerWSPOJOInterceptors(WSComponentDescription componentDescription) {
        for (ViewDescription view : componentDescription.getViews()) {
            view.getConfigurators().add(new ViewConfigurator() {
                @Override
                public void configure(DeploymentPhaseContext context, ComponentConfiguration componentConfiguration, ViewDescription description, ViewConfiguration configuration) throws DeploymentUnitProcessingException {
                    configuration.addViewInterceptor(TXFPOJOInterceptor.FACTORY, InterceptorOrder.View.TXF_INTERCEPTOR);
                }
            });
        }
    }

    public void undeploy(final DeploymentUnit unit) {

        // does nothing
    }

}
