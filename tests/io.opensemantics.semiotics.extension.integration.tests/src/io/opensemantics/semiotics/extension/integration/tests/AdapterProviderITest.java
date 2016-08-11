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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.core.ECPProject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.ServiceRegistration;

import io.opensemantics.semiotics.extension.api.Adapter;
import io.opensemantics.semiotics.extension.api.AdapterProvider;
import io.opensemantics.semiotics.extension.api.Cursor;

public class AdapterProviderITest extends AbstractECPITest {

  private static AdapterProvider adapterProvider;
  
  @BeforeClass
  public static void beforeClass() {
    adapterProvider = getService(AdapterProviderITest.class, AdapterProvider.class);
  }

  @AfterClass
  public static void afterClass() {
    adapterProvider = null;
  }

  @Test
  public void shouldLoadAdapterProvider() {
    assertNotNull(adapterProvider);
  }

  /*
   * We mock an adapter and register it as an OSGi service.
   * We then call the adapter via the provider and ensure its
   * methods are called.
   */
  @Test
  public void shouldAdapt() throws Exception {
    // Create our mocks
    Adapter mockAdapter = mock(Adapter.class);
    when(mockAdapter.isAdaptable(isA(MockObject.class))).thenReturn(true);
    when(mockAdapter.isAdaptable(isA(MockObject.class), any())).thenReturn(true);
    final Collection<Class<? extends EObject>> expectedTypes = new ArrayList<>();
    when(mockAdapter.getAdaptableTypes(isA(MockObject.class))).thenReturn(expectedTypes); 
    final CountDownLatch latch = new CountDownLatch(1);
    when(mockAdapter.update(isA(MockObject.class), any(), any())).thenAnswer(new LatchCursorAnswer(latch));

    // Register mock
    ServiceRegistration<Adapter> adapterRegister =
        registerService(
            AdapterProviderITest.class,
            Adapter.class,
            mockAdapter,
            new Hashtable<String, String>()
        );

    // Create ECP project (leaks implementation details)
    ECPProject ecpProject = projectManager.createProject(provider, "TestProject");
    assertTrue(ecpProject.isOpen());

    // Call service
    final MockObject mockObject = new MockObject();
    adapterProvider.getAdaptableTypes(mockObject);
    adapterProvider.isAdaptable(mockObject);
    adapterProvider.publish(mockObject, EObject.class);
    // async, wait for latch
    assertTrue(latch.await(5, TimeUnit.SECONDS));

    // Verify
    verify(mockAdapter, atLeastOnce()).getAdaptableTypes(isA(MockObject.class));
    verify(mockAdapter, atLeastOnce()).isAdaptable(isA(MockObject.class));
    verify(mockAdapter, atLeastOnce()).update(isA(MockObject.class), any(), any());

    // Cleanup
    ecpProject.delete();
    adapterRegister.unregister();

  }

  private static class MockObject {
    
  }
  
  private static class LatchCursorAnswer implements Answer<Cursor> {
    private CountDownLatch latch;

    public LatchCursorAnswer(CountDownLatch latch) {
      super();
      this.latch = latch;
    }

    @Override
    public Cursor answer(InvocationOnMock invocation) throws Throwable {
      latch.countDown();
      final Cursor mockCursor = mock(Cursor.class);
      when(mockCursor.getCursor()).thenReturn("cursor");
      return mockCursor;
    }

  }
}
