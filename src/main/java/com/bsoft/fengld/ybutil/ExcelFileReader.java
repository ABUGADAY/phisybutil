package com.bsoft.fengld.ybutil;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fengld on 2017/8/20.
 * E-mail : fengld@bsoft.com.cn
 */
public class ExcelFileReader {
    private static final int name_index = ExcelFile.getName_index();
    private static final int type_index = ExcelFile.getType_index();
    private static final int length_index = ExcelFile.getLength_index();
    private static final int alias_index = ExcelFile.getAlias_index();
    private static final int remark_index = ExcelFile.getRemark_index();
    private static final int name_index4Js = ExcelFile.getName_index4Js();

    public static int getName_index() {
        return name_index;
    }

    public static int getType_index() {
        return type_index;
    }

    public static int getLength_index() {
        return length_index;
    }

    public static int getAlias_index() {
        return alias_index;
    }

    public static int getRemark_index() {
        return remark_index;
    }

    public static int getName_index4Js() {
        return name_index4Js;
    }

    public static boolean readerExcel(String path, java.util.List<java.util.List<String>> rows_list) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        FileInputStream fint = new FileInputStream(file);
        POIFSFileSystem poiFileSystem = new POIFSFileSystem(fint);
        HSSFWorkbook workbook = new HSSFWorkbook(poiFileSystem);
        //获取第一张Sheet表
        HSSFSheet sheet = workbook.getSheetAt(0);

        //我们既可能通过Sheet的名称来访问它，也可以通过下标来访问它。如果通过下标来访问的话，要注意的一点是下标从0开始，就像数组一样。
        //获取第一列数据(字段名)
        int rowNum = sheet.getLastRowNum();//获得总行数
        //获取总列数
        int columnNum = sheet.getRow(0).getPhysicalNumberOfCells();
        for (int i = 0; i <= rowNum; i++) {
            java.util.List<String> row_list = new ArrayList<String>();
            HSSFRow row;
            row = sheet.getRow(i);
            for (int j = 0; j < columnNum; j++) {
                row_list.add(row.getCell(j).toString());
            }
            rows_list.add(row_list);
        }
        fint.close();
        return true;
    }

    /**
     * 读取Excel 文件返回 对应的数组
     * @param rows_list
     * @return
     */
    public static ArrayList<Map<String,Object>> getExcelContent(java.util.List<java.util.List<String>> rows_list) throws PinyinException {
        ArrayList<Map<String,Object>> content_list = new ArrayList<Map<String,Object>>();
        for(int i=0 ; i<rows_list.size() ; i++){//每行
            Map<String , Object>content_map = new HashMap<String,Object>();
            for(int j = 0; j < rows_list.get(i).size(); j++){//每列
                if(j == name_index)
                    if(isContainsChinese(rows_list.get(i).get(name_index))){
                        content_map.put("name" , PinyinHelper.getShortPinyin(rows_list.get(i).get(name_index)).toUpperCase());
                    }
                    else{
                        content_map.put("name", rows_list.get(i).get(name_index));
                    }
                if(j == type_index)
                    content_map.put("type" , rows_list.get(i).get(type_index).split("\\.")[0]);
                if(j == length_index)
                    content_map.put("length" , rows_list.get(i).get(length_index).split("\\.")[0]);
                if(j == alias_index)
                    content_map.put("alias" , rows_list.get(i).get(alias_index));
                if(j == remark_index)
                    content_map.put("remark" , rows_list.get(i).get(remark_index));
                if(j == name_index4Js)
                    content_map.put("name4Js" , rows_list.get(i).get(name_index4Js));
            }
            content_list.add(content_map);
        }
        return content_list;
    }

    public static boolean isContainsChinese(String str){
        for (int i = 0; i < str.length(); i++) {
            if (str.substring(i, i + 1).matches("[\\u4e00-\\u9fbb]+")) {
                return true;
            }
        }
        return false;
    }
}
