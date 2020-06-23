package com.github.o5h.skynet.rpcagent.installation;

import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;


public class Installation implements InstallationNodeContribution {

    Installation(InstallationAPIProvider apiProvider,
                 DataModel model,
                 InstallationView view,
                 CreationContext context) {
    }

    @Override
    public void openView() {
    }

    @Override
    public void closeView() {

    }

    @Override
    public void generateScript(ScriptWriter writer) {
    }

}
