package com.bsoft.fengld.ybutil;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by Fengld on 2017/8/17.
 * E-mail : fengld@bsoft.com.cn
 */
public class ExcelFileFilter  extends FileFilter {
    public String getDescription() {
        return "*.xls;*.xlsx;*.xlsm";
    }

    @Override
    public boolean accept(File file) {
        String name = file.getName();
        return file.isDirectory() || name.toLowerCase().endsWith(".xls") || name.toLowerCase().endsWith(".xlsx")  || name.toLowerCase().endsWith(".xlsm");  // 仅显示目录和xls、xlsx文件
    }
}