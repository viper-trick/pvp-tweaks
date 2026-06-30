package com.pvptweaks.sound;

import net.minecraft.client.sounds.FiniteAudioStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class WavAudioStream implements FiniteAudioStream {
    private final AudioInputStream ais;
    private final AudioFormat format;

    public WavAudioStream(InputStream is) throws IOException {
        try {
            this.ais = AudioSystem.getAudioInputStream(is);
            this.format = ais.getFormat();
        } catch (Exception e) {
            throw new IOException("Failed to parse WAV stream", e);
        }
    }

    @Override
    public AudioFormat getFormat() {
        return this.format;
    }

    @Override
    public ByteBuffer read(int size) throws IOException {
        int bytesPerSample = format.getSampleSizeInBits() / 8;
        int totalBytes = size * bytesPerSample;
        byte[] buf = new byte[totalBytes];
        int read = ais.read(buf);
        if (read <= 0) return ByteBuffer.allocate(0);
        ByteBuffer result = ByteBuffer.allocate(read);
        result.put(buf, 0, read);
        result.flip();
        return result;
    }

    @Override
    public ByteBuffer readAll() throws IOException {
        byte[] allBytes = ais.readAllBytes();
        ByteBuffer result = ByteBuffer.allocate(allBytes.length);
        result.put(allBytes);
        result.flip();
        return result;
    }

    @Override
    public void close() throws IOException {
        ais.close();
    }
}
