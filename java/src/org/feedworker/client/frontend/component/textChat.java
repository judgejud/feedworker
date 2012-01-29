package org.feedworker.client.frontend.component;

import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.Utilities;

import org.feedworker.client.frontend.Mediator;

/**
 *
 * @author luca judge
 */
public class textChat extends JEditorPane{
    private StyledDocument sd;
    private int start, end;
    private String text;    
    private TreeMap<String, ImageIcon> map = null;

    public textChat() {
        super();
        setEditorKit(new StyledEditorKit());
        setEditable(false);
        sd = (StyledDocument) getDocument();
        map = Mediator.getIstance().getMapEmoticons();
        initListener();
        
    }
    
    public void addMessage(String message) throws BadLocationException{
        sd.insertString(sd.getLength(), message, null);
        setCaretPosition(sd.getLength());
    }
    
    private void initListener() {
        sd.addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(final DocumentEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (e.getDocument() instanceof StyledDocument) {
                            Iterator<String> iter = map.keySet().iterator();
                            try {
                                while(iter.hasNext()){
                                    String key = iter.next();
                                    searchText(e, key, map.get(key));
                                }
                            } catch (BadLocationException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
            }
            @Override
            public void removeUpdate(DocumentEvent e) {}
            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
    }
    
    private void searchText(DocumentEvent e, String search, ImageIcon img) throws BadLocationException{
        start = Utilities.getRowStart(this, Math.max(0, e.getOffset()-1));    
        end = Utilities.getWordStart(this, e.getOffset() + e.getLength());
        text = sd.getText(start, end-start);
        int i = text.indexOf(search);
        while(i>=0) {
            SimpleAttributeSet attrs = new SimpleAttributeSet(sd.getCharacterElement(start+i).getAttributes());
            if (StyleConstants.getIcon(attrs) == null) {
                StyleConstants.setIcon(attrs, img);
                sd.remove(start+i, 2);
                sd.insertString(start+i, search, attrs);
            }
            i = text.indexOf(search, i+2);
        }
    }
}