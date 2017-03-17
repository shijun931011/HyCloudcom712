package timechat.fcgz.sj.time.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;

public class CropUtil {
	/**
	 * 压缩图片并返回图片字节数据
	 * 
	 * @param b
	 *            ：被压缩的图片
	 * @param len
	 *            ：指定被压缩后的最大宽度或高度
	 * @param size
	 *            :指定被压缩后的最大容量
	 * @return
	 */
	public static byte[] compressPhotoByte(Bitmap b, int len, int maxSize) {
		int w = b.getWidth();
		int h = b.getHeight();
		float s;
		if (w < len && h < len) {
			s = 1;
		}
		if (w > h) {
			s = (float) len / w;
		} else {
			s = (float) len / h;
		}
		Matrix matrix = new Matrix();
		matrix.postScale(s, s);
		// 压缩图片
		Bitmap newB = Bitmap.createBitmap(b, 0, 0, w ,h, matrix, false);
		// 将压缩后的图片转换为字节数组，如果字节数组大小超过600K，继续压缩
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int qt = 40;
		newB.compress(CompressFormat.JPEG, qt, bos);
		int size = bos.size();
		while (qt != 0 && size > maxSize) {
			if (qt < 0)
				qt = 0;
			bos.reset();
			newB.compress(CompressFormat.JPEG, qt, bos);
			size = bos.size();
			qt -= 10;
		}
		System.out.println("压缩后的图片大小：" + bos.size());
		return bos.toByteArray();
	}

	/**
	 * 关闭IO流
	 * 
	 * @param in
	 * @param out
	 */
	public static void closeIO(InputStream in, OutputStream out) {
		try {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * bitmap转化为byte[]
	 * 
	 * @param bm
	 * @return
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * 缓存图片到本地存储卡
	 * 
	 * @param in
	 *            ：输入流
	 * @param nameKey
	 *            ：文件名称
	 */
	public static File makeTempFile(Context context, Bitmap photo,
			String nameKey) {
		// 判断是否有存储卡
		String filepath = context.getFilesDir().getAbsolutePath()
				+ File.separator + nameKey;
		byte[] tempData = CropUtil.compressPhotoByte(photo, 1920, 600 * 1024);
		// 为了防止图片失真，此处直接将获取的图像放到文件中，可能会在内存不足的情况下出现问题，图片一般大的会达到500多KB
		// byte[] tempData = Bitmap2Bytes(photo);
		// 将压缩后的图片缓存到程序根目录下（权限）
		File bFile = new File(filepath);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(bFile);
			fos.write(tempData);
			fos.flush();
			if (bFile.exists() && bFile.length() > 0)
				return bFile;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CropUtil.closeIO(null, fos);
		}
		return null;
	}
}
