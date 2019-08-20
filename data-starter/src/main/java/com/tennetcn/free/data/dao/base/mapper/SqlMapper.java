package com.tennetcn.free.data.dao.base.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.ClassUtil;
import com.tennetcn.free.data.dao.base.IMapper;
import com.tennetcn.free.data.message.IDbModel;
import com.tennetcn.free.data.message.ModelBase;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.util.MetaObjectUtil;

/**
 * @author chenghuan
 * @email 79763939@qq.com
 * @createtime 2016年5月10日 下午10:29:04
 * @comment
 */
public class SqlMapper {

	private final MSUtils msUtils;
	private final SqlSession sqlSession;

	/**
	 * 构造方法，默认缓存MappedStatement
	 *
	 * @param sqlSession
	 */
	public SqlMapper(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
		this.msUtils = new MSUtils(sqlSession.getConfiguration());
	}

	/**
	 * 获取List中最多只有一个的数据
	 *
	 * @param list
	 *            List结果
	 * @param <T>
	 *            泛型类型
	 * @return
	 */
	private <T> T getOne(List<T> list) {
		if (list.size() == 1) {
			return list.get(0);
		} else if (list.size() > 1) {
			throw new TooManyResultsException(
					"Expected one result (or null) to be returned by selectOne(), but found: "
							+ list.size());
		} else {
			return null;
		}
	}

	/**
	 * 查询返回一个结果，多个结果时抛出异常
	 *
	 * @param sql
	 *            执行的sql
	 * @return
	 */
	public Map<String, Object> selectOne(String sql) {
		List<Map<String, Object>> list = selectList(sql);
		return getOne(list);
	}

	/**
	 * 查询返回一个结果，多个结果时抛出异常
	 *
	 * @param sql
	 *            执行的sql
	 * @param value
	 *            参数
	 * @return
	 */
	public Map<String, Object> selectOne(String sql, Object value) {
		List<Map<String, Object>> list = selectList(sql, value);
		return getOne(list);
	}
	
	public int queryCount(String sql, Object value) {
		List<Map<String, Object>> list = selectList(sql, value);
		Map<String,Object> map= getOne(list);
		return Integer.parseInt(map.get(map.keySet().toArray()[0]).toString());
	}

	/**
	 * 查询返回一个结果，多个结果时抛出异常
	 *
	 * @param sql
	 *            执行的sql
	 * @param resultType
	 *            返回的结果类型
	 * @param <T>
	 *            泛型类型
	 * @return
	 */
	public <T> T selectOne(String sql, Class<T> resultType) {
		List<T> list = selectList(sql, resultType);
		return getOne(list);
	}

	/**
	 * 查询返回一个结果，多个结果时抛出异常
	 *
	 * @param sql
	 *            执行的sql
	 * @param value
	 *            参数
	 * @param resultType
	 *            返回的结果类型
	 * @param <T>
	 *            泛型类型
	 * @return
	 */
	public <T> T selectOne(String sql, Object value, Class<T> resultType) {
		List<T> list = selectList(sql, value, resultType);
		return getOne(list);
	}

	/**
	 * 查询返回List<Map<String, Object>>
	 *
	 * @param sql
	 *            执行的sql
	 * @return
	 */
	public List<Map<String, Object>> selectList(String sql) {
		String msId = msUtils.select(sql);
		return sqlSession.selectList(msId);
	}

	/**
	 * 查询返回List<Map<String, Object>>
	 *
	 * @param sql
	 *            执行的sql
	 * @param value
	 *            参数
	 * @return
	 */
	public List<Map<String, Object>> selectList(String sql, Object value) {
		Class<?> parameterType = value != null ? value.getClass() : null;
		String msId = msUtils.selectDynamic(sql, parameterType);
		return sqlSession.selectList(msId, value);
	}
	
	/**
	 * 返回分页的 List<Map<String, Object>>
	 * @param sql
	 * @param value
	 * @param rowBounds
	 * @return
	 */
	public List<Map<String, Object>> selectListEx(String sql, Object value,RowBounds rowBounds) {
		Class<?> parameterType = value != null ? value.getClass() : null;
		String msId = msUtils.selectDynamic(sql, parameterType);
		return sqlSession.selectList(msId, value,rowBounds);
	}
	
	public <T> List<T> selectList(String sql,Object value,RowBounds rowBounds) {
		Class<?> parameterType = value != null ? value.getClass() : null;
		String msId = msUtils.selectDynamic(sql, parameterType);
		return sqlSession.selectList(msId,value,rowBounds);
	}

	/**
	 * 查询返回指定的结果类型
	 *
	 * @param sql
	 *            执行的sql
	 * @param resultType
	 *            返回的结果类型
	 * @param <T>
	 *            泛型类型
	 * @return
	 */
	public <T> List<T> selectList(String sql, Class<T> resultType) {
		String msId;
		if (resultType == null) {
			msId = msUtils.select(sql);
		} else {
			msId = msUtils.select(sql, resultType);
		}
		return sqlSession.selectList(msId);
	}
	
	public <T> List<T> selectList(String sql,RowBounds rowBounds, Class<T> resultType) {
		String msId;
		if (resultType == null) {
			msId = msUtils.select(sql);
		} else {
			msId = msUtils.select(sql, resultType);
		}
		return sqlSession.selectList(msId,null,rowBounds);
	}
	

	/**
	 * 查询返回指定的结果类型
	 *
	 * @param sql
	 *            执行的sql
	 * @param value
	 *            参数
	 * @param resultType
	 *            返回的结果类型
	 * @param <T>
	 *            泛型类型
	 * @return
	 */
	public <T> List<T> selectList(String sql, Object value, Class<T> resultType) {
		String msId;
		Class<?> parameterType = value != null ? value.getClass() : null;
		if (resultType == null) {
			msId = msUtils.selectDynamic(sql, parameterType);
		} else {
			msId = msUtils.selectDynamic(sql, parameterType, resultType);
		}
		return sqlSession.selectList(msId, value);
	}
	
	
	
	public <T> List<T> selectList(String sql, Object value,RowBounds rowBounds, Class<T> resultType) {
		String msId;
		Class<?> parameterType = value != null ? value.getClass() : null;
		if (resultType == null) {
			msId = msUtils.selectDynamic(sql, parameterType);
		} else {
			msId = msUtils.selectDynamic(sql, parameterType, resultType);
		}
		return sqlSession.selectList(msId, value,rowBounds);
	}

	/**
	 * 插入数据
	 *
	 * @param sql
	 *            执行的sql
	 * @return
	 */
	public int insert(String sql) {
		String msId = msUtils.insert(sql);
		return sqlSession.insert(msId);
	}

	/**
	 * 插入数据
	 *
	 * @param sql
	 *            执行的sql
	 * @param value
	 *            参数
	 * @return
	 */
	public int insert(String sql, Object value) {
		Class<?> parameterType = value != null ? value.getClass() : null;
		String msId = msUtils.insertDynamic(sql, parameterType);
		return sqlSession.insert(msId, value);
	}

	/**
	 * 更新数据
	 *
	 * @param sql
	 *            执行的sql
	 * @return
	 */
	public int update(String sql) {
		String msId = msUtils.update(sql);
		return sqlSession.update(msId);
	}

	/**
	 * 更新数据
	 *
	 * @param sql
	 *            执行的sql
	 * @param value
	 *            参数
	 * @return
	 */
	public int update(String sql, Object value) {
		Class<?> parameterType = value != null ? value.getClass() : null;
		String msId = msUtils.updateDynamic(sql, parameterType);
		return sqlSession.update(msId, value);
	}

	/**
	 * 删除数据
	 *
	 * @param sql
	 *            执行的sql
	 * @return
	 */
	public int delete(String sql) {
		String msId = msUtils.delete(sql);
		return sqlSession.delete(msId);
	}

	/**
	 * 删除数据
	 *
	 * @param sql
	 *            执行的sql
	 * @param value
	 *            参数
	 * @return
	 */
	public int delete(String sql, Object value) {
		Class<?> parameterType = value != null ? value.getClass() : null;
		String msId = msUtils.deleteDynamic(sql, parameterType);
		return sqlSession.delete(msId, value);
	}

	private class MSUtils {
		private Configuration configuration;
		private LanguageDriver languageDriver;

		private MSUtils(Configuration configuration) {
			this.configuration = configuration;
			languageDriver = configuration.getDefaultScriptingLanguageInstance();
		}

		/**
		 * 创建MSID
		 *
		 * @param sql
		 *            执行的sql
		 * @param sql
		 *            执行的sqlCommandType
		 * @return
		 */
		private String newMsId(String sql, SqlCommandType sqlCommandType) {
			StringBuilder msIdBuilder = new StringBuilder(
					sqlCommandType.toString());
			msIdBuilder.append(".").append(sql.hashCode());
			return msIdBuilder.toString();
		}

		/**
		 * 是否已经存在该ID
		 *
		 * @param msId
		 * @return
		 */
		private boolean hasMappedStatement(String msId) {
			return configuration.hasStatement(msId, false);
		}

		/**
		 * 创建一个查询的MS
		 *
		 * @param msId
		 * @param sqlSource
		 *            执行的sqlSource
		 * @param resultType
		 *            返回的结果类型
		 */
		private void newSelectMappedStatement(String msId, SqlSource sqlSource, final Class<?> resultType) {
			List<ResultMap> resultMaps = new ArrayList();
			try{
				// 继承了ModelBase就是实现了IDbModel接口
				if(IDbModel.class.isAssignableFrom(resultType)){
					// 取出resultType上有Entity类或者是直接父类为ModelBase的type进行resultMap的映射
					EntityTable entityTable = EntityHelper.getEntityTable(getModelBaseChild(resultType));

					//更改type
					ResultMap rm = entityTable.getResultMap(configuration);
					MetaObjectUtil.forObject(rm).setValue("type",resultType);

					resultMaps.add(rm);
				}else{
					resultMaps.add(new ResultMap.Builder(configuration,"defaultResultMap", resultType,new ArrayList<ResultMapping>(0)).build());
				}
			}catch (MapperException ex){
				resultMaps.add(new ResultMap.Builder(configuration,"defaultResultMap", resultType,new ArrayList<ResultMapping>(0)).build());
			}
			MappedStatement ms = new MappedStatement.Builder(configuration,msId, sqlSource, SqlCommandType.SELECT).resultMaps(resultMaps).build();

			// 缓存
			configuration.addMappedStatement(ms);
		}

		// 获取直接父类是ModelBase的类
		private Class<?> getModelBaseChild(Class<?> resultType){
			if(ModelBase.class.equals(resultType.getSuperclass())){
				return resultType;
			}
			if(resultType==null||Object.class.equals(resultType.getSuperclass())){
				return null;
			}
			return getModelBaseChild(resultType.getSuperclass());
		}

		/**
		 * 创建一个简单的MS
		 *
		 * @param msId
		 * @param sqlSource
		 *            执行的sqlSource
		 * @param sqlCommandType
		 *            执行的sqlCommandType
		 */
		private void newUpdateMappedStatement(String msId, SqlSource sqlSource,SqlCommandType sqlCommandType) {
			List<ResultMap> resultMapList=new ArrayList<ResultMap>();
			resultMapList.add(new ResultMap.Builder(configuration,"defaultResultMap", int.class,new ArrayList<ResultMapping>(0)).build());
			MappedStatement ms = new MappedStatement.Builder(configuration,msId, sqlSource, sqlCommandType).resultMaps(resultMapList).build();
			// 缓存
			configuration.addMappedStatement(ms);
		}

		private String select(String sql) {
			String msId = newMsId(sql, SqlCommandType.SELECT);
			if (hasMappedStatement(msId)) {
				return msId;
			}
			StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql);
			newSelectMappedStatement(msId, sqlSource, Map.class);
			return msId;
		}

		private String selectDynamic(String sql, Class<?> parameterType) {
			String msId = newMsId(sql + parameterType, SqlCommandType.SELECT);
			if (hasMappedStatement(msId)) {
				return msId;
			}
			SqlSource sqlSource = languageDriver.createSqlSource(configuration,
					sql, parameterType);
			newSelectMappedStatement(msId, sqlSource, Map.class);
			return msId;
		}

		private String select(String sql, Class<?> resultType) {
			String msId = newMsId(resultType + sql, SqlCommandType.SELECT);
			if (hasMappedStatement(msId)) {
				return msId;
			}
			StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql);
			newSelectMappedStatement(msId, sqlSource, resultType);
			return msId;
		}

		private String selectDynamic(String sql, Class<?> parameterType,
				Class<?> resultType) {
			String msId = newMsId(resultType + sql + parameterType,
					SqlCommandType.SELECT);
			if (hasMappedStatement(msId)) {
				reCalcResultMapType(msId,resultType);
				return msId;
			}
			SqlSource sqlSource = languageDriver.createSqlSource(configuration,
					sql, parameterType);
			newSelectMappedStatement(msId, sqlSource, resultType);
			return msId;
		}

		/*
		// 此处进行判断，是在处理一种特殊情况
		// user 为数据库model，有两个子类 user1,user2
		// 1、进行查询 querylist(sql,user1)
		// 2、进行查询 querylist(sql,user2)
		// 3、进行查询 querylist(sql,user1)
		// 此时在进行第三步查询的时候，会提示说无法将user2转换为user1
		// 原因就在此，因为在执行第二步的时候，entityTable(user)对应的ResultMap的resultType被改为了user2，
		// 第3步进行查询的时候，走了hasMappedStatement逻辑，无法在对entityTable(user)设置ResultMap对应的resultType
		// 所以在此处加入了一个判断，如果ms中的第一个resultMap中的type与传入的resultType不相等就改变一次值
		 */
		private void reCalcResultMapType(String msId,Class<?> resultType){
			MappedStatement ms = configuration.getMappedStatement(msId);
			List<ResultMap> rms = ms.getResultMaps();
			ResultMap rm = rms.get(0);
			if(resultType !=rm.getType()){
				MetaObjectUtil.forObject(rm).setValue("type",resultType);
			}
		}

		private String insert(String sql) {
			String msId = newMsId(sql, SqlCommandType.INSERT);
			if (hasMappedStatement(msId)) {
				return msId;
			}
			StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql);
			newUpdateMappedStatement(msId, sqlSource, SqlCommandType.INSERT);
			return msId;
		}

		private String insertDynamic(String sql, Class<?> parameterType) {
			String msId = newMsId(sql + parameterType, SqlCommandType.INSERT);
			if (hasMappedStatement(msId)) {
				return msId;
			}
			SqlSource sqlSource = languageDriver.createSqlSource(configuration,
					sql, parameterType);
			newUpdateMappedStatement(msId, sqlSource, SqlCommandType.INSERT);
			return msId;
		}

		private String update(String sql) {
			String msId = newMsId(sql, SqlCommandType.UPDATE);
			if (hasMappedStatement(msId)) {
				return msId;
			}
			StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql);
			newUpdateMappedStatement(msId, sqlSource, SqlCommandType.UPDATE);
			return msId;
		}

		private String updateDynamic(String sql, Class<?> parameterType) {
			String msId = newMsId(sql + parameterType, SqlCommandType.UPDATE);
			if (hasMappedStatement(msId)) {
				return msId;
			}
			SqlSource sqlSource = languageDriver.createSqlSource(configuration,
					sql, parameterType);
			newUpdateMappedStatement(msId, sqlSource, SqlCommandType.UPDATE);
			return msId;
		}

		private String delete(String sql) {
			String msId = newMsId(sql, SqlCommandType.DELETE);
			if (hasMappedStatement(msId)) {
				return msId;
			}
			StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql);
			newUpdateMappedStatement(msId, sqlSource, SqlCommandType.DELETE);
			return msId;
		}

		private String deleteDynamic(String sql, Class<?> parameterType) {
			String msId = newMsId(sql + parameterType, SqlCommandType.DELETE);
			if (hasMappedStatement(msId)) {
				return msId;
			}
			SqlSource sqlSource = languageDriver.createSqlSource(configuration,
					sql, parameterType);
			newUpdateMappedStatement(msId, sqlSource, SqlCommandType.DELETE);
			return msId;
		}
	}

}
