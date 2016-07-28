package com.test.eventSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class testEventSource extends HttpServlet {

	/**
	 * 序列化
	 */
	private static final long serialVersionUID = 4274372399273348125L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/event-stream;charset=UTF-8");
		// 解决跨域问题
		resp.setHeader("Access-Control-Allow-Origin", "*");
		
		
		PrintWriter pw=resp.getWriter();
		pw.write("data:"+(new Date()).toString()+"\n\n");
		pw.flush();
		pw.close();
	}
}
