package org.feedworker.util;

//IMPORT JAVA
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Arrays;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

/**
 * 
 * @author luca
 */
public class Samba {

    private final byte[] BUFFER = new byte[60416];
    private final String SMB = "smb://";
    private static Samba cifs = null;
    private static String ip, domain, username, password, cartella;
    private String pathBase;
    private NtlmPasswordAuthentication auth;

    public static Samba getIstance(String _ip, String _condiv, String _domain,
            String _username, String _password) {
        if (cifs == null) {
            ip = _ip;
            domain = _domain;
            username = _username;
            password = _password;
            cartella = _condiv;
            cifs = new Samba();
        }
        return cifs;
    }

    private Samba() {
        System.setProperty("jcifs.smb.client.rcv_buf_size", "60416");
        System.setProperty("jcifs.smb.client.snd_buf_size", "16644");
        auth = new NtlmPasswordAuthentication(domain, username, password);
        pathBase = SMB + ip + "/" + cartella + "/";
    }
    
    public boolean isDir(String path) throws MalformedURLException, SmbException{
        return new SmbFile(path, auth).exists();
    }

    public boolean moveFromLocal(File local, String path) throws IOException {
        boolean verify = false;
        InputStream in = new FileInputStream(local);
        String sambaPath = pathBase;
        if (path != null) {
            sambaPath += path + "/";
        }
        sambaPath += local.getName();
        sambaPath.replaceAll(" ", "%20");
        SmbFile server = new SmbFile(sambaPath, auth);
        OutputStream out = new SmbFileOutputStream(server);
        try {
            while (true) {
                synchronized (BUFFER) {
                    int amountRead = in.read(BUFFER);
                    if (amountRead == -1) {
                        break;
                    }
                    out.write(BUFFER, 0, amountRead);
                }
            }
        } catch (Exception e) {
        } finally {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        }
        return verify;
    }

    public void moveFile(String filename, String newPath, String newName)
            throws SmbException, MalformedURLException {
        String pathOldFile = pathBase + filename;
        String pathNewFile = pathBase + newPath + "/" + newName;
        SmbFile old = new SmbFile(pathOldFile, auth);
        SmbFile moved = new SmbFile(pathNewFile, auth);
        old.renameTo(moved);
    }

    // TODO: capire chi è che lancia eccezioni doppie
    public boolean testConn() throws MalformedURLException, SmbException,
            UnknownHostException, IOException {
        boolean test = false;
        String composizione = SMB + ip + "/" + cartella + "/test.txt";
        SmbFile server = new SmbFile(composizione, auth);
        OutputStream out = new SmbFileOutputStream(server);
        out.close();
        server.delete();
        test = true;
        return test;
    }

    public String[] listDir(String dir, String ext)
            throws MalformedURLException, SmbException {
        String sambaPath = pathBase;
        String[] list = null;
        if (dir != null) {
            sambaPath += dir + "/";
        }
        sambaPath.replaceAll(" ", "%20");
        SmbFile server = new SmbFile(sambaPath, auth);
        list = server.list(new ExtensionFilter(ext)); // Get list of names
        Arrays.sort(list); // Sort it (Data Structuring chapter))
        return list;
    }

    public static void resetInstance() {
        cifs = null;
    }
}