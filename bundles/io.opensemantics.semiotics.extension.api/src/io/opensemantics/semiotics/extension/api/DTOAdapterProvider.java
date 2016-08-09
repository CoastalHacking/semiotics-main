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
package io.opensemantics.semiotics.extension.api;

import java.util.List;

public interface DTOAdapterProvider {

  /**
   * 
   * @param topic  A domain-specific object
   * @param type  A DTO type
   * @return a DTO created from the domain-specific object, null if not adaptable
   */
  DTO adapt(Object source, DTOType type);

  /**
   * 
   * @param topic  A domain-specific object
   * @return list of potential DTO types adaptable from the topic, empty list if none
   */
  List<DTOType> getAdaptableTypes(Object source);

  /**
   * 
   * @param topic  A domain-specific object
   * @param type  A DTO type
   * @return if the object can be adapted to the DTO type
   */
  boolean isAdaptable(Object source, DTOType type);

  default String toTopic(String value) {
    return value.replaceAll("[^\\w-]", "_");
  }

}
