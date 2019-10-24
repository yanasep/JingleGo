package utils;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This class defines methods for loading resources. (image, audio, text file)
 */
public class Utils {

    public static Image loadImage(String filename, Class<?> classname) {
        try {
            return ImageIO.read(classname.getResource(filename));
        } catch (IOException e) {
            System.err.println("<Utils.loadImage>: image cannot be loaded. " + filename);
            return null;
        }
    }

    public static AudioInputStream getAudioStream(String filename, Class<?> classname) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(classname.getResource(filename));
            return audioStream;
        } catch (UnsupportedAudioFileException | IOException e) {
            System.err.println("<Utils.getAudioStream>: audio cannot be loaded. " + filename);
            return null;
        }
    }

    public static Clip getClip(AudioInputStream stream) {

        AudioFormat format = stream.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        try {
            return (Clip) AudioSystem.getLine(info);
        } catch (LineUnavailableException | java.lang.IllegalArgumentException e) {
            return null;
        }
    }

    public static List<String> readFile(String filename, Class<?> classname) {

        List<String> lines = new ArrayList<>();

        try {
            InputStream is = classname.getResourceAsStream(filename);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            for (String line = br.readLine(); line != null; line = br.readLine()) {
                lines.add(line);
            }

            br.close();
            return lines;

        } catch (IOException e) {
            System.err.println("<Utils.readFile>: stage file cannot be loaded. " + filename);
            return null;
        }
    }
}