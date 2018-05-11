package com.tipray.core.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * WSHttpServletRequestWrapper
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public class WSHttpServletRequestWrapper extends HttpServletRequestWrapper {

	private byte[] body;

	public WSHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		if (request.getContentLength() > 0) {
			body = new byte[request.getContentLength()];
		} else {
			body = new byte[0];
		}
		int count = 0;
		int total = 0;
		while ((count = request.getInputStream().read(body, total, body.length - total)) != -1) {
			total += count;
		}
		request.getInputStream().read(body);
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	@Override
	public ServletInputStream getInputStream() {
		final ByteArrayInputStream bais = new ByteArrayInputStream(body);
		return new ServletInputStream() {

			@Override
			public int read() {
				return bais.read();
			}

			@Override
			public boolean isFinished() {
				return false;
			}

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public void setReadListener(ReadListener readListener) {

			}
		};
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}
}
