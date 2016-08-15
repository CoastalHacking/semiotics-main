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
package io.opensemantics.semiotics.extension.provider.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.osgi.service.component.annotations.Component;

import io.opensemantics.semiotics.extension.api.Adapter;
import io.opensemantics.semiotics.extension.api.Cursor;
import io.opensemantics.semiotics.extension.provider.EObjectCursor;
import io.opensemantics.semiotics.model.assessment.Application;
import io.opensemantics.semiotics.model.assessment.Assessment;
import io.opensemantics.semiotics.model.assessment.Controller;
import io.opensemantics.semiotics.model.assessment.Resource;
import io.opensemantics.semiotics.model.assessment.Sink;

@Component(
    property="adaptableType=org.eclipse.core.runtime.IAdaptable"
)
public class IAdaptableAdapter implements Adapter {

  @Override
  public List<Class<? extends EObject>> getAdaptableTypes(Object source) {
    List<Class<? extends EObject>> results = new ArrayList<>();
    if (!(source instanceof IAdaptable)) return results;

    final IAdaptable iAdaptable = (IAdaptable) source;
    if (iAdaptable.getAdapter(IProject.class) != null) {
      results.add(Application.class);
    }

    IFile iFile = iAdaptable.getAdapter(IFile.class);
    IJavaElement iJavaElement = iAdaptable.getAdapter(IJavaElement.class);

    // IFile || IJavaElement
    if (iFile != null || iJavaElement != null) {
      results.add(Resource.class);
      results.add(Controller.class);
      results.add(Sink.class);
    }

    return results;
  }

  @Override
  public Cursor update(Object source, Class<?> clazz, EObject selection) {
    if (!isAdaptable(source, clazz)) return null;
    IAdaptable iAdaptable = (IAdaptable)source;
    
    EObject result = null;

    final UpdateHelper helper = new UpdateHelper(iAdaptable);
    final Assessment assessment = AdapterUtil.getSelection(selection, Assessment.class);
    final Application application = AdapterUtil.getOrCreateApplication(helper.getApplicationName(), assessment);

    if (clazz.equals(Application.class)) {
      result = application;
    } else if (clazz.equals(Resource.class)) {
      result = AdapterUtil.getOrCreateResource(helper.getResourceName(), application);
    } else if (clazz.equals(Controller.class)) {
      result = AdapterUtil.getOrCreateController(helper.getChildName(), application);
    } else if (clazz.equals(Sink.class)) {
      result = AdapterUtil.getOrCreateSink(helper.getChildName(), application);
    }

    return new EObjectCursor(result);
  }
  
  private static class UpdateHelper {

    private IFile iFile;
    private IJavaElement iJavaElement;
    private IProject iProject;
    private IResource iResource;

    public void setIFile(IAdaptable adaptable) {
      iFile = adaptable.getAdapter(IFile.class);
    }

    public void setIJavaElement(IAdaptable adaptable) {
      iJavaElement = adaptable.getAdapter(IJavaElement.class);
      if (iJavaElement != null) {
        iProject = iJavaElement.getJavaProject().getProject();
        iResource = iJavaElement.getResource();
      }
    }

    public void setIProject(IAdaptable adaptable) {
      this.iProject = adaptable.getAdapter(IProject.class);
    }

    public void setIResource(IAdaptable adaptable) {
      this.iResource = adaptable.getAdapter(IResource.class);
    }

    private String getIResourceName(String prior) {
      return (iResource != null) ? iResource.getProjectRelativePath().toString() : prior;
    }
    
    private String getIFileName(String prior) {
      return (iFile != null) ? iFile.getName() : prior;
    }
    
    public UpdateHelper(IAdaptable source) {
      super();
      // Least to more specific
      setIProject(source);
      setIResource(source);
      setIFile(source);
      setIJavaElement(source); 
    }

    public String getResourceName() {
      return getIFileName(getIResourceName(""));
    }

    public String getApplicationName() {
      return (iProject != null) ? iProject.getName() : "";
    }
    
    public String getChildName() {
      String results = getIFileName(getIResourceName(""));

      if (iJavaElement != null) {
        results = iJavaElement.getElementName();
        // e.g. class
        IType iType = iJavaElement.getAdapter(IType.class);
        // e.g. some random selection within a class
        ITypeRoot iTypeRoot = iJavaElement.getAdapter(ITypeRoot.class);
        // e.g. method, field
        IMember iMember = iJavaElement.getAdapter(IMember.class);
        if (iType != null) {
          results = iType.getFullyQualifiedName();
        } else if (iTypeRoot != null) {
          IType otherIType = iTypeRoot.findPrimaryType();
          if (otherIType != null) results = otherIType.getFullyQualifiedName();
        } else if (iMember != null) {
          results = iMember.getElementName();
          IType memberIType = iMember.getDeclaringType();
          if (memberIType != null) {
            results = String.format("%s.%s", iMember.getDeclaringType().getFullyQualifiedName(), results);
          }
        }
      }
      return results;
    }
    
    
  }

  @Override
  public boolean isAdaptable(Object source, Class<?> clazz) {
    if (!(source instanceof IAdaptable)) return false;

    final IAdaptable iAdaptable = (IAdaptable) source;
    boolean isIProject = (iAdaptable.getAdapter(IProject.class) != null);
    boolean isIFile = (iAdaptable.getAdapter(IFile.class) != null);
    boolean isJavaElement = (iAdaptable.getAdapter(IJavaElement.class) != null);

    if (clazz == null) return (isIProject || isIFile || isJavaElement);

    // IProject
    if (clazz.equals(Application.class)) return isIProject;
    
    // IFile & IJavaElement
    final boolean isIFileOrIJavaElement = isIFile || isJavaElement;
    if (clazz.equals(Resource.class)) return isIFileOrIJavaElement;
    if (clazz.equals(Controller.class)) return isIFileOrIJavaElement;
    if (clazz.equals(Sink.class)) return isIFileOrIJavaElement;

    return false;
  }

  /*
   * Nothing to republish
   * 
   * (non-Javadoc)
   * @see io.opensemantics.semiotics.extension.api.Adapter#republish(java.lang.Object)
   */
  @Override
  public Collection<Object> republish(Object source) {
    return new ArrayList<>();
  }

}
