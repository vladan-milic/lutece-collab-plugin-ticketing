package fr.paris.lutece.plugins.ticketing.util.sort;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Locale;

import fr.paris.lutece.portal.service.util.AppLogService;

public class TicketAttributeComparator  implements Comparator<Object>, Serializable
{
    private static final long serialVersionUID = 8552197766086300259L;
    private String _strSortedAttribute;
    private boolean _bIsASC;

    public TicketAttributeComparator(String strSortedAttribute, boolean bIsASC) {
        this._strSortedAttribute = strSortedAttribute;
        this._bIsASC = bIsASC;
    }

    public TicketAttributeComparator(String strSortedAttribute) {
        this._strSortedAttribute = strSortedAttribute;
        this._bIsASC = true;
    }

    public int compare(Object o1, Object o2) {
        int nStatus = 0;
        Method method1 = this.getMethod(o1);
        Method method2 = this.getMethod(o2);
        if (method1 != null && method2 != null && method1.getReturnType() == method2.getReturnType()) {
            try {
                Object oRet1 = method1.invoke(o1);
                Object oRet2 = method2.invoke(o2);
                String strReturnType = method1.getReturnType().getName();
                Class<?> returnType = method1.getReturnType();
                if (oRet1 == null) {
                    if ( oRet2 != null )
                    {
                        nStatus = -1;
                    } // sinon nStatus = 0
                } else if (oRet2 == null) {
                    nStatus = 1;
                } else if (strReturnType.equals("java.lang.String")) {
                    oRet1 =  StringUtils.foldToAscii( ((String) oRet1).trim() );
                    oRet2 =  StringUtils.foldToAscii( ((String) oRet2).trim() );
                    nStatus = ((String)oRet1).toLowerCase(Locale.ENGLISH).compareTo(((String)oRet2).toLowerCase(Locale.ENGLISH));
                } else if (!returnType.isPrimitive() && !this.isComparable(returnType)) {
                    if (returnType.isEnum()) {
                        nStatus = oRet1.toString().compareTo(oRet2.toString());
                    }
                } else {
                    nStatus = ((Comparable)oRet1).compareTo((Comparable)oRet2);
                }
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException var10) {
                AppLogService.error(var10);
            }
        }

        if (!this._bIsASC) {
            nStatus *= -1;
        }

        return nStatus;
    }

    private Method getMethod(Object obj) {
        Method method = null;
        String strFirstLetter = this._strSortedAttribute.substring(0, 1).toUpperCase();
        String strMethodName = "get" + strFirstLetter + this._strSortedAttribute.substring(1, this._strSortedAttribute.length());

        try {
            method = obj.getClass().getMethod(strMethodName);
        } catch (Exception var6) {
            AppLogService.error(var6);
        }

        return method;
    }

    private boolean isComparable(Class<?> clazz) {
        Class[] var2 = clazz.getInterfaces();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Class<?> interfac = var2[var4];
            if (interfac.equals(Comparable.class)) {
                return true;
            }
        }

        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            return this.isComparable(superClass);
        } else {
            return false;
        }
    }
}
