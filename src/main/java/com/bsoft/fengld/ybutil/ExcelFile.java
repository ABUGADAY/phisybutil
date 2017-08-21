package com.bsoft.fengld.ybutil;

/**
 * Created by Fengld on 2017/8/20.
 * E-mail : fengld@bsoft.com.cn
 */
public class ExcelFile
{
    private static int name_index4Js = 1;   //Js中显示的对方接口的参数名
    private static int type_index = 5;      //字段类型
    private static int length_index = 3;    //字段长度
    private static int alias_index = 2;     //字段别名
    private static int remark_index = 4;    //备注
    private static int name_index = alias_index;    //我方字段名，自动中文转拼音首字母大写



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

    public static int getName_index4Js() {
        return name_index4Js;
    }

    public static void setName_index4Js(int name_index4self) {
        ExcelFile.name_index4Js = name_index4self;
    }

}
