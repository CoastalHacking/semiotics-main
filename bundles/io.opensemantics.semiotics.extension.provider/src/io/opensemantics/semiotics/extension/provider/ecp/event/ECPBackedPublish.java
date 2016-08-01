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
package io.opensemantics.semiotics.extension.provider.ecp.event;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import io.opensemantics.semiotics.extension.api.event.Publish;

@Component
public class ECPBackedPublish implements Publish {

  private EventAdmin eventAdmin;
  
  @Reference
  void bindEventAdmin(EventAdmin eventAdmin) {
    this.eventAdmin = eventAdmin;
  }
  
  void unbindEventAdmin(EventAdmin eventAdmin) {
    this.eventAdmin = null;
  }
  
  @Override
  public void asAssessment(String projectName, Object data) {
    if (eventAdmin != null) {
      final Map<String, Object> eventMap = new HashMap<>();
      eventMap.put(EMFStoreEventConstants.PROP_DATA, data);
      eventMap.put(EMFStoreEventConstants.PROP_PROJECT_NAME, projectName);
      eventAdmin.postEvent(new Event(EMFStoreEventConstants.TOPIC_APPLICATION_ADD, eventMap));
    }
  }
}
