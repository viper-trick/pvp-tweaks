package com.pvptweaks.sound;

import com.pvptweaks.PvpTweaksMod;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Converts audio files (MP3, WAV, FLAC, AAC, M4A) to mono OGG Vorbis
 * using the system's ffmpeg binary.
 *
 * ffmpeg is pre-installed on most Linux distributions.
 * Linux Mint:  sudo apt install ffmpeg
 */
public class Mp3Converter {

    /** Supported input extensions that need conversion. */
    public static boolean needsConversion(String filename) {
        String low = filename.toLowerCase();
        return low.endsWith(".mp3") || low.endsWith(".wav")
            || low.endsWith(".flac") || low.endsWith(".aac")
            || low.endsWith(".m4a");
    }

    /**
     * Converts the given audio file to a mono OGG Vorbis file.
     *
     * @param src  source audio file (MP3, WAV, FLAC, etc.)
     * @param dest destination .ogg file to write
     * @return true on success, false on failure
     */
    public static boolean convertToOgg(Path src, Path dest) {
        if (!isFfmpegAvailable()) {
            PvpTweaksMod.LOGGER.error(
                "[PVP Tweaks] ffmpeg not found. Install it with:  sudo apt install ffmpeg");
            return false;
        }
        try {
            Files.createDirectories(dest.getParent());
            // -ac 1      : convert to mono (MC requires mono for 3D audio)
            // -ar 44100  : 44.1 kHz sample rate
            // libvorbis  : OGG Vorbis encoder
            // -q:a 5     : quality level 5 (~160 kbps)
            // -y         : overwrite without asking
            ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-y",
                "-i", src.toAbsolutePath().toString(),
                "-ac", "1",
                "-ar", "44100",
                "-c:a", "libvorbis",
                "-q:a", "5",
                dest.toAbsolutePath().toString()
            );
            pb.redirectErrorStream(true);
            Process proc = pb.start();
            String output = new String(proc.getInputStream().readAllBytes());
            int exitCode = proc.waitFor();
            if (exitCode != 0) {
                PvpTweaksMod.LOGGER.error(
                    "[PVP Tweaks] ffmpeg conversion failed (exit {}): {}", exitCode, output);
                return false;
            }
            PvpTweaksMod.LOGGER.info("[PVP Tweaks] Converted {} -> {}", src.getFileName(), dest.getFileName());
            return true;
        } catch (IOException | InterruptedException e) {
            PvpTweaksMod.LOGGER.error("[PVP Tweaks] Conversion error: {}", e.getMessage());
            return false;
        }
    }

    /** Returns true if the 'ffmpeg' binary is reachable on PATH. */
    public static boolean isFfmpegAvailable() {
        try {
            Process p = new ProcessBuilder("ffmpeg", "-version").start();
            p.waitFor();
            return p.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
