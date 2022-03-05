package com.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

import com.service.WebFaceCompare;
import com.service.studentDataPhoto;

/**
 * Servlet implementation class FaceCompareServlet
 */
@WebServlet("/FaceCompareServlet")
public class FaceCompareServlet extends HttpServlet {
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
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		String studentid = null;
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
				if(!item.isFormField()) {//spicture
					//�ļ��ϴ�
					//��ȡ�ļ���,getFieldName�ǻ�ȡ��ͨ���ֶε�Nameֵ
					//getName()�ǻ�ȡ�ļ���
					//String fileName = item.getName();
					
					String longFileName = item.getName();
                    String picType = longFileName.substring(longFileName.indexOf(".")+1);
                    InputStream is = null;
                    //�ж��Ƿ���ͼƬ�������ϴ��ļ�������
                    if(!("jpg".equals(picType))) {
                    	request.setAttribute("upLoadSuccess", "ͼƬ��������");
                    	System.out.println("ͼƬ��������");
                    	break;
                    }
                    
                    int newWay = -1;
                    String path = null;
                    String fileName = null;
					
					//�ж����õ������
					String agent = request.getHeader("User-Agent");
					if(agent.toLowerCase().indexOf("firefox")!=-1 || agent.toLowerCase().indexOf("edg")!=-1) {
						//������/Edg�ϴ��ļ���������
						path = request.getSession().getServletContext().getRealPath("/upload");
						char indexChar = '\\';
	                    fileName = item.getName().substring(longFileName.lastIndexOf(indexChar) + 1,longFileName.length());
	                    File file = new File(path,fileName);
	                    try {
							item.write(file);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} //�ϴ�
	                    newWay = 0;
	                    //is = new FileInputStream(path+"\\"+fileName);
						
					}else {
						newWay = 1;
						//is = new FileInputStream(longFileName);
					}
					int photoNum = studentDataPhoto.selectHasPhotoNum();
					List<InputStream> islist = new ArrayList<InputStream>();
					islist = studentDataPhoto.compareFace();
					String text = null;
					float standard1 = (float) 0.67;
					float standard2 = (float) 1.00;
					int result = -1;
					int countNum = 0;
					for(InputStream in:islist) {
						InputStream tmp1 = null;
						if(newWay == 0)
							tmp1 = new FileInputStream(path+"\\"+fileName);
						else if(newWay == 1)
							tmp1 = new FileInputStream(longFileName);
						InputStream tmp2 = in;
						try {
							text = WebFaceCompare.compareFace(tmp1, tmp2);
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(text!=null) {
							//�Ƚϴ�����
							String temp = text.substring(text.lastIndexOf(":") + 2,text.lastIndexOf(":") + 8);
							float tempNum = Float.parseFloat(temp);
							System.out.println("tempNum:"+tempNum );
							if(tempNum > standard1 && tempNum < standard2) {
								result = countNum;
								break;
							}else if(tempNum>standard2) {
								//ͼƬ��������
								break;
							}
							//in.close();
						}
						countNum ++;
					}
					

					if(result != -1) {
						//ȡ����ǰ��ͼƬ��id�������е���Ϣ
						studentid = studentDataPhoto.getLimitStudent(result);
						response.getWriter().print(studentid);
						request.setAttribute("id", studentid);
					}
					
					//is.close();						
				}		
			}
		}
		request.getRequestDispatcher("/photo.jsp").forward(request, response);
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
