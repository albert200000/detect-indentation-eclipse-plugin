package com.example.indentdetective;

import org.eclipse.swt.widgets.Display;
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

        // 1. Listen for windows opening/closing. The first windowOpened event
        //    (during startup) is the cold-start signal: the window and its
        //    restored editors now exist, so we can scan them.
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
                // Give the newly opened window one more event-loop turn to
                // finish restoring its parts, then scan its editors.
                Display.getDefault().asyncExec(() -> scanOpenEditors(workbench));
            }
        };
        workbench.addWindowListener(windowListener);

        // 2. Fallback: windows that already exist right now (e.g. the plugin
        //    was activated late). The part listener is attached immediately;
        //    the scan is deferred by one event-loop turn so that any editors
        //    still being restored are fully initialized when we look at them.
        for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
            if (window.getPartService() != null) {
                window.getPartService().addPartListener(indentListener);
            }
        }
        if (workbench.getWorkbenchWindows().length > 0) {
            Display.getDefault().asyncExec(() -> scanOpenEditors(workbench));
        }
    }

    /**
     * Applies the indentation analysis to every already-open text editor in
     * every window, covering editors that were restored before listeners were
     * attached (no partActivated event fired for them).
     * Must run on the UI thread.
     */
    private void scanOpenEditors(IWorkbench workbench) {
        for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
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
                    windowListener = null;
                }

                // Remove part listener from all open windows
                if (indentListener != null) {
                    for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
                        if (window.getPartService() != null) {
                            window.getPartService().removePartListener(indentListener);
                        }
                    }
                }
            }
        }
        indentListener = null;
        plugin = null;
        super.stop(context);
    }

    public static Activator getDefault() {
        return plugin;
    }
}