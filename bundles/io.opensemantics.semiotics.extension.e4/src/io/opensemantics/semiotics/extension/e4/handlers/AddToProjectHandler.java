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

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.resources.IFile;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IPartService;

import io.opensemantics.semiotics.extension.api.AdapterProvider;

public class AddToProjectHandler {

  @Inject
  ESelectionService selectionService;
  
  @Execute
  public void execute(
      @Optional @Named (IServiceConstants.ACTIVE_SELECTION) ISelection selection,
      @Optional @Named (DynamicTextEditorMenuHandler.COMMAND_PARAM_TYPE) String type,
      @Optional IPartService iPartService,
      AdapterProvider adapterProvider) {

    if (type == null) return;

    final Class<?> clazz;
    try {
      clazz = Class.forName(type);
    } catch (ClassNotFoundException cnf) {
      // TODO : log
      cnf.printStackTrace();
      return;
    }

    IFile iFile = HandlerUtil.getIFileFromIPartService(iPartService);
    if (iFile != null) adapterProvider.publish(iFile, clazz);

    if (selection != null && !selection.isEmpty()) {
      adapterProvider.publish(selection, clazz);
    }

  }

}
