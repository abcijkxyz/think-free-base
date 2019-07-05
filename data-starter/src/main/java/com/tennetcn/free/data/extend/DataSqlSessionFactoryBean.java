package com.tennetcn.free.data.extend;

import static org.springframework.util.ObjectUtils.isEmpty;
import static org.springframework.util.StringUtils.hasLength;
import static org.springframework.util.StringUtils.tokenizeToStringArray;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import cn.hutool.core.util.ArrayUtil;
import com.tennetcn.free.core.utils.CommonApplicationContextUtil;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;

import com.tennetcn.free.data.extend.config.InterceptorPluginBean;
import com.tennetcn.free.data.extend.config.MapperLocationBean;
import com.tennetcn.free.data.extend.config.TypeAliasBean;


/**
 * @author chenghuan
 * @email 79763939@qq.com
 * @createtime 2018年3月28日 上午9:46:30
 * @comment
 */

public class DataSqlSessionFactoryBean extends SqlSessionFactoryBean {

	private static final Log LOGGER = LogFactory.getLog(DataSqlSessionFactoryBean.class);

	private List<String> getTypeAliasPackagesByBean() {
		Map<String, TypeAliasBean> typeAliasBeanMaps = CommonApplicationContextUtil.getCurrentContext().getBeansOfType(TypeAliasBean.class);

		if (typeAliasBeanMaps == null) {
			return null;
		}
		List<String> packageNames = new ArrayList<String>();
		for (TypeAliasBean typeAliasBean : typeAliasBeanMaps.values()) {
			packageNames.addAll(typeAliasBean.getPackageNames());
		}
		return packageNames;
	}

	private Resource[] getMapperLocationByBean() {
		Map<String, MapperLocationBean> mapperLocationBeanMaps = CommonApplicationContextUtil.getCurrentContext().getBeansOfType(MapperLocationBean.class);

		if (mapperLocationBeanMaps == null) {
			return null;
		}
		List<Resource> resources = new ArrayList<Resource>();
		for (MapperLocationBean mapperLocationBean : mapperLocationBeanMaps.values()) {
			resources.addAll(Arrays.asList(mapperLocationBean.getMapperLocations()));
		}
		return resources.toArray(new Resource[resources.size()]);

	}

	private Interceptor[] getInterceptorPluginByBean() {
		Map<String, InterceptorPluginBean> interceptorPluginBeanMaps = CommonApplicationContextUtil.getCurrentContext().getBeansOfType(InterceptorPluginBean.class);

		if (interceptorPluginBeanMaps == null) {
			return null;
		}
		List<Interceptor> interceptors = new ArrayList<Interceptor>();
		for (InterceptorPluginBean interceptorPluginBean : interceptorPluginBeanMaps.values()) {
			interceptors.addAll(Arrays.asList(interceptorPluginBean.getPlugins()));
		}
		return interceptors.toArray(new Interceptor[interceptors.size()]);

	}

	private SqlSessionFactoryBuilder localSqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();

	private Configuration localConfiguration;

	private Properties localConfigurationProperties;

	private Resource localConfigLocation;

	private ObjectFactory localObjectFactory;

	private ObjectWrapperFactory localObjectWrapperFactory;

	private String localTypeAliasesPackage;

	private String localTypeHandlersPackage;

	private Class<?>[] localTypeAliases;

	private Interceptor[] localPlugins;

	private Class<?> localTypeAliasesSuperType;

	private TypeHandler<?>[] localTypeHandlers;

	private DataSource localDataSource;

	private TransactionFactory localTransactionFactory;

	private String localEnvironment = SqlSessionFactoryBean.class.getSimpleName();

	private Resource[] localMapperLocations;

	@Override
	public void setConfiguration(Configuration configuration) {
		this.localConfiguration = configuration;
		super.setConfiguration(configuration);
	}

	@Override
	public void setConfigurationProperties(Properties sqlSessionFactoryProperties) {
		this.localConfigurationProperties = sqlSessionFactoryProperties;
		super.setConfigurationProperties(sqlSessionFactoryProperties);
	}

	@Override
	public void setConfigLocation(Resource configLocation) {
		this.localConfigLocation = configLocation;
		super.setConfigLocation(configLocation);
	}

	public Resource getLocalConfigLocation() {
		return localConfigLocation;
	}

	@Override
	public void setObjectFactory(ObjectFactory objectFactory) {
		this.localObjectFactory = objectFactory;
		super.setObjectFactory(objectFactory);
	}

	@Override
	public void setObjectWrapperFactory(ObjectWrapperFactory objectWrapperFactory) {
		this.localObjectWrapperFactory = objectWrapperFactory;
		super.setObjectWrapperFactory(objectWrapperFactory);
	}

	@Override
	public void setTypeAliases(Class<?>[] typeAliases) {
		this.localTypeAliases = typeAliases;
		super.setTypeAliases(typeAliases);
	}

	@Override
	public void setTypeAliasesPackage(String typeAliasesPackage) {
		this.localTypeAliasesPackage = typeAliasesPackage;
		super.setTypeAliasesPackage(typeAliasesPackage);
	}

	@Override
	public void setTypeHandlersPackage(String typeHandlersPackage) {
		this.localTypeHandlersPackage = typeHandlersPackage;
		super.setTypeHandlersPackage(typeHandlersPackage);
	}

	@Override
	public void setPlugins(Interceptor[] plugins) {
		this.localPlugins = plugins;
		super.setPlugins(plugins);
	}

	public Interceptor[] getLocalPlugins() {
		return ArrayUtil.addAll(localPlugins, getInterceptorPluginByBean());
	}

	@Override
	public void setTypeAliasesSuperType(Class<?> typeAliasesSuperType) {
		this.localTypeAliasesSuperType = typeAliasesSuperType;
		super.setTypeAliasesSuperType(typeAliasesSuperType);
	}

	@Override
	public void setTypeHandlers(TypeHandler<?>[] typeHandlers) {
		this.localTypeHandlers = typeHandlers;
		super.setTypeHandlers(typeHandlers);
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		this.localDataSource = dataSource;
		super.setDataSource(dataSource);
	}

	@Override
	public void setTransactionFactory(TransactionFactory transactionFactory) {
		this.localTransactionFactory = transactionFactory;
		super.setTransactionFactory(transactionFactory);
	}

	@Override
	public void setEnvironment(String environment) {
		this.localEnvironment = environment;
		super.setEnvironment(environment);
	}

	@Override
	public void setMapperLocations(Resource[] mapperLocations) {
		this.localMapperLocations = mapperLocations;
		super.setMapperLocations(mapperLocations);
	}

	public Resource[] getLocalMapperLocations() {
		return ArrayUtil.addAll(localMapperLocations, getMapperLocationByBean());
	}

	@Override
	protected SqlSessionFactory buildSqlSessionFactory() throws IOException {
		Configuration configuration_;
		System.out.println("buildSqlSessionFactory");
		XMLConfigBuilder xmlConfigBuilder = null;
		if (this.localConfiguration != null) {
			configuration_ = this.localConfiguration;
			if (configuration_.getVariables() == null) {
				configuration_.setVariables(this.localConfigurationProperties);
			} else if (this.localConfigurationProperties != null) {
				configuration_.getVariables().putAll(this.localConfigurationProperties);
			}
		} else if (this.localConfigLocation != null) {
			xmlConfigBuilder = new XMLConfigBuilder(this.localConfigLocation.getInputStream(), null, this.localConfigurationProperties);
			configuration_ = xmlConfigBuilder.getConfiguration();
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Property `configuration` or 'configLocation' not specified, using default MyBatis Configuration");
			}
			configuration_ = new Configuration();
			configuration_.setVariables(this.localConfigurationProperties);
		}

		if (this.localObjectFactory != null) {
			configuration_.setObjectFactory(this.localObjectFactory);
		}

		if (this.localObjectWrapperFactory != null) {
			configuration_.setObjectWrapperFactory(this.localObjectWrapperFactory);
		}

		if (getVfs() != null) {
			configuration_.setVfsImpl(getVfs());
		}
		if (hasLength(this.localTypeAliasesPackage)) {
			String[] typeAliasPackageArray = tokenizeToStringArray(this.localTypeAliasesPackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
			for (String packageToScan : typeAliasPackageArray) {
				configuration_.getTypeAliasRegistry().registerAliases(packageToScan, localTypeAliasesSuperType == null ? Object.class : localTypeAliasesSuperType);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Scanned package: '" + packageToScan + "' for aliases");
				}
			}
		}

		if (!isEmpty(this.localTypeAliases)) {
			for (Class<?> typeAlias : this.localTypeAliases) {
				configuration_.getTypeAliasRegistry().registerAlias(typeAlias);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Registered type alias: '" + typeAlias + "'");
				}
			}
		}

		// 加载自定义的package
		List<String> packageNames = getTypeAliasPackagesByBean();
		if (packageNames != null) {
			for (String packageName : packageNames) {
				configuration_.getTypeAliasRegistry().registerAliases(packageName);
			}
		}

		if (!isEmpty(getLocalPlugins())) {
			for (Interceptor plugin : getLocalPlugins()) {
				configuration_.addInterceptor(plugin);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Registered plugin: '" + plugin + "'");
				}
			}
		}

		if (hasLength(this.localTypeHandlersPackage)) {
			String[] typeHandlersPackageArray = tokenizeToStringArray(this.localTypeHandlersPackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
			for (String packageToScan : typeHandlersPackageArray) {
				configuration_.getTypeHandlerRegistry().register(packageToScan);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Scanned package: '" + packageToScan + "' for type handlers");
				}
			}
		}

		if (!isEmpty(this.localTypeHandlers)) {
			for (TypeHandler<?> typeHandler : this.localTypeHandlers) {
				configuration_.getTypeHandlerRegistry().register(typeHandler);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Registered type handler: '" + typeHandler + "'");
				}
			}
		}

		if (getDatabaseIdProvider() != null) {// fix #64 set databaseId before parse mapper xmls
			try {
				configuration_.setDatabaseId(getDatabaseIdProvider().getDatabaseId(this.localDataSource));
			} catch (SQLException e) {
				throw new NestedIOException("Failed getting a databaseId", e);
			}
		}

		if (getCache() != null) {
			configuration_.addCache(getCache());
		}

		if (xmlConfigBuilder != null) {
			try {
				xmlConfigBuilder.parse();

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Parsed configuration file: '" + this.localConfigLocation + "'");
				}
			} catch (Exception ex) {
				throw new NestedIOException("Failed to parse config resource: " + this.localConfigLocation, ex);
			} finally {
				ErrorContext.instance().reset();
			}
		}

		if (this.localTransactionFactory == null) {
			this.localTransactionFactory = new SpringManagedTransactionFactory();
		}

		configuration_.setEnvironment(new Environment(this.localEnvironment, this.localTransactionFactory, this.localDataSource));
		
		if (!isEmpty(getLocalMapperLocations())) {
			for (Resource mapperLocation : getLocalMapperLocations()) {
				if (mapperLocation == null) {
					continue;
				}

				try {
					XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(mapperLocation.getInputStream(), configuration_, mapperLocation.toString(), configuration_.getSqlFragments());
					xmlMapperBuilder.parse();
				} catch (Exception e) {
					throw new NestedIOException("Failed to parse mapping resource: '" + mapperLocation + "'", e);
				} finally {
					ErrorContext.instance().reset();
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Parsed mapper file: '" + mapperLocation + "'");
				}
			}
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Property 'mapperLocations' was not specified or no matching resources found");
			}
		}
		
		return this.localSqlSessionFactoryBuilder.build(configuration_);
	}
}