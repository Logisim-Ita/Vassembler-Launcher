package org.altervista.logisim.Vassembler_Launcher;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.lang3.SystemUtils;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

public class Utils {
    static final String JDK_VERSION = "openjdk-18.0.1.1";
    static final String DOT_VASSEMBLER_PATH = System.getProperty("user.home")+File.separator+".Vassembler";
    static final String JDK_PATH = DOT_VASSEMBLER_PATH+File.separator+JDK_VERSION;
    static final String JDK_TEMP_ZIP_PATH = getTempDir();
    static final String JDK_JAVA_PATH = JDK_PATH+File.separator+"jdk-18.0.1.1"+File.separator+"bin"+File.separator+"java";
    static final String JDK_DOWNLOAD_MAIN_URL = "https://download.java.net/java/GA/jdk18.0.1.1/65ae32619e2f40f3a9af3af1851d6e19/2/GPL/"+JDK_VERSION+"_";
    static final String JAR_PATH = DOT_VASSEMBLER_PATH+File.separator+"Vassembler.jar";

    public static String getTempDir() {
        String tempDirString = System.getProperty("java.io.tmpdir");
        if(!tempDirString.endsWith(File.separator)){
            tempDirString = tempDirString + File.separator;
        }
        tempDirString = tempDirString + JDK_VERSION;
        return tempDirString;
    }

    /**
     * Detect System OS and return the download URL from download.java.net
     * @return Object[URL, (boolean)isTarGZ]
     */
    public static Object[] getURLForOS(){
        Object[] returnObjects = new Object[2];
        try {
            if (SystemUtils.IS_OS_WINDOWS){
                returnObjects[0] = new URL(JDK_DOWNLOAD_MAIN_URL+"windows-x64_bin.zip");
                returnObjects[1] = false;
                return returnObjects;
            }
            else if (SystemUtils.IS_OS_MAC){
                // TODO: Manage macOS / AArch64
                returnObjects[0] = new URL(JDK_DOWNLOAD_MAIN_URL + "macos-x64_bin.tar.gz");
                returnObjects[1] = true;
                return returnObjects;
            }
            else if(SystemUtils.IS_OS_LINUX){
                System.out.println("Linux");
                // TODO: Manage Linux / AArch64
                returnObjects[0] = new URL(JDK_DOWNLOAD_MAIN_URL + "linux-x64_bin.tar.gz");
                returnObjects[1] = true;
                return returnObjects;
            }
            else
                return null;
        } catch (MalformedURLException ex) {
            Logger.log(ex.toString());
            return null;
        }
    }

    /**
     * Download and extract from a defined URL in the defined JDK_TEMP_ZIP_PATH
     * @param url Download URL
     * @param isTarGz
     */
    public static void downloadFromURL(URL url,boolean isTarGz){
        String archiveFileURLString = JDK_TEMP_ZIP_PATH;
        if (isTarGz)
            archiveFileURLString = archiveFileURLString + ".tar.gz";
        else
            archiveFileURLString = archiveFileURLString + ".zip";

        // Download from URL
        try {
            BufferedInputStream inputStream = new BufferedInputStream(url.openStream());
            FileOutputStream fileOS = new FileOutputStream(archiveFileURLString);
            byte data[] = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                    fileOS.write(data, 0, byteContent);
            }
            fileOS.close();
        } catch (IOException ex){
           Logger.log(ex.toString());
        }

        // Extract downloaded ZIP
        try {
            Archiver archiver = null;
            if (isTarGz)
                archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
            else
                archiver = ArchiverFactory.createArchiver(ArchiveFormat.ZIP);
            
            File source = new File(archiveFileURLString);
            File destination = new File(JDK_PATH);
            archiver.extract(source, destination);
            source.delete();
        } catch (IOException ex) {
            Logger.log(ex.toString());
        }  
    }

    /**
     * Check if JDK exist in JDK_PATH and download it if missing
     */
    public static void getJDK() {
        Path jdkPath = Paths.get(JDK_PATH);
        if (!Files.exists(jdkPath)){
            Object[] urlForOS = getURLForOS();
            downloadFromURL((URL)urlForOS[0],(Boolean)urlForOS[1]);
        }
    }

    /**
     * Copy Vassembler JAR from this JAR to JAR_PATH
     */
    public static void copyJAR() {
        try {
            Path destination = Paths.get(JAR_PATH);
             if (Files.exists(destination))
                   return;
            if (!Files.exists(destination.getParent()))
                new File(DOT_VASSEMBLER_PATH).mkdirs();
            InputStream source = Main.class.getResourceAsStream("/Vassembler.jar");
            Files.copy(source, destination);
        } catch (IOException ex) {
            // TODO: handle errors   
            Logger.log(ex.toString());
        }
    }
    
    /**
     * Launch the JAR via system terminal
     * @param portable if the java location is JDK_JAVA_PATH
     */
    public static void launchRealJAR(boolean portable) {
        String javaLocation = null;
        if (portable){
            javaLocation = JDK_JAVA_PATH;
            if (SystemUtils.IS_OS_WINDOWS)
                javaLocation = javaLocation + ".exe";
        }else
            javaLocation = "java";
        try {
            String launchCommand = javaLocation + " -jar " + JAR_PATH;
            System.out.println(launchCommand);
            Runtime.getRuntime().exec(launchCommand);
           
        } catch (IOException ex) {
            Logger.log("Can't launch the JAR " + ex.toString());
        }
    }

    
}
