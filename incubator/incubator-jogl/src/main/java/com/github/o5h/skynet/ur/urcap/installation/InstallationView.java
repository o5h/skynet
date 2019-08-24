package com.github.o5h.skynet.ur.urcap.installation;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeView;

import javax.swing.*;

public class InstallationView implements SwingInstallationNodeView<Installation> {

    InstallationView(ViewAPIProvider api) {
    }

    @Override
    public void buildUI(JPanel jPanel, final Installation installationNode) {
        JPanel panel = new JPanel();
        jPanel.add(panel);
        final GLProfile profile = GLProfile.get(GLProfile.GL2ES2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        // The canvas
        final GLCanvas glcanvas = new GLCanvas(capabilities);
        BasicFrame b = new BasicFrame();
        glcanvas.addGLEventListener(b);
        glcanvas.setSize(400, 400);
        panel.add(glcanvas);
    }


    public class BasicFrame implements GLEventListener {

        @Override
        public void display(GLAutoDrawable arg0) {
            // method body
        }

        @Override
        public void dispose(GLAutoDrawable arg0) {
            //method body
        }

        @Override
        public void init(GLAutoDrawable arg0) {
            // method body
        }

        @Override
        public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int arg3, int arg4) {
            // method body
            final GL2ES2 gl = drawable.getGL().getGL2ES2();
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
            gl.glClearColor(0,1,0,0);
        }
    }
}
