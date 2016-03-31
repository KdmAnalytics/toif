
package com.kdmanalytics.toif.rcp.internal.util;

/*******************************************************************************
 * Copyright (c) 2013 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

final public class BuildInformation {
  
  private Manifest manifest = null;
  
  private Attributes attributes = null;
  
  private File jarFile = null;
  
  public BuildInformation() {
  }
  
  public BuildInformation(Object object) {
    manifest = getManifest(object);
    
    attributes = manifest.getMainAttributes();
  }
  
  public BuildInformation(File jarFile) {
    JarFile jar;
    try {
      jar = new JarFile(jarFile);
      manifest = jar.getManifest();
      
      if (manifest != null) attributes = manifest.getMainAttributes();
      
      this.jarFile = jarFile;
    } catch (MalformedURLException e) {
      // Just skip
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public boolean isValid() {
    if (manifest == null) return false;
    else return true;
  }
  
  // -------------------------------------------------------------
  // Return Manifest Associated with this object
  // -------------------------------------------------------------
  private Manifest getManifest(Object object) {
    try {
      URL manifestUrl = object.getClass().getProtectionDomain().getCodeSource().getLocation();
      
      JarFile jar = new JarFile(manifestUrl.getFile());
      Manifest manifest = jar.getManifest();
      jar.close();
      
      return manifest;
    } catch (MalformedURLException e) {
      // Just skip
    } catch (IOException e) {
      // Just skip
    }
    
    return null;
  }
  
  final public String getVersion() {
    if (manifest == null) return null;
    else {
      String versionString = KDMVersionFormat(attributes.getValue("Implementation-Version"));
      if (versionString == null) {
        // Implementation value not set .. try to use bundle tags
        versionString = bundleVersionFormat(attributes.getValue("Bundle-Version"));
      }
      
      return versionString;
    }
  }
  
  private String KDMVersionFormat(String versionString) {
    if (versionString == null) return null;
    
    // Check if KDM format
    if (!versionString.trim().matches("[0-9].[0-9].[0-9].*.\\([0-9]*-.*.\\)")) return versionString;
    else {
      String token[] = versionString.split(" ");
      if (token.length == 2) {
        // extract version #
        String version = token[0];
        if (version.contains("-")) version = version.substring(0, version.indexOf('-'));
        
        // extract build #
        String buildNumber = token[1];
        if (buildNumber.contains("-") && buildNumber.length() > 2) buildNumber = token[1].substring(1, token[1].indexOf(
                                                                                                                        "-"));
                                                                                                                        
        // Generate KDM version string
        if (versionString.contains("SNAPSHOT")) return (version + '-' + buildNumber + " (development)");
        else return (version + '-' + buildNumber + " (production)");
      }
      
      return versionString;
    }
  }
  
  private String bundleVersionFormat(String versionString) {
    if (versionString == null) return null;
    
    int index = versionString.lastIndexOf('.');
    if (index != -1) {
      String tt = versionString.substring(index + 1);
      
      // make sure that is is valid time code
      if (tt.matches("[0-9]*") && tt.length() >= 8) {
        String version = versionString.substring(0, index);
        if (isKdmEntity()) return version + "-" + tt + " (patch)";
        else return version + "-" + tt;
      }
    }
    
    return versionString;
  }
  
  public boolean isKdmEntity() {
    Attributes attributes = manifest.getMainAttributes();
    
    String vendor = attributes.getValue("Impelmentation-Vendor");
    if (vendor != null) if (vendor.contains("KDM Analytics")) return true;
    
    vendor = attributes.getValue("Bundle-Vendor");
    if (vendor != null) if (vendor.contains("KDM Analytics") || vendor.contains("KDMANALYTICS")) return true;
    
    return false;
  }
  
  // --------------------------------------------------------------
  // Get title
  // --------------------------------------------------------------
  final public String getTitle() {
    String title = attributes.getValue("Bundle-Name");
    if (title == null || title.contains("%")) title = attributes.getValue("Implementation-Title");
    
    return title;
  }
  
  // --------------------------------------------------------------
  // Get Vendor
  // --------------------------------------------------------------
  final public String getVendor() {
    String vendor = attributes.getValue("Bundle-Vendor");
    if (vendor == null || vendor.contains("%")) vendor = attributes.getValue("Implementation-Vendor");
    
    return vendor;
  }
  
  final public File getJarFile() {
    return jarFile;
  }
}
