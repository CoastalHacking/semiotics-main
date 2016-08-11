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

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

public interface Adapter {

  /**
   * Updates an assessment based on the given source and desired type
   * 
   * @param source  A domain-specific object
   * @param type  A class type
   * @param eObject  A non-null EObject
   * @return a non-null object to be used as a cursor
   * @throws AdapterException if cannot adapt input
   */
  Cursor update(Object source, Class<?> clazz, EObject selection) throws AdapterException;

  /**
   * 
   * @param source  A domain-specific object
   * @return list of potential classes adaptable from the domain-specific object, empty list if none
   */
  Collection<Class<? extends EObject>> getAdaptableTypes(Object source);

  /**
   * 
   * @param source  A domain-specific object
   * @return if the object can be adapted to some type
   */
  default boolean isAdaptable(Object source) {
    return isAdaptable(source, null);
  }

  /**
   * 
   * @param source  A domain-specific object
   * @param clazz  A class type
   * @return if the object can be adapted to some type
   */
  boolean isAdaptable(Object source, Class<?> clazz);

}
