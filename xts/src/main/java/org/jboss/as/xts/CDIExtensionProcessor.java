/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.xts;

import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.reflect.DeploymentReflectionIndex;
import org.jboss.as.weld.deployment.WeldAttachments;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.metadata.MetadataImpl;

import javax.enterprise.inject.spi.Extension;
import java.lang.reflect.Constructor;

/**
 * @author paul.robinson@redhat.com, 2012-02-09
 */
public class CDIExtensionProcessor implements DeploymentUnitProcessor {

    private static final String[] EMPTY_STRING_ARRAY = {};

    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {

        final DeploymentUnit unit = phaseContext.getDeploymentUnit();

        try {
            final DeploymentReflectionIndex index = unit.getAttachment(Attachments.REFLECTION_INDEX);
            final ClassLoader cl = unit.getAttachment(Attachments.MODULE).getClassLoader();
            final Class<Extension> serviceClass = (Class<Extension>) cl.loadClass("org.jboss.narayana.txframework.impl.as.TXFrameworkCDIExtension");
            final Constructor<Extension> ctor = index.getClassIndex(serviceClass).getConstructor(EMPTY_STRING_ARRAY);
            final Extension extension =  ctor.newInstance();
            final Metadata<Extension> metadata = new MetadataImpl<Extension>(extension, unit.getName());
            unit.addToAttachmentList(WeldAttachments.PORTABLE_EXTENSIONS, metadata);
        } catch (ClassNotFoundException e) {
            //Temporary hack as this class will disappear when WFLY-1370 is resolved.
            System.err.println("CDI Extension not in classpath for: " + unit.getName());
        } catch (Exception e) {
            throw XtsAsMessages.MESSAGES.cannotLoadCDIExtension();
        }
    }

    public void undeploy(DeploymentUnit context) {

    }
}