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

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class DTO implements Serializable {

  /**
   * 
   * @return The supported DTO type
   */
  public DTOType type;

  /**
   *
   * @return An implementation-specific value expected to match the regex [\\w-]+
   */
  public String topic;
}
