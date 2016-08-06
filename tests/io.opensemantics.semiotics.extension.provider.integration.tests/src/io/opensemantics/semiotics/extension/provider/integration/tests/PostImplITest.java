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
package io.opensemantics.semiotics.extension.provider.integration.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Dictionary;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventHandler;

import io.opensemantics.semiotics.extension.api.DTO;
import io.opensemantics.semiotics.extension.api.DTOType;
import io.opensemantics.semiotics.extension.api.event.Post;
import io.opensemantics.semiotics.extension.integration.tests.AbstractITest;
import io.opensemantics.semiotics.extension.integration.tests.event.CountDownLatchEventHandler;
import io.opensemantics.semiotics.extension.provider.api.PostEventUtil;

public class PostImplITest extends AbstractITest {

  private static Post postService;
  
  @BeforeClass
  public static void beforeClass() {
    postService = getService(PostImplITest.class, Post.class);
  }
  
  @AfterClass
  public static void afterClass() {
    postService = null;
  }

  @Test
  public void shouldBind() {
    assertNotNull(postService);
  }
  
  @Test
  public void subscriberShouldSeePost() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    final String source = "source_class";
    final DTOType dtoType = DTOType.APPLICATION;

    final DTO dto = new DTO() {private static final long serialVersionUID = 1L;};
    dto.type = dtoType;
    dto.topic = source;
    
    // Register our handler
    final CountDownLatchEventHandler eventHandler = new CountDownLatchEventHandler(latch);
    Dictionary<String, String> properties = PostEventUtil.getProperties(source, dtoType);
    final ServiceRegistration<EventHandler> registration = AbstractITest.registerService(PostImplITest.class, EventHandler.class, eventHandler, properties);

    // Post to service, with the assumption under the hood it's using EventAdmin to post
    postService.add(dto);

    // See if our latch decreases
    assertTrue(latch.await(30, TimeUnit.SECONDS));

    // Unregister our handler
    registration.unregister();
    
  }

  @Test
  public void subscriberShouldNotSeePostWrongType() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    final String someSource = "some_source";
    final DTO dto = new DTO() {private static final long serialVersionUID = 1L;};
    dto.type = DTOType.APPLICATION;
    dto.topic = someSource;    
    
    // Register our handler
    final CountDownLatchEventHandler eventHandler = new CountDownLatchEventHandler(latch);
    Dictionary<String, String> properties = PostEventUtil.getProperties(someSource, DTOType.SOURCE);
    final ServiceRegistration<EventHandler> registration = AbstractITest.registerService(PostImplITest.class, EventHandler.class, eventHandler, properties);

    // Post to service, with the assumption under the hood it's using EventAdmin to post
    postService.add(dto);

    // Our latch shouldn't see anything. Setting to 1 second because
    // it won't pass, so no point in waiting longer
    assertFalse(latch.await(1, TimeUnit.SECONDS));

    // Unregister our handler
    registration.unregister();
    
  }


  @Test
  public void subscriberShouldNotSeePostWrongSource() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    final DTOType dtoType = DTOType.SOURCE;
    final DTO dto = new DTO() {private static final long serialVersionUID = 1L;};
    dto.type = dtoType;
    dto.topic = "some_source";

    // Register our handler
    final CountDownLatchEventHandler eventHandler = new CountDownLatchEventHandler(latch);
    Dictionary<String, String> properties = PostEventUtil.getProperties("different_source", dtoType);
    final ServiceRegistration<EventHandler> registration = AbstractITest.registerService(PostImplITest.class, EventHandler.class, eventHandler, properties);

    // Post to service, with the assumption under the hood it's using EventAdmin to post
    postService.add(dto);

    // Our latch shouldn't see anything. Setting to 1 second because
    // it won't pass, so no point in waiting longer
    assertFalse(latch.await(1, TimeUnit.SECONDS));

    // Unregister our handler
    registration.unregister();
    
  }
}
