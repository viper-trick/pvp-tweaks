package net.minecraft.client.realms;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.logging.LogUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Locale;
import java.util.OptionalLong;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.CheckReturnValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.dto.WorldDownload;
import net.minecraft.client.realms.exception.RealmsDefaultUncaughtExceptionHandler;
import net.minecraft.client.realms.gui.screen.RealmsDownloadLatestWorldScreen;
import net.minecraft.nbt.NbtCrashException;
import net.minecraft.nbt.NbtException;
import net.minecraft.util.Util;
import net.minecraft.util.path.SymlinkValidationException;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class FileDownload {
	private static final Logger LOGGER = LogUtils.getLogger();
	private volatile boolean cancelled;
	private volatile boolean finished;
	private volatile boolean error;
	private volatile boolean extracting;
	@Nullable
	private volatile File backupFile;
	private volatile File resourcePackPath;
	@Nullable
	private volatile CompletableFuture<?> future;
	@Nullable
	private Thread currentThread;
	private static final String[] INVALID_FILE_NAMES = new String[]{
		"CON",
		"COM",
		"PRN",
		"AUX",
		"CLOCK$",
		"NUL",
		"COM1",
		"COM2",
		"COM3",
		"COM4",
		"COM5",
		"COM6",
		"COM7",
		"COM8",
		"COM9",
		"LPT1",
		"LPT2",
		"LPT3",
		"LPT4",
		"LPT5",
		"LPT6",
		"LPT7",
		"LPT8",
		"LPT9"
	};

	@Nullable
	private <T> T submit(CompletableFuture<T> future) throws Throwable {
		this.future = future;
		if (this.cancelled) {
			future.cancel(true);
			return null;
		} else {
			try {
				try {
					return (T)future.join();
				} catch (CompletionException var3) {
					throw var3.getCause();
				}
			} catch (CancellationException var4) {
				return null;
			}
		}
	}

	private static HttpClient createClient() {
		return HttpClient.newBuilder().executor(Util.getDownloadWorkerExecutor()).connectTimeout(Duration.ofMinutes(2L)).build();
	}

	private static Builder createRequestBuilder(String uri) {
		return HttpRequest.newBuilder(URI.create(uri)).timeout(Duration.ofMinutes(2L));
	}

	@CheckReturnValue
	public static OptionalLong contentLength(String uri) {
		try {
			HttpClient httpClient = createClient();

			OptionalLong var3;
			try {
				HttpResponse<Void> httpResponse = httpClient.send(createRequestBuilder(uri).HEAD().build(), BodyHandlers.discarding());
				var3 = httpResponse.headers().firstValueAsLong("Content-Length");
			} catch (Throwable var5) {
				if (httpClient != null) {
					try {
						httpClient.close();
					} catch (Throwable var4) {
						var5.addSuppressed(var4);
					}
				}

				throw var5;
			}

			if (httpClient != null) {
				httpClient.close();
			}

			return var3;
		} catch (Exception var6) {
			LOGGER.error("Unable to get content length for download");
			return OptionalLong.empty();
		}
	}

	public void downloadWorld(WorldDownload download, String message, RealmsDownloadLatestWorldScreen.DownloadStatus status, LevelStorage storage) {
		if (this.currentThread == null) {
			this.currentThread = new Thread(() -> {
				HttpClient httpClient = createClient();

				label205: {
					try {
						try {
							this.backupFile = File.createTempFile("backup", ".tar.gz");
							this.download(status, httpClient, download.downloadLink(), this.backupFile);
							this.extract(message.trim(), this.backupFile, storage, status);
						} catch (Exception var23) {
							LOGGER.error("Caught exception while downloading world", (Throwable)var23);
							this.error = true;
						} finally {
							this.future = null;
							if (this.backupFile != null) {
								this.backupFile.delete();
							}

							this.backupFile = null;
						}

						if (this.error) {
							break label205;
						}

						String string2 = download.resourcePackUrl();
						if (!string2.isEmpty() && !download.resourcePackHash().isEmpty()) {
							try {
								this.backupFile = File.createTempFile("resources", ".tar.gz");
								this.download(status, httpClient, string2, this.backupFile);
								this.validateAndCopy(status, this.backupFile, download);
							} catch (Exception var22) {
								LOGGER.error("Caught exception while downloading resource pack", (Throwable)var22);
								this.error = true;
							} finally {
								this.future = null;
								if (this.backupFile != null) {
									this.backupFile.delete();
								}

								this.backupFile = null;
							}
						}

						this.finished = true;
					} catch (Throwable var26) {
						if (httpClient != null) {
							try {
								httpClient.close();
							} catch (Throwable var21) {
								var26.addSuppressed(var21);
							}
						}

						throw var26;
					}

					if (httpClient != null) {
						httpClient.close();
					}

					return;
				}

				if (httpClient != null) {
					httpClient.close();
				}
			});
			this.currentThread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
			this.currentThread.start();
		}
	}

	private void download(RealmsDownloadLatestWorldScreen.DownloadStatus status, HttpClient client, String uri, File output) throws IOException {
		HttpRequest httpRequest = createRequestBuilder(uri).GET().build();

		HttpResponse<InputStream> httpResponse;
		try {
			httpResponse = this.submit(client.sendAsync(httpRequest, BodyHandlers.ofInputStream()));
		} catch (Error var14) {
			throw var14;
		} catch (Throwable var15) {
			LOGGER.error("Failed to download {}", uri, var15);
			this.error = true;
			return;
		}

		if (httpResponse != null && !this.cancelled) {
			if (httpResponse.statusCode() != 200) {
				this.error = true;
			} else {
				status.totalBytes = httpResponse.headers().firstValueAsLong("Content-Length").orElse(0L);
				InputStream inputStream = (InputStream)httpResponse.body();

				try {
					OutputStream outputStream = new FileOutputStream(output);

					try {
						inputStream.transferTo(new FileDownload.DownloadCountingOutputStream(outputStream, status));
					} catch (Throwable var13) {
						try {
							outputStream.close();
						} catch (Throwable var12) {
							var13.addSuppressed(var12);
						}

						throw var13;
					}

					outputStream.close();
				} catch (Throwable var16) {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (Throwable var11) {
							var16.addSuppressed(var11);
						}
					}

					throw var16;
				}

				if (inputStream != null) {
					inputStream.close();
				}
			}
		}
	}

	public void cancel() {
		if (this.backupFile != null) {
			this.backupFile.delete();
			this.backupFile = null;
		}

		this.cancelled = true;
		CompletableFuture<?> completableFuture = this.future;
		if (completableFuture != null) {
			completableFuture.cancel(true);
		}
	}

	public boolean isFinished() {
		return this.finished;
	}

	public boolean isError() {
		return this.error;
	}

	public boolean isExtracting() {
		return this.extracting;
	}

	public static String findAvailableFolderName(String folder) {
		folder = folder.replaceAll("[\\./\"]", "_");

		for (String string : INVALID_FILE_NAMES) {
			if (folder.equalsIgnoreCase(string)) {
				folder = "_" + folder + "_";
			}
		}

		return folder;
	}

	private void untarGzipArchive(String name, @Nullable File archive, LevelStorage storage) throws IOException {
		Pattern pattern = Pattern.compile(".*-([0-9]+)$");
		int i = 1;

		for (char c : SharedConstants.INVALID_CHARS_LEVEL_NAME) {
			name = name.replace(c, '_');
		}

		if (StringUtils.isEmpty(name)) {
			name = "Realm";
		}

		name = findAvailableFolderName(name);

		try {
			for (LevelStorage.LevelSave levelSave : storage.getLevelList()) {
				String string = levelSave.getRootPath();
				if (string.toLowerCase(Locale.ROOT).startsWith(name.toLowerCase(Locale.ROOT))) {
					Matcher matcher = pattern.matcher(string);
					if (matcher.matches()) {
						int j = Integer.parseInt(matcher.group(1));
						if (j > i) {
							i = j;
						}
					} else {
						i++;
					}
				}
			}
		} catch (Exception var43) {
			LOGGER.error("Error getting level list", (Throwable)var43);
			this.error = true;
			return;
		}

		String string2;
		if (storage.isLevelNameValid(name) && i <= 1) {
			string2 = name;
		} else {
			string2 = name + (i == 1 ? "" : "-" + i);
			if (!storage.isLevelNameValid(string2)) {
				boolean bl = false;

				while (!bl) {
					i++;
					string2 = name + (i == 1 ? "" : "-" + i);
					if (storage.isLevelNameValid(string2)) {
						bl = true;
					}
				}
			}
		}

		TarArchiveInputStream tarArchiveInputStream = null;
		File file = new File(MinecraftClient.getInstance().runDirectory.getAbsolutePath(), "saves");

		try {
			file.mkdir();
			tarArchiveInputStream = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(archive))));

			for (TarArchiveEntry tarArchiveEntry = tarArchiveInputStream.getNextTarEntry();
				tarArchiveEntry != null;
				tarArchiveEntry = tarArchiveInputStream.getNextTarEntry()
			) {
				File file2 = new File(file, tarArchiveEntry.getName().replace("world", string2));
				if (tarArchiveEntry.isDirectory()) {
					file2.mkdirs();
				} else {
					file2.createNewFile();
					FileOutputStream fileOutputStream = new FileOutputStream(file2);

					try {
						IOUtils.copy(tarArchiveInputStream, fileOutputStream);
					} catch (Throwable var37) {
						try {
							fileOutputStream.close();
						} catch (Throwable var36) {
							var37.addSuppressed(var36);
						}

						throw var37;
					}

					fileOutputStream.close();
				}
			}
		} catch (Exception var41) {
			LOGGER.error("Error extracting world", (Throwable)var41);
			this.error = true;
		} finally {
			if (tarArchiveInputStream != null) {
				tarArchiveInputStream.close();
			}

			if (archive != null) {
				archive.delete();
			}

			try (LevelStorage.Session session2 = storage.createSession(string2)) {
				session2.removePlayerAndSave(string2);
			} catch (NbtException | NbtCrashException | IOException var39) {
				LOGGER.error("Failed to modify unpacked realms level {}", string2, var39);
			} catch (SymlinkValidationException var40) {
				LOGGER.warn("Failed to download file", (Throwable)var40);
			}

			this.resourcePackPath = new File(file, string2 + File.separator + "resources.zip");
		}
	}

	private void extract(String name, File archive, LevelStorage storage, RealmsDownloadLatestWorldScreen.DownloadStatus status) {
		if (status.bytesWritten >= status.totalBytes && !this.cancelled && !this.error) {
			try {
				this.extracting = true;
				this.untarGzipArchive(name, archive, storage);
			} catch (IOException var6) {
				LOGGER.error("Error extracting archive", (Throwable)var6);
				this.error = true;
			}
		}
	}

	private void validateAndCopy(RealmsDownloadLatestWorldScreen.DownloadStatus status, File file, WorldDownload download) {
		if (status.bytesWritten >= status.totalBytes && !this.cancelled) {
			try {
				String string = Hashing.sha1().hashBytes(Files.toByteArray(file)).toString();
				if (string.equals(download.resourcePackHash())) {
					FileUtils.copyFile(file, this.resourcePackPath);
					this.finished = true;
				} else {
					LOGGER.error("Resourcepack had wrong hash (expected {}, found {}). Deleting it.", download.resourcePackHash(), string);
					FileUtils.deleteQuietly(file);
					this.error = true;
				}
			} catch (IOException var5) {
				LOGGER.error("Error copying resourcepack file: {}", var5.getMessage());
				this.error = true;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class DownloadCountingOutputStream extends CountingOutputStream {
		private final RealmsDownloadLatestWorldScreen.DownloadStatus status;

		public DownloadCountingOutputStream(OutputStream stream, RealmsDownloadLatestWorldScreen.DownloadStatus status) {
			super(stream);
			this.status = status;
		}

		@Override
		protected void afterWrite(int n) throws IOException {
			super.afterWrite(n);
			this.status.bytesWritten = this.getByteCount();
		}
	}
}
