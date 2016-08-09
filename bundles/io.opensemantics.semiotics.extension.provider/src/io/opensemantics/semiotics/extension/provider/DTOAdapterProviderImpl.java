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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import io.opensemantics.semiotics.extension.api.DTO;
import io.opensemantics.semiotics.extension.api.DTOAdapter;
import io.opensemantics.semiotics.extension.api.DTOAdapterProvider;
import io.opensemantics.semiotics.extension.api.DTOType;

@Component
public class DTOAdapterProviderImpl implements DTOAdapterProvider {
  
  private Map<String,DTOAdapter> dtoAdapters = new ConcurrentHashMap<>();
  @Reference(
      cardinality=ReferenceCardinality.MULTIPLE,
      policy=ReferencePolicy.DYNAMIC
  )
  void bindDTOAdapter(DTOAdapter dtoAdapter) {
    this.dtoAdapters.put(dtoAdapter.toString(), dtoAdapter);
  }
  void unbindDTOAdapter(DTOAdapter dtoAdapter) {
    this.dtoAdapters.remove(dtoAdapter.toString());
  }
  
  @Override
  public DTO adapt(Object source, DTOType type) {
    //FIXME: shouldn't break on first match; should return all
    for (DTOAdapter adapter: dtoAdapters.values()) {
      DTO dto = adapter.adapt(source, type);
      if (dto != null) return dto;
    }
    return null;
  }

  @Override
  public List<DTOType> getAdaptableTypes(Object source) {
    Set<DTOType> set = new HashSet<>(); 
    for (DTOAdapter adapter: dtoAdapters.values()) {
      set.addAll(adapter.getAdaptableTypes(source));
    }
    return new ArrayList<DTOType>(set);
  }

  @Override
  public boolean isAdaptable(Object source, DTOType type) {
    for (DTOAdapter adapter: dtoAdapters.values()) {
      DTO dto = adapter.adapt(source, type);
      if (dto != null) return true;
    }
    return false;
  }

}
