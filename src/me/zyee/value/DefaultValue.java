package me.zyee.value;

import com.intellij.psi.PsiType;


/**
 * @author yee
 * @date 2018/11/15
 */
public class DefaultValue {
    private static final Integer DEFAULT_INT = 0;
    private static final Boolean DEFAULT_BOOL = false;
    private static final Long DEFAULT_LONG = 0L;
    private static final Double DEFAULT_DOUBLE = 0D;
    private static final Byte DEFAULT_BYTE = 0x0;
    private static final Short DEFAULT_SHORT = 0;
    private static final Character DEFAULT_CHAR = ' ';
    private static final Float DEFAULT_FLOAT = 0F;


    public static Object value(PsiType type) {
        if (PsiType.BYTE.equals(type)) {
            return DEFAULT_BYTE;
        }
        if (PsiType.CHAR.equals(type)) {
            return DEFAULT_CHAR;
        }
        if (PsiType.DOUBLE.equals(type)) {
            return DEFAULT_DOUBLE;
        }
        if (PsiType.FLOAT.equals(type)) {
            return DEFAULT_FLOAT;
        }
        if (PsiType.INT.equals(type)) {
            return DEFAULT_INT;
        }
        if (PsiType.LONG.equals(type)) {
            return DEFAULT_LONG;
        }
        if (PsiType.SHORT.equals(type)) {
            return DEFAULT_SHORT;
        }
        if (PsiType.BOOLEAN.equals(type)) {
            return DEFAULT_BOOL;
        }
        return "null";
    }
}
