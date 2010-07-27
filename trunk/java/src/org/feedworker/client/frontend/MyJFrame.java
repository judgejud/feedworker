package org.feedworker.client.frontend;
//IMPORT JAVA
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
//IMPORT JAVAX
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
//IMPORT JRSS2SUB
import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.frontend.events.MyJFrameEvent;
import org.feedworker.client.frontend.events.MyJFrameEventListener;
import org.feedworker.util.Convert;
//IMPORT MYUTILS
import org.lp.myUtils.lang.Lang;
/**Gui base per java 1.5
 *
 * @author luca judge
 */
public class MyJFrame extends JFrame implements WindowListener, MyJFrameEventListener {
    protected final String SUB_RSS = "favicon.gif";

    private final Dimension SCREENSIZE = new Dimension(1024, 768);
    private final Dimension TABBEDSIZE = new Dimension(1024, 580);
    private final String AUTHORNAME = "Luka Judge";
    private final String BUILD = "172";
    private final String NAME = "jRss2Sub 1.06";

    protected paneSetting jpSettings;
    protected JTabbedPane jtabbedpane;   
    protected textpaneLog jtpLog;
    
    private Mediator proxy = Mediator.getIstance();
    private ApplicationSettings prop = ApplicationSettings.getIstance();
    private paneItasa jpItasa;
    private paneTorrent jpTorrent;
    private paneSubsf jpSubsf;
    /**Costruttore*/
    public MyJFrame(){
        super();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setPreferredSize(SCREENSIZE);
        this.setMinimumSize(SCREENSIZE);
        this.setTitle(NAME + " build " + BUILD + " by " + AUTHORNAME);
        this.setIconImage(Convert.getResourceImage(SUB_RSS));
        initComponents();
        initMenuBar();
        initListeners();
        this.setVisible(true);
    }

    /**Inizializzo i componenti per la GUI */
    private void initComponents() {
        this.setLayout(new BorderLayout());
        jtabbedpane = new JTabbedPane();
        jtabbedpane.setPreferredSize(TABBEDSIZE);
        this.add(jtabbedpane, BorderLayout.CENTER);
        jpSettings = paneSetting.getPanel();
        if (prop.hasItasaOption()){
            jpItasa = paneItasa.getPanel();
            jtabbedpane.addTab("ItalianSubs", jpItasa);
        }
        if (prop.hasSubsfactoryOption()){
            jpSubsf = paneSubsf.getPanel();
            jtabbedpane.addTab("SubsFactory", jpSubsf);
        }
        if (prop.hasTorrentOption()){
            jpTorrent = paneTorrent.getPanel();
            jtabbedpane.addTab("Torrent", jpTorrent);
        }
        if (prop.enabledCustomDestinationFolder())
            jtabbedpane.addTab("Destinazione Sub", paneRole.getPanel());
        jtabbedpane.addTab("Impostazioni", jpSettings);

        jtpLog = new textpaneLog();
        JScrollPane jScrollText1 = new JScrollPane(jtpLog);
        jScrollText1.setPreferredSize(new Dimension(1000,140));
        add(jScrollText1, BorderLayout.SOUTH);

        jtpLog.appendOK("Versione java in uso: " + Lang.getJavaVersion());

        if (prop.isFirstTimeRun()){
            jtabbedpane.setSelectedComponent(jpSettings);
            changeEnabledButton(false);
            jtpLog.appendOK("Benvenuto al primo utilizzo.");
            jtpLog.appendAlert("Per poter usare il client, " +
                    "devi configurare le impostazioni presenti nella specifica sezione");
        } else {
            jpSettings.settingsValue();            
            jtpLog.appendOK("Ciao " + prop.getMyitasaUsername());
            jtpLog.appendOK("Impostazioni caricate da " + prop.getSettingsFilename());
        }
        
        pack();
    }
    /**inizializza la barra di menù*/
    private void initMenuBar(){
        // Create the menu bar
        JMenuBar menuBar = new JMenuBar();
        // Create a menu
        JMenu menu_Jrss = new JMenu(" jRss2sub ");
        menuBar.add(menu_Jrss);
        JMenuItem itemJrss01 = new JMenuItem(" Pulisci log ");
        itemJrss01.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jtpLog.setText(null);
            }
        });
        menu_Jrss.add(itemJrss01);

        JMenuItem itemJrssWriteXML = new JMenuItem("Salva regole su file");
        itemJrssWriteXML.setEnabled(false);
        itemJrssWriteXML.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        menu_Jrss.add(itemJrssWriteXML);

        JMenuItem itemJrssClose = new JMenuItem(" Chiudi ");
        itemJrssClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                appClosing();
            }
        });
        menu_Jrss.add(itemJrssClose);

        JMenu menu_Laf = new JMenu(" Look & Feel ");
        menuBar.add(menu_Laf);
        JCheckBoxMenuItem[] itemsLaf = new JCheckBoxMenuItem[7];
        itemsLaf[0] = new JCheckBoxMenuItem("Standard");
        itemsLaf[1] = new JCheckBoxMenuItem("Blackmoon");
        itemsLaf[2] = new JCheckBoxMenuItem("Blackstar");
        itemsLaf[3] = new JCheckBoxMenuItem("Blueice");
        itemsLaf[4] = new JCheckBoxMenuItem("Bluesteel");
        itemsLaf[5] = new JCheckBoxMenuItem("GreenDream");
        itemsLaf[6] = new JCheckBoxMenuItem("Silvermoon");
        ButtonGroup bg = new ButtonGroup();
        for (int i=0; i<itemsLaf.length; i++){
            bg.add(itemsLaf[i]);
            menu_Laf.add(itemsLaf[i]);
        }
        
        JMenu menu_syno = new JMenu(" Synology ");
        JMenuItem itemSynoVideoMove = new JMenuItem("Sposta video");
        JMenuItem itemSynoStatus = new JMenuItem("Stato");
        JMenuItem itemSynoClearFinish = new JMenuItem("Elimina task completati");
        itemSynoVideoMove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.synoMoveVideo();
            }
        });
        itemSynoStatus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.synoStatus();
            }
        });
        itemSynoClearFinish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.synoClearFinish();
            }
        });
        menu_syno.add(itemSynoVideoMove);
        menu_syno.add(itemSynoStatus);
        menu_syno.add(itemSynoClearFinish);
        menuBar.add(menu_syno);

        JMenu menu_Help = new JMenu(" Help ");
        menuBar.add(menu_Help);
        JMenuItem itemHelpSettingGlobal = new JMenuItem(" Impostazioni globali ");
        JMenuItem itemHelpSettingItasa  = new JMenuItem(" Impostazioni itasa ");
        JMenuItem itemHelpRole = new JMenuItem(" Regola sub ");
        itemHelpRole.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(getParent(), helpRoleSub(),
                        "Come creare le regole per i sottotitoli", JOptionPane.PLAIN_MESSAGE);
            }
        });
        menu_Help.add(itemHelpRole);
        // Install the menu bar in the frame
        setJMenuBar(menuBar);
    }
    /**inizializza il pannello per l'help regola sub
     *
     * @return pannello helprolesub
     */
    private JPanel helpRoleSub(){
        Dimension dim = new Dimension(500,300);
        JPanel jpanel = new JPanel();
        jpanel.setPreferredSize(dim);
        jpanel.setVisible(true);
        JTextArea jtaHelp = new JTextArea();
        jtaHelp.setPreferredSize(dim);
        jtaHelp.append("Serie: inserire il nome del file scaricato sostituendo i \".\" con lo spazio");
        jtaHelp.append("\nesempio: Lost.s06e06.sub.itasa.srt -->lost (n.b. no spazio)");
        jtaHelp.append("\nesempio: Spartacus.Blood.And.Sand.s01e06.sub.itasa.srt -->" +
                "spartacus blood and sand");
        jtaHelp.append("\n\nStagione: immettere il numero della stagione per la quale si vuole " +
                "creare la regola");
        jtaHelp.append("\nad eccezione degli anime/cartoon dove la stagione molto probabilmente è " +
                "unica e quindi 1");
        jtaHelp.append("\n\nQualità: * = tutte le versioni dei sub ; normale = sub qualità standard");
        jtaHelp.append("\n720p = versione 720p; 1080p = versione 1080p; dvdrip = versione dvdrip");
        jtaHelp.append("\nbluray = versione bluray; hr = versione hr");
        jtaHelp.append("\n\\ = se avete già impostato una regola per una versione e volete che le " +
                "altre versioni");
        jtaHelp.append("\nvadino in un'altra cartella basterà selezionare questa opzione " +
                "\"differenziale\"");
        jtaHelp.append("\n\nPercorso: specificare il percorso dove desiderate che vi estragga il sub");
        jpanel.add(jtaHelp);
        return jpanel;
    }
    /**chiude l'applicazione*/
    protected void appClosing(){
        String temp = jpSettings.getDataAggiornamento();
        this.dispose();
        proxy.closeApp(temp);
    }
    @Override
    public void windowClosing(WindowEvent e) {
        int returnCode = JOptionPane.showConfirmDialog(this,
                    "Sei sicuro di voler uscire?", "Info",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (returnCode == 1) {
            return;
        }
        appClosing();
    }               
    @Override
    public void objReceived(MyJFrameEvent evt) {
        if (evt.getDate()!=null)
            jpSettings.setDataAggiornamento(evt.getDate());
        if (evt.getOperaz()!=null){
            if (evt.getOperaz().equalsIgnoreCase("ADD_PANE_RULEZ"))
                jtabbedpane.addTab("Destinazione avanzata",paneRole.getPanel());
            else if (evt.getOperaz().equalsIgnoreCase("REMOVE_PANE_RULEZ"))
                jtabbedpane.remove(paneRole.getPanel());
            else if (evt.getOperaz().equalsIgnoreCase("ENABLED_BUTTON"))
                changeEnabledButton(true);
        }
    }
    /**risponde all'evento di cambiare lo stato (abilitato/disabilitato) dei bottoni
     *
     * @param e stato
     */
    protected void changeEnabledButton(boolean e){
        if (prop.hasItasaOption())
            jpItasa.setButtonEnabled(e);
        if (prop.hasSubsfactoryOption())
            jpSubsf.setEnableButton(e);
        if (prop.hasTorrentOption())
            jpTorrent.setButtonEnabled(e);
    }
    /**inizializza i listener per l'ascolto*/
    private void initListeners(){
        addWindowListener(this);
        proxy.setTextPaneListener(jtpLog);
        proxy.setFrameListener(this);
    }    
    @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
} // end class
