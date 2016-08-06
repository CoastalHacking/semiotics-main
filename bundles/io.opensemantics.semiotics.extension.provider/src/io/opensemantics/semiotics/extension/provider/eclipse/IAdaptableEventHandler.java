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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import io.opensemantics.semiotics.extension.api.ModelFocal;
import io.opensemantics.semiotics.extension.provider.api.PostEventUtil;

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

  @Override
  public void handleEvent(Event event) {
    // TODO: hide these details?
    final Object data = event.getProperty(PostEventUtil.PROP_DATA);

    if (data instanceof IProjectDTO) {
      final IProjectDTO dto = (IProjectDTO) data;
      switch (dto.type) {
      case APPLICATION:
        break;
      default:
        break;
      }
    }
  }

  /*
  private void addAsApplication(String projectName, IProject iProject) {

    final String iProjectName = iProject.getName();

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
      changeCommands.add(new TypedChangeCommand<Assessment>(assessment) {
        @Override
        protected void doExecute() {
          ecpProject.getContents().add(root);
        }
      });
    }

    changeCommands.add(new TypedChangeCommand<Assessment>(assessment) {
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
        }
      }
    });

  }
*/

}
