package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlGpuFence implements GpuFence {
	private long handle = GlStateManager._glFenceSync(GlConst.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);

	@Override
	public void close() {
		if (this.handle != 0L) {
			GlStateManager._glDeleteSync(this.handle);
			this.handle = 0L;
		}
	}

	@Override
	public boolean awaitCompletion(long l) {
		if (this.handle == 0L) {
			return true;
		} else {
			int i = GlStateManager._glClientWaitSync(this.handle, 0, l);
			if (i == GlConst.GL_TIMEOUT_EXPIRED) {
				return false;
			} else if (i == GlConst.GL_WAIT_FAILED) {
				throw new IllegalStateException("Failed to complete GPU fence: " + GlStateManager._getError());
			} else {
				return true;
			}
		}
	}
}
