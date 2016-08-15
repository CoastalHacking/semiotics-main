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
/**
 * 
 */
package io.opensemantics.semiotics.extension.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.emf.ecore.EObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import io.opensemantics.semiotics.extension.api.Adapter;
import io.opensemantics.semiotics.extension.api.AdapterProvider;

/**
 * @author jonpasski
 *
 */
@Component
public class AdapterProviderImpl implements AdapterProvider {

  static final String ADAPT = "adapt";
  static final String TOPIC = "io/opensemantics/semiotics/extension/Adapter";
  static final String TOPIC_ALL = "io/opensemantics/semiotics/extension/Adapter/*";
  static final String TOPIC_ADAPT = "io/opensemantics/semiotics/extension/Adapter/adapt";
  static final String SOURCE = "source";
  static final String CLASS = "class";

  private EventAdmin eventAdmin;
  @Reference
  void bindEventAdmin(EventAdmin eventAdmin) {
    this.eventAdmin = eventAdmin;
  }
  
  void unbindEventAdmin(EventAdmin eventAdmin) {
    this.eventAdmin = null;
  }

  private Map<String, Adapter> adapters = new ConcurrentHashMap<>();
  @Reference(
      cardinality=ReferenceCardinality.MULTIPLE,
      policy=ReferencePolicy.DYNAMIC
  )
  void bindAdapter(Adapter dtoAdapter) {
    this.adapters.put(dtoAdapter.toString(), dtoAdapter);
  }
  void unbindAdapter(Adapter dtoAdapter) {
    this.adapters.remove(dtoAdapter.toString());
  }
  /* (non-Javadoc)
   * @see io.opensemantics.semiotics.extension.api.AdapterProvider#publish(java.lang.Object, java.lang.Class)
   */
  @Override
  public void publish(Object source, Class<?> clazz) {
    final Map<String, Object> map = new HashMap<>();
    map.put(SOURCE, source);
    map.put(CLASS, clazz);
    eventAdmin.postEvent(new Event(TOPIC_ADAPT, map));
    for (Adapter adapter: adapters.values()) {
      for (Object obj : adapter.republish(source)) {
      // Recursive
      this.publish(obj, clazz);
      }
    }
  }

  /* (non-Javadoc)
   * @see io.opensemantics.semiotics.extension.api.AdapterProvider#getAdaptableTypes(java.lang.Object)
   */
  @Override
  public List<Class<? extends EObject>> getAdaptableTypes(Object source) {
    final Set<Class<? extends EObject>> results = new LinkedHashSet<>();
    for (Adapter adapter: adapters.values()) {
      results.addAll(adapter.getAdaptableTypes(source));
      for (Object obj : adapter.republish(source)) {
        // Recursive
        results.addAll(this.getAdaptableTypes(obj));
      }
    }
    return new ArrayList<>(results);
  }
  
  /* (non-Javadoc)
   * @see io.opensemantics.semiotics.extension.api.AdapterProvider#isAdaptable(java.lang.Object)
   */
  @Override
  public boolean isAdaptable(Object source) {
    for (Adapter adapter: adapters.values()) {
      if (adapter.isAdaptable(source)) return true;
      for (Object obj : adapter.republish(source)) {
        // Recursive
        if (this.isAdaptable(obj)) return true;
      }

    }
    return false;
  }

}
