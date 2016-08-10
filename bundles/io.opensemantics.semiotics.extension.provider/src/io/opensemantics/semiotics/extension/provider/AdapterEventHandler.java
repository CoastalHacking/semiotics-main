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

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.ecp.core.ECPProjectManager;
import org.eclipse.emf.edit.command.ChangeCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import io.opensemantics.semiotics.extension.api.Adapter;
import io.opensemantics.semiotics.extension.api.ModelFocal;
import io.opensemantics.semiotics.model.assessment.Assessment;
import io.opensemantics.semiotics.model.assessment.AssessmentFactory;

@Component(
    property={EventConstants.EVENT_TOPIC + "=" + AdapterProviderImpl.TOPIC_ADAPT}
)
public class AdapterEventHandler implements EventHandler {

  private Map<String, Adapter> adapters = new ConcurrentHashMap<>();
  @Reference(
      cardinality=ReferenceCardinality.MULTIPLE,
      policy=ReferencePolicy.DYNAMIC
  )
  void bindAdapter(Adapter dtoAdapter) {
    this.adapters.put(dtoAdapter.toString(), dtoAdapter);
  }
  void unbindAdapter(Adapter dtoAdapter) {
    this.adapters.remove(dtoAdapter.toString());
  }

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
    final Object source = event.getProperty(AdapterProviderImpl.SOURCE);
    Class<?> clazz = (Class<?>)event.getProperty(AdapterProviderImpl.CLASS); 

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
    // TODO: log if not
    if (!(editingDomain instanceof TransactionalEditingDomain)) return;

    // Does the focus service have an assessment?
    Assessment assessment = null;
    if (focuses.length > 1) {
      if (focuses[1] instanceof Assessment) {
        assessment = (Assessment) focuses[1];
      }
    }
    
    // Is there an assessment in the ECPProject?
    if (assessment == null) {
      for (Object content : project.getContents()) {
        // FIXME: breaks on first assesssment
        if (content instanceof Assessment) {
          assessment = (Assessment)content;
          break;
        }
      }
    }

    // Guess not, make one
    if (assessment == null) {
      assessment = AssessmentFactory.eINSTANCE.createAssessment();
      final AssessmentChangeCommand command = new AssessmentChangeCommand(assessment, project);
      // next two lines are sensitive to ordering
      editingDomain.getCommandStack().execute(command);
      modelFocus.setFocus(command.getCursor());
    }

    // Grab the model focus and adapt to an EObject
    Object focus = modelFocus.getFocus();
    final EObject selection;
    if (focus instanceof EObject) {
      selection = (EObject)focus;
    } else {
      selection = assessment;
    }

    // Create additional changes
    for (Adapter adapter: adapters.values()) {
      if (adapter.isAdaptable(source, clazz)) {

        final AdapterChangeCommand command = new AdapterChangeCommand(adapter, selection, source, clazz);
        // next two lines are sensitive to ordering
        editingDomain.getCommandStack().execute(command);
        final Object cursor = command.getCursor();
        if (cursor != null) {
          modelFocus.setFocus(cursor);
        }
      }
    }
  }

}
