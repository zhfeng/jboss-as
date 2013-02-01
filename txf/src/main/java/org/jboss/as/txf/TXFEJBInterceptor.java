package org.jboss.as.txf;

import org.jboss.as.ee.component.ComponentView;
import org.jboss.as.naming.ManagedReference;
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
public class TXFEJBInterceptor implements Interceptor {

    public static final String CONTEXT_KEY = "org.jboss.as.txf.InterceptorContextKey";

    public static final InterceptorFactory FACTORY = new ImmediateInterceptorFactory(new TXFEJBInterceptor());

    protected TXFEJBInterceptor() {
    }

    @Override
    public Object processInvocation(InterceptorContext context) throws Exception {

        ComponentView cv = context.getPrivateData(ComponentView.class);
        ManagedReference mr = cv.createInstance();
        Object serviceInstance = mr.getInstance();
        Class serviceClass = context.getTarget().getClass();
        Method serviceMethod = context.getMethod();

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
            return new TXFEJBInterceptor();
        }
    }
}