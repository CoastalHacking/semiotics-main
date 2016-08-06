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
package io.opensemantics.semiotics.extension.provider;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import io.opensemantics.semiotics.extension.api.DTO;
import io.opensemantics.semiotics.extension.api.event.Post;
import io.opensemantics.semiotics.extension.provider.api.PostEventUtil;

@Component
public class PostImpl implements Post {

  public static final String PROP_DATA = "sourceData";

  protected EventAdmin eventAdmin;
  
  @Reference
  void bindEventAdmin(EventAdmin eventAdmin) {
    this.eventAdmin = eventAdmin;
  }
  
  void unbindEventAdmin(EventAdmin eventAdmin) {
    this.eventAdmin = null;
  }

  @Override
  public void add(DTO dto) {
    if (dto == null) return;

    final Map<String, Object> eventMap = new HashMap<>();
    eventMap.put(PostEventUtil.PROP_DATA, dto);
    eventAdmin.postEvent(new Event(PostEventUtil.getTopic(dto.topic), eventMap));
  }

}
