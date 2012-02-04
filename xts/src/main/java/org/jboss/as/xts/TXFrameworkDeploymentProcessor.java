package org.jboss.as.xts;

import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.webservices.util.WSAttachmentKeys;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerChainMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerChainsMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData;
import org.jboss.wsf.spi.metadata.webservices.PortComponentMetaData;
import org.jboss.wsf.spi.metadata.webservices.WebserviceDescriptionMetaData;
import org.jboss.wsf.spi.metadata.webservices.WebservicesMetaData;

import javax.xml.namespace.QName;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.as.webservices.util.ASHelper.getAnnotations;

public class TXFrameworkDeploymentProcessor implements DeploymentUnitProcessor {

    public void deploy(final DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {

        final DeploymentUnit unit = phaseContext.getDeploymentUnit();
        final List<AnnotationInstance> webServiceRefAnnotations = getAnnotations(unit, DotName.createSimple("javax.jws.WebService"));
        final List<String> wstxEndpoints = getWSTXEndpoints(unit);

        WebservicesMetaData webservicesMetaData = new WebservicesMetaData();
        boolean modified = false;

        for (AnnotationInstance webserviceAnnotation : webServiceRefAnnotations) {

            final ClassInfo classInfo = (ClassInfo) webserviceAnnotation.target();
            final String endpointClass = classInfo.name().toString();

            if (wstxEndpoints.contains(endpointClass)) {
                addHandlerToEndpoint(webservicesMetaData, webserviceAnnotation, endpointClass);
                modified = true;
            }
        }

        if (modified) {
            unit.putAttachment(WSAttachmentKeys.WEBSERVICES_METADATA_KEY, webservicesMetaData);
        }
    }

    private void addHandlerToEndpoint(WebservicesMetaData wsWebservicesMetaData, AnnotationInstance annotationInstance, String endpointClass) {

        WebserviceDescriptionMetaData descriptionMetaData = new WebserviceDescriptionMetaData(wsWebservicesMetaData);

        final UnifiedHandlerChainsMetaData unifiedHandlerChainsMetaData = buildHandlerChains("com.arjuna.mw.wst11.service.JaxWSHeaderContextProcessor");
        final QName portQname = getPortQname(annotationInstance);
        final PortComponentMetaData portComponent = buildPortComponent(endpointClass, portQname, unifiedHandlerChainsMetaData, descriptionMetaData);
        descriptionMetaData.addPortComponent(portComponent);
        wsWebservicesMetaData.addWebserviceDescription(descriptionMetaData);
    }

    private QName getPortQname(AnnotationInstance annotationInstance) {
        final String portName = annotationInstance.value("portName").asString();
        final String targetNamespace = annotationInstance.value("targetNamespace").toString();
        return new QName(targetNamespace, portName);
    }

    private PortComponentMetaData buildPortComponent(String endpointClass, QName portQname, UnifiedHandlerChainsMetaData unifiedHandlerChainsMetaData, WebserviceDescriptionMetaData descriptionMetaData) {

        PortComponentMetaData portComponent = new PortComponentMetaData(descriptionMetaData);
        portComponent.setHandlerChains(unifiedHandlerChainsMetaData);
        portComponent.setServiceEndpointInterface(endpointClass);
        portComponent.setWsdlPort(portQname);

        return portComponent;
    }

    private UnifiedHandlerChainsMetaData buildHandlerChains(String... handlerClasses) {

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

    private void registerHandlersWithAS() {
        // WSEndpointHandlersMapping mapping =
        // unit.getAttachment(WS_ENDPOINT_HANDLERS_MAPPING_KEY);
        // Set<String> handlers = mapping.getHandlers(endpointClass);

        // WSEndpointHandlersMapping mapping = new
        // WSEndpointHandlersMapping();
        // Set<String> handlers = new HashSet<String>();

        // handlers.add(TXFrameworkHandler.class.getName());
        // mapping.registerEndpointHandlers(endpointClass, handlers);

        // unit.putAttachment(WS_ENDPOINT_HANDLERS_MA
        // PPING_KEY, mapping);
    }

    private List<String> getWSTXEndpoints(DeploymentUnit unit) {

        List<String> wsat = endpointsWithAnnotation(unit, "org.jboss.narayana.txframework.api.annotation.transaction.WSAT");
        List<String> wsba = endpointsWithAnnotation(unit, "org.jboss.narayana.txframework.api.annotation.transaction.WSBA");

        List<String> result = new ArrayList<String>();
        result.addAll(wsat);
        result.addAll(wsba);

        return result;
    }

    private List<String> endpointsWithAnnotation(DeploymentUnit unit, String className) {

        final List<AnnotationInstance> annotations = getAnnotations(unit, DotName.createSimple(className));

        final List<String> endpoints = new ArrayList<String>();
        for (AnnotationInstance annotationInstance : annotations) {
            final ClassInfo classInfo = (ClassInfo) annotationInstance.target();
            final String endpointClass = classInfo.name().toString();
            endpoints.add(endpointClass);
        }
        return endpoints;
    }

    public void undeploy(final DeploymentUnit unit) {

        // does nothing
    }

}
