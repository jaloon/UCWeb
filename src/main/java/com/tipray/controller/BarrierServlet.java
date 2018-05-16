package com.tipray.controller;

import com.tipray.cache.AsynUdpCommCache;
import com.tipray.cache.SerialNumberCache;
import com.tipray.net.NioUdpServer;
import com.tipray.util.SpringBeanUtil;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.ByteBuffer;

// @WebServlet(value = "/api/barrier", asyncSupported=true)
public class BarrierServlet extends HttpServlet {
    private static final NioUdpServer UDP_SERVER = SpringBeanUtil.getBean(NioUdpServer.class);
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        short serial = SerialNumberCache.getNextSerialNumber((short)1501);
        System.out.println("send: " + serial);
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(serial);
        final AsyncContext asyncContext = request.startAsync();
        AsynUdpCommCache.putAsyncContext((int)serial,asyncContext);
        UDP_SERVER.send(buffer);
    }
}
