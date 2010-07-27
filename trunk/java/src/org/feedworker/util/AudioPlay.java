package org.feedworker.util;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lp.myUtils.Sound;

/**
 *
 * @author luca
 */
public class AudioPlay {
    /**suona un WAV predefinito
     *
     * @throws UnsupportedAudioFileException
     * @throws LineUnavailableException
     * @throws IOException
     */
    public static void playWav() throws UnsupportedAudioFileException,
                                    LineUnavailableException, IOException{
        play("beam2.wav");
    } //playwav

    /**Suona un wav a scelta
     *
     * @param wav
     * @throws UnsupportedAudioFileException
     * @throws LineUnavailableException
     * @throws IOException
     */
    public static void playWav(String wav) throws UnsupportedAudioFileException,
                                            LineUnavailableException, IOException{
        play(wav);
    }

    /**Esegue il suono
     *
     * @param wav
     * @throws UnsupportedAudioFileException
     * @throws LineUnavailableException
     * @throws IOException
     */
    private static void play(String s) throws UnsupportedAudioFileException,
                                        LineUnavailableException, IOException{
        String wav = ResourceLocator.getResourcePath() + s;
        URL url = ResourceLocator.convertStringToURL(wav);        
        Sound.playUrl(url);
    }
}