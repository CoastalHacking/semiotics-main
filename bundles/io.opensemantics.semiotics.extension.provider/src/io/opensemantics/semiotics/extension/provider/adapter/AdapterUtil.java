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
package io.opensemantics.semiotics.extension.provider.adapter;

import org.eclipse.emf.ecore.EObject;

import io.opensemantics.semiotics.model.assessment.Application;
import io.opensemantics.semiotics.model.assessment.Applications;
import io.opensemantics.semiotics.model.assessment.Assessment;
import io.opensemantics.semiotics.model.assessment.AssessmentFactory;
import io.opensemantics.semiotics.model.assessment.Controller;
import io.opensemantics.semiotics.model.assessment.Controllers;
import io.opensemantics.semiotics.model.assessment.GraphNode;
import io.opensemantics.semiotics.model.assessment.Node;
import io.opensemantics.semiotics.model.assessment.Resource;
import io.opensemantics.semiotics.model.assessment.Resources;
import io.opensemantics.semiotics.model.assessment.Sink;
import io.opensemantics.semiotics.model.assessment.Sinks;
import io.opensemantics.semiotics.model.assessment.Snippet;

public class AdapterUtil {

  public static <T> T getSelection(EObject selection, Class<T> clazz) {
    if (selection == null) return null;
    try {
      return clazz.cast(selection);
    } catch (ClassCastException cce) {
      return getSelection(selection.eContainer(), clazz);
    }
  }

  public static Application getOrCreateApplication(String projectName, Assessment assessment) {
    Applications applications = assessment.getApplications();
    if (applications == null) {
      applications = AssessmentFactory.eINSTANCE.createApplications();
      assessment.setApplications(applications);
    }
    
    Application application = null;
    for (Application someApplication : applications.getApplications()) {
      if (someApplication.getLabel() != null && someApplication.getLabel().equals(projectName)) {
        application = someApplication;
        break;
      }
    }
    if (application == null) {
      application = AssessmentFactory.eINSTANCE.createApplication();
      application.setLabel(projectName);
      applications.getApplications().add(application);
    }
    return application;
  }

  public static Resource getOrCreateResource(String resourceName, Application application) {
    Resources resources = application.getResources();
    if (resources == null) {
      resources = AssessmentFactory.eINSTANCE.createResources();
      application.setResources(resources);
    }

    Resource resource = null;
    for (Resource someResource: resources.getResources()) {
      if (someResource.getLabel().equals(resourceName)) {
        resource = someResource;
      }
    }

    if (resource == null) {
      resource = AssessmentFactory.eINSTANCE.createResource();
      resource.setLabel(resourceName);
      resources.getResources().add(resource);
    }
    return resource;
  }

  public static Controller getOrCreateController(String controllerName, Application application) {
    Controllers controllers = application.getControllers();
    if (controllers == null) {
      controllers = AssessmentFactory.eINSTANCE.createControllers();
      application.setControllers(controllers);
    }
    Controller controller = null;
    for (Controller someController: controllers.getControllers()) {
      if (someController.getLabel().equals(controllerName)) {
        controller = someController;
      }
    }
    
    if (controller == null) {
      controller = AssessmentFactory.eINSTANCE.createController();
      controller.setLabel(controllerName);
      controllers.getControllers().add(controller);
    }
    return controller;
  }

  public static Sink getOrCreateSink(String sinkName, Application application) {
    Sinks sinks = application.getSinks();
    if (sinks == null) {
      sinks = AssessmentFactory.eINSTANCE.createSinks();
      application.setSinks(sinks);
    }
    Sink sink = null;
    for (Sink someSink: sinks.getSinks()) {
      if (someSink.getLabel().equals(sinkName)) {
        sink = someSink;
      }
    }
    
    if (sink == null) {
      sink = AssessmentFactory.eINSTANCE.createSink();
      sink.setLabel(sinkName);
      sinks.getSinks().add(sink);
    }
    return sink;
  }
  
  public static Snippet getOrCreateSnippet(
      String contents,
      int lineStart,
      int lineEnd,
      int columnStart,
      int columnEnd,
      Node node) {

    Snippet snippet = null;
    
    // TODO: use EMF reflective API instead of this hardcoded hack
    for (GraphNode graphNode : node.getChildren()) {
      if (graphNode instanceof Snippet) {
        Snippet someSnippet = (Snippet) graphNode;
        if (someSnippet.getContents().equals(contents)) {
          snippet = someSnippet;
        }
      }
    }
    if (snippet == null) {
      snippet = AssessmentFactory.eINSTANCE.createSnippet();
      snippet.setColumnStart(columnStart);
      snippet.setColumnEnd(columnEnd);
      snippet.setLineStart(lineStart);
      snippet.setLineEnd(lineEnd);
      snippet.setContents(contents);
      node.getChildren().add(snippet);
    }

    return snippet;
  }
}
