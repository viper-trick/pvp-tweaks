package net.minecraft.client.texture;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.TextureFormat;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CubemapTexture extends ReloadableTexture {
	private static final String[] TEXTURE_SUFFIXES = new String[]{"_1.png", "_3.png", "_5.png", "_4.png", "_0.png", "_2.png"};

	public CubemapTexture(Identifier identifier) {
		super(identifier);
	}

	@Override
	public TextureContents loadContents(ResourceManager resourceManager) throws IOException {
		Identifier identifier = this.getId();
		TextureContents textureContents = TextureContents.load(resourceManager, identifier.withSuffixedPath(TEXTURE_SUFFIXES[0]));

		TextureContents var15;
		try {
			int i = textureContents.image().getWidth();
			int j = textureContents.image().getHeight();
			NativeImage nativeImage = new NativeImage(i, j * 6, false);
			textureContents.image().copyRect(nativeImage, 0, 0, 0, 0, i, j, false, true);

			for (int k = 1; k < 6; k++) {
				TextureContents textureContents2 = TextureContents.load(resourceManager, identifier.withSuffixedPath(TEXTURE_SUFFIXES[k]));

				try {
					if (textureContents2.image().getWidth() != i || textureContents2.image().getHeight() != j) {
						throw new IOException(
							"Image dimensions of cubemap '"
								+ identifier
								+ "' sides do not match: part 0 is "
								+ i
								+ "x"
								+ j
								+ ", but part "
								+ k
								+ " is "
								+ textureContents2.image().getWidth()
								+ "x"
								+ textureContents2.image().getHeight()
						);
					}

					textureContents2.image().copyRect(nativeImage, 0, 0, 0, k * j, i, j, false, true);
				} catch (Throwable var13) {
					if (textureContents2 != null) {
						try {
							textureContents2.close();
						} catch (Throwable var12) {
							var13.addSuppressed(var12);
						}
					}

					throw var13;
				}

				if (textureContents2 != null) {
					textureContents2.close();
				}
			}

			var15 = new TextureContents(nativeImage, new TextureResourceMetadata(true, false, MipmapStrategy.MEAN, 0.0F));
		} catch (Throwable var14) {
			if (textureContents != null) {
				try {
					textureContents.close();
				} catch (Throwable var11) {
					var14.addSuppressed(var11);
				}
			}

			throw var14;
		}

		if (textureContents != null) {
			textureContents.close();
		}

		return var15;
	}

	@Override
	protected void load(NativeImage image) {
		GpuDevice gpuDevice = RenderSystem.getDevice();
		int i = image.getWidth();
		int j = image.getHeight() / 6;
		this.close();
		this.glTexture = gpuDevice.createTexture(this.getId()::toString, 21, TextureFormat.RGBA8, i, j, 6, 1);
		this.glTextureView = gpuDevice.createTextureView(this.glTexture);

		for (int k = 0; k < 6; k++) {
			gpuDevice.createCommandEncoder().writeToTexture(this.glTexture, image, 0, k, 0, 0, i, j, 0, j * k);
		}
	}
}
