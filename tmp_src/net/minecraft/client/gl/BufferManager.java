package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import java.nio.ByteBuffer;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.ARBBufferStorage;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GLCapabilities;

@Environment(EnvType.CLIENT)
public abstract class BufferManager {
	public static BufferManager create(GLCapabilities capabilities, Set<String> usedCapabilities, GpuDeviceInfo deviceInfo) {
		if (capabilities.GL_ARB_direct_state_access && GlBackend.allowGlArbDirectAccess && !deviceInfo.shouldDisableArbDirectAccess()) {
			usedCapabilities.add("GL_ARB_direct_state_access");
			return new BufferManager.ARBBufferManager();
		} else {
			return new BufferManager.DefaultBufferManager();
		}
	}

	abstract int createBuffer();

	abstract void setBufferData(int buffer, long size, @GpuBuffer.Usage int usage);

	abstract void setBufferData(int buffer, ByteBuffer data, @GpuBuffer.Usage int usage);

	abstract void setBufferSubData(int buffer, long offset, ByteBuffer data, @GpuBuffer.Usage int usage);

	abstract void setBufferStorage(int buffer, long size, @GpuBuffer.Usage int usage);

	abstract void setBufferStorage(int buffer, ByteBuffer data, @GpuBuffer.Usage int usage);

	@Nullable
	abstract ByteBuffer mapBufferRange(int buffer, long offset, long length, int access, @GpuBuffer.Usage int usage);

	abstract void unmapBuffer(int buffer, @GpuBuffer.Usage int usage);

	abstract int createFramebuffer();

	abstract void setupFramebuffer(int framebuffer, int colorAttachment, int depthAttachment, int mipLevel, int bindTarget);

	abstract void setupBlitFramebuffer(
		int readFramebuffer, int writeFramebuffer, int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter
	);

	abstract void flushMappedBufferRange(int buffer, long offset, long length, @GpuBuffer.Usage int usage);

	abstract void copyBufferSubData(int fromBuffer, int toBuffer, long readOffset, long writeOffset, long size);

	@Environment(EnvType.CLIENT)
	static class ARBBufferManager extends BufferManager {
		@Override
		int createBuffer() {
			GlStateManager.incrementTrackedBuffers();
			return ARBDirectStateAccess.glCreateBuffers();
		}

		@Override
		void setBufferData(int buffer, long size, @GpuBuffer.Usage int usage) {
			ARBDirectStateAccess.glNamedBufferData(buffer, size, GlConst.bufferUsageToGlEnum(usage));
		}

		@Override
		void setBufferData(int buffer, ByteBuffer data, @GpuBuffer.Usage int usage) {
			ARBDirectStateAccess.glNamedBufferData(buffer, data, GlConst.bufferUsageToGlEnum(usage));
		}

		@Override
		void setBufferSubData(int buffer, long offset, ByteBuffer data, @GpuBuffer.Usage int usage) {
			ARBDirectStateAccess.glNamedBufferSubData(buffer, offset, data);
		}

		@Override
		void setBufferStorage(int buffer, long size, @GpuBuffer.Usage int usage) {
			ARBDirectStateAccess.glNamedBufferStorage(buffer, size, GlConst.bufferUsageToGlFlag(usage));
		}

		@Override
		void setBufferStorage(int buffer, ByteBuffer data, @GpuBuffer.Usage int usage) {
			ARBDirectStateAccess.glNamedBufferStorage(buffer, data, GlConst.bufferUsageToGlFlag(usage));
		}

		@Nullable
		@Override
		ByteBuffer mapBufferRange(int buffer, long offset, long length, int access, @GpuBuffer.Usage int usage) {
			return ARBDirectStateAccess.glMapNamedBufferRange(buffer, offset, length, access);
		}

		@Override
		void unmapBuffer(int buffer, int usage) {
			ARBDirectStateAccess.glUnmapNamedBuffer(buffer);
		}

		@Override
		public int createFramebuffer() {
			return ARBDirectStateAccess.glCreateFramebuffers();
		}

		@Override
		public void setupFramebuffer(int framebuffer, int colorAttachment, int depthAttachment, int mipLevel, @GpuBuffer.Usage int bindTarget) {
			ARBDirectStateAccess.glNamedFramebufferTexture(framebuffer, GlConst.GL_COLOR_ATTACHMENT0, colorAttachment, mipLevel);
			ARBDirectStateAccess.glNamedFramebufferTexture(framebuffer, GlConst.GL_DEPTH_ATTACHMENT, depthAttachment, mipLevel);
			if (bindTarget != 0) {
				GlStateManager._glBindFramebuffer(bindTarget, framebuffer);
			}
		}

		@Override
		public void setupBlitFramebuffer(
			int readFramebuffer, int writeFramebuffer, int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter
		) {
			ARBDirectStateAccess.glBlitNamedFramebuffer(readFramebuffer, writeFramebuffer, srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
		}

		@Override
		void flushMappedBufferRange(int buffer, long offset, long length, @GpuBuffer.Usage int usage) {
			ARBDirectStateAccess.glFlushMappedNamedBufferRange(buffer, offset, length);
		}

		@Override
		void copyBufferSubData(int fromBuffer, int toBuffer, long readOffset, long writeOffset, long size) {
			ARBDirectStateAccess.glCopyNamedBufferSubData(fromBuffer, toBuffer, readOffset, writeOffset, size);
		}
	}

	@Environment(EnvType.CLIENT)
	static class DefaultBufferManager extends BufferManager {
		private int getTarget(@GpuBuffer.Usage int i) {
			if ((i & 32) != 0) {
				return 34962;
			} else if ((i & 64) != 0) {
				return 34963;
			} else {
				return (i & 128) != 0 ? 35345 : 36663;
			}
		}

		@Override
		int createBuffer() {
			return GlStateManager._glGenBuffers();
		}

		@Override
		void setBufferData(int buffer, long size, @GpuBuffer.Usage int usage) {
			int i = this.getTarget(usage);
			GlStateManager._glBindBuffer(i, buffer);
			GlStateManager._glBufferData(i, size, GlConst.bufferUsageToGlEnum(usage));
			GlStateManager._glBindBuffer(i, 0);
		}

		@Override
		void setBufferData(int buffer, ByteBuffer data, @GpuBuffer.Usage int usage) {
			int i = this.getTarget(usage);
			GlStateManager._glBindBuffer(i, buffer);
			GlStateManager._glBufferData(i, data, GlConst.bufferUsageToGlEnum(usage));
			GlStateManager._glBindBuffer(i, 0);
		}

		@Override
		void setBufferSubData(int buffer, long offset, ByteBuffer data, @GpuBuffer.Usage int usage) {
			int i = this.getTarget(usage);
			GlStateManager._glBindBuffer(i, buffer);
			GlStateManager._glBufferSubData(i, offset, data);
			GlStateManager._glBindBuffer(i, 0);
		}

		@Override
		void setBufferStorage(int buffer, long size, @GpuBuffer.Usage int usage) {
			int i = this.getTarget(usage);
			GlStateManager._glBindBuffer(i, buffer);
			ARBBufferStorage.glBufferStorage(i, size, GlConst.bufferUsageToGlFlag(usage));
			GlStateManager._glBindBuffer(i, 0);
		}

		@Override
		void setBufferStorage(int buffer, ByteBuffer data, @GpuBuffer.Usage int usage) {
			int i = this.getTarget(usage);
			GlStateManager._glBindBuffer(i, buffer);
			ARBBufferStorage.glBufferStorage(i, data, GlConst.bufferUsageToGlFlag(usage));
			GlStateManager._glBindBuffer(i, 0);
		}

		@Nullable
		@Override
		ByteBuffer mapBufferRange(int buffer, long offset, long length, int access, @GpuBuffer.Usage int usage) {
			int i = this.getTarget(usage);
			GlStateManager._glBindBuffer(i, buffer);
			ByteBuffer byteBuffer = GlStateManager._glMapBufferRange(i, offset, length, access);
			GlStateManager._glBindBuffer(i, 0);
			return byteBuffer;
		}

		@Override
		void unmapBuffer(int buffer, @GpuBuffer.Usage int usage) {
			int i = this.getTarget(usage);
			GlStateManager._glBindBuffer(i, buffer);
			GlStateManager._glUnmapBuffer(i);
			GlStateManager._glBindBuffer(i, 0);
		}

		@Override
		void flushMappedBufferRange(int buffer, long offset, long length, @GpuBuffer.Usage int usage) {
			int i = this.getTarget(usage);
			GlStateManager._glBindBuffer(i, buffer);
			GL30.glFlushMappedBufferRange(i, offset, length);
			GlStateManager._glBindBuffer(i, 0);
		}

		@Override
		void copyBufferSubData(int fromBuffer, int toBuffer, long readOffset, long writeOffset, long size) {
			GlStateManager._glBindBuffer(GlConst.GL_COPY_READ_BUFFER, fromBuffer);
			GlStateManager._glBindBuffer(GlConst.GL_COPY_WRITE_BUFFER, toBuffer);
			GL31.glCopyBufferSubData(36662, 36663, readOffset, writeOffset, size);
			GlStateManager._glBindBuffer(GlConst.GL_COPY_READ_BUFFER, 0);
			GlStateManager._glBindBuffer(GlConst.GL_COPY_WRITE_BUFFER, 0);
		}

		@Override
		public int createFramebuffer() {
			return GlStateManager.glGenFramebuffers();
		}

		@Override
		public void setupFramebuffer(int framebuffer, int colorAttachment, int depthAttachment, int mipLevel, int bindTarget) {
			int i = bindTarget == 0 ? GlConst.GL_DRAW_FRAMEBUFFER : bindTarget;
			int j = GlStateManager.getFrameBuffer(i);
			GlStateManager._glBindFramebuffer(i, framebuffer);
			GlStateManager._glFramebufferTexture2D(i, GlConst.GL_COLOR_ATTACHMENT0, GlConst.GL_TEXTURE_2D, colorAttachment, mipLevel);
			GlStateManager._glFramebufferTexture2D(i, GlConst.GL_DEPTH_ATTACHMENT, GlConst.GL_TEXTURE_2D, depthAttachment, mipLevel);
			if (bindTarget == 0) {
				GlStateManager._glBindFramebuffer(i, j);
			}
		}

		@Override
		public void setupBlitFramebuffer(
			int readFramebuffer, int writeFramebuffer, int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter
		) {
			int i = GlStateManager.getFrameBuffer(GlConst.GL_READ_FRAMEBUFFER);
			int j = GlStateManager.getFrameBuffer(GlConst.GL_DRAW_FRAMEBUFFER);
			GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, readFramebuffer);
			GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, writeFramebuffer);
			GlStateManager._glBlitFrameBuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
			GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, i);
			GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, j);
		}
	}
}
