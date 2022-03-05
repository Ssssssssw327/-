package com.dao;

import java.io.InputStream;
import java.util.List;

import com.entity.studentData;

public interface studentDataDao {
	/**
	 * ���ݲ�ѯ����ѯѧ�������Ϣ
	 */
	public List<studentData> selectStudentData(String sql, String[] param);
	/**
	 * ���ݲ�ѯ����ѯѧ�������Ƭ
	 */
	public InputStream selectStudentPhoto(String sql, String[] param);
	/**
	 * ����ѧ�������Ϣ
	 */
	public  int updateStudentData(String sql, Object[] param);
	/**
	 * ���ݲ�ѯ����ѯӵ����Ƭ��ѧ������
	 */
	public int selectHasPhotoNum(String sql, String[] param);
	/**
	 * ���ݲ�ѯ����ѯ����ѧ�������Ƭ
	 */
	public List<InputStream> selectStudentPhotos(String sql, String[] param);
}
