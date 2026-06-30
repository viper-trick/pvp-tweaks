package com.pvptweaks.mixin;

import com.mojang.blaze3d.audio.SoundBuffer;
import com.pvptweaks.sound.WavAudioStream;
import net.minecraft.Util;
import net.minecraft.client.sounds.*;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.FiniteAudioStream;
import net.minecraft.client.sounds.JOrbisAudioStream;
import net.minecraft.client.sounds.LoopingAudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Mixin(SoundBufferLibrary.class)
public abstract class SoundLoaderMixin {

    @Shadow @Final private ResourceProvider resourceManager;
    @Shadow @Final private Map<ResourceLocation, CompletableFuture<SoundBuffer>> cache;

    /**
     * @author Antigravity
     * @reason Support WAV files transparently with caching
     */
    @Overwrite
    public CompletableFuture<SoundBuffer> getCompleteBuffer(ResourceLocation id) {
        return this.cache.computeIfAbsent(id, id2 -> CompletableFuture.supplyAsync(() -> {
            try {
                InputStream inputStream = this.resourceManager.open(id2);
                try {
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    bis.mark(16);
                    byte[] magic = new byte[4];
                    int read = bis.read(magic);
                    bis.reset();

                    FiniteAudioStream stream;
                    if (read >= 4 && magic[0] == 'R' && magic[1] == 'I' && magic[2] == 'F' && magic[3] == 'F') {
                        stream = new WavAudioStream(bis);
                    } else {
                        stream = new JOrbisAudioStream(bis);
                    }

                    try {
                        SoundBuffer staticSound = new SoundBuffer(stream.readAll(), stream.getFormat());
                        stream.close();
                        return staticSound;
                    } catch (Throwable t) {
                        try { stream.close(); } catch (Throwable t2) { t.addSuppressed(t2); }
                        throw t;
                    }
                } finally {
                    if (inputStream != null) inputStream.close();
                }
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }, Util.nonCriticalIoPool()));
    }

    /**
     * @author Antigravity
     * @reason Support WAV files transparently
     */
    @Overwrite
    public CompletableFuture<AudioStream> getStream(ResourceLocation id, boolean repeatInstantly) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                InputStream inputStream = this.resourceManager.open(id);
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                bis.mark(16);
                byte[] magic = new byte[4];
                int read = bis.read(magic);
                bis.reset();

                if (read >= 4 && magic[0] == 'R' && magic[1] == 'I' && magic[2] == 'F' && magic[3] == 'F') {
                    return (AudioStream)(repeatInstantly ? new LoopingAudioStream(WavAudioStream::new, bis) : new WavAudioStream(bis));
                } else {
                    return (AudioStream)(repeatInstantly ? new LoopingAudioStream(JOrbisAudioStream::new, bis) : new JOrbisAudioStream(bis));
                }
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }, Util.nonCriticalIoPool());
    }
}
