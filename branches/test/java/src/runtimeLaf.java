import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class runtimeLaf extends JFrame{

    public runtimeLaf() {
        super();
        //setLaf("de.javasoft.plaf.synthetica.SyntheticaStandardLookAndFeel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(500, 400));
        setLayout(new FlowLayout());
        add(new JLabel("test"));
        add(new JButton("test"));
        add(new JCheckBox("test"));
        
        JMenuBar jmb = new JMenuBar();
        setJMenuBar(jmb);
        JMenu jm = new JMenu("Look & Feel");
        JCheckBoxMenuItem jcbmi1 = new JCheckBoxMenuItem("Standard");
        JCheckBoxMenuItem jcbmi2 = new JCheckBoxMenuItem("Bluemoon");
        JCheckBoxMenuItem jcbmi3 = new JCheckBoxMenuItem("Blackstar");
        JCheckBoxMenuItem jcbmi4 = new JCheckBoxMenuItem("Bluesteel");
        jm.add(jcbmi1);
        jm.add(jcbmi2);
        jm.add(jcbmi3);
        jm.add(jcbmi4);
        jmb.add(jm);
        ButtonGroup bg = new ButtonGroup();
        bg.add(jcbmi1);
        bg.add(jcbmi2);
        bg.add(jcbmi3);
        bg.add(jcbmi4);
        setVisible(true);
        
        jcbmi1.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                setLaf("de.javasoft.plaf.synthetica.SyntheticaStandardLookAndFeel");
            }
        });
        jcbmi2.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                setLaf("de.javasoft.plaf.synthetica.SyntheticaBlueMoonLookAndFeel");
            }
        });
        jcbmi3.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                setLaf("de.javasoft.plaf.synthetica.SyntheticaBlackStarLookAndFeel");
            }
        });
        jcbmi4.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                setLaf("de.javasoft.plaf.synthetica.SyntheticaBlueSteelLookAndFeel");
            }
        });
    }
    
    private void setLaf(String laf){
        try {
            UIManager.setLookAndFeel(laf);
            SwingUtilities.updateComponentTreeUI(this);
            pack();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        new runtimeLaf();
    }   
}