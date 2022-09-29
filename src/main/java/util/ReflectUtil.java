package util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class ReflectUtil {
    public static Object getValue(Object paramObject, String propertyName) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        if (paramObject instanceof Map){
            Object value = ((Map<?, ?>) paramObject).get(propertyName);
            return value;
        }else if(paramObject instanceof List){

        }else{
            Class<?> objectClass = paramObject.getClass();
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, objectClass);
            Method getMethod = propertyDescriptor.getReadMethod();
            return getMethod.invoke(paramObject);
        }
        return null;
    }
}
