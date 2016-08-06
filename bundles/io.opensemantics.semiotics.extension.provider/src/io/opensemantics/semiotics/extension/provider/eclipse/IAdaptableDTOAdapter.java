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
package io.opensemantics.semiotics.extension.provider.eclipse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.IAdaptable;
import org.osgi.service.component.annotations.Component;

import io.opensemantics.semiotics.extension.api.DTO;
import io.opensemantics.semiotics.extension.api.DTOAdapter;
import io.opensemantics.semiotics.extension.api.DTOType;

@Component
public class IAdaptableDTOAdapter implements DTOAdapter {

  @Override
  public DTO adapt(Object source, DTOType type) {
    if (!isAdaptable(source, type)) return null;

    final IAdaptable iAdaptable = (IAdaptable) source;
    switch (type) {
    case APPLICATION:
      IProject iProject = Adapters.adapt(iAdaptable, IProject.class, true);
      if (iProject == null) return null;
      IProjectDTO dto = new IProjectDTO();
      dto.topic = IAdaptableConstants.IADAPTABLE_SUBTOPIC;
      dto.type = DTOType.APPLICATION;
      dto.name = iProject.getName();
      return dto;
    case SINK:
    case SNIPPET:
    case SOURCE:
    default:
      return null;
    }

  }

  @Override
  public List<DTOType> getAdaptableTypes(Object source) {
    List<DTOType> results = new ArrayList<>();
    if (!(source instanceof IAdaptable)) return results;

    final IAdaptable iAdaptable = (IAdaptable) source;
    if (Adapters.adapt(iAdaptable, IProject.class, true) != null)
      results.add(DTOType.APPLICATION);

    return results;
  }

  @Override
  public boolean isAdaptable(Object source, DTOType type) {
    if (!(source instanceof IAdaptable)) return false;

    final IAdaptable iAdaptable = (IAdaptable) source;
    switch (type) {
    case APPLICATION:
      IProject iProject = Adapters.adapt(iAdaptable, IProject.class, true);
      if (iProject == null) return false;
      break;
    case SINK:
    case SNIPPET:
    case SOURCE:
    default:
      return false;
    }

    return true;
  }

}
