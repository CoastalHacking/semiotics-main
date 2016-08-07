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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.ecp.core.ECPProjectManager;
import org.eclipse.emf.edit.command.ChangeCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;

import io.opensemantics.semiotics.extension.api.ModelFocal;
import io.opensemantics.semiotics.extension.provider.ecp.TypedChangeCommand;
import io.opensemantics.semiotics.model.assessment.Application;
import io.opensemantics.semiotics.model.assessment.Applications;
import io.opensemantics.semiotics.model.assessment.Assessment;
import io.opensemantics.semiotics.model.assessment.AssessmentFactory;

@Component(
    property=EventConstants.EVENT_TOPIC + "=" + EMFStoreEventConstants.TOPIC_APPLICATION_ADD
)
public class AddApplicationSubscriber implements EventHandler {

  private LogService logger;
  private ECPProjectManager projectManager;
  private ModelFocal modelFocus;
  
  @Reference
  void bindLogService(LogService logService) {
    this.logger = logService;
  }

  void unbindLogService(LogService logService) {
    this.logger = null;
  }
  
  @Reference
  void bindProjectManager(ECPProjectManager projectManager) {
    this.projectManager = projectManager;
  }

  void unbindProjectManager(ECPProjectManager projectManager) {
    this.projectManager = null;
  }

  @Reference
  void bindModelFocus(ModelFocal modelFocus) {
    this.modelFocus = modelFocus;
  }

  void unbindModelFocus(ModelFocal modelFocus) {
    this.modelFocus = modelFocus;
  }
  
  @Override
  public void handleEvent(Event event) {
    final String projectName = (String) event.getProperty(EMFStoreEventConstants.PROP_PROJECT_NAME);
    final Object data = event.getProperty(EMFStoreEventConstants.PROP_DATA);
    if (data instanceof IProject) {
      IProject iProject = (IProject)data;
      String message = String.format("%s received ADD for project %s", getClass().getName(), projectName);
      log(LogService.LOG_INFO, message);
      addAsApplication(projectName, iProject);
    }
  }

  private void addAsApplication(String projectName, IProject iProject) {

    final String iProjectName = iProject.getName();
    
    // boiler-plate code start.
    if (projectManager == null) {
      String message = String.format("ProjectManager unbound; cannot add project %s", projectName);
      log(LogService.LOG_WARNING, message);
      return;
    }
    
    final ECPProject ecpProject = projectManager.getProject(projectName);
    if (ecpProject == null || !ecpProject.isOpen()) {
      String message = String.format(
          "No ECPProject with the name of %s or it is not open, not adding %s to it.",
          projectName, iProjectName);
      log(LogService.LOG_WARNING, message);
      return;
    }

    final EditingDomain editingDomain = ecpProject.getEditingDomain();
    if (!(editingDomain instanceof TransactionalEditingDomain)) {
      String message = String.format(
          "The EditingDomain for ECPProject %s is not an instance of TransactionalEditingDomain",
          projectName);
      log(LogService.LOG_WARNING, message);
      return;
    }
    // boiler-plate code end.

    // Get the root EObject
    Assessment assessment = null;
    for (Object content : ecpProject.getContents()) {
      if (content instanceof Assessment) {
        // FIXME: breaks on first match
        assessment = (Assessment)content;
        break;
      }
    }

    // Change commands
    List<ChangeCommand> changeCommands = new ArrayList<>();
    if (assessment == null) {
      assessment = AssessmentFactory.eINSTANCE.createAssessment();
      changeCommands.add(new TypedChangeCommand<Assessment>(assessment, ecpProject) {
        @Override
        protected void doExecute() {
          ecpProject.getContents().add(root);
        }
      });
    }

    changeCommands.add(new TypedChangeCommand<Assessment>(assessment, ecpProject) {
      @Override
      protected void doExecute() {
        // Create or get applications container
        Applications applications = root.getApplications();
        if (applications == null) {
          applications = AssessmentFactory.eINSTANCE.createApplications();
          root.setApplications(applications);
        }

        EList<Application> apps = applications.getApplications();        
        Application application = null;
        for (Application app : apps) {
          if (app.getLabel() != null && app.getLabel().equals(iProjectName)) {
            application = app;
            break;
          }
        }

        if (application == null) {
          application = AssessmentFactory.eINSTANCE.createApplication();
          application.setLabel(iProjectName);
          apps.add(application);
          modelFocus.setFocus(application);
        } else {
          String message = String.format("Application already exists for project %s", iProjectName);
          log(LogService.LOG_WARNING, message);
        }
      }
    });

    // Apply changes
    for (ChangeCommand command: changeCommands) {
      editingDomain.getCommandStack().execute(command);
    }

  }

  private void log(int severity, String message) {
    if (logger != null) {
      logger.log(severity, message);
    }
  }

}
