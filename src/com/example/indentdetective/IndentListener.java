package com.example.indentdetective;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
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

    /**
     * Caches the indentation analysis per editor input so that switching to an
     * already-analyzed tab reuses the previous result instead of re-analyzing.
     */
    private final Map<IEditorInput, IndentResult> resultCache = new HashMap<>();

    /**
     * Shared core: analyze the editor's document and apply the detected
     * indentation settings. Used by both the activation-event path and the
     * startup scan over already-open editors.
     */
    public void handlePart(IWorkbenchPart part) {
        if (part instanceof ITextEditor) {
            ITextEditor editor = (ITextEditor) part;
            IEditorInput input = editor.getEditorInput();
            if (input == null) {
                return;
            }

            IDocumentProvider provider = editor.getDocumentProvider();
            if (provider == null) {
                return;
            }

            IDocument document = provider.getDocument(input);
            if (document != null) {
                IndentResult result = getOrCreateResult(input, document);
                applyPreferences(editor, result);
            }
        }
    }

    private IndentResult getOrCreateResult(IEditorInput input, IDocument document) {
        IndentResult cached = resultCache.get(input);
        if (cached == null) {
            cached = IndentDetective.analyze(document);
            resultCache.put(input, cached);
        }
        return cached;
    }

    @Override
    public void partActivated(IWorkbenchPartReference partRef) {
        handlePart(partRef.getPart(false));
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
