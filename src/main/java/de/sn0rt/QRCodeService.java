package de.sn0rt;

import io.nayuki.qrcodegen.QrCode;
import jakarta.enterprise.context.ApplicationScoped;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@ApplicationScoped
public class QRCodeService
{
	public QrCode generateQrCode(String data)
	{
		return QrCode.encodeText(data, QrCode.Ecc.MEDIUM);
	}

	public String toBase64Image(QrCode qr, int scale, int border)
	{
		BufferedImage img = toImage(qr, scale, border);
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			ImageIO.write(img, "PNG", baos);
			byte[] bytes = baos.toByteArray();
			return Base64.getEncoder().encodeToString(bytes);
		}
		catch (IOException e)
		{
			throw new RuntimeException("Failed to generate QR code image", e);
		}
	}

	public BufferedImage toImage(QrCode qr, int scale, int border)
	{
		int size = qr.size;
		int imgSize = (size + border * 2) * scale;
		BufferedImage img = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);

		for (int y = 0; y < imgSize; y++)
		{
			for (int x = 0; x < imgSize; x++)
			{
				boolean module = qr.getModule((x / scale) - border, (y / scale) - border);
				img.setRGB(x, y, module ? 0x000000 : 0xFFFFFF);
			}
		}

		return img;
	}
}
