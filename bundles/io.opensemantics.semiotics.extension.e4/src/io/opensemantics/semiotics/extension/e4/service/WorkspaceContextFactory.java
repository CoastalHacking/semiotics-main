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
package io.opensemantics.semiotics.extension.e4.service;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import io.opensemantics.semiotics.extension.api.Workspace;

@Component(
    property={"service.context.key:String=io.opensemantics.semiotics.extension.api.Workspace"},
    service=ContextFunction.class
)
public class WorkspaceContextFactory extends ContextFunction {

  private Workspace workspace;
  
  @Override
  public Object compute(IEclipseContext context) {
    ContextInjectionFactory.inject(workspace, context);
    return workspace;
  }

  @Reference
  public void bindWorkspace(Workspace workspace) {
    this.workspace = workspace;
  }
  
  public void unbindWorkspace(Workspace workspace) {
    this.workspace = null;
  }
}
