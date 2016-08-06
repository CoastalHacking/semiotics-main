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
package io.opensemantics.semiotics.extension.provider.api;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.regex.Pattern;

import org.osgi.service.event.EventConstants;

import io.opensemantics.semiotics.extension.api.DTOType;

public final class PostEventUtil {
  
  private static final String TOPIC_BASE = "io/opensemantics/semiotics/extension/api/event/Post";
  public static final String EVENT_TOPIC_BASE = EventConstants.EVENT_TOPIC + "=" + PostEventUtil.TOPIC_BASE + "/";
  public static final String PROP_DATA = "sourceData";
  // Topic can only contain [0-9A-Za-z_-]
  private static final Pattern TOPIC_PATTERN = Pattern.compile("[\\w-]+");

  /**
   * 
   * @param topic A valid OSGi sub-topic name
   */
  public final static void validateTopic(String source) {
    if (!TOPIC_PATTERN.matcher(source).matches()) {
      throw new IllegalArgumentException("Invalid topic name for " + source);
    }
  }

  
  public final static String getTopic(String source) {
    validateTopic(source);
    return String.format("%s/%s", TOPIC_BASE, source);
  }

  public final static Dictionary<String, String> getProperties(String source, DTOType dtoType) {
    Hashtable<String, String> properties = new Hashtable<String, String>();
    properties.put(EventConstants.EVENT_TOPIC, getTopic(source));
    return properties;
  }
}
