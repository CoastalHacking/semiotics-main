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

import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.IStructuredSelection;

public class AddAssessmentHandler {

  @Inject
  ESelectionService selectionService;
  
  @Execute
  public void execute(
      @Named (IServiceConstants.ACTIVE_SELECTION) @Optional IStructuredSelection selection,
      @Named ("io.opensemantics.semiotics.extension.e4.param.add.assessment.project") @Optional String projectName) {

    if (selection == null || projectName == null) return;

    @SuppressWarnings("rawtypes")
    final Iterator it = selection.iterator();
    while (it.hasNext()) {
      final Object element = it.next();
      if (element instanceof IAdaptable) {
        final IProject iProject = (IProject)((IAdaptable)element).getAdapter(IProject.class);
        if (iProject != null) {
          // System.out.println("Project: " + iProject.getName());
        }
      }
    }
  }
}
