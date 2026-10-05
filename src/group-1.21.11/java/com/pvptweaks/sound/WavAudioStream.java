package com.pvptweaks.sound;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import net.minecraft.client.sounds.FloatSampleSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WavAudioStream implements FloatSampleSource {
    private final AudioInputStream ais;
    private final AudioFormat format;
    private final byte[] buffer = new byte[8192];

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
    public boolean readChunk(FloatConsumer consumer) throws IOException {
        int read = ais.read(buffer);
        if (read <= 0) return false;

        int bytesPerSample = format.getSampleSizeInBits() / 8;
        int channels = format.getChannels();
        
        for (int i = 0; i < read; i += bytesPerSample) {
            float sample;
            if (bytesPerSample == 2) {
                short s = (short) ((buffer[i] & 0xFF) | (buffer[i + 1] << 8));
                sample = s / 32768.0f;
            } else {
                sample = (buffer[i] & 0xFF) / 128.0f - 1.0f;
            }
            consumer.accept(sample);
        }
        return true;
    }

    @Override
    public ByteBuffer read(int size) throws IOException {
        // Use default implementation from BufferedAudioStream
        return FloatSampleSource.super.read(size);
    }

    @Override
    public void close() throws IOException {
        ais.close();
    }
}
