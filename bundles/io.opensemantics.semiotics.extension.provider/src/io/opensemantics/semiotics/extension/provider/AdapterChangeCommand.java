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
import org.eclipse.emf.edit.command.ChangeCommand;

import io.opensemantics.semiotics.extension.api.Adapter;
import io.opensemantics.semiotics.extension.api.Cursor;

public class AdapterChangeCommand extends ChangeCommand implements Cursor {

  final private Adapter adapter;
  final private EObject selection;
  final Object source;
  final Class<?> clazz;
  Object cursor;

  public AdapterChangeCommand(Adapter adapter, EObject selection, Object source, Class<?> clazz) {
    super(selection);
    this.adapter = adapter;
    this.selection = selection;
    this.source = source;
    this.clazz = clazz;
  }
  
  public Object getCursor() {
    return cursor;
  }

  @Override
  protected void doExecute() {
    this.cursor = adapter.update(source, clazz, selection).getCursor();
  }

}
