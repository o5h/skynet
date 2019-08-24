package com.github.o5h.skynet.ur.urcap.installation;

import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;


public class Installation implements InstallationNodeContribution {

    private final InstallationView view;

    Installation(InstallationAPIProvider apiProvider,
                 DataModel model,
                 InstallationView view,
                 CreationContext context) {
        this.view = view;
    }

    @Override
    public void openView() {
        view.onOpen();
    }

    @Override
    public void closeView() {
        view.onClose();
    }

    @Override
    public void generateScript(ScriptWriter writer) {
    }

}
