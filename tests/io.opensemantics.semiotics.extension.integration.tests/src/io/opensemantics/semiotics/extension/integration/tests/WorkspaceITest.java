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
package io.opensemantics.semiotics.extension.integration.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.eclipse.emf.ecp.core.ECPProject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.opensemantics.semiotics.extension.api.Project;
import io.opensemantics.semiotics.extension.api.Workspace;

public class WorkspaceITest extends AbstractITest {

  private static Workspace workspace;

  @BeforeClass
  public static void beforeClass() {
    workspace = getService(WorkspaceITest.class, Workspace.class);
    workspace.getProjects().clear();
  }

  @AfterClass
  public static void afterClass() {
    workspace = null;
  }

  @Before
  public void setup() {
    assertTrue(workspace.getProjects().isEmpty());
  }

  @Test
  public void shouldLoadSerivces() {
    assertNotNull(workspace);
  }

  @Test
  public void shouldAddProjectOnCreate() throws Exception {
    final String expected = "addCreate";
    // Add ECP Project
    ECPProject ecpProject = projectManager.createProject(provider, expected);
    Collection<Project> projects = workspace.getProjects();
    // Which adds a Project
    assertTrue(projects.size() == 1);
    Project project = projects.iterator().next();
    assertNotNull(project);
    final String actual = project.getName(); 
    assertEquals(expected, actual);
    // Clean-up 
    ecpProject.delete();
  }

  @Test
  public void shouldRemoveProjectOnDelete() throws Exception {
    ECPProject ecpProject = projectManager.createProject(provider, "removeDelete");
    assertTrue(workspace.getProjects().size() == 1);
    ecpProject.delete();
    assertTrue(workspace.getProjects().isEmpty());
  }

  @Test
  public void shouldRemoveOnClose() throws Exception {
    ECPProject ecpProject = projectManager.createProject(provider, "removeClose");
    ecpProject.close();
    assertTrue(workspace.getProjects().isEmpty());
    ecpProject.delete();
  }
  
  @Test
  public void shouldAddonOpen() throws Exception {
    final String expected = "addOpen";
    ECPProject ecpProject = projectManager.createProject(provider, expected);
    ecpProject.close();
    ecpProject.open();
    Collection<Project> projects = workspace.getProjects();
    assertTrue(projects.size() == 1);
    final String actual = projects.iterator().next().getName(); 
    assertEquals(expected, actual);
    ecpProject.delete();
  }
}
