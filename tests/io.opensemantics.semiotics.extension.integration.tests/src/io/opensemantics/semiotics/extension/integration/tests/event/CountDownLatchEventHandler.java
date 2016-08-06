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
package io.opensemantics.semiotics.extension.integration.tests.event;

import java.util.concurrent.CountDownLatch;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class CountDownLatchEventHandler implements EventHandler {

  private final CountDownLatch latch;
  
  public CountDownLatchEventHandler(CountDownLatch latch) {
    this.latch = latch;
  }

  @Override
  public void handleEvent(Event event) {
    latch.countDown();
  }

}
