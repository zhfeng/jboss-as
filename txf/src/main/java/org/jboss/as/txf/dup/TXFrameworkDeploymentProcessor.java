package org.jboss.as.txf.dup;

import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.webservices.injection.WSEndpointHandlersMapping;
import org.jboss.as.webservices.util.WSAttachmentKeys;
import org.jboss.as.txf.dup.jandex.BridgeType;
import org.jboss.as.txf.dup.jandex.TransactionalAnnotation;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerChainMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerChainsMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData;
import org.jboss.wsf.spi.metadata.webservices.PortComponentMetaData;
import org.jboss.wsf.spi.metadata.webservices.WebserviceDescriptionMetaData;
import org.jboss.wsf.spi.metadata.webservices.WebservicesMetaData;

import javax.xml.namespace.QName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TXFrameworkDeploymentProcessor implements DeploymentUnitProcessor {

    private static final String TX_BRIDGE_HANDLER = "org.jboss.jbossts.txbridge.inbound.JaxWSTxInboundBridgeHandler";
    private static final String TX_CONTEXT_HANDLER = "com.arjuna.mw.wst11.service.JaxWSHeaderContextProcessor";

    public void deploy(final DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {

        final DeploymentUnit unit = phaseContext.getDeploymentUnit();

        WebservicesMetaData webservicesMetaData = new WebservicesMetaData();

        boolean modifiedWSMeta = false;

        for (String endpoint : Helper.getDeploymentClasses(unit)) {
            try {

                EndpointMetaData endpointMetaData = EndpointMetaData.build(unit, endpoint);

                if (endpointMetaData.isTXFrameworkEnabled()) {

                    TXFrameworkDeploymentMarker.mark(unit);

                    if (endpointMetaData.isWebservice()) {

                        List<String> handlers = new ArrayList<String>();
                        TransactionalAnnotation TransactionalAnnotation = endpointMetaData.getTransactionalAnnotation();
                        if (shouldBridge(TransactionalAnnotation)) {
                            handlers.add(TX_BRIDGE_HANDLER);
                        }
                        handlers.add(TX_CONTEXT_HANDLER);

                        addHandlerToEndpoint(webservicesMetaData, endpointMetaData, endpoint, handlers);
                        registerHandlersWithAS(unit, endpoint, handlers);
                        modifiedWSMeta = true;
                    }

                }

            } catch (TXFrameworkException e) {
                throw new DeploymentUnitProcessingException("Error processing endpoint '" + endpoint + "'", e);
            }
        }

        if (modifiedWSMeta) {
            unit.putAttachment(WSAttachmentKeys.WEBSERVICES_METADATA_KEY, webservicesMetaData);
        }
    }

    private boolean shouldBridge(TransactionalAnnotation TransactionalAnnotation) {
        if (TransactionalAnnotation == null) {
            return false;
        }
        if (TransactionalAnnotation.getBridgeType() == null) {
            return false;
        }
        BridgeType bridgeType = TransactionalAnnotation.getBridgeType();
        return (bridgeType.equals(BridgeType.JTA) || bridgeType.equals(BridgeType.DEFAULT));
    }

    private void addHandlerToEndpoint(WebservicesMetaData wsWebservicesMetaData, EndpointMetaData endpointMetaData, String endpointClass, List<String> handlers) {

        WebserviceDescriptionMetaData descriptionMetaData = new WebserviceDescriptionMetaData(wsWebservicesMetaData);

        final UnifiedHandlerChainsMetaData unifiedHandlerChainsMetaData = buildHandlerChains(handlers);
        final QName portQname = endpointMetaData.getWebServiceAnnotation().buildPortQName();
        final PortComponentMetaData portComponent = buildPortComponent(endpointMetaData.isEJB(), endpointClass, portQname, unifiedHandlerChainsMetaData, descriptionMetaData);
        descriptionMetaData.addPortComponent(portComponent);
        wsWebservicesMetaData.addWebserviceDescription(descriptionMetaData);
    }

    private PortComponentMetaData buildPortComponent(boolean isEJB, String endpointClass, QName portQname, UnifiedHandlerChainsMetaData unifiedHandlerChainsMetaData, WebserviceDescriptionMetaData descriptionMetaData) {

        PortComponentMetaData portComponent = new PortComponentMetaData(descriptionMetaData);
        portComponent.setHandlerChains(unifiedHandlerChainsMetaData);
        portComponent.setServiceEndpointInterface(endpointClass);
        portComponent.setWsdlPort(portQname);

        if (isEJB) {
            portComponent.setEjbLink(getClassName(endpointClass));
        } else {
            portComponent.setServletLink(endpointClass);
        }

        return portComponent;
    }

    private UnifiedHandlerChainsMetaData buildHandlerChains(List<String> handlerClasses) {

        UnifiedHandlerChainMetaData unifiedHandlerChainMetaData = new UnifiedHandlerChainMetaData();

        for (String handlerClass : handlerClasses) {
            UnifiedHandlerMetaData handlerMetaData = new UnifiedHandlerMetaData();
            handlerMetaData.setHandlerClass(handlerClass);
            unifiedHandlerChainMetaData.addHandler(handlerMetaData);
        }

        UnifiedHandlerChainsMetaData unifiedHandlerChainsMetaData = new UnifiedHandlerChainsMetaData();
        unifiedHandlerChainsMetaData.addHandlerChain(unifiedHandlerChainMetaData);

        return unifiedHandlerChainsMetaData;
    }

    private void registerHandlersWithAS(DeploymentUnit unit, String endpointClass, List<String> handlersToAdd) {

        WSEndpointHandlersMapping mapping = unit.getAttachment(WSAttachmentKeys.WS_ENDPOINT_HANDLERS_MAPPING_KEY);
        if (mapping == null) {
            mapping = new WSEndpointHandlersMapping();
            unit.putAttachment(WSAttachmentKeys.WS_ENDPOINT_HANDLERS_MAPPING_KEY, mapping);
        }

        Set<String> existingHandlers = mapping.getHandlers(endpointClass);
        if (existingHandlers == null) {
            existingHandlers = new HashSet<String>();
        } else {
            //Existing collection is an unmodifiableSet
            existingHandlers = new HashSet<String>(existingHandlers);
        }

        for (String handler : handlersToAdd) {
            if (existingHandlers.contains(handler)) {
                return;
            }
            existingHandlers.add(handler);
        }
        mapping.registerEndpointHandlers(endpointClass, existingHandlers);
    }

    private String getClassName(String fQClass) {
        String[] split = fQClass.split("\\.");
        return split[split.length-1];
    }

    public void undeploy(final DeploymentUnit unit) {

        // does nothing
    }

}
