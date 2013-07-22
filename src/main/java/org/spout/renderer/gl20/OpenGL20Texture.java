package org.spout.renderer.gl20;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.spout.renderer.Texture;

/**
 * @author thehutch
 */
public class OpenGL20Texture extends Texture {

	@Override
	public void create() {
		if (textureSource == null) {
			throw new IllegalStateException("Texture source has not been set.");
		}
		if (minFilter == null || magFilter == null) {
			throw new IllegalStateException("Texture filters have not been set.");
		}
		if (wrapT == null || wrapS == null) {
			throw new IllegalStateException("Texture wraps have not been set.");
		}
		try {
			BufferedImage bufferedImage = ImageIO.read(textureSource);
			this.width = bufferedImage.getWidth();
			this.height = bufferedImage.getHeight();

			int[] pixels = new int[width * height];
			bufferedImage.getRGB(0, 0, width, height, pixels, 0, width);

			ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
			for (int y = 0 ; y < height ; y++) {
				for (int x = 0 ; x < width ; x++) {
					int pixel = pixels[height * width + x];
					buffer.put((byte) ((pixel >> 16) & 0xFF)); //Red
					buffer.put((byte) ((pixel >> 8) & 0xFF)); //Green
					buffer.put((byte) (pixel & 0xFF)); //Blue
					buffer.put((byte) ((pixel >> 24) & 0xFF)); //Alpha
				}
			}
			buffer.flip();

			GL11.glEnable(GL11.GL_TEXTURE);
			//Generate and bind texture
			id = GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

			//Set pixel storage mode
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

			//Set the vertical and horizontal texture wrap
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrapT.getWrap());
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wrapS.getWrap());

			//Set the min and max texture filter
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minFilter.getFilter());
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, magFilter.getFilter());

			//Upload the texture to the GPU
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

			//Close the textures input stream
			textureSource.close();
			textureSource = null;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		super.create();
	}

	@Override
	public void destroy() {
		checkCreated();
		GL11.glDeleteTextures(id);
		id = 0;
		super.destroy();
	}

	public void bind() {
		checkCreated();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	}

	public void unbind() {
		checkCreated();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
}