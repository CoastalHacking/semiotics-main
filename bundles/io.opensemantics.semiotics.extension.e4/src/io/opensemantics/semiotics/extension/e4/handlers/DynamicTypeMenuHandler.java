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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Named;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.viewers.IStructuredSelection;

import io.opensemantics.semiotics.extension.api.DTOAdapter;
import io.opensemantics.semiotics.extension.api.DTOType;

public class DynamicTypeMenuHandler {
  
  public static final String COMMAND = "io.opensemantics.semiotics.extension.e4.command.add.type";
  public static final String COMMAND_PARAM_TYPE = "io.opensemantics.semiotics.extension.e4.commandparameter.type";

  @AboutToShow
  @Optional
  void aboutToShow(
      List<MMenuElement> items,
      MApplication application,
      EModelService modelServices,
      DTOAdapter adapter,
      @Named (IServiceConstants.ACTIVE_SELECTION) IStructuredSelection selection) {

    Set<DTOType> types = new HashSet<>();
    @SuppressWarnings("rawtypes")
    final Iterator it = selection.iterator();
    while (it.hasNext()) {
      final Object element = it.next();
      types.addAll(adapter.getAdaptableTypes(element));
    }

    for (DTOType type: types) {
      final MHandledMenuItem menuItem = modelServices.createModelElement(MHandledMenuItem.class);
      final List<MCommand> commands = modelServices.findElements(application, COMMAND, MCommand.class, null);
      // TODO: l18n
      menuItem.setLabel(type.toString());
      menuItem.setCommand(commands.get(0));

      final MParameter projectName = modelServices.createModelElement(MParameter.class);
      projectName.setName(COMMAND_PARAM_TYPE);
      projectName.setValue(type.toString());
      menuItem.getParameters().add(projectName);

      items.add(menuItem);
    }
  }


}
