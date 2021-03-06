package com.tennetcn.free.quartz.job.commJob;

import cn.hutool.json.JSONUtil;
import com.tennetcn.free.core.util.SpringContextUtils;
import com.tennetcn.free.quartz.exception.QuartzBizException;
import com.tennetcn.free.quartz.job.BaseJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
public abstract class BatchCommonJob implements BaseJob {
	public static final String EXEC_SERVICE = "EXEC_SERVICE";
	public static final String EXEC_METHOD = "EXEC_METHOD";
	public static final String EXEC_PARAMETER = "EXEC_PARAMETER";
	public static final String TASK_NAME = "TASK_NAME";

	public boolean invoke(JobDataMap map) throws JobExecutionException {
		String json = null;
		try {
			json = JSONUtil.toJsonStr(map);
			String service = map.getString(EXEC_SERVICE);
			String method = map.getString(EXEC_METHOD);

			Object obj = SpringContextUtils.getCurrentContext().getBean(service);
			Class<?> clazz = obj.getClass();
			try {
				Method mt = clazz.getMethod(method, Map.class);
				mt.invoke(obj, map);
			} catch (NoSuchMethodException ne) {
				Method mt = clazz.getMethod(method);
				mt.invoke(obj);
			}
			return true;
		} catch (NoSuchMethodException ne) {
			log.error("execute job fail,no method find. " + json, ne);
			throw new JobExecutionException("execute job fail,no method find. " + ne.getMessage() + ";" + json, ne);
		} catch (InvocationTargetException e) {
			log.error("execute job fail,no target fail. " + json, e.getTargetException());
			throw new JobExecutionException("execute job fail,no target fail. " + e.getMessage() + ";" + json, e);
		} catch (Exception ex) {
			log.error("execute job fail. " + json, ex);
			throw new JobExecutionException("execute job fail. " + ex.getMessage() + ";" + json, ex);
		}
	}

}
