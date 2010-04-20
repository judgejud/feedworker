package org.jrss2sub.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import org.jrss2sub.util.ManageException;
import org.jrss2sub.util.ResourceLocator;

import org.lp.myUtils.crypto.DesEncrypter;

public class Property {

    private final String FILE_SETTINGS =  ResourceLocator.getWorkspace() + "settings.properties";
    private final String pwdProp = "jRss2Sub_property";
    private final String pwdValue = "value_jRss2Sub";
    
    private static Property prop = null;
    private String rssItasa, rssMyItasa, itasaUser, itasaPwd, subDest, rss_agg, lastDate, 
            lookFeel, torDest,sambaIP, sambaDir, sambaUser, sambaPwd, sambaDomain,
            rssSubsf, timeout, build, font;
    private boolean subsfactory, down_auto, audioRSS, torrent, itasa, firstRun, dirLocal,
            runIcon, runPc, advancedDest;
    private File f = new File(FILE_SETTINGS);
    private Properties properties;
    private DesEncrypter desProp, desValue;
    private ManageException error = ManageException.getIstance();

    /**Costruttore privato*/
    private Property(){
        properties = new Properties();
        try{
            desProp = new DesEncrypter(pwdProp);
            desValue = new DesEncrypter(pwdValue);
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        }
        if (f.exists()) {
            try {
                // Read properties file.
                properties.load(new FileInputStream(FILE_SETTINGS));
                setRssItasa(getDecryptValue("RSSITASA"));
                setRssMyItasa(getDecryptValue("RSSMYITASA"));
                setItasaUser(getDecryptValue("ITASAUSER"));
                setItasaPwd(getDecryptValue("ITASAPWD"));
                setSubDest(getDecryptValue("SUB_DEST"));
                setSubsFactory(Boolean.parseBoolean(getDecryptValue("SUBSFACTORY")));
                setRss_agg(getDecryptValue("RSS_AGG"));
                setDown_auto(Boolean.parseBoolean(getDecryptValue("DOWN_AUTO")));
                setLastDate(getDecryptValue("DATE"));
                setLookFeel(getDecryptValue("LOOKFEEL"));
                setAudioRSS(Boolean.parseBoolean(getDecryptValue("AUDIORSS")));
                setTorrent(Boolean.parseBoolean(getDecryptValue("TORRENT")));
                firstRun = Boolean.parseBoolean(getDecryptValue("FIRSTRUN"));
                setItasa(Boolean.parseBoolean(getDecryptValue("ITALIANSUBS")));
                setDirLocal(Boolean.parseBoolean(getDecryptValue("DIRLOCAL")));
                setTorrentDest(getDecryptValue("TOR_DEST"));
                setRssSubsf(getDecryptValue("RSSSUBSF"));
                setSambaDir(getDecryptValue("SAMBA_DIR"));
                setSambaDomain(getDecryptValue("SAMBA_DOMAIN"));
                setSambaIP(getDecryptValue("SAMBA_IP"));
                setSambaPwd(getDecryptValue("SAMBA_PWD"));
                setSambaUser(getDecryptValue("SAMBA_USER"));
                setTimeout(getDecryptValue("TIMEOUT"));
                setFont(getDecryptValue("FONT"));
                setAdvancedDest(Boolean.parseBoolean(getDecryptValue("ADVANCED_DEST")));
            } catch (IOException e) {
                error.launch(e, getClass(),null);
            }
        } else
            writeInit();        
    }

    private String getDecryptValue(String _prop){
        try{
            String value = properties.getProperty(desProp.encrypt(_prop));
            if (value!=null)
                return desValue.decrypt(value);        
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
        return null;
    }

    /**Restituisce l'istanza statica della classe
     *
     * @return istanza property
     */
    public static Property getIstance(){
        if (prop == null)
            prop = new Property();
        return prop;
    }

    /**Scrive i settaggi su file*/
    public void writeGlobal(){
        try{
            properties.setProperty(desProp.encrypt("SUB_DEST"), desValue.encrypt(subDest));
            properties.setProperty(desProp.encrypt("RSS_AGG"), desValue.encrypt(rss_agg));
            properties.setProperty(desProp.encrypt("LOOKFEEL"), desValue.encrypt(lookFeel));
            properties.setProperty(desProp.encrypt("AUDIORSS"),
                    desValue.encrypt(Boolean.toString(audioRSS)));
            properties.setProperty(desProp.encrypt("DIRLOCAL"),
                    desValue.encrypt(Boolean.toString(dirLocal)));
            properties.setProperty(desProp.encrypt("SAMBA_DIR"), desValue.encrypt(sambaDir));
            properties.setProperty(desProp.encrypt("SAMBA_DOMAIN"), desValue.encrypt(sambaDomain));
            properties.setProperty(desProp.encrypt("SAMBA_IP"), desValue.encrypt(sambaIP));
            properties.setProperty(desProp.encrypt("SAMBA_PWD"), desValue.encrypt(sambaPwd));
            properties.setProperty(desProp.encrypt("SAMBA_USER"), desValue.encrypt(sambaUser));
            properties.setProperty(desProp.encrypt("TIMEOUT"), desValue.encrypt(timeout));
            //properties.setProperty(desProp.encrypt("FONT"), desValue.encrypt(font));
            properties.setProperty(desProp.encrypt("ADVANCED_DEST"),
                    desValue.encrypt(Boolean.toString(advancedDest)));
            store();        
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }// end write

    /**Scrive i settaggi su file*/
    public void writeItasa(){
        try{
            properties.setProperty(desProp.encrypt("RSSITASA"), desValue.encrypt(rssItasa));
            properties.setProperty(desProp.encrypt("RSSMYITASA"), desValue.encrypt(rssMyItasa));
            properties.setProperty(desProp.encrypt("ITASAUSER"), desValue.encrypt(itasaUser));
            properties.setProperty(desProp.encrypt("ITASAPWD"), desValue.encrypt(itasaPwd));
            properties.setProperty(desProp.encrypt("DOWN_AUTO"),
                    desValue.encrypt(Boolean.toString(down_auto)));
            store();
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }// end write

    public void writeSubsf(){
        try{
            properties.setProperty(desProp.encrypt("RSSSUBSF"), desValue.encrypt(rssSubsf));
            store();
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }

    public void writeTorrent(){
        try{
            properties.setProperty(desProp.encrypt("TOR_DEST"), desValue.encrypt(torDest));
            store();       
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }

    public void writeFirstRunFalse(){
        firstRun = false;
        try{
            properties.setProperty(desProp.encrypt("FIRSTRUN"),
                    desValue.encrypt(Boolean.toString(firstRun)));
            store();
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }

    // Write properties file.
    private void store() throws IOException {
        properties.store(new FileOutputStream(FILE_SETTINGS), null);
    }

    private void writeInit(){
        itasa = true;
        firstRun = true;
        torrent = true;
        subsfactory = true;
        build = "121";
        try {
            properties.setProperty(desProp.encrypt("ITALIANSUBS"), 
                    desValue.encrypt(Boolean.toString(itasa)));
            properties.setProperty(desProp.encrypt("FIRSTRUN"), 
                    desValue.encrypt(Boolean.toString(firstRun)));
            properties.setProperty(desProp.encrypt("TORRENT"), 
                    desValue.encrypt(Boolean.toString(torrent)));
            properties.setProperty(desProp.encrypt("SUBSFACTORY"), 
                    desValue.encrypt(Boolean.toString(subsfactory)));
            properties.setProperty(desProp.encrypt("BUILD"),
                    desValue.encrypt(build));
            properties.store(new FileOutputStream(FILE_SETTINGS), null);
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }

    /**scrive solo l'ultima data dell'aggiornamento rss*/
    public void writeOnlyLastDate(){
        try{
            properties.setProperty(desProp.encrypt("DATE"), desValue.encrypt(lastDate));
            store();
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }//end writeOnlyLastDate

    public boolean isAudioRSS() {
        return audioRSS;
    }

    public void setAudioRSS(boolean audioRSS) {
        this.audioRSS = audioRSS;
    }

    public boolean isItasa() {
        return itasa;
    }

    private void setItasa(boolean itasa) {
        this.itasa = itasa;
    }

    public boolean isTorrent() {
        return torrent;
    }

    private void setTorrent(boolean torrent) {
        this.torrent = torrent;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public String getItasaPwd() {
        if (itasaPwd==null)
            itasaPwd = "";
        return itasaPwd;
    }

    public String getRssItasa() {
        if (rssItasa==null)
            rssItasa = "";
        return rssItasa;
    }

    public String getRssMyItasa() {
        if (rssMyItasa==null)
            rssMyItasa = "";
        return rssMyItasa;
    }

    public boolean isDown_auto() {
        return down_auto;
    }

    public String getRss_agg() {
        return rss_agg;
    }

    public String getSubDest() {
        return subDest;
    }

    public String getItasaUser() {
        if (itasaUser==null)
            itasaUser="";
        return itasaUser;
    }

    public void setItasaPwd(String pwd) {
        this.itasaPwd = pwd;
    }

    public void setRssItasa(String rss) {
        this.rssItasa = rss;
    }

    public void setRssMyItasa(String rss) {
        this.rssMyItasa = rss;
    }

    public void setItasaUser(String user) {
        this.itasaUser = user;
    }

    public void setSubDest(String subDest) {
        this.subDest = subDest;
    }

    public boolean isSubsfactory() {
        return subsfactory;
    }

    private void setSubsFactory(boolean b) {
        subsfactory = b;
    }

    public void setDown_auto(boolean auto) {
        down_auto = auto;
    }

    public void setRss_agg(String rss_agg) {
        this.rss_agg = rss_agg;
    }

    public String getFILE_SETTINGS() {
        return FILE_SETTINGS;
    }

    public String getLookFeel() {
        if (lookFeel==null)
            lookFeel="";
        return lookFeel;
    }

    public void setLookFeel(String lookFeel) {
        this.lookFeel = lookFeel;
    }

    public String getTorrentDest() {
        return torDest;
    }

    public void setTorrentDest(String torDest) {
        this.torDest = torDest;
    }

    public boolean isFirstRun() {
        return firstRun;
    }

    public String getSambaDir() {
        return sambaDir;
    }

    public void setSambaDir(String sambaDir) {
        this.sambaDir = sambaDir;
    }

    public String getSambaIP() {
        return sambaIP;
    }

    public void setSambaIP(String sambaIP) {
        this.sambaIP = sambaIP;
    }

    public String getSambaPwd() {
        return sambaPwd;
    }

    public void setSambaPwd(String sambaPwd) {
        this.sambaPwd = sambaPwd;
    }

    public String getSambaUser() {
        return sambaUser;
    }

    public void setSambaUser(String sambaUser) {
        this.sambaUser = sambaUser;
    }

    public boolean isDirLocal() {
        return dirLocal;
    }

    public void setDirLocal(boolean dirLocal) {
        this.dirLocal = dirLocal;
    }

    public String getRssSubsf() {
        return rssSubsf;
    }

    public void setRssSubsf(String rssSubsf) {
        this.rssSubsf = rssSubsf;
    }

    public String getSambaDomain() {
        return sambaDomain;
    }

    public void setSambaDomain(String sambaDomain) {
        this.sambaDomain = sambaDomain;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public boolean isAdvancedDest() {
        return advancedDest;
    }

    public void setAdvancedDest(boolean advancedDest) {
        this.advancedDest = advancedDest;
    }

    public boolean isRunIcon() {
        return runIcon;
    }

    public void setRunIcon(boolean runIcon) {
        this.runIcon = runIcon;
    }

    public boolean isRunPc() {
        return runPc;
    }

    public void setRunPc(boolean runPc) {
        this.runPc = runPc;
    }
}//end class