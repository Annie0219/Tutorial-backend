package com.zph.course.support.interceptor;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author zhangweiwen at 2019/4/12
 */
@Slf4j
@Intercepts(
        {
                @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
        }
)
public class SqlStatementInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlStatementInterceptor.class);

    private static final String SQL_PARAM = "SQL_PARAM";
    private static final String SQL_EXEC = "SQL_EXEC";

    @Override
    public Object intercept(Invocation invocation) {
        Object[] args = invocation.getArgs();
        String methodName = invocation.getMethod().getName();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = invocation.getArgs()[1];
        BoundSql boundSql = ms.getBoundSql(parameter);
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        try {
            return invocation.proceed();
        } catch (Exception e) {
            LOGGER.error("执行失败！param:{}sql:{}", JSON.toJSONString(parameter), sql);
            LOGGER.error("执行失败！" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public Object plugin(Object arg0) {
        return Plugin.wrap(arg0, this);
    }

    @Override
    public void setProperties(Properties arg0) {
    }
}
