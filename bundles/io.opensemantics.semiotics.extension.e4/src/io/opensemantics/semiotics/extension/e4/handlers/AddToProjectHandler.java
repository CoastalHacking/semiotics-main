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
package io.opensemantics.semiotics.extension.e4.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.AboutToHide;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.IStructuredSelection;

import io.opensemantics.semiotics.extension.api.DTO;
import io.opensemantics.semiotics.extension.api.DTOAdapter;
import io.opensemantics.semiotics.extension.api.DTOType;
import io.opensemantics.semiotics.extension.api.event.Post;
import io.opensemantics.semiotics.extension.api.event.Publish;

public class AddToProjectHandler {

  @Inject
  ESelectionService selectionService;
  
  @Execute
  @Optional
  public void execute(
      @Named (IServiceConstants.ACTIVE_SELECTION) IStructuredSelection selection,
      @Named (DynamicTypeMenuHandler.COMMAND_PARAM_TYPE) String type,
      DTOAdapter adapter,
      Post post) {

    @SuppressWarnings("rawtypes")
    final Iterator it = selection.iterator();
    final DTOType dtoType = DTOType.valueOf(type);
    while (it.hasNext()) {
      final Object element = it.next();
      DTO dto = adapter.adapt(element, dtoType);
      post.add(dto);
    }
  }

}
