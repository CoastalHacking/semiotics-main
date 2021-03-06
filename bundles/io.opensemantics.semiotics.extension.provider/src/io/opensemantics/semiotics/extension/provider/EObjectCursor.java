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
package io.opensemantics.semiotics.extension.provider;

import org.eclipse.emf.ecore.EObject;

import io.opensemantics.semiotics.extension.api.Cursor;

public class EObjectCursor implements Cursor {

  private EObject eObject;

  public EObjectCursor(EObject eObject) {
    this.eObject = eObject;
  }

  @Override
  public Object getCursor() {
    return eObject;
  }

}
