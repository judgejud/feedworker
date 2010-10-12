package org.feedworker.util;
//IMPORT JAVA
import java.io.File;
import java.io.FilenameFilter;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFilenameFilter;
/**Classe Filtro per le estensioni dei nomi di file
 *
 * @author luca
 */
public class ExtensionFilter implements FilenameFilter, SmbFilenameFilter {
    private String ext;
    /**Costruttore
     *
     * @param ext estensione su cui applicare il filtro
     */
    public ExtensionFilter(String ext) {
        this.ext = "." + ext;
    }
    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(ext);
    }
    @Override
    public boolean accept(SmbFile dir, String name) throws SmbException {
        return name.endsWith(ext);
    }
}