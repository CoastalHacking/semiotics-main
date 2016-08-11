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
package io.opensemantics.semiotics.extension.provider.tests.adapter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.ITextSelection;
import org.junit.Test;
import org.mockito.Mockito;

import io.opensemantics.semiotics.extension.api.Adapter;
import io.opensemantics.semiotics.extension.api.Cursor;
import io.opensemantics.semiotics.extension.provider.adapter.AdapterUtil;
import io.opensemantics.semiotics.extension.provider.adapter.IAdaptableAdapter;
import io.opensemantics.semiotics.extension.provider.adapter.ISelectionAdapter;
import io.opensemantics.semiotics.model.assessment.Application;
import io.opensemantics.semiotics.model.assessment.Assessment;
import io.opensemantics.semiotics.model.assessment.AssessmentFactory;
import io.opensemantics.semiotics.model.assessment.Controller;
import io.opensemantics.semiotics.model.assessment.Snippet;

public class ISelectionAdapterTest {

  @Test
  public void shouldAdaptISelection() {
    Adapter adapter = new ISelectionAdapter();
    
    ITextSelection iTextSelection = Mockito.mock(ITextSelection.class);
    final int startLine = 1;
    final int endLine = 2;
    final int startColumn = 0;
    final int endColumn = 5;
    when(iTextSelection.getStartLine()).thenReturn(startLine);
    when(iTextSelection.getEndLine()).thenReturn(endLine);
    when(iTextSelection.getOffset()).thenReturn(startColumn);
    // hack
    when(iTextSelection.getLength()).thenReturn(endColumn);
    
    assertTrue(adapter.isAdaptable(iTextSelection));
    assertTrue(adapter.isAdaptable(iTextSelection, Snippet.class));

    Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    Application application = AdapterUtil.getOrCreateApplication("project", assessment);
    Controller controller = AdapterUtil.getOrCreateController("controller", application);
    Cursor cursor = adapter.update(iTextSelection, Snippet.class, controller);
    assertTrue(cursor.getCursor() instanceof Snippet);
    Snippet snippet = (Snippet)cursor.getCursor();
    assertNotNull(snippet);
    
    assertEquals(startLine, snippet.getLineStart());
    assertEquals(endLine, snippet.getLineEnd());
    assertEquals(startColumn, snippet.getColumnStart());
    assertEquals(endColumn, snippet.getColumnEnd());
  }

  @Test
  public void shouldReturnCorrectTypesForITextSelection() {
    Adapter adapter = new ISelectionAdapter();
    ITextSelection iTextSelection = Mockito.mock(ITextSelection.class);
    Collection<Class<? extends EObject>> types = adapter.getAdaptableTypes(iTextSelection);
    assertTrue(types.size() == 1);
    assertTrue(types.contains(Snippet.class));
  }

}
