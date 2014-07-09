/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import com.intellij.execution.ui.ClassBrowser;
import com.intellij.ide.util.ClassFilter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaCodeFragment;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.EditorTextFieldWithBrowseButton;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.plugin.idea.util.IDEUtil;

/**
 * @author Adam Wyłuda
 */
public class JavaClassChooserComponentBuilder extends AbstractJavaChooserComponentBuilder
{
    @Override
    @SuppressWarnings("unchecked")
    protected EditorTextFieldWithBrowseButton createEditorField()
    {
        final Project project = IDEUtil.projectFromContext(context);
        EditorTextFieldWithBrowseButton editorField = new EditorTextFieldWithBrowseButton(
                project,
                true,
                new JavaCodeFragment.VisibilityChecker()
                {
                    @Override
                    public Visibility isDeclarationVisible(PsiElement declaration, PsiElement place)
                    {
                        if (declaration instanceof PsiClass)
                        {
                            return Visibility.VISIBLE;
                        }
                        return Visibility.NOT_VISIBLE;
                    }
                });

        new ClassBrowser(project, "Select a Java class")
        {

            @Override
            protected ClassFilter.ClassFilterWithScope getFilter() throws NoFilterException
            {
                return new ClassFilter.ClassFilterWithScope()
                {
                    public GlobalSearchScope getScope()
                    {
                        return GlobalSearchScope.allScope(project);
                    }

                    public boolean isAccepted(final PsiClass aClass)
                    {
                        return true;
                    }
                };
            }

            @Override
            protected PsiClass findClass(String className)
            {
                return null;
            }
        }.setField(editorField);

        return editorField;
    }


    @Override
    protected Class<?> getProducedType()
    {
        return String.class;
    }

    @Override
    protected String getSupportedInputType()
    {
        return InputType.JAVA_CLASS_PICKER;
    }
}
