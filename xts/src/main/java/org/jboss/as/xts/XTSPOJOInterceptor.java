package org.jboss.as.xts;

import org.jboss.as.ee.component.ComponentInstance;
import org.jboss.invocation.ImmediateInterceptorFactory;
import org.jboss.invocation.Interceptor;
import org.jboss.invocation.InterceptorContext;
import org.jboss.invocation.InterceptorFactory;
import org.jboss.invocation.InterceptorFactoryContext;
import org.jboss.narayana.txframework.api.annotation.service.ServiceRequest;
import org.jboss.narayana.txframework.impl.ServiceInvocationMeta;
import org.jboss.narayana.txframework.impl.handlers.HandlerFactory;
import org.jboss.narayana.txframework.impl.handlers.ProtocolHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author paul.robinson@redhat.com 01/02/2013
 */
public class XTSPOJOInterceptor implements Interceptor {

    public static final InterceptorFactory FACTORY = new ImmediateInterceptorFactory(new XTSPOJOInterceptor());

    protected XTSPOJOInterceptor() {
    }

    @Override
    public Object processInvocation(InterceptorContext context) throws Exception {

        ComponentInstance componentInstance = context.getPrivateData(ComponentInstance.class);
        Object serviceInstance = componentInstance.getInstance();
        Method serviceMethod = context.getMethod();
        Class serviceClass = serviceInstance.getClass();

        boolean txf = false;
        for (Annotation a : serviceMethod.getDeclaredAnnotations()) {
            if (a instanceof ServiceRequest) {
                txf = true;
            }
        }

        if (!txf) {
            return context.proceed();
        }

        Object result;
        ProtocolHandler protocolHandler = HandlerFactory.getInstance(new ServiceInvocationMeta(serviceInstance, serviceClass, serviceMethod));
        try {
            protocolHandler.preInvocation();
            result = context.proceed();
            protocolHandler.notifySuccess();
        } catch (Exception e) {
            protocolHandler.notifyFailure();
            throw e;
        }

        return result;
    }


    public static class Factory implements InterceptorFactory {

        @Override
        public Interceptor create(final InterceptorFactoryContext context) {
            return new XTSPOJOInterceptor();
        }
    }
}