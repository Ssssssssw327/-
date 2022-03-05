package com.dao.Impl;

import com.dao.BaseDao;
import com.dao.LoginDao;
import java.sql.*;
public class LoginDaoImpl extends BaseDao implements LoginDao {
	private Connection conn = null; // �������ݿ�����

	private PreparedStatement pstmt = null; // ����ִ��SQL���

	private ResultSet rs = null; // �û������ѯ�����
	@Override
	public int login(String sql, String[] param) {
		int flag = -1; //��¼���Ĭ��ֵ,-1:ϵͳ�쳣��0���û���������� 1����½�ɹ�
		int result = -1;
		try {
			conn = getConn(); // �õ����ݿ�����
			pstmt = conn.prepareStatement(sql); // �õ�PreparedStatement����
			if (param != null) {
				for (int i = 0; i < param.length; i++) {
					pstmt.setString(i + 1, param[i]); // ΪԤ����sql���ò���
				}
			}
			rs = pstmt.executeQuery(); // ִ��SQL���
			if(rs.next()) {
				result = rs.getInt("identity");
			}
			if(result>0) {
				flag = result;
			}else {
				flag = 0; //�û���������������
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = -1;
		} finally {
			super.closeAll(conn, pstmt, rs);
		}
		return flag;
	}

}
