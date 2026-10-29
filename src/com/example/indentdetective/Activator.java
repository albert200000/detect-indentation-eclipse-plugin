package com.example.indentdetective;

import org.eclipse.core.runtime.ILog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * The bundle activator registers the {@link IndentListener} with the workbench
 * part service when the plugin starts and removes it when the plugin stops.
 */
public class Activator extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "com.example.indentdetective";
    private static Activator plugin;
    private IndentListener indentListener;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        indentListener = new IndentListener();

        ILog log = ILog.of(FrameworkUtil.getBundle(Activator.class));
        log.info("TEST");

        PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            if (window != null && window.getPartService() != null) {
                window.getPartService().addPartListener(indentListener);
            }
        });
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (indentListener != null) {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window != null && window.getPartService() != null) {
                window.getPartService().removePartListener(indentListener);
            }
        }
        plugin = null;
        super.stop(context);
    }

    public static Activator getDefault() {
        return plugin;
    }
}
