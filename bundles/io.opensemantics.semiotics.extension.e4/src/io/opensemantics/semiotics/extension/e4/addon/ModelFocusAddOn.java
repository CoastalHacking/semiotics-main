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
package io.opensemantics.semiotics.extension.e4.addon;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.modeling.ISelectionListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;

import io.opensemantics.semiotics.extension.api.ModelFocal;

public class ModelFocusAddOn {

  private static final String ECP_E3_VIEW_ID = "org.eclipse.emf.ecp.ui.ModelExplorerView";
  // Managed
  private ISelectionListener selectionListener;
  private Object selectionListenerLock = new Object();

  @Inject
  ESelectionService selectionService;

  @Inject
  ModelFocal modelFocusService;

  // vogella to the rescue
  @Inject
  @Optional
  private void addSelectionListener(
      @UIEventTopic(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE) final MApplication application) {
    synchronized (selectionListenerLock) {
      if (selectionListener == null) {
        selectionListener = new ISelectionListener() {
          @Override
          public void selectionChanged(MPart part, Object selection) {
            // Unset quickly
            if (selection instanceof ISelection) {
              if (((ISelection)selection).isEmpty()) {
                modelFocusService.setFocus(null);
                modelFocusService.setFocuses(new Object[]{});
                return;
              }
            }
            if (selection instanceof ITreeSelection) {
              ITreeSelection tree = (ITreeSelection) selection;
              TreePath[] paths = tree.getPaths();
              // FIXME: hardcoded first path
              if (paths != null && paths.length > 0) {
                TreePath path = paths[0];
                Object[] focuses = new Object[path.getSegmentCount()];
                for (int i=0; i < path.getSegmentCount(); i++) {
                  focuses[i] = path.getSegment(i);
                }
                modelFocusService.setFocuses(focuses);
              }
            }
            if (selection instanceof IStructuredSelection) {
              IStructuredSelection iStruct = (IStructuredSelection)selection;
              // Only set when one distinct element is chosen
              if (iStruct.size() == 1) {
                final Object object = iStruct.getFirstElement();
                modelFocusService.setFocus(object);
              }
            }
          }
        };
        selectionService.addSelectionListener(ECP_E3_VIEW_ID, selectionListener);
      }
    }
  }

  @PreDestroy
  public void preDestroy() {
    synchronized (selectionListenerLock) {
      if (selectionListener != null && selectionService != null) {
        selectionService.removeSelectionListener(selectionListener);
      }
    }

  }

}
