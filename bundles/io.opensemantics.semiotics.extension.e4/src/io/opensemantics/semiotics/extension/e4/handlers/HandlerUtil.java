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
package io.opensemantics.semiotics.extension.e4.handlers;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;

public class HandlerUtil {

  public static IFile getIFileFromIPartService(IPartService iPartService) {
    if (iPartService != null) {
      IWorkbenchPart workbenchPart = iPartService.getActivePart();
      if (workbenchPart instanceof AbstractTextEditor) {
        AbstractTextEditor textEditor = (AbstractTextEditor)workbenchPart;
        IFileEditorInput iFileEditor = textEditor.getEditorInput().getAdapter(IFileEditorInput.class);
        if (iFileEditor != null) {
          return iFileEditor.getFile();
        }
      }
    }
    return null;
  }

}
