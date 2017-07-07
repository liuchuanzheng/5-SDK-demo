package com.feiliu.flgamesdk.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
/**
 * 输入输出流转换工具类
 * @author liuchuanzheng
 *
 */
public class StreamTools {
	/**
	 * 将输入流转换为字符串
	 * @param is 输入流
	 * @return 文本字符串
	 * @throws Exception
	 */
	public static String readStream(InputStream is) throws Exception{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while((len = is.read(buffer))!=-1){
			baos.write(buffer, 0, len);
		}
		is.close();
		String temp =  baos.toString();
		return temp;
	}
}
