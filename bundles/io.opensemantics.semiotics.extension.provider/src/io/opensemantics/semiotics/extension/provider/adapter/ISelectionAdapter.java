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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import io.opensemantics.semiotics.extension.api.Adapter;
import io.opensemantics.semiotics.extension.api.Cursor;
import io.opensemantics.semiotics.extension.provider.EObjectCursor;
import io.opensemantics.semiotics.model.assessment.Application;
import io.opensemantics.semiotics.model.assessment.Node;
import io.opensemantics.semiotics.model.assessment.Snippet;

@Component(
    property={"adaptableType=org.eclipse.jface.viewers.ISelection"}
)
public class ISelectionAdapter implements Adapter {


  private Adapter iAdaptableAdapter;
  
  @Reference(
      target="(adaptableType=org.eclipse.core.runtime.IAdaptable)"
  )
  void bindAdapterProvider(Adapter adapter) {
    this.iAdaptableAdapter = adapter;
  }
  void unbindAdapterProvider(Adapter adapter) {
    this.iAdaptableAdapter = null;
  }

  @Override
  public List<Class<? extends EObject>> getAdaptableTypes(Object source) {
    List<Class<? extends EObject>> results = new ArrayList<>();
    if (!(source instanceof ISelection)) return results;

    final ISelection iSelection = (ISelection) source;

    if (iSelection instanceof ITextSelection) {
      results.add(Snippet.class);
    }
    if (iSelection instanceof IStructuredSelection) {
      // Defers to IAdaptableAdapter
      results.add(Application.class);
    }

    return results;
  }

  @Override
  public Cursor update(Object source, Class<?> clazz, EObject selection) {
    if (!isAdaptable(source, clazz)) return null;
    
    EObject result = null;

    final ISelection iSelection = (ISelection) source;
    if (clazz.equals(Snippet.class)) {
      final Node node = AdapterUtil.getSelection(selection, Node.class);
      if (node == null) {
        // TODO: better logging
        return null;
      }
      // Snippet
      ITextSelection iTextSelection = (ITextSelection) iSelection;
      final String label = iTextSelection.getText();
      final int lineStart = iTextSelection.getStartLine();
      final int lineEnd =  iTextSelection.getEndLine();
      final int columnStart = iTextSelection.getOffset();
      // FIXME : this is incorrect; column end != length of line
      final int columnEnd = iTextSelection.getLength();
      result = AdapterUtil.getOrCreateSnippet(
          label,
          lineStart,
          lineEnd,
          columnStart,
          columnEnd,
          node);
    } else if (clazz.equals(Application.class)) {
      IStructuredSelection iStructSelection = (IStructuredSelection) iSelection;
      for (final @SuppressWarnings("rawtypes") Iterator it = iStructSelection.iterator(); it.hasNext();) {
        Object object = it.next();
        if (object instanceof IProject) {
          return iAdaptableAdapter.update(object, Application.class, selection);
        }
      }
    }

    return new EObjectCursor(result);
  }


  @Override
  public boolean isAdaptable(Object source, Class<?> clazz) {
    if (!(source instanceof ISelection)) return false;
    final ISelection iSelection = (ISelection) source;

    boolean isTextSelection = (iSelection instanceof ITextSelection);
    boolean isStructuredSelection = (iSelection instanceof IStructuredSelection);

    if (clazz == null) return (isTextSelection || isStructuredSelection);

    if (clazz.equals(Snippet.class)) return isTextSelection;
    
    // Defers to IAdaptableAdapter
    if (clazz.equals(Application.class)) return isStructuredSelection;

    return false;
  }

}
