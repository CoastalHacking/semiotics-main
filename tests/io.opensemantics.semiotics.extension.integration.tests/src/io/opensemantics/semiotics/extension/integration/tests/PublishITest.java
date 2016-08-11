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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecp.changebroker.spi.ChangeBroker;
import org.eclipse.emf.ecp.core.ECPProject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import io.opensemantics.semiotics.extension.api.event.Publish;
import io.opensemantics.semiotics.model.assessment.Application;
import io.opensemantics.semiotics.model.assessment.Applications;
import io.opensemantics.semiotics.model.assessment.Assessment;
import io.opensemantics.semiotics.model.assessment.AssessmentPackage;

public class PublishITest extends AbstractECPITest {

  private static Publish publisher;
  // Tycho needs this dependency explicitly called out since it's
  // not used elsewhere.
  private static ChangeBroker changeBroker;

  @BeforeClass
  public static void beforeClass() {
    publisher = getService(PublishITest.class, Publish.class);
    changeBroker = getService(PublishITest.class, ChangeBroker.class);
  }

  @AfterClass
  public static void afterClass() {
    publisher = null;
    changeBroker = null;
  }

  @Test
  public void shouldLoadPublish() {
    assertNotNull(publisher);
    assertNotNull(changeBroker);
  }

  @Test
  public void shouldPublish() throws Exception {
    final String projectName = "shouldPublish";

    // Setup a latch that gets decremented when we observe a
    // change to the underlying Application. We want to know
    // when the application is added to the applications containment ref.
    // This is about as agnostic and clean as I can think...
    final CountDownLatch latch = new CountDownLatch(1);
    final ApplicationChangeObserver changeObserver = new ApplicationChangeObserver(latch);
    EReference eRef = AssessmentPackage.eINSTANCE.getApplications_Applications();
    changeBroker.subscribeToFeature(changeObserver, eRef);

    // Create ECP project
    ECPProject ecpProject = projectManager.createProject(provider, projectName);
    assertTrue(ecpProject.isOpen());
    
    // Mock IProject
    String expected = "org.example.foo";
    IProject iProject = Mockito.mock(IProject.class);
    when(iProject.getName()).thenReturn(expected);
    
    // Publish and wait
    publisher.asAssessment(projectName, iProject);
    assertTrue(latch.await(30, TimeUnit.SECONDS));

    // Verify and assert
    EList<Object> contents = ecpProject.getContents();
    assertFalse(contents.isEmpty());

    Object root = contents.get(0);
    assertTrue(root instanceof Assessment);

    Assessment assessment = (Assessment)root;
    Applications applications = assessment.getApplications();
    assertNotNull(applications);
    
    EList<Application> apps = applications.getApplications();
    assertFalse(apps.isEmpty());
    Application app = apps.get(0);
    assertNotNull(app);
    String actual = app.getLabel();
    assertEquals(expected, actual);

    // Cleanup
    ecpProject.delete();
    changeBroker.unsubsribe(changeObserver);
  }

}
