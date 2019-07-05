package com.tennetcn.free.data.dao.base.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.annotation.Resource;

import lombok.Data;
import org.apache.ibatis.session.SqlSessionFactory;

import com.tennetcn.free.data.message.ModelBase;
import org.springframework.beans.factory.annotation.Autowired;

/** 
 * @author      chenghuan
 * @email       79763939@qq.com
 * @comment 
 */

@Data
public abstract class DbContext<E extends ModelBase> {
	protected Class<E> entityClass;

	@SuppressWarnings("unchecked")
	public DbContext() {
		Type genType = this.getClass().getGenericSuperclass();
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		this.entityClass = (Class<E>) params[0];	
	}

	@Autowired
	protected SqlSessionFactory sqlSessionFactory;
}