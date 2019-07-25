package com.github.o5h.skynet.ur.urcap.bundle;

import com.github.o5h.skynet.ur.urcap.installation.InstallationService;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        context.registerService(SwingInstallationNodeService.class, new InstallationService(), null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}
