package com.pvptweaks.mixin;

import com.pvptweaks.sound.WavAudioStream;
import net.minecraft.client.sound.*;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
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

@Mixin(SoundLoader.class)
public abstract class SoundLoaderMixin {

    @Shadow @Final private ResourceFactory resourceFactory;
    @Shadow @Final private Map<Identifier, CompletableFuture<StaticSound>> loadedSounds;

    /**
     * @author Antigravity
     * @reason Support WAV files transparently with caching
     */
    @Overwrite
    public CompletableFuture<StaticSound> loadStatic(Identifier id) {
        return this.loadedSounds.computeIfAbsent(id, id2 -> CompletableFuture.supplyAsync(() -> {
            try {
                InputStream inputStream = this.resourceFactory.open(id2);
                try {
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    bis.mark(16);
                    byte[] magic = new byte[4];
                    int read = bis.read(magic);
                    bis.reset();

                    NonRepeatingAudioStream stream;
                    if (read >= 4 && magic[0] == 'R' && magic[1] == 'I' && magic[2] == 'F' && magic[3] == 'F') {
                        stream = new WavAudioStream(bis);
                    } else {
                        stream = new OggAudioStream(bis);
                    }

                    try {
                        StaticSound staticSound = new StaticSound(stream.readAll(), stream.getFormat());
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
        }, Util.getDownloadWorkerExecutor()));
    }

    /**
     * @author Antigravity
     * @reason Support WAV files transparently
     */
    @Overwrite
    public CompletableFuture<AudioStream> loadStreamed(Identifier id, boolean repeatInstantly) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                InputStream inputStream = this.resourceFactory.open(id);
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                bis.mark(16);
                byte[] magic = new byte[4];
                int read = bis.read(magic);
                bis.reset();

                if (read >= 4 && magic[0] == 'R' && magic[1] == 'I' && magic[2] == 'F' && magic[3] == 'F') {
                    return (AudioStream)(repeatInstantly ? new RepeatingAudioStream(WavAudioStream::new, bis) : new WavAudioStream(bis));
                } else {
                    return (AudioStream)(repeatInstantly ? new RepeatingAudioStream(OggAudioStream::new, bis) : new OggAudioStream(bis));
                }
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }, Util.getDownloadWorkerExecutor());
    }
}
