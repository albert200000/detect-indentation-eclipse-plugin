package com.example.indentdetective;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "com.example.indentdetective";
    private static Activator plugin;
    private IndentListener indentListener;
    private IWindowListener windowListener;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        indentListener = new IndentListener();
    }

    void registerListeners() {
        IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null) return;

        // 1. Listen for future windows opening/closing
        windowListener = new IWindowListener() {
            @Override
            public void windowActivated(IWorkbenchWindow window) {}

            @Override
            public void windowDeactivated(IWorkbenchWindow window) {}

            @Override
            public void windowClosed(IWorkbenchWindow window) {
                if (window.getPartService() != null) {
                    window.getPartService().removePartListener(indentListener);
                }
            }

            @Override
            public void windowOpened(IWorkbenchWindow window) {
                if (window.getPartService() != null) {
                    window.getPartService().addPartListener(indentListener);
                }
            }
        };
        workbench.addWindowListener(windowListener);

        // 2. Attach to all *already existing* windows right now
        for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
            if (window.getPartService() != null) {
                window.getPartService().addPartListener(indentListener);
            }
            // Cover the cold-start case: editors already open at startup were
            // created+activated *before* this listener was attached, so no
            // partActivated event fired for them. Scan them directly.
            for (IWorkbenchPage page : window.getPages()) {
                for (IEditorPart editor : page.getEditors()) {
                    if (editor instanceof ITextEditor) {
                        indentListener.handlePart(editor);
                    }
                }
            }
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (PlatformUI.isWorkbenchRunning()) {
            IWorkbench workbench = PlatformUI.getWorkbench();
            if (workbench != null) {
                // Remove window listener
                if (windowListener != null) {
                    workbench.removeWindowListener(windowListener);
                }

                // Remove part listener from all open windows
                for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
                    if (window.getPartService() != null && indentListener != null) {
                        window.getPartService().removePartListener(indentListener);
                    }
                }
            }
        }
        plugin = null;
        super.stop(context);
    }

    public static Activator getDefault() {
        return plugin;
    }
}