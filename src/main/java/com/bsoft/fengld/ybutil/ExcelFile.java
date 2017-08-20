package com.bsoft.fengld.ybutil;

/**
 * Created by Fengld on 2017/8/20.
 * E-mail : fengld@bsoft.com.cn
 */
public class ExcelFile
{
    private static int name_index = 3;
    private static int type_index = 1;
    private static int length_index = 2;
    private static int alias_index = 3;
    private static int remark_index = 4;

    /**
     * 先以默认配置为准，以后加上界面选择序号
     * @return
     */

    public static int getName_index() {
        return name_index;
    }

    public static void setName_index(int name_index) {
        ExcelFile.name_index = name_index;
    }

    public static int getType_index() {
        return type_index;
    }

    public static void setTpye_index(int type_index) {
        ExcelFile.type_index = type_index;
    }

    public static int getLength_index() {
        return length_index;
    }

    public static void setLength_index(int length_index) {
        ExcelFile.length_index = length_index;
    }

    public static int getAlias_index() {
        return alias_index;
    }

    public static void setAlias_index(int alias_index) {
        ExcelFile.alias_index = alias_index;
    }

    public static int getRemark_index() {
        return remark_index;
    }

    public static void setRemark_index(int remark_index) {
        ExcelFile.remark_index = remark_index;
    }



}
