package org.feedworker.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import org.feedworker.util.ManageException;
import org.feedworker.util.ResourceLocator;

import org.lp.myUtils.crypto.DesEncrypter;

public class ApplicationSettings {

    private static ApplicationSettings instance = null;
    private final String SETTINGS_FILENAME = ResourceLocator.getWorkspace()
            + "settings.properties";
    private final File SETTINGS_FILE = new File(SETTINGS_FILENAME);
    private final String ENCRYPTION_PROPERTY_PWD = FeedWorkerClient.getApplicationName()
            + "property";
    private final String ENCRYPTION_VALUE_PWD = FeedWorkerClient.getApplicationName()
            + "value";
    private String itasaFeedURL, myitasaFeedURL, myitasaUsername, myitasaPassword,
            subtitleDestinationFolder, refreshInterval, lastDateTimeRefresh,
            applicationLookAndFeel, torrentDestinationFolder,
            cifsShareLocation, cifsSharePath, cifsShareUsername,
            cifsSharePassword, cifsShareDomain, subsfactoryFeedURL,
            httpTimeout, applicationBuild, applicationFont;
    private boolean hasSubsfactoryOption, hasTorrentOption, hasItasaOption,
            isAutoDownload, enableAudioAdvisor, isApplicationFirstTimeUsed, isLocalFolder,
            enableIconizedRun, enableRunAtStartup,
            enableCustomDestinationFolder;
    private Properties properties;
    private DesEncrypter propertyEncrypter, valueEncrypter;
    private ManageException error = ManageException.getIstance();

    /** Costruttore privato */
    private ApplicationSettings() {
        properties = new Properties();

        try {
            propertyEncrypter = new DesEncrypter(ENCRYPTION_PROPERTY_PWD);
            valueEncrypter = new DesEncrypter(ENCRYPTION_VALUE_PWD);
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        }

        try {
            if (SETTINGS_FILE.exists()) {
                // Read properties file.
                properties.load(new FileInputStream(SETTINGS_FILE));

                setItasaFeedURL(getDecryptedValue("ITASA_FEED_URL"));
                setMyitasaFeedURL(getDecryptedValue("MYITASA_FEED_URL"));
                setMyitasaUsername(getDecryptedValue("MYITASA_USERNAME"));
                setMyitasaPassword(getDecryptedValue("MYITASA_PASSWORD"));
                setSubsfactoryFeedURL(getDecryptedValue("SUBSFACTORY_FEED_URL"));
                setSubtitleDestinationFolder(getDecryptedValue("SUBTITLE_DESTINATION_FOLDER"));
                setSubfactoryOption(Boolean.parseBoolean(getDecryptedValue("SUBSFACTORY")));
                setRefreshInterval(getDecryptedValue("REFRESH_INTERVAL"));
                setAutoDownload(Boolean.parseBoolean(getDecryptedValue("IS_AUTO_DOWNLOAD")));
                setLastDateTimeRefresh(getDecryptedValue("LAST_DATETIME_REFRESH"));
                setApplicationLookAndFeel(getDecryptedValue("APPLICATION_LOOK_AND_FEEL"));
                enableAudioAdvisor(Boolean.parseBoolean(getDecryptedValue("ENABLE_AUDIO_ADVISOR")));
                setTorrentOption(Boolean.parseBoolean(getDecryptedValue("TORRENT")));
                isApplicationFirstTimeUsed = Boolean.parseBoolean(
                        getDecryptedValue("IS_APPLICATION_FIRST_TIME_USED"));
                setItasaOption(Boolean.parseBoolean(getDecryptedValue("ITALIANSUBS")));
                localFolder(Boolean.parseBoolean(getDecryptedValue("IS_LOCAL_FOLDER")));
                setTorrentDestinationFolder(getDecryptedValue("TORRENT_DESTINATION_FOLDER"));
                setCifsSharePath(getDecryptedValue("CIFS_SHARE_PATH"));
                setCifsShareDomain(getDecryptedValue("CIFS_SHARE_DOMAIN"));
                setCifsShareLocation(getDecryptedValue("CIFS_SHARE_LOCATION"));
                setCifsSharePassword(getDecryptedValue("CIFS_SHARE_PASSWORD"));
                setCifsShareUsername(getDecryptedValue("CIFS_SHARE_USERNAME"));
                setHttpTimeout(getDecryptedValue("HTTP_TIMEOUT"));
                setApplicationFont(getDecryptedValue("APPLICATION_FONT"));
                enableCustomDestinationFolder(Boolean.parseBoolean(
                        getDecryptedValue("ENABLE_CUSTOM_DESTINATION_FOLDER")));
            } else {
                loadDefaultSettings();
                storeSettings();
            }
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }

    /**Restituisce l'istanza statica della classe
     *
     * @return istanza property
     */
    public static ApplicationSettings getIstance() {
        if (instance == null) {
            instance = new ApplicationSettings();
        }
        return instance;
    }

    private String getDecryptedValue(String property) {
        try {
            String value = properties.getProperty(propertyEncrypter.encrypt(property));
            if (value != null) {
                return valueEncrypter.decrypt(value);
            }
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
        return null;
    }

    /** Scrive i settaggi su file */
    public void writeGeneralSettings() {
        try {
            properties.setProperty(propertyEncrypter.encrypt("SUBTITLE_DESTINATION_FOLDER"),
                    valueEncrypter.encrypt(subtitleDestinationFolder));
            properties.setProperty(propertyEncrypter.encrypt("REFRESH_INTERVAL"),
                    valueEncrypter.encrypt(refreshInterval));
            properties.setProperty(propertyEncrypter.encrypt("CLIENT_LOOK_AND_FEEL"),
                    valueEncrypter.encrypt(applicationLookAndFeel));
            properties.setProperty(propertyEncrypter.encrypt("ENABLE_AUDIO_ADVISOR"),
                    valueEncrypter.encrypt(Boolean.toString(enableAudioAdvisor)));
            properties.setProperty(propertyEncrypter.encrypt("IS_LOCAL_FOLDER"),
                    valueEncrypter.encrypt(Boolean.toString(isLocalFolder)));
            properties.setProperty(propertyEncrypter.encrypt("CIFS_SHARE_PATH"),
                    valueEncrypter.encrypt(cifsSharePath));
            properties.setProperty(propertyEncrypter.encrypt("CIFS_SHARE_DOMAIN"),
                    valueEncrypter.encrypt(cifsShareDomain));
            properties.setProperty(propertyEncrypter.encrypt("CIFS_SHARE_IP"),
                    valueEncrypter.encrypt(cifsShareLocation));
            properties.setProperty(propertyEncrypter.encrypt("CIFS_SHARE_PASSWORD"),
                    valueEncrypter.encrypt(cifsSharePassword));
            properties.setProperty(propertyEncrypter.encrypt("CIFS_SHARE_USERNAME"),
                    valueEncrypter.encrypt(cifsShareUsername));
            properties.setProperty(propertyEncrypter.encrypt("HTTP_TIMEOUT"),
                    valueEncrypter.encrypt(httpTimeout));
            // properties.setProperty(desProp.encrypt("FONT"),
            // desValue.encrypt(font));
            properties.setProperty(propertyEncrypter.encrypt("ENABLE_CUSTOM_DESTINATION_FOLDER"),
                    valueEncrypter.encrypt(Boolean.toString(enableCustomDestinationFolder)));
            storeSettings();
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }// end write

    /** Scrive i settaggi su file */
    public void writeItasaSettings() {
        try {
            properties.setProperty(propertyEncrypter.encrypt("ITASA_FEED_URL"),
                    valueEncrypter.encrypt(itasaFeedURL));
            properties.setProperty(propertyEncrypter.encrypt("MYITASA_FEED_URL"),
                    valueEncrypter.encrypt(myitasaFeedURL));
            properties.setProperty(propertyEncrypter.encrypt("MYITASA_USERNAME"),
                    valueEncrypter.encrypt(myitasaUsername));
            properties.setProperty(propertyEncrypter.encrypt("MYITASA_PASSWORD"),
                    valueEncrypter.encrypt(myitasaPassword));
            properties.setProperty(propertyEncrypter.encrypt("IS_AUTO_DOWNLOAD"),
                    valueEncrypter.encrypt(Boolean.toString(isAutoDownload)));
            storeSettings();
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }// end write

    public void writeSubsfactorySettings() {
        try {
            properties.setProperty(propertyEncrypter.encrypt("SUBSFACTORY_FEED_URL"),
                    valueEncrypter.encrypt(subsfactoryFeedURL));
            storeSettings();
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }

    public void writeTorrentSettings() {
        try {
            properties.setProperty(propertyEncrypter.encrypt("TORRENT_DESTINATION_FOLDER"),
                    valueEncrypter.encrypt(torrentDestinationFolder));
            storeSettings();
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }
    
     public void writeApplicationFirstTimeUsedFalse() {
        isApplicationFirstTimeUsed = false;
        try {
            properties.setProperty(propertyEncrypter.encrypt("IS_APPLICATION_FIRST_TIME_USED"),
            valueEncrypter.encrypt(Boolean.toString(isApplicationFirstTimeUsed)));
            storeSettings();
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
     }
     
    // Write properties file.
    private void storeSettings() throws IOException {
        properties.store(new FileOutputStream(SETTINGS_FILENAME), null);
    }

    private void loadDefaultSettings() {
        hasItasaOption = true;
        hasSubsfactoryOption = true;
        hasTorrentOption = true;
        isApplicationFirstTimeUsed = true;
        applicationBuild = "121";

        try {
            properties.setProperty(propertyEncrypter.encrypt("ITALIANSUBS"),
                    valueEncrypter.encrypt(Boolean.toString(hasItasaOption)));
            properties.setProperty(propertyEncrypter.encrypt("IS_APPLICATION_FIRST_TIME_USED"),
                    valueEncrypter.encrypt(Boolean.toString(isApplicationFirstTimeUsed)));
            properties.setProperty(propertyEncrypter.encrypt("TORRENT"),
                    valueEncrypter.encrypt(Boolean.toString(hasTorrentOption)));
            properties.setProperty(propertyEncrypter.encrypt("SUBSFACTORY"),
                    valueEncrypter.encrypt(Boolean.toString(hasSubsfactoryOption)));
            properties.setProperty(propertyEncrypter.encrypt("BUILD"),
                    valueEncrypter.encrypt(applicationBuild));
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }

    /** scrive solo l'ultima data dell'aggiornamento rss */
    public void writeOnlyLastDate() {
        try {
            properties.setProperty(propertyEncrypter.encrypt("DATE"),
                    valueEncrypter.encrypt(lastDateTimeRefresh));
            storeSettings();
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }// end writeOnlyLastDate

    public boolean enabledAudioAdvisor() {
        return enableAudioAdvisor;
    }

    public void enableAudioAdvisor(boolean enableAudioAdvisor) {
        this.enableAudioAdvisor = enableAudioAdvisor;
    }

    public boolean hasItasaOption() {
        return hasItasaOption;
    }

    private void setItasaOption(boolean hasItasaOption) {
        this.hasItasaOption = hasItasaOption;
    }

    public boolean hasTorrentOption() {
        return hasTorrentOption;
    }

    private void setTorrentOption(boolean hasTorrentOption) {
        this.hasTorrentOption = hasTorrentOption;
    }

    public String getLastDateTimeRefresh() {
        return lastDateTimeRefresh;
    }

    public void setLastDateTimeRefresh(String lastDateTimeRefresh) {
        this.lastDateTimeRefresh = lastDateTimeRefresh;
    }

    public String getMyitasaUsername() {
        if (myitasaUsername == null) {
            myitasaUsername = "";
        }
        return myitasaUsername;
    }

    public void setMyitasaUsername(String myitasaUsername) {
        this.myitasaUsername = myitasaUsername;
    }

    public String getMyitasaPassword() {
        if (myitasaPassword == null) {
            myitasaPassword = "";
        }
        return myitasaPassword;
    }

    public void setMyitasaPassword(String myitasaPassword) {
        this.myitasaPassword = myitasaPassword;
    }

    public String getItasaFeedURL() {
        if (itasaFeedURL == null) {
            itasaFeedURL = "";
        }
        return itasaFeedURL;
    }

    public void setItasaFeedURL(String itasaFeedURL) {
        this.itasaFeedURL = itasaFeedURL;
    }

    public String getMyitasaFeedURL() {
        if (myitasaFeedURL == null) {
            myitasaFeedURL = "";
        }
        return myitasaFeedURL;
    }

    public void setMyitasaFeedURL(String myitasaFeedURL) {
        this.myitasaFeedURL = myitasaFeedURL;
    }

    public String getSubsfactoryFeedURL() {
        return subsfactoryFeedURL;
    }

    public void setSubsfactoryFeedURL(String subsfactoryFeedURL) {
        this.subsfactoryFeedURL = subsfactoryFeedURL;
    }

    public boolean isAutoDownload() {
        return isAutoDownload;
    }

    public void setAutoDownload(boolean isAutoDownload) {
        this.isAutoDownload = isAutoDownload;
    }

    public String getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(String refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public String getSubtitleDestinationFolder() {
        return subtitleDestinationFolder;
    }

    public void setSubtitleDestinationFolder(String subtitleDestinationFolder) {
        this.subtitleDestinationFolder = subtitleDestinationFolder;
    }

    public boolean hasSubsfactoryOption() {
        return hasSubsfactoryOption;
    }

    private void setSubfactoryOption(boolean hasSubsfactoryOption) {
        this.hasSubsfactoryOption = hasSubsfactoryOption;
    }

    public String getSettingsFilename() {
        return SETTINGS_FILENAME;
    }

    public String getApplicationLookAndFeel() {
        if (applicationLookAndFeel == null) {
            applicationLookAndFeel = "";
        }
        return applicationLookAndFeel;
    }

    public void setApplicationLookAndFeel(String applicationLookAndFeel) {
        this.applicationLookAndFeel = applicationLookAndFeel;
    }

    public String getTorrentDestinationFolder() {
        return torrentDestinationFolder;
    }

    public void setTorrentDestinationFolder(String torrentDestinationFolder) {
        this.torrentDestinationFolder = torrentDestinationFolder;
    }

    public boolean isApplicationFirstTimeUsed() {
        return isApplicationFirstTimeUsed;
    }

    public String getCifsSharePath() {
        return cifsSharePath;
    }

    public void setCifsSharePath(String cifsSharePath) {
        this.cifsSharePath = cifsSharePath;
    }

    public String getCifsShareLocation() {
        return cifsShareLocation;
    }

    public void setCifsShareLocation(String cifsShareLocation) {
        this.cifsShareLocation = cifsShareLocation;
    }

    public String getCifsShareDomain() {
        return cifsShareDomain;
    }

    public void setCifsShareDomain(String cifsShareDomain) {
        this.cifsShareDomain = cifsShareDomain;
    }

    public String getCifsShareUsername() {
        return cifsShareUsername;
    }

    public void setCifsShareUsername(String cifsShareUsername) {
        this.cifsShareUsername = cifsShareUsername;
    }

    public String getCifsSharePassword() {
        return cifsSharePassword;
    }

    public void setCifsSharePassword(String cifsSharePassword) {
        this.cifsSharePassword = cifsSharePassword;
    }

    public boolean isLocalFolder() {
        return isLocalFolder;
    }

    public void localFolder(boolean isLocalFolder) {
        this.isLocalFolder = isLocalFolder;
    }

    public String getHttpTimeout() {
        return httpTimeout;
    }

    public void setHttpTimeout(String httpTimeout) {
        this.httpTimeout = httpTimeout;
    }

    public String getApplicationFont() {
        return applicationFont;
    }

    public void setApplicationFont(String applicationFont) {
        this.applicationFont = applicationFont;
    }

    public boolean enabledCustomDestinationFolder() {
        return enableCustomDestinationFolder;
    }

    public void enableCustomDestinationFolder(boolean enableCustomDestinationFolder) {
        this.enableCustomDestinationFolder = enableCustomDestinationFolder;
    }

    public boolean enabledIconizedRun() {
        return enableIconizedRun;
    }

    public void enableIconizedRun(boolean enableIconizedRun) {
        this.enableIconizedRun = enableIconizedRun;
    }

    public boolean enabledRunAtStartup() {
        return enableRunAtStartup;
    }

    public void enableRunAtStartup(boolean enableRunAtStartup) {
        this.enableRunAtStartup = enableRunAtStartup;
    }
}// end class