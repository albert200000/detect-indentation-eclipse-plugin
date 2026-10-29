package com.example.indentdetective;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Listens for editor activation/open events, runs the indentation analysis on
 * the active document and applies the detected indentation settings to the editor.
 */
public class IndentListener implements IPartListener2 {

    @Override
    public void partActivated(IWorkbenchPartReference partRef) {
        IWorkbenchPart part = partRef.getPart(false);
        if (part instanceof ITextEditor) {
            ITextEditor editor = (ITextEditor) part;
            IDocumentProvider provider = editor.getDocumentProvider();

            if (provider == null || editor.getEditorInput() == null) {
                return;
            }

            IDocument document = provider.getDocument(editor.getEditorInput());
            if (document != null) {
                IndentResult result = IndentDetective.analyze(document);
                applyPreferences(editor, result);
            }
        }
    }

    private void applyPreferences(ITextEditor editor, IndentResult result) {
        // Apply the detected indentation settings using the shared EditorsUI preference store.
        IPreferenceStore store = EditorsUI.getPreferenceStore();
        if (store == null) {
            return;
        }

        // Use valid Eclipse AbstractDecoratedTextEditorPreferenceConstants
        store.setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH, result.getTabWidth());
        store.setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS, result.isUseSpaces());
    }

    @Override public void partOpened(IWorkbenchPartReference partRef) {}
    @Override public void partClosed(IWorkbenchPartReference partRef) {}
    @Override public void partDeactivated(IWorkbenchPartReference partRef) {}
    @Override public void partBroughtToTop(IWorkbenchPartReference partRef) {}
    @Override public void partHidden(IWorkbenchPartReference partRef) {}
    @Override public void partVisible(IWorkbenchPartReference partRef) {}
    @Override public void partInputChanged(IWorkbenchPartReference partRef) {}
}