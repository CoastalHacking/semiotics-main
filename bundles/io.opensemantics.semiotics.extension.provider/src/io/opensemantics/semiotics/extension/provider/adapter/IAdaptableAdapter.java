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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;
import org.osgi.service.component.annotations.Component;

import io.opensemantics.semiotics.extension.api.Adapter;
import io.opensemantics.semiotics.extension.api.Cursor;
import io.opensemantics.semiotics.extension.provider.EObjectCursor;
import io.opensemantics.semiotics.model.assessment.Application;
import io.opensemantics.semiotics.model.assessment.Assessment;
import io.opensemantics.semiotics.model.assessment.Controller;
import io.opensemantics.semiotics.model.assessment.Resource;
import io.opensemantics.semiotics.model.assessment.Sink;

@Component(
    property="adaptableType=org.eclipse.core.runtime.IAdaptable"
)
public class IAdaptableAdapter implements Adapter {

  @Override
  public List<Class<? extends EObject>> getAdaptableTypes(Object source) {
    List<Class<? extends EObject>> results = new ArrayList<>();
    if (!(source instanceof IAdaptable)) return results;

    final IAdaptable iAdaptable = (IAdaptable) source;
    if (iAdaptable.getAdapter(IProject.class) != null) {
      results.add(Application.class);
    }

    if (iAdaptable.getAdapter(IFile.class) != null) {
      results.add(Resource.class);
      results.add(Controller.class);
      results.add(Sink.class);
    }

    return results;
  }

  @Override
  public Cursor update(Object source, Class<?> clazz, EObject selection) {
    if (!isAdaptable(source, clazz)) return null;

    EObject result = null;

    if (clazz.equals(Application.class)) {
      // Application
      IProject iProject = (IProject) source;
      final Assessment assessment = AdapterUtil.getSelection(selection, Assessment.class);
      result = AdapterUtil.getOrCreateApplication(iProject.getName(), assessment);
    } else if (clazz.equals(Resource.class)) {
      // Resource
      IFile iFile = (IFile) source;
      IProject iProject = iFile.getProject();
      final Assessment assessment = AdapterUtil.getSelection(selection, Assessment.class);
      Application application = AdapterUtil.getOrCreateApplication(iProject.getName(), assessment);
      result = AdapterUtil.getOrCreateResource(iFile.getName(), application);

    } else if (clazz.equals(Controller.class)) {
      // Controller
      IFile iFile = (IFile) source;
      IProject iProject = iFile.getProject();
      final Assessment assessment = AdapterUtil.getSelection(selection, Assessment.class);
      Application application = AdapterUtil.getOrCreateApplication(iProject.getName(), assessment);
      result = AdapterUtil.getOrCreateController(iFile.getName(), application);
    } else if (clazz.equals(Sink.class)) {
      // Sink
      IFile iFile = (IFile) source;
      IProject iProject = iFile.getProject();
      final Assessment assessment = AdapterUtil.getSelection(selection, Assessment.class);
      Application application = AdapterUtil.getOrCreateApplication(iProject.getName(), assessment);
      result = AdapterUtil.getOrCreateSink(iFile.getName(), application);
    }

    return new EObjectCursor(result);
  }

  @Override
  public boolean isAdaptable(Object source, Class<?> clazz) {
    if (!(source instanceof IAdaptable)) return false;

    final IAdaptable iAdaptable = (IAdaptable) source;
    boolean isIProject = (iAdaptable.getAdapter(IProject.class) != null);
    boolean isIFile = (iAdaptable.getAdapter(IFile.class) != null);

    if (clazz == null) return (isIProject || isIFile);

    if (clazz.equals(Application.class)) return isIProject;

    if (clazz.equals(Resource.class)) return isIFile;
    if (clazz.equals(Controller.class)) return isIFile;
    if (clazz.equals(Sink.class)) return isIFile;

    return false;
  }

}
