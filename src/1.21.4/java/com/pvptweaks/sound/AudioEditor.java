package com.pvptweaks.sound;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Path;

public class AudioEditor {

    public static double getDuration(Path input) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(input.toFile());
            AudioFormat format = ais.getFormat();
            long frames = ais.getFrameLength();
            ais.close();
            if (frames != AudioSystem.NOT_SPECIFIED) {
                return (double) frames / format.getFrameRate();
            }
        } catch (Exception e) {}
        return 0.0;
    }

    public static boolean processAudio(Path input, Path output, double startSec, double endSec, double speedMult) {
        try {
            File inFile = input.toFile();
            AudioInputStream ais = AudioSystem.getAudioInputStream(inFile);
            AudioFormat format = ais.getFormat();
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = ais.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            ais.close();
            
            byte[] audioData = baos.toByteArray();
            
            int bytesPerSec = (int) (format.getSampleRate() * format.getFrameSize());
            int startByte = (int) (startSec * bytesPerSec);
            startByte = startByte - (startByte % format.getFrameSize());
            
            int endByte = audioData.length;
            if (endSec > 0 && endSec > startSec) {
                endByte = (int) (endSec * bytesPerSec);
                endByte = endByte - (endByte % format.getFrameSize());
            }
            if (endByte > audioData.length) endByte = audioData.length;
            if (startByte >= endByte) return false;
            
            byte[] sliced = new byte[endByte - startByte];
            System.arraycopy(audioData, startByte, sliced, 0, sliced.length);
            
            byte[] finalData = sliced;
            if (speedMult != 1.0 && speedMult > 0.1 && speedMult < 10.0) {
                int frameSize = format.getFrameSize();
                int numFrames = sliced.length / frameSize;
                int newNumFrames = (int) (numFrames / speedMult);
                byte[] stretched = new byte[newNumFrames * frameSize];
                
                for (int i = 0; i < newNumFrames; i++) {
                    int origFrame = (int) (i * speedMult);
                    if (origFrame >= numFrames) origFrame = numFrames - 1;
                    System.arraycopy(sliced, origFrame * frameSize, stretched, i * frameSize, frameSize);
                }
                finalData = stretched;
            }
            
            ByteArrayInputStream bais = new ByteArrayInputStream(finalData);
            AudioInputStream newAis = new AudioInputStream(bais, format, finalData.length / format.getFrameSize());
            
            AudioSystem.write(newAis, AudioFileFormat.Type.WAVE, output.toFile());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
