/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.idea.context;

import java.util.Optional;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.context.UIRegion;

import com.intellij.openapi.application.ex.ApplicationUtil;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.SelectionModel;

/**
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class UIRegionImpl implements UIRegion<Resource<?>>
{
   private final Resource<?> resource;
   private final Document document;
   private final SelectionModel selectionModel;

   public UIRegionImpl(Resource<?> resource, Document document, SelectionModel selectionModel)
   {
      this.resource = resource;
      this.document = document;
      this.selectionModel = selectionModel;
   }

   @Override
   public int getEndLine()
   {
      return document.getLineNumber(getEndPosition()) + 1;
   }

   @Override
   public int getStartLine()
   {
      return document.getLineNumber(getStartPosition()) + 1;
   }

   @Override
   public int getStartPosition()
   {
      return ApplicationUtil.tryRunReadAction(() -> selectionModel.getSelectionStart());
   }

   @Override
   public int getEndPosition()
   {
      return ApplicationUtil.tryRunReadAction(() -> selectionModel.getSelectionEnd());
   }

   @Override
   public Optional<String> getText()
   {
      String text = ApplicationUtil.tryRunReadAction(() -> selectionModel.getSelectedText());
      return Optional.ofNullable(text);
   }

   @Override
   public Resource<?> getResource()
   {
      return resource;
   }

   @Override
   public String toString()
   {
      return "UIRegionImpl [getEndLine()=" + getEndLine() + ", getStartLine()=" + getStartLine()
               + ", getStartPosition()=" + getStartPosition() + ", getEndPosition()=" + getEndPosition()
               + ", getText()=" + getText() + ", getResource()=" + getResource() + "]";
   }
}
