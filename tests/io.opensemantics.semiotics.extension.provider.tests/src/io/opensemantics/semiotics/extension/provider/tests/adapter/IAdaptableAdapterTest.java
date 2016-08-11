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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.opensemantics.semiotics.extension.api.Adapter;
import io.opensemantics.semiotics.extension.api.Cursor;
import io.opensemantics.semiotics.extension.provider.adapter.IAdaptableAdapter;
import io.opensemantics.semiotics.model.assessment.Application;
import io.opensemantics.semiotics.model.assessment.Assessment;
import io.opensemantics.semiotics.model.assessment.AssessmentFactory;
import io.opensemantics.semiotics.model.assessment.Controller;
import io.opensemantics.semiotics.model.assessment.Resource;
import io.opensemantics.semiotics.model.assessment.Sink;

/*
 * Run via a headless application configuration
 */
public class IAdaptableAdapterTest {
  
  private static final String projectName = "project";
  private static final String testFile = "foo.txt";
  private static final String testFileResource = "/src/io/opensemantics/semiotics/extension/provider/tests/adapter/" + testFile; 
  private static IProject iProject;
  private static IFile iFile;

  @BeforeClass
  public static void beforeClass() {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    IWorkspaceRoot root = workspace.getRoot();
    iProject = root.getProject(projectName);
    try {
      if (!iProject.exists()) iProject.create(null);
      if (!iProject.isOpen()) iProject.open(null);
      iFile = iProject.getFile(testFile);
      if (!iFile.exists()) {
        InputStream source = IAdaptableAdapterTest.class.getResourceAsStream(testFileResource);
        iFile.create(source, IResource.NONE, null);
      }
    } catch (CoreException e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @AfterClass
  public static void afterClass() {
    if (iProject != null) {
      try {
        iProject.close(null);
        iProject.delete(/*deleteContent*/true, /*force*/true, null);
      } catch (CoreException e) {
      }
    }
  }
  
  @Test
  public void shouldHaveIProject() {
    assertNotNull(iProject);
  }

  @Test
  public void shouldAdaptIProject() {
    Adapter adapter = new IAdaptableAdapter();
    assertTrue(adapter.isAdaptable(iProject));
  }
  
  @Test
  public void shouldAdaptIProjectAsApplication() {
    Adapter adapter = new IAdaptableAdapter();

    final String expected = projectName;
    assertTrue(adapter.isAdaptable(iProject, Application.class));
    Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    Cursor cursor = adapter.update(iProject, Application.class, assessment);
    Object cursorValue = cursor.getCursor();
    assertNotNull(cursorValue);
    assertTrue(cursorValue instanceof Application);
    String actual = ((Application)cursorValue).getLabel();
    assertEquals(expected, actual);
  }
  
  @Test
  public void shouldAdaptIFile() {
    Adapter adapter = new IAdaptableAdapter();
    assertTrue(adapter.isAdaptable(iFile));    
  }

  @Test
  public void shouldAdaptIFileAsResource() {
    Adapter adapter = new IAdaptableAdapter();

    final String expected = testFile;
    assertTrue(adapter.isAdaptable(iFile, Resource.class));
    Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    Cursor cursor = adapter.update(iFile, Resource.class, assessment);
    Object cursorValue = cursor.getCursor();
    assertNotNull(cursorValue);
    assertTrue(cursorValue instanceof Resource);
    String actual = ((Resource)cursorValue).getLabel();
    assertEquals(expected, actual);
  }
  
  @Test
  public void shouldAdaptIFileAsController() {
    Adapter adapter = new IAdaptableAdapter();

    final String expected = testFile;
    assertTrue(adapter.isAdaptable(iFile, Controller.class));
    Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    Cursor cursor = adapter.update(iFile, Controller.class, assessment);
    Object cursorValue = cursor.getCursor();
    assertNotNull(cursorValue);
    assertTrue(cursorValue instanceof Controller);
    String actual = ((Controller)cursorValue).getLabel();
    assertEquals(expected, actual);
  }
  
  @Test
  public void shouldAdaptIFileAsSink() {
    Adapter adapter = new IAdaptableAdapter();

    final String expected = testFile;
    assertTrue(adapter.isAdaptable(iFile, Sink.class));
    Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    Cursor cursor = adapter.update(iFile, Sink.class, assessment);
    Object cursorValue = cursor.getCursor();
    assertNotNull(cursorValue);
    assertTrue(cursorValue instanceof Sink);
    String actual = ((Sink)cursorValue).getLabel();
    assertEquals(expected, actual);
  }
  
  @Test
  public void shouldReturnCorrectTypesForIProject() {
    Adapter adapter = new IAdaptableAdapter();
    Collection<Class<? extends EObject>> types = adapter.getAdaptableTypes(iProject);
    assertTrue(types.size() == 1);
    assertTrue(types.contains(Application.class));
  }

  @Test
  public void shouldReturnCorrectTypesForIFile() {
    Adapter adapter = new IAdaptableAdapter();
    Collection<Class<? extends EObject>> types = adapter.getAdaptableTypes(iFile);
    assertTrue(types.size() == 3);
    assertTrue(types.contains(Controller.class));
    assertTrue(types.contains(Resource.class));
    assertTrue(types.contains(Sink.class));
  }
}
