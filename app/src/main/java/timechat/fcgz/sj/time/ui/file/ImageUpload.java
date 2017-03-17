package timechat.fcgz.sj.time.ui.file;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.webkit.MimeTypeMap;

/**
 * 上传图片
 * 
 * @author zyzhang
 * @date 2016-1-28 下午6:58:48
 */
public class ImageUpload extends Thread {

	private ImageUploadListener mListener = null;
	private String mUrl = "";
	private String mStringData = null;
	private int mTag = 0;
	Map<String, String> mUids = new HashMap<String, String>();
	Map<String, String> mFilepaths = new HashMap<String, String>();
	private String mFilepath;
	public static int ERROR_NET = -1;

	private static final int MSG_RESULT = 0;
	private static final int MSG_ERROR = 1;
	private boolean mExit = false;
	private boolean isImage = true;

	String res = "";
	String result = null;

	/**
	 * 定义一个接口 用户回调
	 */
	public interface ImageUploadListener {

		public void onResult(ImageUpload request, int tag);

		public void onError(int errorCode, int tag);
	}

	public ImageUpload() {

	}

	public ImageUpload(String url, Map<String, String> mUid,
			Map<String, String> mFilepath) {
		setRequest(url, mUid, mFilepath);
		isImage = true;
	}

	public ImageUpload(String url, String filepath) {
		setRequest(url, filepath);
		isImage = false;
	}

	public void setRequest(String url, String filepath) {
		mUrl = url;
		mFilepath = filepath;
	}

	public void setRequest(String url, Map<String, String> mUid,
			Map<String, String> mFilepath) {
		mUrl = url;
		mUids = mUid;
		mFilepaths = mFilepath;
	}

	public void startRequest(ImageUploadListener listener) {
		startRequest(listener, 0);
	}

	public void startRequest(ImageUploadListener listener, int tag) {
		mListener = listener;
		mTag = tag;
		// 启动线程
		start();
	}

	@Override
	public void run() {
		if (isImage) {
			formUpload(mUrl, mUids, mFilepaths, mTag);
		} else {
			try {
				uploadfile(mUrl, mFilepath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String uploadfile(String url, String filePath) throws IOException {

		File file = new File(filePath);
		if (!file.exists() || !file.isFile()) {
			throw new IOException("文件不存在");
		}
		/**
		 * 第一部分
		 */
		URL urlObj = new URL(url);
		// 连接
		HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
		/**
		 * 设置关键值
		 */
		con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false); // post方式不能使用缓存
		// 设置请求头信息
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Charset", "UTF-8");
		// 设置边界
		String BOUNDARY = "----------" + System.currentTimeMillis();
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary="
				+ BOUNDARY);
		// 请求正文信息
		// 第一部分：
		StringBuilder sb = new StringBuilder();
		sb.append("--"); // 必须多两道线
		sb.append(BOUNDARY);
		sb.append("\r\n");
		sb.append("Content-Disposition: form-data;name=\"file\";filename=\""
				+ file.getName() + "\"\r\n");
		sb.append("Content-Type:application/octet-stream\r\n\r\n");
		byte[] head = sb.toString().getBytes("utf-8");
		// 获得输出流
		OutputStream out = new DataOutputStream(con.getOutputStream());
		// 输出表头
		out.write(head);
		// 文件正文部分
		// 把文件已流文件的方式 推入到url中
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		int bytes = 0;
		byte[] bufferOut = new byte[1024];
		while ((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut, 0, bytes);
		}
		in.close();
		// 结尾部分
		byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
		out.write(foot);
		out.flush();
		out.close();
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		try {
			// 定义BufferedReader输入流来读取URL的响应
			reader = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				// System.out.println(line);
				buffer.append(line);
			}
			if (result == null) {
				result = buffer.toString();
			}
			throwResult(mTag);
		} catch (IOException e) {
			throwError(ERROR_NET, mTag);
		} finally {
			if (reader != null) {
				reader.close();
			}

		}
		return result;
	}

	private String formUpload(String urlStr, Map<String, String> textMap,
			Map<String, String> fileMap, int mTag) {

		HttpURLConnection conn = null;
		String BOUNDARY = "---------------------------123821742118716"; // boundary就是request头和上传文件内容的分隔符
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(30000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);

			OutputStream out = new DataOutputStream(conn.getOutputStream());
			// text
			if (textMap != null) {
				StringBuffer strBuf = new StringBuffer();
				Iterator iter = textMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					strBuf.append("\r\n").append("--").append(BOUNDARY)
							.append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\""
							+ inputName + "\"\r\n\r\n");
					strBuf.append(inputValue);
				}
				out.write(strBuf.toString().getBytes());
			}

			// file
			if (fileMap != null) {
				Iterator iter = fileMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					File file = new File(inputValue);
					String filename = file.getName();

					String extension = getExtension(file);
					String contentType = MimeTypeMap.getSingleton()
							.getMimeTypeFromExtension(extension);

					if (filename.endsWith(".png")) {
						contentType = "image/png";
					}
					if (contentType == null || contentType.equals("")) {
						contentType = "application/octet-stream";
					}

					StringBuffer strBuf = new StringBuffer();
					strBuf.append("\r\n").append("--").append(BOUNDARY)
							.append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\""
							+ "file" + "\"; filename=\"" + filename
							+ "\"\r\n");
					strBuf.append("Content-Type:" + contentType + "\r\n\r\n");

					out.write(strBuf.toString().getBytes());

					DataInputStream in = new DataInputStream(
							new FileInputStream(file));
					int bytes = 0;
					byte[] bufferOut = new byte[1024];
					while ((bytes = in.read(bufferOut)) != -1) {
						out.write(bufferOut, 0, bytes);
					}
					in.close();
				}
			}

			byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
			out.write(endData);
			out.flush();
			out.close();

			// 读取返回数据
			StringBuffer strBuf = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				strBuf.append(line).append("\n");
			}
			res = strBuf.toString();
			reader.close();
			reader = null;

			throwResult(mTag);

		} catch (Exception e) {
			System.out.println("发送POST请求出错。" + urlStr);
			e.printStackTrace();
			throwError(ERROR_NET, mTag);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return res;
	}

	private static String getExtension(final File file) {
		String suffix = "";
		String name = file.getName();
		final int idx = name.lastIndexOf(".");
		if (idx > 0) {
			suffix = name.substring(idx + 1);
		}
		return suffix;
	}

	/**
	 * 请求成功
	 * 
	 * @param tag
	 */
	private void throwResult(int tag) {
		if (mListener == null && !mExit)
			return;
		Message msg = mUiHandler.obtainMessage(MSG_RESULT, 0, tag, this);
		mUiHandler.sendMessage(msg);
	}

	/**
	 * 请求失败
	 * 
	 * @param errorCode
	 * @param tag
	 */
	private void throwError(int errorCode, int tag) {
		if (mListener == null && !mExit)
			return;
		Message msg = mUiHandler.obtainMessage(MSG_ERROR, errorCode, tag);
		mUiHandler.sendMessage(msg);
	}

	private Handler mUiHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			if (mExit || mListener == null)
				return;
			switch (msg.what) {
			case MSG_RESULT:
				mListener.onResult((ImageUpload) msg.obj, msg.arg2);
				break;
			case MSG_ERROR:
				mListener.onError(msg.arg1, msg.arg2);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	public String getResultString() {
		return res;
	}

	public String getresString() {
		return result;
	}

}
