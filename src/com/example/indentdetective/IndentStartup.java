package com.example.indentdetective;

import org.eclipse.ui.IStartup;

public class IndentStartup implements IStartup {
    @Override
    public void earlyStartup() {
        // Loading this class forces the bundle to activate,
        // so Activator.start() runs.
        Activator a = Activator.getDefault();
        if (a == null) {
            return;
        }
        a.registerListeners();
    }
}