package com.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.service.studentDataPhoto;
/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		request.setCharacterEncoding("utf-8");
		//��Ӧ����
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		//����ύ�ı��еı����Ƿ�ΪMultipart
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		String studentId = (String)request.getSession().getAttribute("studentId");
		if(isMultipart == true ) { //�ж�ǰ̨form�Ƿ���mutipart����
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			//�������� ͨ��paraseRequest����form�����е������ֶΣ����浽items�����У���ǰ̨���ݵ�sno sname spicture��ʱ�ͱ�������items�У�
			List<FileItem> items = null;
			try {
				items = upload.parseRequest(request);
			} catch (FileUploadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//����items
			//���õ�����
			Iterator<FileItem> iter = items.iterator();
			while(iter.hasNext()) {
				FileItem item = iter.next();
				String itemName = item.getFieldName();
				//�ж�ǰ̨�ֶ�����ͨform���ֶΣ������ļ��ֶ�
				if(item.isFormField()) {
					if(itemName.equals("id")) {//����name�����ж�
						studentId = item.getString("utf-8");
					}else {//����name�����ж�
						System.out.print("�����ֶ�");
					}
				}else if(item.isFormField() == false && studentId != null){//spicture
					//�ļ��ϴ�
					//��ȡ�ļ���,getFieldName�ǻ�ȡ��ͨ���ֶε�Nameֵ
					//getName()�ǻ�ȡ�ļ���
					//String fileName = item.getName();
					
					String longFileName = item.getName();
                    String picType = longFileName.substring(longFileName.indexOf(".")+1);
                    //�ж��Ƿ���ͼƬ�������ϴ��ļ�������
                    if(!("png".equals(picType)||"jpg".equals(picType)||"bmp".equals(picType))) {
                    	request.setAttribute("upLoadSuccess", "ͼƬ��������");
                    	System.out.println("ͼƬ��������");
                    	break;
                    }
                    
                    //�����ϴ��ļ�ʱ���õ�����ʱ�ļ���С�� ������DiskFileItemFactory
                    //factory.setSizeThreshold(10240);
                    //������ʱ�ļ�Ŀ¼
                    //factory.setRepository(new File("d:\\uploadtemp"));
                    //�����ϴ������ļ��Ĵ�С����λ���ֽ�B��������ServletFileUpload
                    //upload.setSizeMax(20480);
                   
                    
					//��ȡ�ļ����ݲ��ϴ�
					//�����ļ�·��,ָ���ϴ�λ��(������·��)
					//��ȡ������·��				
					//boolean tryOnce = studentDataPhoto.uploadPhoto(studentId, longFileName);
					boolean tryOnce = false;
					//�ж����õ������
					String agent = request.getHeader("User-Agent");
					if(agent.toLowerCase().indexOf("firefox")!=-1 || agent.toLowerCase().indexOf("edg")!=-1) {
						//�������ϴ��ļ���������
						String path = request.getSession().getServletContext().getRealPath("/upload");
						char indexChar = '\\';
	                    String fileName = item.getName().substring(longFileName.lastIndexOf(indexChar) + 1,longFileName.length());
	                    File file = new File(path,fileName);
	                    try {
							item.write(file);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} //�ϴ�
	                    tryOnce = studentDataPhoto.uploadPhoto(studentId, path+"\\"+fileName);
						System.out.print(fileName+"�ϴ��ɹ���");
						System.out.print(longFileName+"�������ݿ�ɹ���");
						if(tryOnce) {
							request.setAttribute("upLoadSuccess", "success");	
						}else {
							request.setAttribute("upLoadSuccess", "fail");
						}
						
					}else {
						tryOnce = studentDataPhoto.uploadPhoto(studentId, longFileName);
						if(tryOnce) {
							System.out.print(longFileName+"�������ݿ�ɹ���");
							request.setAttribute("upLoadSuccess", "success");
						}
						
					}					
				}		
			}
			//������ǿ��forѭ��
//			for(FileItem fileitem: items) {
//				String itemName = fileitem.getFieldName();
//				if(itemName.equals("sno")) {//����name�����ж�
//					
//				}else if(itemName.equals("sname")) {//����name�����ж�
//					
//				}else if(itemName.equals("spicture")) {//����name�����ж�
//					
//				}
//			}
		}			
		request.getRequestDispatcher("uploadMenu.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
