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
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
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
  private static final String testClass = "Foo";
  private static final String testFile = testClass + ".java";
  private static final String testFileResource = "/src/" + testFile;
  private static IProject iProject;
  private static IFile iFile;
  private static IJavaProject iJavaProject;
  private static Adapter adapter;

  @BeforeClass
  public static void beforeClass() {
    adapter = new IAdaptableAdapter();
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    IWorkspaceRoot root = workspace.getRoot();
    iProject = root.getProject(projectName);
    try {
      // Open project
      if (!iProject.exists()) iProject.create(null);
      if (!iProject.isOpen()) iProject.open(null);

      // Add Java nature and create Java project
      IProjectDescription desc = iProject.getDescription();
      desc.setNatureIds(new String[] {
         JavaCore.NATURE_ID});
      iProject.setDescription(desc, null);
      iJavaProject = JavaCore.create(iProject);

      // Add classpath, can be done prior to directories existing
      IClasspathEntry jreEntry = JavaRuntime.getDefaultJREContainerEntry();
      IClasspathEntry[] buildPath = { 
          JavaCore.newSourceEntry(iProject.getFullPath().append("src")), 
          jreEntry};
      // TODO: the code below causes a stack trace; figure out how to trigger it
      iJavaProject.setRawClasspath(buildPath, iProject.getFullPath().append(
              "bin"), null);

      // Create project directories
      IFolder binDir = iProject.getFolder("bin");
      IFolder srcDir = iProject.getFolder("src");
      if (!binDir.exists()) binDir.create(/*force*/IResource.NONE, /*local*/ false, null);
      if (!srcDir.exists()) srcDir.create(/*force*/IResource.NONE, /*local*/ false, null);

      // Add source
      iFile = srcDir.getFile(testFile);
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
    if (iJavaProject != null) {
      try {
        iJavaProject.close();
      } catch (JavaModelException e) {
        e.printStackTrace();
      }
    }
    if (iProject != null) {
      try {
        iProject.close(null);
        iProject.delete(/*deleteContent*/true, /*force*/true, null);
      } catch (CoreException e) {
        e.printStackTrace();
      }
    }
    adapter = null;
  }
  
  @Test
  public void shouldHaveStaticValuesSet() {
    assertNotNull(iProject);
    assertNotNull(iJavaProject);
  }

  @Test
  public void shouldAdaptIProject() {
    assertTrue(adapter.isAdaptable(iProject));
  }
  
  @Test
  public void shouldAdaptIJavaProject() {
    assertTrue(adapter.isAdaptable(iJavaProject));
  }

  @Test
  public void shouldAdaptIProjectAsApplication() {
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
    assertTrue(adapter.isAdaptable(iFile));    
  }

  @Test
  public void shouldAdaptIFileAsResource() {
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
  public void shouldAdaptIJavaElementClassAsResource() throws JavaModelException {
    IJavaElement iJavaElement = iJavaProject.findType(testClass);
    final String expected = "src/Foo.java";
    assertTrue(adapter.isAdaptable(iJavaElement, Resource.class));
    Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    Cursor cursor = adapter.update(iJavaElement, Resource.class, assessment);
    Object cursorValue = cursor.getCursor();
    assertNotNull(cursorValue);
    assertTrue(cursorValue instanceof Resource);
    String actual = ((Resource)cursorValue).getLabel();
    assertEquals(expected, actual);
  }

  @Test
  public void shouldAdaptIJavaElementClassAsController() throws JavaModelException {
    IJavaElement iJavaElement = iJavaProject.findType(testClass);
    final String expected = testClass;
    assertTrue(adapter.isAdaptable(iJavaElement, Controller.class));
    Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    Cursor cursor = adapter.update(iJavaElement, Controller.class, assessment);
    Object cursorValue = cursor.getCursor();
    assertNotNull(cursorValue);
    assertTrue(cursorValue instanceof Controller);
    String actual = ((Controller)cursorValue).getLabel();
    assertEquals(expected, actual);
  }
  
  @Test
  public void shouldAdaptIJavaElementClassAsSink() throws JavaModelException {
    IJavaElement iJavaElement = iJavaProject.findType(testClass);
    final String expected = testClass;
    assertTrue(adapter.isAdaptable(iJavaElement, Sink.class));
    Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    Cursor cursor = adapter.update(iJavaElement, Sink.class, assessment);
    Object cursorValue = cursor.getCursor();
    assertNotNull(cursorValue);
    assertTrue(cursorValue instanceof Sink);
    String actual = ((Sink)cursorValue).getLabel();
    assertEquals(expected, actual);
  }

  @Test
  public void shouldAdaptIMethodAsResource() throws JavaModelException {
    IType iType = (IType)iJavaProject.findType(testClass);
    IMethod iMethod = iType.getMethod("biz", new String[]{});
    // Resources use file names not class / method names
    final String expected = "src/Foo.java"; 
    assertTrue(adapter.isAdaptable(iMethod, Resource.class));
    Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    Cursor cursor = adapter.update(iMethod, Resource.class, assessment);
    Object cursorValue = cursor.getCursor();
    assertNotNull(cursorValue);
    assertTrue(cursorValue instanceof Resource);
    String actual = ((Resource)cursorValue).getLabel();
    assertEquals(expected, actual);
  }

  @Test
  public void shouldAdaptIMethodAsController() throws JavaModelException {
    IType iType = (IType)iJavaProject.findType(testClass);
    String method = "biz";
    IMethod iMethod = iType.getMethod(method, new String[]{});
    final String expected = testClass + "." + method;
    assertTrue(adapter.isAdaptable(iMethod, Controller.class));
    Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    Cursor cursor = adapter.update(iMethod, Controller.class, assessment);
    Object cursorValue = cursor.getCursor();
    assertNotNull(cursorValue);
    assertTrue(cursorValue instanceof Controller);
    String actual = ((Controller)cursorValue).getLabel();
    assertEquals(expected, actual);
  }
  
  @Test
  public void shouldAdaptIMethodAsSink() throws JavaModelException {
    IType iType = (IType)iJavaProject.findType(testClass);
    String method = "biz";
    IMethod iMethod = iType.getMethod(method, new String[]{});
    final String expected = testClass + "." + method;
    assertTrue(adapter.isAdaptable(iMethod, Sink.class));
    Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    Cursor cursor = adapter.update(iMethod, Sink.class, assessment);
    Object cursorValue = cursor.getCursor();
    assertNotNull(cursorValue);
    assertTrue(cursorValue instanceof Sink);
    String actual = ((Sink)cursorValue).getLabel();
    assertEquals(expected, actual);
  }

  @Test
  public void shouldReturnCorrectTypesForIProject() {
    Collection<Class<? extends EObject>> types = adapter.getAdaptableTypes(iProject);
    assertTrue(types.size() == 1);
    assertTrue(types.contains(Application.class));
  }

  @Test
  public void shouldReturnCorrectTypesForIFile() {
    Collection<Class<? extends EObject>> types = adapter.getAdaptableTypes(iFile);
    assertTrue(types.size() == 3);
    assertTrue(types.contains(Controller.class));
    assertTrue(types.contains(Resource.class));
    assertTrue(types.contains(Sink.class));
  }
  
  @Test
  public void shouldReturnCorrectTypesForIJavaElementClass() throws JavaModelException {
    IJavaElement iJavaElement = iJavaProject.findType(testClass);
    Collection<Class<? extends EObject>> types = adapter.getAdaptableTypes(iJavaElement);
    assertTrue(types.size() == 3);
    assertTrue(types.contains(Controller.class));
    assertTrue(types.contains(Resource.class));
    assertTrue(types.contains(Sink.class));
  }
  
  @Test
  public void shouldReturnCorrectTypesForIJavaElementMethod() throws JavaModelException {
    IType iType = (IType)iJavaProject.findType(testClass);
    final String expected = "biz";
    IMethod iMethod = iType.getMethod(expected, new String[]{});
    Collection<Class<? extends EObject>> types = adapter.getAdaptableTypes(iMethod);
    assertTrue(types.size() == 3);
    assertTrue(types.contains(Controller.class));
    assertTrue(types.contains(Resource.class));
    assertTrue(types.contains(Sink.class));
  }
}
