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

import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public abstract class AbstractITest {

  // http://blog.vogella.com/2016/07/04/osgi-component-testing/
  // This leaks ServiceTrackers
  protected static <B, T> T getService(Class<B> bundleClass, Class<T> serviceClass) {
    Bundle bundle = FrameworkUtil.getBundle(bundleClass);
    if (bundle != null) {
      // Ensure a call to get the context returns something non-null
      try {
        bundle.start();
      } catch (BundleException e1) {
        return null;
      }
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

  protected static <B, T> ServiceRegistration<T> registerService(Class<B> bundleClass, Class<T> serviceClass, T service, Dictionary<String, ?>properties) {
    Bundle bundle = FrameworkUtil.getBundle(bundleClass);
    if (bundle != null) {
      // Ensure a call to get the context returns something non-null
      try {
        bundle.start();
        return bundle.getBundleContext().registerService(serviceClass, service, properties);
      } catch (BundleException e1) {
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
}
