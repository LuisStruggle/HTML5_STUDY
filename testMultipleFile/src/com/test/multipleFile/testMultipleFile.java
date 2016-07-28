package com.test.multipleFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

public class testMultipleFile extends HttpServlet {

	/**
	 * 序列化
	 */
	private static final long serialVersionUID = -7593727012658300962L;

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String json = "{\"status\":\"1\"}";
		
		response.setContentType("application/json; charset=utf-8");
		// 解决跨域问题
		response.setHeader("Access-Control-Allow-Origin", "*");			
		
		
		PrintWriter out = response.getWriter();

		try {
			// 判断enctype属性是否为multipart/form-data
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			
			request.setCharacterEncoding("UTF-8");
			
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List<FileItem> items = upload.parseRequest(new ServletRequestContext(request));

			String fileName = "";
			File savefile;
			String apkPath = "E:/";

			for (FileItem item : items) {
				// 判断是普通字段还是文件字段
				if (item.isFormField()) {
					// 获取字段名
					String FieldName = item.getFieldName();
					// 获取字段值
					String value = item.getString();
					
					System.out.println(FieldName+":"+value);
				}else{
					// item.getName()是获取文件名
					fileName = apkPath + item.getName();
					
					savefile = new File(fileName);
					
					item.write(savefile);
				}
			}
			
			json = "{\"status\":\"1\"}";
		} catch (Exception e) {
			//e.printStackTrace();
			json = "{\"status\":\"0\"}";
		} finally {			
			out.write(json);
			
			out.flush();
			
			out.close();
		}
	}
}
