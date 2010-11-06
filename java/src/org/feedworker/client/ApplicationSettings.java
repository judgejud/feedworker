package org.feedworker.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import org.feedworker.util.ManageException;
import org.feedworker.util.ResourceLocator;
import org.jfacility.javax.crypto.DesEncrypter;

public class ApplicationSettings {

    private static ApplicationSettings instance = null;
    private final String SETTINGS_FILENAME = ResourceLocator.getWorkspace()
            + "settings.properties";
    private final File SETTINGS_FILE = new File(SETTINGS_FILENAME);
    private final String ENCRYPTION_PROPERTY_PWD = FeedWorkerClient.getApplication().getName() + "property";
    private final String ENCRYPTION_VALUE_PWD = FeedWorkerClient.getApplication().getName() + "value";
    private String itasaFeedURL, myitasaFeedURL, itasaUsername, itasaPassword,
            subtitleDestinationFolder, refreshInterval, lastDateTimeRefresh,
            applicationLookAndFeel, torrentDestinationFolder,
            cifsShareLocation, cifsSharePath, cifsShareUsername,
            cifsSharePassword, cifsShareDomain, subsfactoryFeedURL, mySubsfactoryFeedUrl,
            httpTimeout;
    private boolean subsfactoryOption, torrentOption, itasaOption,
            autoDownloadMyItasa, enableAudioAdvisor, applicationFirstTimeUsed,
            localFolder, enableIconizedRun, enableRunAtStartup,
            enableCustomDestinationFolder, autoLoadDownloadMyItasa;
    private Properties properties;
    private DesEncrypter propertyEncrypter, valueEncrypter;
    private ManageException error = ManageException.getIstance();

    /** Costruttore privato */
    private ApplicationSettings() {
        properties = new Properties();
        try {
            propertyEncrypter = new DesEncrypter(ENCRYPTION_PROPERTY_PWD);
            valueEncrypter = new DesEncrypter(ENCRYPTION_VALUE_PWD);

            if (SETTINGS_FILE.exists()) {
                // Read properties file.
                properties.load(new FileInputStream(SETTINGS_FILE));
                
                setItasaFeedURL(getDecryptedValue("ITASA_FEED_URL"));
                setMyitasaFeedURL(getDecryptedValue("MYITASA_FEED_URL"));
                setItasaUsername(getDecryptedValue("ITASA_USERNAME"));
                setItasaPassword(getDecryptedValue("ITASA_PASSWORD"));
                setSubsfactoryFeedURL(getDecryptedValue("SUBSFACTORY_FEED_URL"));
                setSubtitleDestinationFolder(getDecryptedValue("SUBTITLE_DESTINATION_FOLDER"));
                setSubfactoryOption(getBooleanDecryptedValue("SUBSFACTORY"));
                setRefreshInterval(getDecryptedValue("REFRESH_INTERVAL"));
                setAutoDownloadMyItasa(getBooleanDecryptedValue("IS_AUTO_DOWNLOAD_MYITASA"));
                setLastDateTimeRefresh(getDecryptedValue("LAST_DATETIME_REFRESH"));
                setApplicationLookAndFeel(getDecryptedValue("APPLICATION_LOOK_AND_FEEL"));
                setEnableAudioAdvisor(getBooleanDecryptedValue("ENABLE_AUDIO_ADVISOR"));
                setTorrentOption(getBooleanDecryptedValue("TORRENT"));
                applicationFirstTimeUsed = getBooleanDecryptedValue("IS_APPLICATION_FIRST_TIME_USED");
                setItasaOption(getBooleanDecryptedValue("ITALIANSUBS"));
                setLocalFolder(getBooleanDecryptedValue("IS_LOCAL_FOLDER"));
                setTorrentDestinationFolder(getDecryptedValue("TORRENT_DESTINATION_FOLDER"));
                setCifsSharePath(getDecryptedValue("CIFS_SHARE_PATH"));
                setCifsShareDomain(getDecryptedValue("CIFS_SHARE_DOMAIN"));
                setCifsShareLocation(getDecryptedValue("CIFS_SHARE_LOCATION"));
                setCifsSharePassword(getDecryptedValue("CIFS_SHARE_PASSWORD"));
                setCifsShareUsername(getDecryptedValue("CIFS_SHARE_USERNAME"));
                setHttpTimeout(getDecryptedValue("HTTP_TIMEOUT"));
                setEnableCustomDestinationFolder(getBooleanDecryptedValue("ENABLE_CUSTOM_DESTINATION_FOLDER"));
                setEnableIconizedRun(getBooleanDecryptedValue("ENABLE_ICONIZED_RUN"));
                setAutoLoadDownloadMyItasa(getBooleanDecryptedValue("IS_AUTO_LOAD_DOWNLOAD_MYITASA"));
                setMySubsfactoryFeedUrl(getDecryptedValue("MYSUBSFACTORY_FEED_URL"));
            } else {
                loadDefaultSettings();
                storeSettings();
            }
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
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

    private boolean getBooleanDecryptedValue(String property){
        return Boolean.parseBoolean(getDecryptedValue(property));
    }

    /** Scrive i settaggi su file */
    public void writeGeneralSettings() {
        try {
            propertiesCrypting("SUBTITLE_DESTINATION_FOLDER",
                    subtitleDestinationFolder);
            propertiesCrypting("REFRESH_INTERVAL", refreshInterval);
            propertiesCrypting("APPLICATION_LOOK_AND_FEEL",
                    applicationLookAndFeel);
            propertiesCrypting("ENABLE_AUDIO_ADVISOR", enableAudioAdvisor);
            propertiesCrypting("IS_LOCAL_FOLDER", localFolder);
            propertiesCrypting("CIFS_SHARE_PATH", cifsSharePath);
            propertiesCrypting("CIFS_SHARE_DOMAIN", cifsShareDomain);
            propertiesCrypting("CIFS_SHARE_LOCATION", cifsShareLocation);
            propertiesCrypting("CIFS_SHARE_PASSWORD", cifsSharePassword);
            propertiesCrypting("CIFS_SHARE_USERNAME", cifsShareUsername);
            propertiesCrypting("HTTP_TIMEOUT", httpTimeout);
            propertiesCrypting("ENABLE_CUSTOM_DESTINATION_FOLDER",
                    enableCustomDestinationFolder);
            propertiesCrypting("ENABLE_ICONIZED_RUN", enableIconizedRun);

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
            propertiesCrypting("ITASA_FEED_URL", itasaFeedURL);
            propertiesCrypting("MYITASA_FEED_URL", myitasaFeedURL);
            propertiesCrypting("ITASA_USERNAME", itasaUsername);
            propertiesCrypting("ITASA_PASSWORD", itasaPassword);
            propertiesCrypting("IS_AUTO_DOWNLOAD_MYITASA", autoDownloadMyItasa);
            propertiesCrypting("IS_AUTO_LOAD_DOWNLOAD_MYITASA", autoLoadDownloadMyItasa);
            storeSettings();
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }// end write

    void writeSubsfactorySettings() {
        try {
            propertiesCrypting("SUBSFACTORY_FEED_URL", subsfactoryFeedURL);
            propertiesCrypting("MYSUBSFACTORY_FEED_URL", mySubsfactoryFeedUrl);
            storeSettings();
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }

    void writeTorrentSettings() {
        try {
            propertiesCrypting("TORRENT_DESTINATION_FOLDER",
                    torrentDestinationFolder);
            storeSettings();
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }

    void writeApplicationFirstTimeUsedFalse() {
        applicationFirstTimeUsed = false;
        try {
            propertiesCrypting("IS_APPLICATION_FIRST_TIME_USED",
                    applicationFirstTimeUsed);
            storeSettings();
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }

    private void propertiesCrypting(String property, boolean value)
            throws GeneralSecurityException, IOException {
        properties.setProperty(propertyEncrypter.encrypt(property),
                valueEncrypter.encrypt(Boolean.toString(value)));
    }

    private void propertiesCrypting(String property, String value)
            throws GeneralSecurityException, IOException {

        properties.setProperty(propertyEncrypter.encrypt(property),
                valueEncrypter.encrypt(value));
    }

    // Write properties file.
    private void storeSettings() throws IOException {
        properties.store(new FileOutputStream(SETTINGS_FILENAME), null);
    }

    private void loadDefaultSettings() {
        itasaOption = true;
        subsfactoryOption = true;
        torrentOption = true;
        applicationFirstTimeUsed = true;
        applicationLookAndFeel = "Synthetica Standard";
        try {
            propertiesCrypting("ITALIANSUBS", itasaOption);
            propertiesCrypting("IS_APPLICATION_FIRST_TIME_USED",
                    applicationFirstTimeUsed);
            propertiesCrypting("TORRENT", torrentOption);
            propertiesCrypting("SUBSFACTORY", subsfactoryOption);
            propertiesCrypting("APPLICATION_LOOK_AND_FEEL", applicationLookAndFeel);
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }

    /** scrive solo l'ultima data dell'aggiornamento rss */
    public void writeOnlyLastDate() {
        try {
            propertiesCrypting("LAST_DATETIME_REFRESH", lastDateTimeRefresh);
            storeSettings();
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }// end writeOnlyLastDate

    public boolean isEnabledAudioAdvisor() {
        return enableAudioAdvisor;
    }

    public void setEnableAudioAdvisor(boolean enableAudioAdvisor) {
        this.enableAudioAdvisor = enableAudioAdvisor;
    }

    public boolean isItasaOption() {
        return itasaOption;
    }

    private void setItasaOption(boolean hasItasaOption) {
        this.itasaOption = hasItasaOption;
    }

    public boolean isTorrentOption() {
        return torrentOption;
    }

    private void setTorrentOption(boolean hasTorrentOption) {
        this.torrentOption = hasTorrentOption;
    }

    public String getLastDateTimeRefresh() {
        return lastDateTimeRefresh;
    }

    public void setLastDateTimeRefresh(String lastDateTimeRefresh) {
        this.lastDateTimeRefresh = lastDateTimeRefresh;
    }

    public String getItasaUsername() {
        if (itasaUsername == null) {
            itasaUsername = "";
        }
        return itasaUsername;
    }

    public void setItasaUsername(String itasaUsername) {
        this.itasaUsername = itasaUsername;
    }

    public String getItasaPassword() {
        if (itasaPassword == null) {
            itasaPassword = "";
        }
        return itasaPassword;
    }

    public void setItasaPassword(String itasaPassword) {
        this.itasaPassword = itasaPassword;
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

    public boolean isAutoDownloadMyItasa() {
        return autoDownloadMyItasa;
    }

    public void setAutoDownloadMyItasa(boolean isAutoDownload) {
        this.autoDownloadMyItasa = isAutoDownload;
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

    public boolean isSubsfactoryOption() {
        return subsfactoryOption;
    }

    private void setSubfactoryOption(boolean subsfactoryOption) {
        this.subsfactoryOption = subsfactoryOption;
    }

    public String getSettingsFilename() {
        return SETTINGS_FILENAME;
    }

    public String getApplicationLookAndFeel() {
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
        return applicationFirstTimeUsed;
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
        return localFolder;
    }

    public void setLocalFolder(boolean isLocalFolder) {
        this.localFolder = isLocalFolder;
    }

    public String getHttpTimeout() {
        return httpTimeout;
    }

    public void setHttpTimeout(String httpTimeout) {
        this.httpTimeout = httpTimeout;
    }

    public boolean isEnabledCustomDestinationFolder() {
        return enableCustomDestinationFolder;
    }

    public void setEnableCustomDestinationFolder(
            boolean enableCustomDestinationFolder) {
        this.enableCustomDestinationFolder = enableCustomDestinationFolder;
    }

    public boolean isEnabledIconizedRun() {
        return enableIconizedRun;
    }

    public void setEnableIconizedRun(boolean enableIconizedRun) {
        this.enableIconizedRun = enableIconizedRun;
    }

    public boolean isEnabledRunAtStartup() {
        return enableRunAtStartup;
    }

    public void setEnableRunAtStartup(boolean enableRunAtStartup) {
        this.enableRunAtStartup = enableRunAtStartup;
    }

    public boolean isAutoLoadDownloadMyItasa() {
        return autoLoadDownloadMyItasa;
    }

    public void setAutoLoadDownloadMyItasa(boolean autoLoadDownloadMyItasa) {
        this.autoLoadDownloadMyItasa = autoLoadDownloadMyItasa;
    }

    public String getMySubsfactoryFeedUrl() {
        return mySubsfactoryFeedUrl;
    }

    public void setMySubsfactoryFeedUrl(String mySubsfactoryFeedUrl) {
        this.mySubsfactoryFeedUrl = mySubsfactoryFeedUrl;
    }
}// end class