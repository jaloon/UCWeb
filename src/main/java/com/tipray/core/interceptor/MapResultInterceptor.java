package com.tipray.core.interceptor;

import com.tipray.core.annotation.MapResultAnno;
import com.tipray.util.ReflectUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Mybatis返回Map而不是List<Map>插件
 * <p>
 * 这个插件是一个拦截器，拦截所有的请求，然后判断这个请求方法上面有没有@MapResultAnno注解。
 *
 * @author chenlong
 * @version 1.0 2018-05-31
 */
@Intercepts(@Signature(method = "handleResultSets", type = ResultSetHandler.class, args = {Statement.class}))
public class MapResultInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        DefaultResultSetHandler defaultResultSetHandler = (DefaultResultSetHandler) target;
        //这里通过反射，根据类名称来获取内部类，如果出现变更，则需要修改，不过mybatis的插件目前都是这样一种情况
        MappedStatement mappedStatement = ReflectUtil.getFieldValue(defaultResultSetHandler, "mappedStatement");
        String className = StringUtils.substringBeforeLast(mappedStatement.getId(), ".");
        String methodName = StringUtils.substringAfterLast(mappedStatement.getId(), ".");
        Method[] methods = Class.forName(className).getDeclaredMethods();
        if (methods == null) {
            return invocation.proceed();
        }

        //找到需要执行的method 注意这里是根据方法名称来查找，如果出现方法重载，需要认真考虑
        for (Method method : methods) {
            if (methodName.equalsIgnoreCase(method.getName())) {
                //如果添加了注解标识，就将结果转换成map
                MapResultAnno anno = method.getAnnotation(MapResultAnno.class);
                if (anno == null) {
                    return invocation.proceed();
                }
                //进行map的转换
                Statement statement = (Statement) invocation.getArgs()[0];
                return result2Map(statement, anno.keyType(), anno.valType());
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    /**
     * 结果集转为Map
     *
     * @param statement Statement
     * @param keyType   Map键类型
     * @param valType   Map值类型
     * @return 结果集
     * @throws Throwable
     */
    private Object result2Map(Statement statement, Class<?> keyType, Class<?> valType) throws Throwable {
        ResultSet resultSet = statement.getResultSet();
        if (resultSet == null) {
            return null;
        }
        List<Object> resultList = new ArrayList<>();
        Map<Object, Object> map = new HashMap<>();
        while (resultSet.next()) {
            map.put(getObject(resultSet, 1, keyType), getObject(resultSet, 2, valType));
        }
        resultList.add(map);
        return resultList;
    }

    /**
     * 获取字段映射结果
     *
     * @param resultSet   ResultSet
     * @param columnIndex 字段列号（从1开始）
     * @param resultType  字段映射结果类型
     * @return 字段映射结果
     * @throws SQLException
     */
    private Object getObject(ResultSet resultSet, int columnIndex, Class<?> resultType) throws SQLException {
        if (resultType.equals(Byte.TYPE) || resultType.equals(Byte.class)) {
            return resultSet.getByte(columnIndex);
        }
        if (resultType.equals(Short.TYPE) || resultType.equals(Short.class)) {
            return resultSet.getShort(columnIndex);
        }
        if (resultType.equals(Integer.TYPE) || resultType.equals(Integer.class)) {
            return resultSet.getInt(columnIndex);
        }
        if (resultType.equals(Long.TYPE) || resultType.equals(Long.class)) {
            return resultSet.getLong(columnIndex);
        }
        if (resultType.equals(Float.TYPE) || resultType.equals(Float.class)) {
            return resultSet.getFloat(columnIndex);
        }
        if (resultType.equals(Double.TYPE) || resultType.equals(Double.class)) {
            return resultSet.getDouble(columnIndex);
        }
        if (resultType.equals(String.class)) {
            return resultSet.getString(columnIndex);
        }
        if (resultType.equals(byte[].class)) {
            return resultSet.getBytes(columnIndex);
        }
        return resultSet.getObject(columnIndex);
    }
}
