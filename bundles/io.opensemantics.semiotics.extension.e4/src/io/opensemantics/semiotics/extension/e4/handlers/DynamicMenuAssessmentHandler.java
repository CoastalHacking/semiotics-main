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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

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

import io.opensemantics.semiotics.extension.api.Project;
import io.opensemantics.semiotics.extension.api.Workspace;

public class DynamicMenuAssessmentHandler {
  
  @Inject
  Workspace workspace;
  
  @AboutToShow
  public void aboutToShow(
      List<MMenuElement> items,
      MApplication application,
      EModelService modelServices,
      @Named (IServiceConstants.ACTIVE_SELECTION) @Optional IStructuredSelection selection) {

    if (selection == null) return;

    if (workspace != null) {
      for (Project project: workspace.getProjects()) {

        final MHandledMenuItem menuItem = modelServices.createModelElement(MHandledMenuItem.class);
        final List<MCommand> commands = modelServices.findElements(application, "io.opensemantics.semiotics.extension.e4.command.add.assessment", MCommand.class, null);
        menuItem.setLabel(project.getName());
        menuItem.setCommand(commands.get(0));

        final MParameter parameter = modelServices.createModelElement(MParameter.class);
        parameter.setName("io.opensemantics.semiotics.extension.e4.param.add.assessment.project");
        parameter.setValue(project.getName());
        menuItem.getParameters().add(parameter);

        items.add(menuItem);
      }
    }
  }

}
