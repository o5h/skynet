package com.github.o5h.skynet.ur.urcap.installation;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeView;
import demos.es2.RawGL2ES2demo;

import javax.swing.*;

public class InstallationView implements SwingInstallationNodeView<Installation> {

    private GLCanvas glcanvas;
    private JPanel panel;
    private RawGL2ES2demo demo;

    InstallationView(ViewAPIProvider api) {
    }

    @Override
    public void buildUI(JPanel jPanel, final Installation installationNode) {
        this.panel = new JPanel();
        panel.setSize(jPanel.getSize());
        jPanel.add(panel);

    }

    public void onOpen() {
        // The canvas
        final GLProfile profile = GLProfile.get(GLProfile.GL2ES2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        glcanvas = new GLCanvas(capabilities);
         demo = new RawGL2ES2demo();
        glcanvas.addGLEventListener(demo);
        glcanvas.setSize(panel.getWidth(), panel.getHeight());
        panel.add(glcanvas);
        glcanvas.setEnabled(true);
    }

    public void onClose() {
        glcanvas.setEnabled(false);
        glcanvas.disposeGLEventListener(demo, true);
        panel.remove(glcanvas);
    }
}
