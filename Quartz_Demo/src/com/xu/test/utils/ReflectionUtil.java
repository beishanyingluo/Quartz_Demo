package com.xu.test.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.util.StringUtils;



/** 
 * @author  xu
 * @version 1.0 
 * ���乤����
 */
public class ReflectionUtil {
    
    public static void requestToDto(HttpServletRequest request, Object target) {
            Map paraMap = new HashMap();
            Enumeration enume = request.getParameterNames();
            while (enume.hasMoreElements()) {
                String key = (String) enume.nextElement();
                String value = request.getParameter(key);
                ReflectionUtil.allSetProperty(target, key, value);
            }
        }

    /**
     * 
      * mapתDTO
      * 
      * @param source
      * @param target 
      * @return void
     */
    public static void objectToMap(Object source, Map target) {
        try {
            target.putAll(ClassInvokeUtil.initCommonDTO(source));
        }
        catch (Exception e) {
            throw new RuntimeException("fail to  mapToObject!");
        }
    }

    /**
     * 
      * dtoתMap
      * 
      * @param source
      * @param target 
      * @return void
     */
    public static void mapToObject(Map source, Object target) {
        try {
            ClassInvokeUtil.objectToMap(source, target);
        }
        catch (Exception e) {
            throw new RuntimeException("fail to  objectToMap!");
        }
    }

    /**
     * ��source�е�����Copy��ָ��target
     * 
     * @param src
     * @param target
     */
    public static void copyProperties(Object source, Object target) {
        copyProperties(source, target, null);
    }

    /**
     * ��src�е�����Copy��ָ��target�����Ǻ���ָ��������
     * 
     * @param source Դ����
     * @param target Ŀ�����
     * @param ignoreProperties ָ�����Ե�����
     */
    public static void copyProperties(Object source, Object target, String[] ignoreProperties) {
        if (null == source || null == target) {
            return;
        }
        Set excludes = new HashSet();
        if (null != ignoreProperties) {
            excludes.addAll(Arrays.asList(ignoreProperties));
        }
        copyProperties(source, target, excludes, false);
    }

    /**
     * 
     * ������������
     * 
     * @param source Դ����
     * @param target Ŀ�����
     * @param properties ����
     * @param included ����
     */

    private static void copyProperties(Object source, Object target, Set properties, boolean included) {
        try {
            BeanInfo sourceInfo = Introspector.getBeanInfo(source.getClass());
            Map targetDescrs = new HashMap();
            // init
            {
                BeanInfo targetInfo = Introspector.getBeanInfo(target.getClass());
                PropertyDescriptor[] targetPds = targetInfo.getPropertyDescriptors();
                for (int i = 0; i < targetPds.length; i++) {
                    targetDescrs.put(targetPds[i].getName(), targetPds[i]);
                }
            }
            PropertyDescriptor[] pds = sourceInfo.getPropertyDescriptors();
            Object[] params = new Object[1];

            // collect some stat info for debug later...

            for (int i = 0; i < pds.length; i++) {
                String property = pds[i].getName();
                if (included) {
                    if (!properties.contains(property)) {
                        continue;
                    }
                }
                else {
                    if (properties.contains(property)) {
                        continue;
                    }
                }
                PropertyDescriptor targetPD = (PropertyDescriptor) targetDescrs.get(property);
                if (null != targetPD) {
                    Method readMethod = pds[i].getReadMethod();
                    Method writeMethod = targetPD.getWriteMethod();
                    if (null == readMethod || null == writeMethod) {
                        // warning: no read or write
                    }
                    else {
                        try {
                            Class[] ps = writeMethod.getParameterTypes();
                            if (ps == null || ps.length != 1) {
                                continue;
                            }
                            Object propSrc = readMethod.invoke(source, null);
                            params[0] = convertIfNeeded(ps[0], propSrc, pds[i].getName());
                            writeMethod.invoke(target, params);
                        }
                        catch (IllegalArgumentException e) {
                            // warning: failed read or write
                        }
                        catch (IllegalAccessException e) {
                            // warning: failed read or write
                        }
                        catch (InvocationTargetException e) {
                            // warning: failed read or write
                        }
                    }
                }
                else {
                    // warning: no target property
                }
            }
        }
        catch (IntrospectionException e) {
            handleEx(e);
        }
    }

    /**
     * ����ָ�����������
     * 
     * @param target ����
     * @param property ������
     * @param value �µ�����ֵ
     * @throws RuntimeException
     */
    public static void setProperty(Object target, String property, Object value) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass());
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < pds.length; i++) {
                if (pds[i].getName().equals(property)) {
                    Method method = pds[i].getWriteMethod();
                    if (null == method) {
                        throw new IllegalArgumentException("No WriteMethod of property: " + property);
                    }
                    Class[] ps = method.getParameterTypes();
                    if (ps == null || ps.length != 1) {
                        continue;
                    }
                    Object[] params = new Object[1];
                    // method.invoke(source, params);
                    params[0] = convertIfNeeded(ps[0], value, pds[i].getName());
                    method.invoke(target, params);

                    return;
                }
            }
            throw new IllegalArgumentException("No Such property: " + property);
        }
        catch (IntrospectionException e) {
            handleEx(e);
        }
        catch (IllegalAccessException e) {
            handleEx(e);
        }
        catch (InvocationTargetException e) {
            handleEx(e);
        }

        throw new RuntimeException("No Such property: " + property);
    }
    
    public static void allSetProperty(Object target, String property, Object value) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass());
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < pds.length; i++) {
                if (pds[i].getName().equals(property)) {
                    Method method = pds[i].getWriteMethod();
                    if (null == method) {
                       break;
                    }
                    Class[] ps = method.getParameterTypes();
                    if (ps == null || ps.length != 1) {
                        continue;
                    }
                    Object[] params = new Object[1];
                    // method.invoke(source, params);
                    params[0] = convertIfNeeded(ps[0], value, pds[i].getName());
                    method.invoke(target, params);

                    return;
                }
            }
        }
        catch (IntrospectionException e) {
            handleEx(e);
        }
        catch (IllegalAccessException e) {
            handleEx(e);
        }
        catch (InvocationTargetException e) {
            handleEx(e);
        }
    }

    /**
     * 
     * ������������
     * 
     * @param type ����
     * @param value ����
     * @param property ����
     * @return Object ����
     */
    private static Object convertIfNeeded(Class type, Object value, String property) {
        if (null != value) {
            if (value instanceof Number) {
                return convertNumber(type, (Number) value);
            }
            return value;
        }
        // ����ԭʼ����
        if (Integer.class == type || Integer.TYPE == type) {
            return new Integer(0);
        }
        if (Short.class == type || Short.TYPE == type) {
            return new Short((short) 0);
        }
        if (Long.class == type || Long.TYPE == type) {
            return new Long(0);
        }
        if (Float.class == type || Float.TYPE == type) {
            return new Float(0);
        }
        if (Double.class == type || Double.TYPE == type) {
            return new Double(0);
        }
        if (Byte.class == type || Byte.TYPE == type) {
            return new Integer(0);
        }
        if (Character.class == type || Character.TYPE == type) {
            return new Character('\0');
        }
        if (Boolean.class == type || Boolean.TYPE == type) {
            return Boolean.FALSE;
        }

        return value;
    }

    private static Number convertNumber(Class destType, Number value) {
        if (destType == BigDecimal.class) {
            return new BigDecimal(value.toString());
        }
        if (destType == BigInteger.class) {
            return new BigInteger(value.toString());
        }
        if (destType == Long.class || destType == Long.TYPE) {
            return new Long(value.longValue());
        }
        if (destType == Integer.class || destType == Integer.TYPE) {
            return new Integer(value.intValue());
        }
        if (destType == Short.class || destType == Short.TYPE) {
            return new Short(value.shortValue());
        }
        if (destType == Float.class || destType == Float.TYPE) {
            return new Float(value.floatValue());
        }
        if (destType == Double.class || destType == Double.TYPE) {
            return new Double(value.doubleValue());
        }
        if (destType == Byte.class || destType == Byte.TYPE) {
            return new Byte(value.byteValue());
        }

        return value;
    }

    /**
     * 
     * ������������
     * 
     * @param ex �쳣
     */
    private static void handleEx(IntrospectionException ex) {
        throw new RuntimeException("Access Error", ex);
    }

    /**
     * 
     * ������������
     * 
     * @param ex �쳣
     */
    private static void handleEx(IllegalAccessException ex) {
        throw new RuntimeException("Access Error", ex);
    }

    /**
     * 
     * ������������
     * 
     * @param ex �쳣
     */
    private static void handleEx(InvocationTargetException ex) {
        throw new RuntimeException("Invocation Error", ex);
    }



    // static {
    // DateConverter dc = new DateConverter();
    // dc.setUseLocaleFormat(true);
    // dc.setPatterns(new String[] { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss" });
    // ConvertUtils.register(dc, Date.class);
    // }

    /**
     * ����Getter����.
     * 
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static Object invokeGetterMethod(Object target, String propertyName) throws Exception {
        String getterMethodName = "get" + StringUtils.capitalize(propertyName);
        return invokeMethod(target, getterMethodName, new Class[] {}, new Object[] {});
    }

    /**
     * ����Setter����.ʹ��value��Class������Setter����.
     * 
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static void invokeSetterMethod(Object target, String propertyName, Object value) throws Exception {
        invokeSetterMethod(target, propertyName, value, null);
    }

    /**
     * ����Setter����.
     * 
     * @param propertyType ���ڲ���Setter����,Ϊ��ʱʹ��value��Class���.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static void invokeSetterMethod(Object target, String propertyName, Object value, Class < ? > propertyType)
            throws Exception {
        Class < ? > type = propertyType != null ? propertyType : value.getClass();
        String setterMethodName = "set" + StringUtils.capitalize(propertyName);
        invokeMethod(target, setterMethodName, new Class[] { type }, new Object[] { value });
    }

    /**
     * ֱ�Ӷ�ȡ��������ֵ, ����private/protected���η�, ������getter����.
     * 
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    public static Object getFieldValue(final Object object, final String fieldName) throws Exception {
        Field field = getDeclaredField(object, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        }

        makeAccessible(field);

        Object result = null;
        try {
            result = field.get(object);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * ֱ�����ö�������ֵ, ����private/protected���η�, ������setter����.
     * 
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    public static void setFieldValue(final Object object, final String fieldName, final Object value) throws Exception {
        Field field = getDeclaredField(object, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        }

        makeAccessible(field);

        try {
            field.set(object, value);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * ֱ�ӵ��ö��󷽷�, ����private/protected���η�.
     * 
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static Object invokeMethod(final Object object, final String methodName, final Class < ? >[] parameterTypes,
            final Object[] parameters) throws Exception {
        Method method = getDeclaredMethod(object, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + object + "]");
        }
        method.setAccessible(true);
        return method.invoke(object, parameters);
    }

    /**
     * ѭ������ת��, ��ȡ�����DeclaredField.
     * 
     * ������ת�͵�Object���޷��ҵ�, ����null.
     * 
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    protected static Field getDeclaredField(final Object object, final String fieldName) throws Exception {
        if (object == null) {
            return null;
        }
        if (fieldName == null) {
            return null;
        }
        for (Class < ? > superClass = object.getClass(); superClass != Object.class; superClass = superClass
                .getSuperclass()) {
            return superClass.getDeclaredField(fieldName);
        }
        return null;
    }

    /**
     * ǿ������Field�ɷ���.
     */
    protected static void makeAccessible(final Field field) {
        if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
            field.setAccessible(true);
        }
    }

    /**
     * ѭ������ת��, ��ȡ�����DeclaredMethod.
     * 
     * ������ת�͵�Object���޷��ҵ�, ����null.
     */
    protected static Method getDeclaredMethod(Object object, String methodName, Class < ? >[] parameterTypes) {
        if (object == null) {
            return null;
        }
        for (Class < ? > superClass = object.getClass(); superClass != Object.class; superClass = superClass
                .getSuperclass()) {
            try {
                return superClass.getDeclaredMethod(methodName, parameterTypes);
            }
            catch (NoSuchMethodException e) {// NOSONAR
                // Method���ڵ�ǰ�ඨ��,��������ת��
            }
        }
        return null;
    }

    /**
     * ͨ������, ���Class�����������ĸ���ķ��Ͳ���������. ���޷��ҵ�, ����Object.class. eg. public UserDao extends HibernateDao<User>
     * 
     * @param clazz The class to introspect
     * @return the first generic declaration, or Object.class if cannot be determined
     */
    @SuppressWarnings("unchecked")
    public static < T > Class < T > getSuperClassGenricType(final Class clazz) {
        return getSuperClassGenricType(clazz, 0);
    }

    /**
     * ͨ������, ��ö���Classʱ�����ĸ���ķ��Ͳ���������. ���޷��ҵ�, ����Object.class.
     * 
     * ��public UserDao extends HibernateDao<User,Long>
     * 
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be determined
     */
    @SuppressWarnings("unchecked")
    public static Class getSuperClassGenricType(final Class clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }

        return (Class) params[index];
    }

    /**
     * ��ȡ�����еĶ��������(ͨ��getter����), ��ϳ�List.
     * 
     * @param collection ��Դ����.
     * @param propertyName Ҫ��ȡ��������.
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    public static List convertElementPropertyToList(final Collection collection, final String propertyName)
            throws Exception {
        List list = new ArrayList();

        for (Object obj : collection) {
            list.add(PropertyUtils.getProperty(obj, propertyName));
        }

        return list;
    }

    /**
     * ��ȡ�����еĶ��������(ͨ��getter����), ��ϳ��ɷָ���ָ����ַ���.
     * 
     * @param collection ��Դ����.
     * @param propertyName Ҫ��ȡ��������.
     * @param separator �ָ���.
     */
    // @SuppressWarnings("unchecked")
    // public static String convertElementPropertyToString(final Collection collection, final String propertyName,
    // final String separator) {
    // List list = convertElementPropertyToList(collection, propertyName);
    // return StringUtils.join(list, separator);
    // }

    /**
     * ת���ַ�������Ӧ����.
     * 
     * @param value ��ת�����ַ���
     * @param toType ת��Ŀ������
     */
    @SuppressWarnings("unchecked")
    public static < T > T convertStringToObject(String value, Class < T > toType) {
        return (T) ConvertUtils.convert(value, toType);
    }

    /**
     * ������ʱ��checked exceptionת��Ϊunchecked exception.
     */
    public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
        return convertReflectionExceptionToUnchecked(null, e);
    }

    public static RuntimeException convertReflectionExceptionToUnchecked(String desc, Exception e) {
        desc = (desc == null) ? "Unexpected Checked Exception." : desc;
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
                || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException(desc, e);
        }
        else if (e instanceof InvocationTargetException) {
            return new RuntimeException(desc, ((InvocationTargetException) e).getTargetException());
        }
        else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException(desc, e);
    }

    /**
     * �õ��µĶ���
     * 
     * @param <T>
     * @param cls
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static final < T > T getNewInstance(Class < T > cls) throws Exception {
        return cls.newInstance();
    }

    /**
     * �������ͻ�ö���
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     * 
     * @throws ClassNotFoundException
     */
    public static final Object getObjectByClassName(String clazz) throws Exception {
        return Class.forName(clazz).newInstance();
    }

    
}
