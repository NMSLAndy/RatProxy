package xyz.Melody.Utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Base64;

import javax.imageio.ImageIO;

public class IconProcessor {

	private static final byte[] PNG_HEADERS = new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };

	public static boolean checkPNGHeader(byte[] bytes) {
		try {
			if (bytes.length <= 8)
				return false;
			byte[] header = new byte[8];
			System.arraycopy(bytes, 0, header, 0, 8);
			for (int i = 0; i < 8; i++) {
				if (header[i] != PNG_HEADERS[i])
					return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean checkResolution(File file) {
		try {
			BufferedImage image = ImageIO.read(file);
			if (image == null)
				return false;
			if (image.getWidth() != 64)
				return false;
			if (image.getHeight() != 64)
				return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static String base64lize(byte[] bytes) {
		return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
	}

}
