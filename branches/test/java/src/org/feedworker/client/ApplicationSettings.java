package org.feedworker.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import org.feedworker.exception.ManageException;
import org.feedworker.util.ResourceLocator;
import org.jfacility.javax.crypto.DesEncrypter;

public class ApplicationSettings {
    private static ApplicationSettings instance = null;
    private final String SETTINGS_FILENAME = ResourceLocator.getWorkspace()
            + "settings.properties";
    private final File SETTINGS_FILE = new File(SETTINGS_FILENAME);
    private final String ENCRYPTION_PROPERTY_PWD = 
            FeedWorkerClient.getApplication().getName() + "property";
    private final String ENCRYPTION_VALUE_PWD = 
            FeedWorkerClient.getApplication().getName() + "value";
    private String itasaFeedURL, myitasaFeedURL, itasaUsername, itasaPassword,
            subtitleDestinationFolder, refreshInterval, lastDateTimeRefresh,
            applicationLookAndFeel, torrentDestinationFolder,
            cifsShareLocation, cifsSharePath, cifsShareUsername,
            cifsSharePassword, cifsShareDomain, subsfactoryFeedURL, mySubsfactoryFeedUrl,
            httpTimeout, mailTO, mailSMTP, googleUser, googlePwd, googleCalendar, ircNick, ircPwd;
    private boolean subsfactoryOption, torrentOption, 
            autoDownloadMyItasa, enableNotifyAudioRss, enableNotifyAudioSub, 
            applicationFirstTimeUsed, localFolder, enableIconizedRun, 
            enableRunAtStartup, enableAdvancedDownload, autoLoadDownloadMyItasa, 
            enableNotifyMail, enablePaneLog, enablePaneSetting, enablePaneSubDestination, 
            enablePaneSearchSubItasa, enablePaneReminder, reminderOption, enablePaneTorrent,
            enablePaneCalendar, enableNotifySms, enablePaneShow, itasaBlog, enablePaneBlog, 
            itasaPM, calendarDay, enablePaneCalendarDay, enablePaneIrc, itasaNews, 
            showNoDuplicateAll, showNoDuplicateSingle;
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
                applicationFirstTimeUsed = 
                        getBooleanDecryptedValue("IS_APPLICATION_FIRST_TIME_USED");
                //GENERAL
                setReminderOption(getBooleanDecryptedValue("ENABLE_REMINDER"));
                setSubtitleDestinationFolder(
                                getDecryptedValue("SUBTITLE_DESTINATION_FOLDER"));
                setRefreshInterval(getDecryptedValue("REFRESH_INTERVAL"));
                setLastDateTimeRefresh(getDecryptedValue("LAST_DATETIME_REFRESH"));
                applicationLookAndFeel = getDecryptedValue("APPLICATION_LOOK_AND_FEEL");
                setLocalFolder(getBooleanDecryptedValue("IS_LOCAL_FOLDER"));
                setHttpTimeout(getDecryptedValue("HTTP_TIMEOUT"));
                setEnableAdvancedDownload(
                            getBooleanDecryptedValue("ENABLE_ADVANCED_DOWNLOAD"));
                setEnableIconizedRun(getBooleanDecryptedValue("ENABLE_ICONIZED_RUN"));
                //ITASA
                setItasaFeedURL(getDecryptedValue("ITASA_FEED_URL"));
                setMyitasaFeedURL(getDecryptedValue("MYITASA_FEED_URL"));
                setItasaUsername(getDecryptedValue("ITASA_USERNAME"));
                setItasaPassword(getDecryptedValue("ITASA_PASSWORD"));
                setAutoDownloadMyItasa(
                            getBooleanDecryptedValue("IS_AUTO_DOWNLOAD_MYITASA"));
                setAutoLoadDownloadMyItasa(
                        getBooleanDecryptedValue("IS_AUTO_LOAD_DOWNLOAD_MYITASA"));
                setItasaBlog(getBooleanDecryptedValue("ITASA_BLOG"));
                setItasaPM(getBooleanDecryptedValue("ITASA_PM"));
                setItasaNews(getBooleanDecryptedValue("ITASA_NEWS"));
                setCalendarDay(getBooleanDecryptedValue("CALENDAR_DAY"));
                //SUBSFATORY
                setSubsfactoryFeedURL(getDecryptedValue("SUBSFACTORY_FEED_URL"));
                setSubfactoryOption(getBooleanDecryptedValue("SUBSFACTORY"));
                setMySubsfactoryFeedUrl(getDecryptedValue("MYSUBSFACTORY_FEED_URL"));
                //TORRENT
                setTorrentOption(getBooleanDecryptedValue("TORRENT"));
                setTorrentDestinationFolder(
                                getDecryptedValue("TORRENT_DESTINATION_FOLDER"));
                //SAMBA-CIFS
                setCifsSharePath(getDecryptedValue("CIFS_SHARE_PATH"));
                setCifsShareDomain(getDecryptedValue("CIFS_SHARE_DOMAIN"));
                setCifsShareLocation(getDecryptedValue("CIFS_SHARE_LOCATION"));
                setCifsSharePassword(getDecryptedValue("CIFS_SHARE_PASSWORD"));
                setCifsShareUsername(getDecryptedValue("CIFS_SHARE_USERNAME"));
                //VISIBLE PANE
                setEnablePaneCalendar(getBooleanDecryptedValue("ENABLE_PANE_CALENDAR"));
                setEnablePaneLog(getBooleanDecryptedValue("ENABLE_PANE_LOG"));
                setEnablePaneReminder(getBooleanDecryptedValue("ENABLE_PANE_REMINDER"));
                setEnablePaneSearchSubItasa(
                            getBooleanDecryptedValue("ENABLE_PANE_SEARCH_SUB_ITASA"));
                setEnablePaneSetting(getBooleanDecryptedValue("ENABLE_PANE_SETTING"));
                setEnablePaneSubDestination(
                                    getBooleanDecryptedValue("ENABLE_PANE_SUB_DEST"));
                setEnablePaneTorrent(getBooleanDecryptedValue("ENABLE_PANE_TORRENT"));
                setEnablePaneShow(getBooleanDecryptedValue("ENABLE_PANE_SHOW"));
                setEnablePaneBlog(getBooleanDecryptedValue("ENABLE_PANE_BLOG"));
                setEnablePaneCalendarDay(getBooleanDecryptedValue("ENABLE_PANE_CALENDAR_DAY"));
                setEnablePaneIrc(getBooleanDecryptedValue("ENABLE_PANE_IRC"));
                //ADVISOR SETTINGS
                setMailTO(getDecryptedValue("MAIL_TO"));
                setMailSMTP(getDecryptedValue("MAIL_SMTP"));
                setGoogleCalendar(getDecryptedValue("GOOGLE_CALENDAR"));
                setGooglePwd(getDecryptedValue("GOOGLE_PWD"));
                setGoogleUser(getDecryptedValue("GOOGLE_USER"));
                //NOTIFY SETTINGS
                setEnableNotifyAudioRss(getBooleanDecryptedValue("ENABLE_NOTIFY_AUDIO_RSS"));
                setEnableNotifyAudioSub(getBooleanDecryptedValue("ENABLE_NOTIFY_AUDIO_SUB"));
                setEnableNotifyMail(getBooleanDecryptedValue("ENABLE_NOTIFY_MAIL"));
                setEnableNotifySms(getBooleanDecryptedValue("ENABLE_NOTIFY_SMS"));
                //IRC SETTINGS
                setIrcNick(getDecryptedValue("IRC_NICK"));
                setIrcPwd(getDecryptedValue("IRC_PWD"));
                //SHOW SETTINGS 
                setShowNoDuplicateAll(getBooleanDecryptedValue("ENABLE_SHOW_CONTROL_ALL_DUPLICATE"));
                setShowNoDuplicateSingle(getBooleanDecryptedValue("ENABLE_SHOW_CONTROL_SINGLE_DUPLICATE"));
            } else
                loadDefaultSettings();
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
            if (value != null)
                return valueEncrypter.decrypt(value);
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

    public void writeGeneralSettings() {
        try {
            propertiesCrypting("SUBTITLE_DESTINATION_FOLDER",
                                                    subtitleDestinationFolder);
            propertiesCrypting("REFRESH_INTERVAL", refreshInterval);
            propertiesCrypting("IS_LOCAL_FOLDER", localFolder);
            propertiesCrypting("CIFS_SHARE_PATH", cifsSharePath);
            propertiesCrypting("CIFS_SHARE_DOMAIN", cifsShareDomain);
            propertiesCrypting("CIFS_SHARE_LOCATION", cifsShareLocation);
            propertiesCrypting("CIFS_SHARE_PASSWORD", cifsSharePassword);
            propertiesCrypting("CIFS_SHARE_USERNAME", cifsShareUsername);
            propertiesCrypting("HTTP_TIMEOUT", httpTimeout);
            propertiesCrypting("ENABLE_ADVANCED_DOWNLOAD",enableAdvancedDownload);
            propertiesCrypting("ENABLE_ICONIZED_RUN", enableIconizedRun);
            propertiesCrypting("ENABLE_REMINDER", reminderOption);
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }// end write

    public void writeItasaSettings() {
        try {
            propertiesCrypting("ITASA_FEED_URL", itasaFeedURL);
            propertiesCrypting("MYITASA_FEED_URL", myitasaFeedURL);
            propertiesCrypting("ITASA_USERNAME", itasaUsername);
            propertiesCrypting("ITASA_PASSWORD", itasaPassword);
            propertiesCrypting("IS_AUTO_DOWNLOAD_MYITASA", autoDownloadMyItasa);
            propertiesCrypting("IS_AUTO_LOAD_DOWNLOAD_MYITASA", autoLoadDownloadMyItasa);
            propertiesCrypting("ITASA_BLOG", itasaBlog);
            propertiesCrypting("ITASA_PM", itasaPM);
            propertiesCrypting("CALENDAR_DAY", calendarDay);
            propertiesCrypting("ITASA_NEWS", itasaNews);
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }// end write

    public void writeSubsfactorySettings() {
        try {
            propertiesCrypting("SUBSFACTORY_FEED_URL", subsfactoryFeedURL);
            propertiesCrypting("MYSUBSFACTORY_FEED_URL", mySubsfactoryFeedUrl);
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }

    public void writeTorrentSettings() {
        try {
            propertiesCrypting("TORRENT_DESTINATION_FOLDER",
                    torrentDestinationFolder);
            propertiesCrypting("TORRENT", torrentOption);
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }
    
    public void writeAdvisorSettings() {
        try {
            propertiesCrypting("MAIL_TO", mailTO);
            propertiesCrypting("MAIL_SMTP", mailSMTP);
            propertiesCrypting("GOOGLE_CALENDAR", googleCalendar);
            propertiesCrypting("GOOGLE_PWD", googlePwd);
            propertiesCrypting("GOOGLE_USER", googleUser);
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }// end write
    
    public void writeNotifySettings() {
        try {
            propertiesCrypting("ENABLE_NOTIFY_AUDIO_RSS", enableNotifyAudioRss);
            propertiesCrypting("ENABLE_NOTIFY_AUDIO_SUB", enableNotifyAudioSub);
            propertiesCrypting("ENABLE_NOTIFY_MAIL", enableNotifyMail);
            propertiesCrypting("ENABLE_NOTIFY_SMS", enableNotifySms);
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }// end write
    
    public void writeIrcSettings() {
        try {
            propertiesCrypting("IRC_NICK", ircNick);
            propertiesCrypting("IRC_PWD", ircPwd);
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }// end write
    
    public void writeShowSettings() {
        try {
            propertiesCrypting("ENABLE_SHOW_CONTROL_ALL_DUPLICATE", showNoDuplicateAll);
            propertiesCrypting("ENABLE_SHOW_CONTROL_SINGLE_DUPLICATE", showNoDuplicateSingle);
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }// end write
    
    public void writePaneVisibleSetting() {
        try {
            propertiesCrypting("ENABLE_PANE_CALENDAR", enablePaneCalendar);
            propertiesCrypting("ENABLE_PANE_LOG", enablePaneLog);
            propertiesCrypting("ENABLE_PANE_SETTING", enablePaneSetting);
            propertiesCrypting("ENABLE_PANE_SUB_DEST", enablePaneSubDestination);
            propertiesCrypting("ENABLE_PANE_SEARCH_SUB_ITASA", enablePaneSearchSubItasa);
            propertiesCrypting("ENABLE_PANE_REMINDER", enablePaneReminder);
            propertiesCrypting("ENABLE_PANE_TORRENT", enablePaneTorrent);
            propertiesCrypting("ENABLE_PANE_SHOW", enablePaneShow);
            propertiesCrypting("ENABLE_PANE_BLOG", enablePaneBlog);
            propertiesCrypting("ENABLE_PANE_CALENDAR_DAY", enablePaneCalendarDay);
            propertiesCrypting("ENABLE_PANE_IRC", enablePaneIrc);
        } catch (GeneralSecurityException e) {
            error.launch(e, getClass());
        } catch (IOException e) {
            error.launch(e, getClass(), null);
        }
    }// end write

    public void writeApplicationFirstTimeUsedFalse() {
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

    private void propertiesCrypting(String property, boolean value) throws 
                                            GeneralSecurityException, IOException {
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
        subsfactoryOption = true;
        applicationFirstTimeUsed = true;
        applicationLookAndFeel = "Synthetica Standard";
        enablePaneLog = true;
        enablePaneSetting = true;
        try {
            propertiesCrypting("IS_APPLICATION_FIRST_TIME_USED",
                    applicationFirstTimeUsed);
            propertiesCrypting("SUBSFACTORY", subsfactoryOption);
            storeSettings();
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

    public boolean isEnableNotifyAudioRss() {
        return enableNotifyAudioRss;
    }

    public void setEnableNotifyAudioRss(boolean enableAdvisor) {
        enableNotifyAudioRss = enableAdvisor;
    }
    public boolean isEnableNotifyAudioSub() {
        return enableNotifyAudioSub;
    }

    public void setEnableNotifyAudioSub(boolean enableAdvisor) {
        enableNotifyAudioSub = enableAdvisor;
    }
    
    public boolean isEnableNotifyMail() {
        return enableNotifyMail;
    }

    public void setEnableNotifyMail(boolean enableAdvisor) {
        enableNotifyMail = enableAdvisor;
    }

    public boolean isEnableNotifySms() {
        return enableNotifySms;
    }

    public void setEnableNotifySms(boolean enableNotifySms) {
        this.enableNotifySms = enableNotifySms;
    }

    public boolean isTorrentOption() {
        return torrentOption;
    }

    public void setTorrentOption(boolean hasTorrentOption) {
        this.torrentOption = hasTorrentOption;
    }

    public String getLastDateTimeRefresh() {
        return lastDateTimeRefresh;
    }

    public void setLastDateTimeRefresh(String lastDateTimeRefresh) {
        this.lastDateTimeRefresh = lastDateTimeRefresh;
    }
    
    public String getMailTO() {
        return mailTO;
    }

    public void setMailTO(String mail) {
        this.mailTO = mail;
    }

    public String getMailSMTP() {
        return mailSMTP;
    }

    public void setMailSMTP(String mailSMTP) {
        this.mailSMTP = mailSMTP;
    }
    
    public String getItasaUsername() {
        if (itasaUsername == null)
            itasaUsername = "";
        return itasaUsername;
    }

    public void setItasaUsername(String itasaUsername) {
        this.itasaUsername = itasaUsername;
    }

    public String getItasaPassword() {
        if (itasaPassword == null)
            itasaPassword = "";
        return itasaPassword;
    }

    public void setItasaPassword(String itasaPassword) {
        this.itasaPassword = itasaPassword;
    }

    public String getItasaFeedURL() {
        if (itasaFeedURL == null)
            itasaFeedURL = "";
        return itasaFeedURL;
    }

    public void setItasaFeedURL(String itasaFeedURL) {
        this.itasaFeedURL = itasaFeedURL;
    }

    public String getMyitasaFeedURL() {
        if (myitasaFeedURL == null)
            myitasaFeedURL = "";
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

    public void setApplicationLookAndFeel(String laf) throws 
                                            GeneralSecurityException, IOException {
        propertiesCrypting("APPLICATION_LOOK_AND_FEEL", laf);
        storeSettings();
        applicationLookAndFeel = laf;
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

    public boolean isEnabledAdvancedDownload() {
        return enableAdvancedDownload;
    }

    public void setEnableAdvancedDownload(boolean flag) {
        enableAdvancedDownload = flag;
    }

    public boolean isEnabledIconizedRun() {
        return enableIconizedRun;
    }

    public void setEnableIconizedRun(boolean flag) {
        this.enableIconizedRun = flag;
    }

    public boolean isEnabledRunAtStartup() {
        return enableRunAtStartup;
    }

    public void setEnableRunAtStartup(boolean flag) {
        this.enableRunAtStartup = flag;
    }

    public boolean isAutoLoadDownloadMyItasa() {
        return autoLoadDownloadMyItasa;
    }

    public void setAutoLoadDownloadMyItasa(boolean flag) {
        this.autoLoadDownloadMyItasa = flag;
    }

    public String getMySubsfactoryFeedUrl() {
        return mySubsfactoryFeedUrl;
    }

    public void setMySubsfactoryFeedUrl(String mySubsfactoryFeedUrl) {
        this.mySubsfactoryFeedUrl = mySubsfactoryFeedUrl;
    }

    public boolean isEnablePaneLog() {
        return enablePaneLog;
    }

    public void setEnablePaneLog(boolean enablePaneLog) {
        this.enablePaneLog = enablePaneLog;
    }

    public boolean isEnablePaneSearchSubItasa() {
        return enablePaneSearchSubItasa;
    }

    public void setEnablePaneSearchSubItasa(boolean enablePaneSearchSubItasa) {
        this.enablePaneSearchSubItasa = enablePaneSearchSubItasa;
    }

    public boolean isEnablePaneSetting() {
        return enablePaneSetting;
    }

    public void setEnablePaneSetting(boolean enablePaneSetting) {
        this.enablePaneSetting = enablePaneSetting;
    }

    public boolean isEnablePaneSubDestination() {
        return enablePaneSubDestination;
    }

    public void setEnablePaneSubDestination(boolean enablePaneSubDestination) {
        this.enablePaneSubDestination = enablePaneSubDestination;
    }

    public boolean isEnablePaneReminder() {
        return enablePaneReminder;
    }

    public void setEnablePaneReminder(boolean enablePaneReminder) {
        this.enablePaneReminder = enablePaneReminder;
    }

    public boolean isReminderOption() {
        return reminderOption;
    }

    public void setReminderOption(boolean reminderOption) {
        this.reminderOption = reminderOption;
    }

    public String getGoogleCalendar() {
        return googleCalendar;
    }

    public void setGoogleCalendar(String googleCalendar) {
        this.googleCalendar = googleCalendar;
    }

    public String getGooglePwd() {
        return googlePwd;
    }

    public void setGooglePwd(String googlePwd) {
        this.googlePwd = googlePwd;
    }

    public String getGoogleUser() {
        return googleUser;
    }

    public void setGoogleUser(String googleUser) {
        this.googleUser = googleUser;
    }

    public boolean isEnablePaneTorrent() {
        return enablePaneTorrent;
    }

    public void setEnablePaneTorrent(boolean enablePaneTorrent) {
        this.enablePaneTorrent = enablePaneTorrent;
    }

    public boolean isEnablePaneCalendar() {
        return enablePaneCalendar;
    }

    public void setEnablePaneCalendar(boolean enablePaneCalendar) {
        this.enablePaneCalendar = enablePaneCalendar;
    }

    public boolean isEnablePaneShow() {
        return enablePaneShow;
    }

    public void setEnablePaneShow(boolean enablePaneShow) {
        this.enablePaneShow = enablePaneShow;
    }

    public boolean isItasaBlog() {
        return itasaBlog;
    }

    public void setItasaBlog(boolean itasaBlog) {
        this.itasaBlog = itasaBlog;
    }

    public boolean isEnablePaneBlog() {
        return enablePaneBlog;
    }

    public void setEnablePaneBlog(boolean enablePaneBlog) {
        this.enablePaneBlog = enablePaneBlog;
    }

    public boolean isItasaPM() {
        return itasaPM;
    }

    public void setItasaPM(boolean itasaPM) {
        this.itasaPM = itasaPM;
    }

    public boolean isCalendarDay() {
        return calendarDay;
    }

    public void setCalendarDay(boolean calendarDay) {
        this.calendarDay = calendarDay;
    }

    public boolean isEnablePaneCalendarDay() {
        return enablePaneCalendarDay;
    }

    public void setEnablePaneCalendarDay(boolean enablePaneCalendarDay) {
        this.enablePaneCalendarDay = enablePaneCalendarDay;
    }

    public boolean isEnablePaneIrc() {
        return enablePaneIrc;
    }

    public void setEnablePaneIrc(boolean enablePaneIrc) {
        this.enablePaneIrc = enablePaneIrc;
    }

    public String getIrcNick() {
        return ircNick;
    }

    public void setIrcNick(String ircNick) {
        this.ircNick = ircNick;
    }

    public String getIrcPwd() {
        return ircPwd;
    }

    public void setIrcPwd(String ircPwd) {
        this.ircPwd = ircPwd;
    }
    
    public boolean isItasaNews() {
        return itasaNews;
    }

    public void setItasaNews(boolean itasaNews) {
        this.itasaNews = itasaNews;
    }

    public boolean isShowNoDuplicateAll() {
        return showNoDuplicateAll;
    }

    public void setShowNoDuplicateAll(boolean showNoDuplicateAll) {
        this.showNoDuplicateAll = showNoDuplicateAll;
    }

    public boolean isShowNoDuplicateSingle() {
        return showNoDuplicateSingle;
    }

    public void setShowNoDuplicateSingle(boolean showNoDuplicateSingle) {
        this.showNoDuplicateSingle = showNoDuplicateSingle;
    }
}// end class