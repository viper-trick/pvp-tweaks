package com.pvptweaks.sound;

import com.pvptweaks.PvpTweaksMod;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import net.minecraft.client.sounds.JOrbisAudioStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Mp3Converter {

    public static boolean convertToOgg(Path src, Path dest) {
        try (InputStream is = Files.newInputStream(src)) {
            byte[] magic = new byte[4];
            int read = is.read(magic);
            
            boolean isOgg = read >= 4 && magic[0] == 'O' && magic[1] == 'g' && magic[2] == 'g' && magic[3] == 'S';
            boolean isWav = read >= 4 && magic[0] == 'R' && magic[1] == 'I' && magic[2] == 'F' && magic[3] == 'F';
            
            if (isOgg) {
                is.close();
                boolean success = convertOggToMonoWav(src, dest);
                if (!success) {
                    Path wavPath = dest.resolveSibling(dest.getFileName().toString().replace(".ogg", ".wav"));
                    if (tryFfmpegConversion(src, wavPath)) {
                        try {
                            Files.deleteIfExists(dest);
                            Files.move(wavPath, dest);
                            return true;
                        } catch (IOException e) { }
                    }
                    return false;
                }
                return true;
            } else if (isWav) {
                is.close();
                if (!src.equals(dest)) {
                    Files.copy(src, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
                return true;
            }
        } catch (IOException e) {
            PvpTweaksMod.LOGGER.error("[PVP Tweaks] Failed to read magic bytes of {}: {}", src, e.getMessage());
            return false;
        }

        // Assume MP3 (or other unsupported that JLayer might handle) and try to convert to WAV
        Path wavPath = dest.resolveSibling(dest.getFileName().toString().replace(".ogg", ".wav"));
        if (convertMp3ToWav(src, wavPath) || tryFfmpegConversion(src, wavPath)) {
            try {
                // Rename wav to ogg so PvpTweaksDynamicPack still finds it
                Files.deleteIfExists(dest);
                Files.move(wavPath, dest);
                return true;
            } catch (IOException e) {
                PvpTweaksMod.LOGGER.error("[PVP Tweaks] Failed to rename WAV to OGG: {}", e.getMessage());
            }
        }
        return false;
    }

    private static boolean convertOggToMonoWav(Path src, Path dest) {
        try (InputStream oggIs = Files.newInputStream(src)) {
            JOrbisAudioStream oggStream = new JOrbisAudioStream(oggIs);
            ByteBuffer pcmBuffer = oggStream.readAll();
            AudioFormat format = oggStream.getFormat();
            oggStream.close();
            
            byte[] pcmBytes;
            if (format.getChannels() > 1) {
                // Downmix to mono
                pcmBytes = new byte[pcmBuffer.remaining() / format.getChannels()];
                int destIndex = 0;
                while (pcmBuffer.hasRemaining()) {
                    int sum = 0;
                    for (int i = 0; i < format.getChannels(); i++) {
                        if (pcmBuffer.hasRemaining()) {
                            short sample = (short) ((pcmBuffer.get() & 0xFF) | (pcmBuffer.get() << 8));
                            sum += sample;
                        }
                    }
                    short averaged = (short) (sum / format.getChannels());
                    pcmBytes[destIndex++] = (byte) (averaged & 0xFF);
                    pcmBytes[destIndex++] = (byte) ((averaged >> 8) & 0xFF);
                }
                format = new AudioFormat(format.getSampleRate(), format.getSampleSizeInBits(), 1, true, false);
            } else {
                pcmBytes = new byte[pcmBuffer.remaining()];
                pcmBuffer.get(pcmBytes);
            }
            
            Path wavPath = dest.resolveSibling(dest.getFileName().toString().replace(".ogg", ".wav"));
            try (ByteArrayInputStream bais = new ByteArrayInputStream(pcmBytes);
                 AudioInputStream ais = new AudioInputStream(bais, format, pcmBytes.length / format.getFrameSize())) {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavPath.toFile());
            }
            
            Files.deleteIfExists(dest);
            Files.move(wavPath, dest);
            return true;
        } catch (Exception e) {
            PvpTweaksMod.LOGGER.error("[PVP Tweaks] OGG to Mono WAV conversion failed: {}", e.getMessage());
            return false;
        }
    }

    private static boolean convertMp3ToWav(Path src, Path dest) {
        try (InputStream is = new FileInputStream(src.toFile())) {
            Bitstream bitstream = new Bitstream(is);
            Decoder decoder = new Decoder();
            
            List<Short> pcmData = new ArrayList<>();
            int sampleRate = 44100;
            
            Header frame;
            while ((frame = bitstream.readFrame()) != null) {
                SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frame, bitstream);
                sampleRate = output.getSampleFrequency();
                short[] pcm = output.getBuffer();
                int channels = output.getChannelCount();
                int length = output.getBufferLength();

                if (channels == 2) {
                    for (int i = 0; i < length; i += 2) {
                        int left = pcm[i];
                        int right = pcm[i+1];
                        int mono = (left + right) / 2;
                        pcmData.add((short)mono);
                    }
                } else {
                    for (int i = 0; i < length; i++) {
                        pcmData.add(pcm[i]);
                    }
                }
                bitstream.closeFrame();
            }
            
            byte[] bytes = new byte[pcmData.size() * 2];
            for (int i = 0; i < pcmData.size(); i++) {
                short s = pcmData.get(i);
                bytes[i * 2] = (byte) (s & 0xff);
                bytes[i * 2 + 1] = (byte) ((s >> 8) & 0xff);
            }
            
            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
            try (AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(bytes), format, pcmData.size())) {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, dest.toFile());
            }
            return true;
        } catch (Exception e) {
            PvpTweaksMod.LOGGER.error("[PVP Tweaks] MP3 to WAV conversion failed: {}", e.getMessage());
            return false;
        }
    }

    private static boolean tryFfmpegConversion(Path src, Path destWav) {
        try {
            Process p = new ProcessBuilder(
                "ffmpeg", "-y", "-i", src.toAbsolutePath().toString(),
                "-ac", "1", "-ar", "44100", "-f", "wav",
                destWav.toAbsolutePath().toString()
            ).redirectErrorStream(true).start();
            p.waitFor();
            return p.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
