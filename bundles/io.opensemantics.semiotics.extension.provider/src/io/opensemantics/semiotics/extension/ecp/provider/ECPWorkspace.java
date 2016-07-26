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
package io.opensemantics.semiotics.extension.ecp.provider;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.ecp.core.util.observer.ECPObserverBus;
import org.eclipse.emf.ecp.core.util.observer.ECPProjectOpenClosedObserver;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import io.opensemantics.semiotics.extension.api.Project;
import io.opensemantics.semiotics.extension.api.Workspace;

@Component
public class ECPWorkspace implements Workspace {

  private final LinkedHashMap<String, Project> projectMap;
  private ECPObserverBus observerBus;
  private OpenClosedObserver openCloseObserver;
  
  public ECPWorkspace() {
    projectMap = new LinkedHashMap<>();
    openCloseObserver = new OpenClosedObserver();
  }

  @Reference
  void bindObserver(ECPObserverBus observerBus) {
    this.observerBus = observerBus;
    this.observerBus.register(openCloseObserver);
  }

  void unbindObserver(ECPObserverBus observerBus) {
    this.observerBus = null;
  }

  @Deactivate
  void deactivate(ComponentContext cc,  BundleContext bc, Map<String,Object> config) {
    if (observerBus != null) {
      observerBus.unregister(openCloseObserver);
    }
  }

  @Override
  public Collection<Project> getProjects() {
    return projectMap.values();
  }

  @Override
  public boolean hasProjects() {
    return !projectMap.isEmpty();
  }

  private void removeProject(ECPProject project) {
    projectMap.remove(project.getName());
  }

  private void addProject(ECPProject project) {
    projectMap.put(project.getName(), new ECPBackedProject(project.getName()));
  }

  private class OpenClosedObserver implements ECPProjectOpenClosedObserver {

    @Override
    public void projectChanged(org.eclipse.emf.ecp.core.ECPProject project, boolean opened) {
      if (opened) {
        ECPWorkspace.this.addProject(project);
      } else {
        ECPWorkspace.this.removeProject(project);
      }
    }

  }
}
