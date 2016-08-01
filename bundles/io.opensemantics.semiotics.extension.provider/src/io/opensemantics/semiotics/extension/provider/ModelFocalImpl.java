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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import io.opensemantics.semiotics.extension.api.ModelFocal;
import io.opensemantics.semiotics.extension.api.ModelFocalUpdateHandler;

@Component
public class ModelFocalImpl implements ModelFocal {

  final List<ModelFocalUpdateHandler> updateHandlers = Collections.synchronizedList(new ArrayList<>());
  final AtomicReference<Object> focus = new AtomicReference<>();
  
  @Override
  public Object getFocus() {
    return focus.get();
  }

  @Override
  public void setFocus(Object model) {
    synchronized (updateHandlers) {
      // not needed for this reference directly
      // however, avoid a TOCTOU-ish issue by including it
      // in this synchronized block
      focus.set(model);
      final Iterator<ModelFocalUpdateHandler> i = updateHandlers.iterator();
      while (i.hasNext()) {
        i.next().onUpdate(model);
      }
    }
  }

  @Reference(
      cardinality=ReferenceCardinality.MULTIPLE, 
      policy=ReferencePolicy.DYNAMIC
  )
  void bindHandler(ModelFocalUpdateHandler updateHandler) {
    updateHandlers.add(updateHandler);
  }

  void unbindHandler(ModelFocalUpdateHandler updateHandler) {
    updateHandlers.remove(updateHandler);
  }
}
