package org.feedworker.client.frontend.panel;

import org.feedworker.client.frontend.component.listTaskBlog;

/**
 *
 * @author Administrator
 */
public class paneBlog extends paneAbstract{
    
    private static paneBlog jpanel = null;
    private listTaskBlog listBlog;

    private paneBlog() {
        super("Blog");
        initializePanel();
        initializeButtons();
        core.setListListener(listBlog);
    }
    
    public static paneBlog getPanel(){
        if (jpanel==null)
            jpanel = new paneBlog();
        return jpanel;
    }

    @Override
    void initializePanel() {
        listBlog = new listTaskBlog(getName());
        jpCenter.add(listBlog);
    }

    @Override
    void initializeButtons() {
        remove(jpAction);
    }
}