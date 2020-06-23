package com.github.o5h.skynet.rpcagent.installation;

import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.installation.ContributionConfiguration;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;
import com.ur.urcap.api.domain.data.DataModel;

import java.util.Locale;

public class InstallationService implements SwingInstallationNodeService<Installation, InstallationView> {

    @Override
    public void configureContribution(ContributionConfiguration configuration) {
    }

    @Override
    public String getTitle(Locale locale) {
        return "SkyNet RPC Agent";
    }

    @Override
    public InstallationView createView(ViewAPIProvider api) {
        return new InstallationView(api);
    }

    @Override
    public Installation createInstallationNode(InstallationAPIProvider apiProvider,
                                               InstallationView view,
                                               DataModel model,
                                               CreationContext context) {
        return new Installation(apiProvider, model, view, context);
    }
}
