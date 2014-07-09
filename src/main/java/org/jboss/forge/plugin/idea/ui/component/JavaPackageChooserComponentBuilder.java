/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import com.intellij.psi.JavaCodeFragment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.EditorTextFieldWithBrowseButton;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.plugin.idea.util.IDEUtil;

/**
 * @author Adam Wyłuda
 */
public class JavaPackageChooserComponentBuilder extends AbstractJavaChooserComponentBuilder
{
    @Override
    protected EditorTextFieldWithBrowseButton createEditorField()
    {
        // TODO Browse packages
        return new EditorTextFieldWithBrowseButton(IDEUtil.projectFromContext(context),
                true,
                new JavaCodeFragment.VisibilityChecker()
                {
                    @Override
                    public Visibility isDeclarationVisible(PsiElement declaration, PsiElement place)
                    {
                        if (declaration instanceof PsiPackage)
                        {
                            return Visibility.VISIBLE;
                        }
                        return Visibility.NOT_VISIBLE;
                    }
                });
    }

    @Override
    protected Class<?> getProducedType()
    {
        return String.class;
    }

    @Override
    protected String getSupportedInputType()
    {
        return InputType.JAVA_PACKAGE_PICKER;
    }
}
