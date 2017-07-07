package com.feiliu.flgamesdk.net.flrequest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

import org.json.JSONObject;

import com.feiliu.flgamesdk.log.MyLogger;

public class FlRequest {
	
	private JSONObject holder;
	private String url;
	public Call newCall;
	public OkHttpClient okHttpClient;
	public FlRequest(JSONObject holder,String url) {
		this.holder = holder;
		this.url = url;
	}

	public void post(Callback callback) {
		try {
			okHttpClient = new OkHttpClient();
			RequestBody requestBody = new RequestBody() {
		        @Override
		        public MediaType contentType() {
		            return MediaType.parse("application/json;charset=UTF-8");
		        }

		        @Override
		        public void writeTo(BufferedSink sink) throws IOException {
		            sink.writeUtf8(holder.toString());
		        }

		    };

		    Request request = new Request.Builder()
		            .url(url)
		            .post(requestBody)
		            .build();

		  newCall = okHttpClient.newCall(request);
		  newCall.enqueue(callback);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
