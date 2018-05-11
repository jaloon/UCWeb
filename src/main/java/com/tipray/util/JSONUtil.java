package com.tipray.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * JSON解析转换工具类
 *
 * @author chenlong
 * @version 2.0 2018-04-27
 */
public class JSONUtil {
    private static ObjectMapper objectMapper = null;

    /**
     * JSON初始化
     */
    static {
        objectMapper = new ObjectMapper();
        // 去掉默认的时间戳格式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 设置为中国上海时区
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        // 序列化时，日期的统一格式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 序列化时，Map的带有null值的entry不被序列化
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        // 空值不序列化
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        // 反序列化时，属性不存在的兼容处理
        objectMapper.getDeserializationConfig().withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 序列化时，如果是空对象（也就是对应的属性没有get方法），不抛异常,
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 反序列化时，如果多了其他属性，不抛出异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 单引号处理
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    private JSONUtil() {
    }

    /**
     * 把Java对象转换为JSON字符串
     * <p>
     * 若对象为byte[]类型，或对象中有成员变量为byte[]类型，Jackson将对象解析成JSON字符串时，<br>
     * 其中byte[]数组转换成字符串的方式是Base64的方式进行转换的，同样解析的时候也是将通过Base64将字符串解析成byte[]
     *
     * @param obj {@link Object} Java对象
     * @return {@link String} JSON字符串
     * @throws JsonProcessingException
     */
    public static String stringify(Object obj) throws JsonProcessingException {
        if (obj == null) {
            // throw new IllegalArgumentException("对象参数不能为空。");
            return null;
        }
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * 把JSON字符串解析为Java对象
     * <p>
     * 可以解析任意对象，包括数组，集合；<br>
     * 解析为byte[]时，JSON字符串可以为Base64编码的字符串，解析时按Base64解码；<br>
     * JSON字符串也可以为[1, 2, 3]的形式，此时正常反序列化
     *
     * @param jsonStr      {@link String} JSON字符串
     * @param valueTypeRef {@link TypeReference} 泛型T为对象具体类型，如：User、List&lt;User&gt、byte[]等形式
     * @return {@link T} Java对象
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static <T> T parse(String jsonStr, TypeReference<T> valueTypeRef)
            throws JsonParseException, JsonMappingException, IOException {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return null;
        }
        return objectMapper.readValue(jsonStr, valueTypeRef);
    }

    /**
     * 解析JSON字符串为Java对象
     *
     * @param jsonStr   {@link String} JSON字符串
     * @param valueType {@link Class} 对象类型（普通对象，数组对象）
     * @return {@link T} java对象
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static <T> T parseToObject(String jsonStr, Class<T> valueType)
            throws JsonParseException, JsonMappingException, IOException {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return null;
        }
        return objectMapper.readValue(jsonStr, valueType);
    }

    /**
     * 解析JSON字符串为Map对象
     *
     * @param jsonStr {@link String} JSON字符串
     * @return {@link Map} Map对象
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static Map<String, Object> parseToMap(String jsonStr)
            throws JsonParseException, JsonMappingException, IOException {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return null;
        }
        JavaType mapType = objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
        return objectMapper.readValue(jsonStr, mapType);
    }

    /**
     * 解析JSON字符串为Java对象（封装类型）数组
     *
     * @param jsonStr   {@link String} JSON字符串
     * @param valueType {@link Class} 数组类型（封装类型，不能为基础类型数组）
     * @return {@link T}[] Java对象数组
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static <T> T[] parseToArray(String jsonStr, Class<T> valueType)
            throws JsonParseException, JsonMappingException, IOException {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return null;
        }
        JavaType javaType = objectMapper.getTypeFactory().constructArrayType(valueType);
        return objectMapper.readValue(jsonStr, javaType);
    }

    /**
     * 解析JSON字符串为byte数组
     *
     * @param jsonStr {@link String} JSON字符串
     * @return {@link byte[]} byte数组
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static byte[] parseToByteArray(String jsonStr) throws JsonParseException, JsonMappingException, IOException {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return null;
        }
        JavaType javaType = objectMapper.getTypeFactory().constructArrayType(byte.class);
        return objectMapper.readValue(jsonStr, javaType);
    }

    /**
     * 解析JSON字符串为short数组
     *
     * @param jsonStr {@link String} JSON字符串
     * @return {@link short[]} short数组
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static short[] parseToShortArray(String jsonStr)
            throws JsonParseException, JsonMappingException, IOException {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return null;
        }
        JavaType javaType = objectMapper.getTypeFactory().constructArrayType(short.class);
        return objectMapper.readValue(jsonStr, javaType);
    }

    /**
     * 解析JSON字符串为int数组
     *
     * @param jsonStr {@link String} JSON字符串
     * @return {@link int[]} int数组
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static int[] parseToIntArray(String jsonStr) throws JsonParseException, JsonMappingException, IOException {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return null;
        }
        JavaType javaType = objectMapper.getTypeFactory().constructArrayType(int.class);
        return objectMapper.readValue(jsonStr, javaType);
    }

    /**
     * 解析JSON字符串为long数组
     *
     * @param jsonStr {@link String} JSON字符串
     * @return {@link long[]} long数组
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static long[] parseToLongArray(String jsonStr) throws JsonParseException, JsonMappingException, IOException {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return null;
        }
        JavaType javaType = objectMapper.getTypeFactory().constructArrayType(long.class);
        return objectMapper.readValue(jsonStr, javaType);
    }

    /**
     * 解析JSON字符串为float数组
     *
     * @param jsonStr {@link String} JSON字符串
     * @return {@link float[]} float数组
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static float[] parseToFloatArray(String jsonStr)
            throws JsonParseException, JsonMappingException, IOException {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return null;
        }
        JavaType javaType = objectMapper.getTypeFactory().constructArrayType(float.class);
        return objectMapper.readValue(jsonStr, javaType);
    }

    /**
     * 解析JSON字符串为double数组
     *
     * @param jsonStr {@link String} JSON字符串
     * @return {@link double[]} double数组
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static double[] parseToDoubleArray(String jsonStr)
            throws JsonParseException, JsonMappingException, IOException {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return null;
        }
        JavaType javaType = objectMapper.getTypeFactory().constructArrayType(double.class);
        return objectMapper.readValue(jsonStr, javaType);
    }

    /**
     * 解析JSON字符串为Map对象集合
     *
     * @param jsonStr {@link String} JSON字符串
     * @return Map对象集合
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static List<Map<String, Object>> parseToCollection(String jsonStr)
            throws JsonParseException, JsonMappingException, IOException {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return null;
        }
        JavaType mapType = objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
        JavaType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, mapType);
        return objectMapper.readValue(jsonStr, collectionType);
    }

    /**
     * 解析JSON字符串为Java对象集合
     * <p>
     * Map对象集合推荐使用{@link #parseToCollection} 方法
     *
     * @param jsonStr   {@link String} JSON字符串
     * @param valueType {@link Class} 对象类型
     * @return {@link T} java对象集合
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static <T> List<T> parseToList(String jsonStr, Class<T> valueType)
            throws JsonParseException, JsonMappingException, IOException {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return null;
        }
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, valueType);
        return objectMapper.readValue(jsonStr, javaType);
    }

    /**
     * 解析JSON字符串为Java对象集合
     * <p>
     * 可以解析所有类型对象的集合，且结果更符合预期；但是效率低下，不推荐使用；<br>
     * 建议使用【<code>parse(json, new TypeReference&lt;List&lt;User&gt&gt(){});</code>】，<br>
     * 其中【<code>List&lt;User&gt</code>】是要解析成的具体对象类型
     *
     * @param jsonStr   {@link String} JSON字符串
     * @param valueType {@link Class<T>} 对象类型
     * @return {@link T} Java对象集合
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static <T> List<T> parseToListAll(String jsonStr, Class<T> valueType)
            throws JsonParseException, JsonMappingException, IOException {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return null;
        }
        List<?> list = objectMapper.readValue(jsonStr, List.class);
        List<T> result = new ArrayList<T>();
        if (valueType == int.class || valueType == Integer.class) {
            for (int i = 0, len = list.size(); i < len; i++) {
                Object obj = list.get(i);
                if (obj instanceof Integer) {
                    result.add((T) obj);
                } else {
                    StringBuffer e = new StringBuffer();
                    e.append("数据类型不符！");
                    e.append("期望类型：").append(valueType.getName()).append('，');
                    e.append("解析类型：").append(obj.getClass().getName()).append('。');
                    throw new JsonParseException(null, e.toString());
                }
            }
        } else if (valueType == byte.class || valueType == Byte.class) {
            for (int i = 0, len = list.size(); i < len; i++) {
                Object obj = list.get(i);
                if (obj instanceof Integer) {
                    Object tObj = ((Integer) obj).byteValue();
                    result.add((T) (tObj));
                } else {
                    StringBuffer e = new StringBuffer();
                    e.append("数据类型不符！");
                    e.append("期望类型：").append(valueType.getName()).append('，');
                    e.append("解析类型：").append(obj.getClass().getName()).append('。');
                    throw new JsonParseException(null, e.toString());
                }
            }
        } else if (valueType == short.class || valueType == Short.class) {
            for (int i = 0, len = list.size(); i < len; i++) {
                Object obj = list.get(i);
                if (obj instanceof Integer) {
                    Object tObj = ((Integer) obj).shortValue();
                    result.add((T) (tObj));
                } else {
                    StringBuffer e = new StringBuffer();
                    e.append("数据类型不符！");
                    e.append("期望类型：").append(valueType.getName()).append('，');
                    e.append("解析类型：").append(obj.getClass().getName()).append('。');
                    throw new JsonParseException(null, e.toString());
                }
            }
        } else if (valueType == long.class || valueType == Long.class) {
            for (int i = 0, len = list.size(); i < len; i++) {
                Object obj = list.get(i);
                if (obj instanceof Integer) {
                    Object tObj = ((Integer) obj).longValue();
                    result.add((T) (tObj));
                } else {
                    StringBuffer e = new StringBuffer();
                    e.append("数据类型不符！");
                    e.append("期望类型：").append(valueType.getName()).append('，');
                    e.append("解析类型：").append(obj.getClass().getName()).append('。');
                    throw new JsonParseException(null, e.toString());
                }
            }
        } else if (valueType == float.class || valueType == Float.class) {
            for (int i = 0, len = list.size(); i < len; i++) {
                Object obj = list.get(i);
                if (obj instanceof Integer) {
                    Object tObj = ((Integer) obj).floatValue();
                    result.add((T) (tObj));
                } else if (obj instanceof Double) {
                    Object tObj = ((Double) obj).floatValue();
                    result.add((T) tObj);
                } else {
                    StringBuffer e = new StringBuffer();
                    e.append("数据类型不符！");
                    e.append("期望类型：").append(valueType.getName()).append('，');
                    e.append("解析类型：").append(obj.getClass().getName()).append('。');
                    throw new JsonParseException(null, e.toString());
                }
            }
        } else if (valueType == double.class || valueType == Double.class) {
            for (int i = 0, len = list.size(); i < len; i++) {
                Object obj = list.get(i);
                if (obj instanceof Integer) {
                    Object tObj = ((Integer) obj).doubleValue();
                    result.add((T) (tObj));
                } else if (obj instanceof Double) {
                    result.add((T) obj);
                } else {
                    StringBuffer e = new StringBuffer();
                    e.append("数据类型不符！");
                    e.append("期望类型：").append(valueType.getName()).append('，');
                    e.append("解析类型：").append(obj.getClass().getName()).append('。');
                    throw new JsonParseException(null, e.toString());
                }
            }
        } else {
            for (int i = 0, len = list.size(); i < len; i++) {
                Object obj = list.get(i);
                if (obj instanceof LinkedHashMap) {
                    T t = parseToObject(stringify(obj), valueType);
                    result.add(t);
                } else {
                    StringBuffer e = new StringBuffer();
                    e.append("数据类型不符！");
                    e.append("期望类型：").append(valueType.getName()).append('，');
                    e.append("解析类型：").append(obj.getClass().getName()).append('。');
                    throw new JsonParseException(null, e.toString());
                }
            }
        }
        return result;
    }

    /**
     * JSON处理含有嵌套关系对象，避免出现异常：net.sf.json.JSONException: There is a cycle in the
     * hierarchy的方法
     * <p>
     * 注意：这样获得到的字符串中，引起嵌套循环的属性会置为null
     *
     * @param obj
     * @return
     */
    public static JSONObject getJsonObject(Object obj) {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        jsonConfig.setJsonPropertyFilter((source, name, value) -> value == null);
        return JSONObject.fromObject(obj, jsonConfig);
    }

    /**
     * JSON处理含有嵌套关系对象，避免出现异常：net.sf.json.JSONException: There is a cycle in the
     * hierarchy的方法
     * <p>
     * 注意：这样获得到的字符串中，引起嵌套循环的属性会置为null
     *
     * @param obj
     * @return
     */
    public static JSONArray getJsonArray(Object obj) {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        jsonConfig.setJsonPropertyFilter((source, name, value) -> value == null);
        return JSONArray.fromObject(obj, jsonConfig);
    }

    /**
     * 把JSONObject转为Map
     *
     * @param jsonObj
     * @return
     */
    public static Map<String, Object> parseJSONObject(JSONObject jsonObj) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (Object key : jsonObj.keySet()) {
            result.put((String) key, jsonObj.get(key));
        }
        return result;
    }

    /**
     * 把JSONArray转为List
     *
     * @param jsonArr
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> parseJSONArray(JSONArray jsonArr) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Iterator<JSONObject> it = jsonArr.iterator();
        Map<String, Object> map;
        while (it.hasNext()) {
            map = new HashMap<String, Object>();
            JSONObject jsonObj = it.next();
            for (Object key : jsonObj.keySet()) {
                map.put((String) key, jsonObj.get(key));
            }
            list.add(map);
        }
        return list;
    }

    /**
     * 解析JSON字符串成一个MAP
     *
     * @param jsonStr json字符串，格式如： {dictTable:"BM_XB",groupValue:"分组值"}
     * @return
     */
    public static Map<String, Object> parseJsonToMap(String jsonStr) {
        JSONObject jsonObj = JSONUtil.getJsonObject(jsonStr);
        return parseJSONObject(jsonObj);
    }

    /**
     * 解析JSON字符串成包含多个MAP的LIST
     *
     * @param jsonStr json字符串，格式如：<br>
     *                [{dictTable:"BM_XB",groupValue:"分组值"},{dictTable:"BM_XB1",groupValue:"分组值1"}]
     * @return
     */
    public static List<Map<String, Object>> parseJsonToList(String jsonStr) {
        JSONArray jsonArray = JSONUtil.getJsonArray(jsonStr);
        return parseJSONArray(jsonArray);
    }
}
