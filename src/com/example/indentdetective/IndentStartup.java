package com.example.indentdetective;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;

public class IndentStartup implements IStartup {
    @Override
    public void earlyStartup() {
        Activator a = Activator.getDefault();
        if (a == null) {
            return;
        }

        Display display = PlatformUI.getWorkbench().getDisplay();
        display.asyncExec(() -> a.registerListeners());
    }
}