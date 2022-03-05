package com.websocket;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ArrayUtils;

import com.entity.Msg;

@ServerEndpoint(value = "/chatroom")
public class WSServPoint {
	//static  Set<Session>set=new HashSet<Session>();
	//static List<String>userList=new ArrayList<String>();
	static Map<Session,String>us=new HashMap<Session,String>();
	Map<String,String>map;
	private Msg ms;
	@OnOpen
	public void onOpen(Session session) throws UnsupportedEncodingException{
	   System.out.println("���ӽ����ɹ�������");
	   //��ȡ�û���
	   String msg=session.getQueryString();
	   //ת��
       msg=URLDecoder.decode(msg,"utf-8");
       //���û�������map
       map=new HashMap<String, String>();
       if(msg.contains("&")){
    	   //���&
    	   String[] sts=msg.split("\\&");
    	   for(String str:sts){
    		   String[]strs=str.split("=");
    		   map.put(strs[0], strs[1]);
    		   }
       }else{
    	   //���û�������map��
    	   String[] strs= msg.split("=");
    	   map.put(strs[0], strs[1]);
       }
       //userList.add(map.get("loginName"));
       System.out.println("map:"+map);
       us.put(session, map.get("loginName"));
       ms=new Msg();
       ms.setType("s");
       ms.setMsgSender("system");
       ms.setMsgDate(new Date());
       ms.setUserList(new ArrayList<String>(us.values()));
       ms.setMsgInfo(" "+map.get("loginName")+"�����ߣ���");
       String jsonStr=JSONObject.toJSONString(ms);
       //set.add(session);
       
       bordcast(us.keySet(),jsonStr);
	}

	@OnClose
	public void onClose(Session session) {
		//userList.remove(map.get("loginName"));
		us.remove(session);
  	  	ms=new Msg();
  	  	ms.setType("s");
        ms.setMsgSender("system");
        ms.setMsgDate(new Date());
        ms.setUserList(new ArrayList<String>(us.values()));
        ms.setMsgInfo(" "+map.get("loginName")+"�����ߣ���");
        String jsonStr=JSONObject.toJSONString(ms);
	    //set.remove(session);
	    bordcast(us.keySet(),jsonStr);
	    System.out.println("�����ѹرգ�����");
	}

	@OnMessage
	public void onMessage(String message,Session session) {
		System.out.println("��Ϣ���գ�����"+message);
 	    Msg ms=new Msg();
 	    ms.setType("p");
 	    ms.setMsgSender(map.get("loginName"));
 	    ms.setMsgDate(new Date());
 	    if(message.startsWith("@")&&message.contains(":")){
 	    	String reivName=message.substring(message.indexOf("@")+1, message.indexOf(":"));
 	    	if(us.containsValue(reivName)){//����˽���Ǹ���
 	    		for(Entry<Session, String> e: us.entrySet()){
 	    			if(reivName.equals(e.getValue())){
 	    				Session reivSession=e.getKey();
 	    				//�޸���Ϣͷ
 	    				message=message.substring(message.indexOf(":")+1);
 	    				ms.setMsgInfo("˽��->"+reivName+""+message);
 	    				ms.setMsgReceiver(reivName);
 	    				String jsonstr = JSONObject.toJSONString(ms);
 	    				Set<Session> hashSet = new HashSet<Session>();
 					    hashSet.add(reivSession);
 					    hashSet.add(session);
 					    bordcast(hashSet, jsonstr);
 					    break;
 	    			}
 	    		}  
 	    	}else{//û�ҵ�˽�ĵ��ˣ����Ϊ����
 	    		ms.setMsgInfo(message);
 	    		String jsonstr= JSONObject.toJSONString(ms);
 	    		bordcast(us.keySet(), jsonstr);
 		  }
 	  }else{ //ֱ�ӹ���
 		  ms.setMsgInfo(message);
 		  String jsonstr= JSONObject.toJSONString(ms);
 		  bordcast(us.keySet(), jsonstr);
 		  
 	  }
// 	    ms.setMsgInfo(message);
// 	    String jsonstr= JSONObject.toJSONString(ms);
// 	    //System.out.println("jsonstr"+jsonstr);
// 	    bordcast(us.keySet(), jsonstr);
	}

	byte[] bc=null;
    @OnMessage
    public void onMessage(byte[] input,Session session,boolean flag){
    	  if(!flag){
    		  //�������ֽ��������ϵ�һ���ֽ�������
    		 bc= ArrayUtils.addAll(bc, input);
    	  }else{
    		//���һ��Ҳ��Ҫ��ӽ�ȥ
    		  bc= ArrayUtils.addAll(bc, input);
    		  //ת����ByteBuffer����
    		  ByteBuffer bb=ByteBuffer.wrap(bc);
    		  
    		  bordcast(us.keySet(),bb);
    		  //���bc
    		  bc=null;
    		  Msg ms=new Msg();
    		  ms.setMsgSender(map.get("loginName"));
    		  ms.setMsgDate(new Date());
    		  ms.setType("img");
    		  String jsonstr= JSONObject.toJSONString(ms);
    		  bordcast(us.keySet(), jsonstr);
    	  }
    	  
    	
    }
	
	@OnError
	public void onError(Throwable t) throws Throwable {
		System.out.println("ϵͳ�쳣������");
	}
	//�㲥
	public void bordcast(Set<Session>set,String message){
   	 for(Session s:set){
   		 try{
   			 s.getBasicRemote().sendText(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   	 }
   }
	
	public void bordcast(Set<Session>set,ByteBuffer bb){
   	 for(Session s:set){
   		 try {
				s.getBasicRemote().sendBinary(bb);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   	 }
   	
   }

}
