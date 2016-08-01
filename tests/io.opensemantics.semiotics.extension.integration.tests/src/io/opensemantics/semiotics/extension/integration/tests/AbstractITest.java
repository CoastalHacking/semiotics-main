/**
 * Copyright 2016 OpenSemantics.IO
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.opensemantics.semiotics.extension.integration.tests;

import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.ecp.core.ECPProjectManager;
import org.eclipse.emf.ecp.core.ECPProvider;
import org.eclipse.emf.ecp.core.ECPProviderRegistry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public abstract class AbstractITest {

  protected static ECPProjectManager projectManager;
  protected static ECPProviderRegistry providerRegistry;
  protected static ECPProvider provider;

  @BeforeClass
  public static void beforeAbstractClass() {
    projectManager = getService(WorkspaceITest.class, ECPProjectManager.class);
    clearProjects(projectManager.getProjects());
    providerRegistry = getService(PublishITest.class, ECPProviderRegistry.class);

    // cheater, cheater, pumpkin eater
    // should be agnostic to the provider type
    provider = providerRegistry.getProvider("org.eclipse.emf.ecp.emfstore.provider");
  }
  
  @AfterClass
  public static void afterAbstractClass() {
    projectManager = null;
    providerRegistry = null;
    provider = null;
  }
  
  @Test
  public void beforeAbstract() {
    assertNotNull(projectManager);
    assertNotNull(providerRegistry);
    assertNotNull(provider);
  }
  
  // http://blog.vogella.com/2016/07/04/osgi-component-testing/
  // This leaks ServiceTrackers
  protected static <B, T> T getService(Class<B> bundleClass, Class<T> serviceClass) {
    Bundle bundle = FrameworkUtil.getBundle(bundleClass);
    if (bundle != null) {
      ServiceTracker<T, T> st = new ServiceTracker<T, T>(
          bundle.getBundleContext(), serviceClass, null);
      st.open();
      if (st != null) {
        try {
          return st.waitForService(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

  protected static <B, T> ServiceRegistration<T> setService(Class<B> bundleClass, Class<T> serviceClass, T service) {
    Bundle bundle = FrameworkUtil.getBundle(bundleClass);
    if (bundle != null) {
      return bundle.getBundleContext().registerService(serviceClass, service, null);
    }
    return null;
  }

  private static void clearProjects(Collection<ECPProject> ecpProjects) {
    if (ecpProjects == null) return;

    for (ECPProject project : ecpProjects) {
      project.delete();
    }
  }
}
