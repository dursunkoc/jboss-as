/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

package org.jboss.as.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jboss.as.naming.WritableServiceBasedNamingStore;
import org.jboss.msc.service.LifecycleContext;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;

/**
 * Abstract service class.
 *
 * @author <a href="mailto:ropalka@redhat.com">Richard Opalka</a>
 */
abstract class AbstractService implements Service<Object> {

    private final Object mBeanInstance;
    private final ServiceName duServiceName;

    /**
     *
     * @param mBeanInstance
     * @param duServiceName the deployment unit's service name
     */
    protected AbstractService(final Object mBeanInstance, final ServiceName duServiceName) {
        this.mBeanInstance = mBeanInstance;
        this.duServiceName = duServiceName;
    }

    /** {@inheritDoc} */
    public final Object getValue() {
        return mBeanInstance;
    }

    protected void invokeLifecycleMethod(final Method method, final LifecycleContext context) throws InvocationTargetException, IllegalAccessException {
        if (method != null) {
            WritableServiceBasedNamingStore.pushOwner(duServiceName);
            final ClassLoader old = SecurityActions.setThreadContextClassLoader(mBeanInstance.getClass().getClassLoader());
            try {
                method.invoke(mBeanInstance);
            } finally {
                SecurityActions.resetThreadContextClassLoader(old);
                WritableServiceBasedNamingStore.popOwner();
            }
        }
    }

}
