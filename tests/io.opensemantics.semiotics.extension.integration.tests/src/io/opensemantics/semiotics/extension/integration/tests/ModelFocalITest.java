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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.ServiceRegistration;

import io.opensemantics.semiotics.extension.api.ModelFocal;
import io.opensemantics.semiotics.extension.api.ModelFocalUpdateHandler;

public class ModelFocalITest extends AbstractITest {

  private static ModelFocal focusService;
  
  @BeforeClass
  public static void beforeClass() {
    focusService = getService(ModelFocalITest.class, ModelFocal.class);
  }
  
  @AfterClass
  public static void afterClass() {
    focusService = null;
  }

  @Test
  public void shouldBind() {
    assertNotNull(focusService);
  } 
  
  @Test
  public void getShouldEqualSet() {
    final Object expected = new Object();
    focusService.setFocus(expected);
    final Object actual = focusService.getFocus();
    assertEquals(expected, actual);
  }

  @Test
  public void shouldNotifyMultipleHandlers() {
    // Register test handlers
    final Handler handlerOne = new Handler();
    ServiceRegistration<ModelFocalUpdateHandler> registrationOne = setService(ModelFocalITest.class, ModelFocalUpdateHandler.class, handlerOne);
    final Handler handlerTwo = new Handler();
    ServiceRegistration<ModelFocalUpdateHandler> registrationTwo = setService(ModelFocalITest.class, ModelFocalUpdateHandler.class, handlerTwo);

    final Object expected = new Object();
    focusService.setFocus(expected);

    assertEquals(expected, handlerOne.actual());
    assertEquals(expected, handlerTwo.actual());

    // Unregister test handlers
    registrationOne.unregister();
    registrationTwo.unregister();
  }

  private class Handler implements ModelFocalUpdateHandler {

    private Object actual;

    @Override
    public void onUpdate(Object model) {
      this.actual = model;
    }

    public Object actual() {
      return actual;
    }
  }
}
