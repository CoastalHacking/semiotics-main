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

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import io.opensemantics.semiotics.extension.api.Project;
import io.opensemantics.semiotics.extension.api.Workspace;

@Component
public class EMFStoreWorkspace implements Workspace {

  private List<Project> projects;

  public EMFStoreWorkspace() {
    projects = new ArrayList<>();
    projects.add(new EMFStoreProject("emf project a"));
    projects.add(new EMFStoreProject("emf project b"));

  }

  @Override
  public List<Project> getProjects() {
    return projects;
  }

  @Override
  public boolean hasProjects() {
    return projects == null ? false : !projects.isEmpty();
  }

}
