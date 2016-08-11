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

import org.junit.Test;

import io.opensemantics.semiotics.extension.provider.adapter.AdapterUtil;
import io.opensemantics.semiotics.model.assessment.Application;
import io.opensemantics.semiotics.model.assessment.Assessment;
import io.opensemantics.semiotics.model.assessment.AssessmentFactory;
import io.opensemantics.semiotics.model.assessment.Controller;
import io.opensemantics.semiotics.model.assessment.Resource;
import io.opensemantics.semiotics.model.assessment.Sink;
import io.opensemantics.semiotics.model.assessment.Snippet;

public class AdapterUtilTest {

  @Test
  public void shouldCreateApplication() {
    Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    assertNull(assessment.getApplications());
    AdapterUtil.getOrCreateApplication("shouldCreateApplication", assessment);
    // implicit assert not null checks
    assertTrue(assessment.getApplications().getApplications().size() == 1);
  }

  @Test
  public void shouldGetApplication() {
    Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    assessment.setApplications(AssessmentFactory.eINSTANCE.createApplications());
    final Application expected = AssessmentFactory.eINSTANCE.createApplication();
    final String label = "shouldGetApplication";
    expected.setLabel(label);
    assessment.getApplications().getApplications().add(expected);
    assertTrue(assessment.getApplications().getApplications().size() == 1);
    final Application actual = AdapterUtil.getOrCreateApplication(label, assessment);
    assertEquals(expected, actual);
  }

  @Test
  public void shouldCreateResource() {
    Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    Application application = AdapterUtil.getOrCreateApplication("test", assessment);
    assertNull(application.getResources());
    AdapterUtil.getOrCreateResource("shouldCreateResource", application);
    assertTrue(application.getResources().getResources().size() == 1);
  }

  @Test
  public void shouldGetResource() {
    final Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    final Application application = AdapterUtil.getOrCreateApplication("test", assessment);
    application.setResources(AssessmentFactory.eINSTANCE.createResources());
    final Resource expected = AssessmentFactory.eINSTANCE.createResource();
    final String label = "shouldGetResource";
    expected.setLabel(label);
    application.getResources().getResources().add(expected);
    assessment.getApplications().getApplications().add(application);
    assertTrue(assessment.getApplications().getApplications().get(0).getResources().getResources().size() == 1);
    final Resource actual = AdapterUtil.getOrCreateResource(label, application);
    assertEquals(expected, actual);
  }

  @Test
  public void shouldCreateController() {
    final Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    final Application application = AdapterUtil.getOrCreateApplication("test", assessment);
    assertNull(application.getControllers());
    AdapterUtil.getOrCreateController("shouldCreateController", application);
    assertTrue(application.getControllers().getControllers().size() == 1);
  }

  @Test
  public void shouldGetController() {
    final Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    final Application application = AdapterUtil.getOrCreateApplication("test", assessment);
    application.setControllers(AssessmentFactory.eINSTANCE.createControllers());
    final Controller expected = AssessmentFactory.eINSTANCE.createController();
    final String label = "shouldGetController";
    expected.setLabel(label);
    application.getControllers().getControllers().add(expected);
    assessment.getApplications().getApplications().add(application);
    assertTrue(assessment.getApplications().getApplications().get(0).getControllers().getControllers().size() == 1);
    final Controller actual = AdapterUtil.getOrCreateController(label, application);
    assertEquals(expected, actual);
  }

  @Test
  public void shouldCreateSink() {
    final Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    final Application application = AdapterUtil.getOrCreateApplication("test", assessment);
    assertNull(application.getSinks());
    AdapterUtil.getOrCreateSink("shouldCreateSink", application);
    assertTrue(application.getSinks().getSinks().size() == 1);
  }

  @Test
  public void shouldGetSink() {
    final Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    final Application application = AdapterUtil.getOrCreateApplication("test", assessment);
    application.setSinks(AssessmentFactory.eINSTANCE.createSinks());
    final Sink expected = AssessmentFactory.eINSTANCE.createSink();
    final String label = "shouldGetSink";
    expected.setLabel(label);
    application.getSinks().getSinks().add(expected);
    assessment.getApplications().getApplications().add(application);
    assertTrue(assessment.getApplications().getApplications().get(0).getSinks().getSinks().size() == 1);
    final Sink actual = AdapterUtil.getOrCreateSink(label, application);
    assertEquals(expected, actual);
  }

  @Test
  public void shouldCreateSnippet() {
    final Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    final Application application = AdapterUtil.getOrCreateApplication("test", assessment);
    final Controller controller = AdapterUtil.getOrCreateController("controller", application);
    assertTrue(controller.getChildren().size() == 0);
    AdapterUtil.getOrCreateSnippet("stuff", 9, 10, 0, 5, controller);
    assertTrue(controller.getChildren().size() == 1);
    
  }

  @Test
  public void shouldGetSnippet() {
    final Assessment assessment = AssessmentFactory.eINSTANCE.createAssessment();
    final Application application = AdapterUtil.getOrCreateApplication("test", assessment);
    final Controller controller = AdapterUtil.getOrCreateController("controller", application);
    final String contents = "stuff";
    final Snippet expected = AssessmentFactory.eINSTANCE.createSnippet();
    expected.setContents(contents);
    controller.getChildren().add(expected);
    assertTrue(assessment.getApplications().getApplications().get(0).getControllers().getControllers().get(0).getChildren().size()==1);
    final Snippet actual = AdapterUtil.getOrCreateSnippet(contents, 0,0,0,0, controller);
    assertEquals(expected, actual);
  }
}
