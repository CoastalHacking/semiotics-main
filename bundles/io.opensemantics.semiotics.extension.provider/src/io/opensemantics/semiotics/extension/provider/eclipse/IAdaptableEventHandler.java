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
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.ecp.core.ECPProjectManager;
import org.eclipse.emf.edit.command.ChangeCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import io.opensemantics.semiotics.extension.api.ModelFocal;
import io.opensemantics.semiotics.extension.provider.api.PostEventUtil;
import io.opensemantics.semiotics.extension.provider.ecp.TypedChangeCommand;
import io.opensemantics.semiotics.model.assessment.Application;
import io.opensemantics.semiotics.model.assessment.Applications;
import io.opensemantics.semiotics.model.assessment.Assessment;
import io.opensemantics.semiotics.model.assessment.AssessmentFactory;

// TODO: hide EventHandler details behind an API Handler
@Component(
    property=IAdaptableConstants.IADAPTABLE_TOPIC
)
public class IAdaptableEventHandler implements EventHandler {

  private ModelFocal modelFocus;
  @Reference
  void bindModelFocus(ModelFocal modelFocus) {
    this.modelFocus = modelFocus;
  }

  void unbindModelFocus(ModelFocal modelFocus) {
    this.modelFocus = modelFocus;
  }

  private ECPProjectManager projectManager;
  @Reference
  void bindProjectManager(ECPProjectManager projectManager) {
    this.projectManager = projectManager;
  }

  void unbindProjectManager(ECPProjectManager projectManager) {
    this.projectManager = null;
  }
  
  @Override
  public void handleEvent(Event event) {
    // TODO: hide these details?
    final Object data = event.getProperty(PostEventUtil.PROP_DATA);

    if (data instanceof IProjectDTO) {
      final IProjectDTO dto = (IProjectDTO) data;
      switch (dto.type) {
      case APPLICATION:
        addAsApplication(dto);
        break;
      default:
        break;
      }
    }
  }

  // FIXME: better logging on false / failure cases
  // FIXME: abstract out design below
  private void addAsApplication(final IProjectDTO dto) {

    // Get a project
    ECPProject project = null;
    final Object[] focuses = modelFocus.getFocuses();
    if (focuses.length > 0) {
      if (focuses[0] instanceof ECPProject) {
        project = (ECPProject) focuses[0];
      }
    }
    if (project == null) {
      Iterator<ECPProject> projIt = projectManager.getProjects().iterator();
      if (projIt.hasNext()) project = projIt.next();
    }
    
    if (project == null || !project.isOpen()) return;

    // Get an editing domain
    final EditingDomain editingDomain = project.getEditingDomain();
    if (!(editingDomain instanceof TransactionalEditingDomain)) return;

    // Get the root EObject
    Assessment assessment = null;
    if (focuses.length > 1) {
      if (focuses[1] instanceof Assessment) {
        assessment = (Assessment) focuses[1];
      }
    }
    if (assessment == null) {
      for (Object content : project.getContents()) {
        if (content instanceof Assessment) {
          assessment = (Assessment)content;
          break;
        }
      }
    }

    // Change commands
    List<ChangeCommand> changeCommands = new ArrayList<>();
    if (assessment == null) {
      assessment = AssessmentFactory.eINSTANCE.createAssessment();
      changeCommands.add(new TypedChangeCommand<Assessment>(assessment, project) {
        @Override
        protected void doExecute() {
          project.getContents().add(root);
        }
      });
    }

    changeCommands.add(new TypedChangeCommand<Assessment>(assessment, project) {
      @Override
      protected void doExecute() {
        Applications applications = root.getApplications();
        if (applications == null) {
          applications = AssessmentFactory.eINSTANCE.createApplications();
          root.setApplications(applications);
        }

        EList<Application> apps = applications.getApplications();        
        Application application = null;
        for (Application app : apps) {
          if (app.getLabel() != null && app.getLabel().equals(dto.name)) {
            application = app;
            break;
          }
        }

        if (application == null) {
          application = AssessmentFactory.eINSTANCE.createApplication();
          application.setLabel(dto.name);
          apps.add(application);
          modelFocus.setFocus(application);
        }
      }
    });

    // Apply changes
    for (ChangeCommand command: changeCommands) {
      editingDomain.getCommandStack().execute(command);
    }
  }

}
