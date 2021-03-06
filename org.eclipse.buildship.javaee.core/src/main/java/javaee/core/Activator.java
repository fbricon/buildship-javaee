/*
 * Copyright (c) 2015 the original author or authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ian Stewart-Binks (Red Hat, Inc.) - initial API and implementation and initial documentation
 */
package javaee.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "org.eclipse.buildship.javaee.core"; //$NON-NLS-1$
    public static final String GRADLE_PLUGIN_PATH = "repo/libs/redhat-plugin-1.0.jar";
    public static final String INIT_GRADLE_PATH = "init.gradle";
    private static Activator plugin;
    
    public Activator() {
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
        
        plugin = this;
        IPath metadataPath = this.getStateLocation();
        
        // TODO: Check that path can be appended like this (a/b/c).
        IPath initGradlePath = metadataPath.append(INIT_GRADLE_PATH);
        IPath pluginPath = metadataPath.append("repo").append("redhat-plugin-1.0.jar");
        Bundle bundle = Platform.getBundle(PLUGIN_ID);
        URL initUrl = bundle.getEntry(INIT_GRADLE_PATH);
        URL pluginUrl = bundle.getEntry(GRADLE_PLUGIN_PATH);
        File initFile = null;
        File pluginFile = null;
        
        try {
            initFile = new File(FileLocator.resolve(initUrl).toURI());
            pluginFile = new File(FileLocator.resolve(pluginUrl).toURI());
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        copyFile(initFile, new File(initGradlePath.toString()));
        copyFile(pluginFile, new File(pluginPath.toString()));
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static Activator getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
    
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.getParentFile().mkdirs();
            destFile.createNewFile();
        }
        
        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
    
}
