package com.bsoft.fengld.ybutil;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;

/**
 * Created by Fengld on 2017/8/17.
 * E-mail : fengld@bsoft.com.cn
 * 支持 2007
 */
public class YbAutoUtil extends JFrame {
    //下拉选项框
    private static JComboBox fileType = null;
    //文件名
    private static String fileName = null;
    //文件路径文本框
    private static JTextField pathTextField = null;
    //出参文本框
    private static JTextArea reultTextArea = null;
    //
    private static String[] fileTypeEnum = {"1.schema文件", "2.mapping文件", "3.建表sql语句", "4.Js入参", "5.Js出参" ,"6.Dic文件" };

    public YbAutoUtil() {
        JFrame utilFrame = new JFrame();
        //窗口大小
        utilFrame.setSize(1000, 600);
//		//窗口位置
        utilFrame.setLocation(200, 90);
        //窗口可关闭(程序关闭)
        utilFrame.setDefaultCloseOperation(utilFrame.EXIT_ON_CLOSE);
        utilFrame.setTitle("文本生成器");
        utilFrame.setResizable(false);
        //自由布局方式
        utilFrame.setLayout(null);

        //下拉选项框说明
        JLabel fileTypeLabel = new JLabel();
        fileTypeLabel.setText("转换的文件类型:");
        Font actionLabelFont = new Font("11", 1, 11);
        fileTypeLabel.setFont(actionLabelFont);
        fileTypeLabel.setBounds(10, 20, 100, 20);
        //下拉选项框
        fileType = new JComboBox(fileTypeEnum);
        fileType.setBounds(100, 20, 130, 20);

        //生成按钮
        JButton buildBut = new JButton();
        buildBut.setText("生成");
        Font buttonFont = new Font("11", 1, 11);
        buildBut.setFont(buttonFont);
        //按钮的位置与大小
        buildBut.setBounds(350, 16, 60, 26);

        //选择按钮
        JButton choosePath = new JButton();
        choosePath.setText("选择");
        choosePath.setFont(buttonFont);
        //按钮的位置与大小
        choosePath.setBounds(10, 56, 60, 26);

        //路径的文本框
        pathTextField = new JTextField();
        pathTextField.setBounds(75, 58, 590, 24);

        //选择监听事件
        choosePath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileSystemView fsv = FileSystemView.getFileSystemView();                            //注意了，这里重要的一句
                JFileChooser jfc = new JFileChooser();
                jfc.setCurrentDirectory(fsv.getHomeDirectory());                                            //得到桌面路径
                ExcelFileFilter excelFilter = new ExcelFileFilter();                                            //excel过滤器
                jfc.addChoosableFileFilter(excelFilter);
                jfc.setFileFilter(excelFilter);
                jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                jfc.showDialog(new JLabel(), "选择");
                File file = jfc.getSelectedFile();
                if (file != null) {
                    String type[] = file.getName().split(".");
                    if (file.isDirectory()) {
                        JOptionPane.showMessageDialog(null, "请勿选择文件夹，请正确选择excel类型文件");
                        return;
                    }
                    fileName = file.getName();
                    pathTextField.setText(file.getAbsolutePath());

                }
            }
        });

        //生成按钮事件
        buildBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.util.List<java.util.List<String>> rows_list = new ArrayList<java.util.List<String>>();
                try {
                    if (ExcelFileReader.readerExcel(pathTextField.getText(), rows_list)) {
                        if ("1.schema文件".equals(fileType.getSelectedItem().toString())) {
                            buildSchema(rows_list);
                        } else if ("2.mapping文件".equals(fileType.getSelectedItem().toString())) {
                            buildMapping(rows_list);
                        } else if ("3.建表sql语句".equals(fileType.getSelectedItem().toString())) {
                            buildSql(rows_list);
                        } else if ("4.Js入参".equals(fileType.getSelectedItem().toString())) {
                            buildInput(rows_list);
                        } else if ("5.Js出参".equals(fileType.getSelectedItem().toString())) {
                            buildOutput(rows_list);
                        } else if ("6.Dic文件".equals(fileType.getSelectedItem().toString())) {
                            buildDictionary(rows_list);
                        }
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null, "当前选择的文件不存在，或者文件格式不符合规范");
                    return;

                }
            }
        });

        //生成提示文本
        JLabel reultLabel = new JLabel();
        reultLabel.setText("生成文本信息:");
        reultLabel.setBounds(20, 90, 100, 30);

        //出参窗口
        reultTextArea = new JTextArea();
        reultTextArea.setLineWrap(true);
        JScrollPane reultScroll = new JScrollPane(reultTextArea);
        reultScroll.setBounds(15, 120, 960, 430);

        //向面板上添加相关控件
        utilFrame.add(fileTypeLabel);
        utilFrame.add(fileType);
        utilFrame.add(buildBut);
        utilFrame.add(choosePath);
        utilFrame.add(pathTextField);

        utilFrame.add(reultLabel);
        utilFrame.add(reultScroll);

        //显示窗口
        utilFrame.setVisible(true);
    }



    public static void main(String[] args) {
        new YbAutoUtil();
    }


    //生成schema文件
    public void buildSchema(java.util.List<java.util.List<String>> rows_list) throws PinyinException {
        java.util.List<String> text_rows = new ArrayList<String>();
        ArrayList<Map<String , Object>>con_list = ExcelFileReader.getExcelContent(rows_list);
        Iterator<Map<String , Object>>it = con_list.iterator();
        while(it.hasNext()){
            String str = "<item";
            Map<String , Object>con_map = it.next();
            for(Map.Entry<String ,Object> entry : con_map.entrySet()){
                if(entry.getKey().equals("remark")||entry.getKey().equals("name4Js"))
                    continue;
                if(entry.getKey().equals("name"))
                    str += " id=\""+entry.getValue()+"\"";
                else if(!entry.getKey().equals("index"))
                    str += " " +entry.getKey()+"=\""+entry.getValue()+"\"";
            }
            if(!con_map.containsKey("type"))
                str += " type = \"String\"";
            str += " />";
            text_rows.add(str);
        }
        reultTextArea.setText("");
        String text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + "" +
                "<entry entityName=\"" + fileName.toUpperCase().split("\\.")[0] + "\"  alias=\"" + fileName.toUpperCase().split("\\.")[0] + "\">\r\n";
        for (int i = 0; i < text_rows.size(); i++) {
            text += "	" + text_rows.get(i) + "\r\n";
        }
        text += "</entry>";
        reultTextArea.setText(text);
    }

    //生成mapping文件
    public void buildMapping(java.util.List<java.util.List<String>> rows_list) throws PinyinException {
        java.util.List<String> text_rows = new ArrayList<String>();
        ArrayList<Map<String , Object>>con_list = ExcelFileReader.getExcelContent(rows_list);
        Iterator<Map<String , Object>>it = con_list.iterator();
        while(it.hasNext()) {
            String str = "<property ";
            Map<String, Object> con_map = it.next();
            boolean flag = true ;
            for (Map.Entry<String, Object> entry : con_map.entrySet()) {
                String type = "";
                if(!con_map.containsKey("type") && flag) {
                    type = "type = \"java.lang.String\" ";
                    str += type;
                    flag = false ;
                }
                if(entry.getKey().equals("remark")||entry.getKey().equals("alias")||entry.getKey().equals("name4Js"))
                    continue;
                else if (entry.getKey().equals("type")) {
                    if (entry.getValue().toString().toLowerCase().equals("varchar2") || entry.getValue().toString().toLowerCase().equals("string"))
                        type = "type = \"java.lang.String\" ";
                    else if (entry.getValue().toString().toLowerCase().equals("number") || entry.getValue().toString().toLowerCase().equals("int") || entry.getValue().toString().toLowerCase().equals("long"))
                        type = "type = \"java.lang.Long\" ";
                    else
                        type = "type= \"" + type.toLowerCase()+"\" ";
                    str += type;
                }
                else if(!entry.getKey().equals("index"))
                    str += " " +entry.getKey()+"=\""+entry.getValue()+"\" ";
            }
            str +="/>";
            text_rows.add(str);
        }
        reultTextArea.setText("");
        String text = "<?xml version=\"1.0\"?>\r\n" +
                "<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"\r\n" +
                "\"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd\">\r\n" +
                "<hibernate-mapping>\r\n" +
                "	<class entity-name=\"" + fileName.toUpperCase().split("\\.")[0] + "\" table=\"" + fileName.toUpperCase().split("\\.")[0] + "\">\r\n";
        for (int i = 0; i < text_rows.size(); i++) {
            text += "		" + text_rows.get(i) + "\r\n";
        }
        text += "	</class>\r\n" +
                "</hibernate-mapping>";
        reultTextArea.setText(text);
    }

    //生成对应的sql建表语句
    public void buildSql(java.util.List<java.util.List<String>> rows_list) throws PinyinException {
        String tableName = "";
        Map<String, Object> sql = new HashMap<String, Object>();
        java.util.List<String> text_rows = new ArrayList<String>();
        java.util.List<String> com_rows = new ArrayList<String>();
        for (int i = 0; i < rows_list.size(); i++) {            //每一行
            String row_str = "";
            String com_str = "";
            for (int j = 0; j < rows_list.get(i).size(); j++) {            //每一列（每个单元格）
//                if (j == ExcelFileReader.getName_index()) {  //参数名
//                    //暂不处理
//                } else
                    if (j == ExcelFileReader.getType_index()) {    //字段类型
                    String type = rows_list.get(i).get(j).trim();
//                        System.out.println(type);
                    if (type.toLowerCase().equals("varchar2") || type.toLowerCase().equals("string")) {
                        sql.put("type", "VARCHAR2(");
                    } else if (type.toLowerCase().equals("number")) {
                        sql.put("type", "NUMBER(");
                    } else {
                        sql.put("type", type.toLowerCase() + "(");
                    }
                } else if (j == ExcelFileReader.getLength_index()) {        //长度
                    sql.put("length", rows_list.get(i).get(j).split("\\.")[0].toString().trim() + ")");
                } else if (j == ExcelFileReader.getAlias_index()) {        //字段名
                    sql.put("colName", PinyinHelper.getShortPinyin(rows_list.get(i).get(j)).toUpperCase());
                    sql.put("colComments", rows_list.get(i).get(j));
                }
            }
            row_str += sql.get("colName").toString() + " " + sql.get("type").toString() + sql.get("length").toString();
            if (i < rows_list.size() - 1)
                row_str += ",";
            com_str += "comment on column " + fileName.toUpperCase().split("\\.")[0] + "." + sql.get("colName") + "\r\n" + "is '" + sql.get("colComments") + "' ;\r\n";
            text_rows.add(row_str);
            com_rows.add(com_str);
        }
        reultTextArea.setText("");
        String text = "--create table with given colname you'd better add index and key by yourself\r\n--只生成列，主键等自己加\r\n"
                + "create table  " + fileName.toUpperCase().split("\\.")[0] + "\r\n" + "(" + "\r\n";
        for (int i = 0; i < text_rows.size(); i++) {
            text += text_rows.get(i) + "\r\n";
        }
        text += ")\r\n;\r\n--Add comments to the columns \r\n";
        for (int i = 0; i < com_rows.size(); i++) {
            text += com_rows.get(i) + "\r\n";
        }
        reultTextArea.setText(text);
    }

    //生成对应的Js入参
    public void buildInput(java.util.List<java.util.List<String>> rows_list) throws PinyinException {
        java.util.List<String> text_rows = new ArrayList<String>();
        ArrayList<Map<String , Object>>con_list = ExcelFileReader.getExcelContent(rows_list);
        Iterator<Map<String , Object>>it = con_list.iterator();
        while(it.hasNext()){
            String str = "request.";
            Map<String , Object>con_map = it.next();
            str += con_map.get("name4Js").toString()+"=  "+PinyinHelper.getShortPinyin(con_map.get("alias").toString()).toUpperCase()+" ;//"+(con_map.containsKey("alias")?con_map.get("alias").toString():"")+"\r\n";
            text_rows.add(str);
        }
        reultTextArea.setText("");
        String text="var request = {}\r\n";
        for(int i=0 ; i<text_rows.size() ; i++)
            text += text_rows.get(i);
        reultTextArea.setText(text);
    }

    //Js出参
    public void buildOutput(java.util.List<java.util.List<String>> rows_list) throws PinyinException {
        java.util.List<String> text_rows = new ArrayList<String>();
        ArrayList<Map<String , Object>>con_list = ExcelFileReader.getExcelContent(rows_list);
        Iterator<Map<String , Object>>it = con_list.iterator();
        while(it.hasNext()){
            String str = "this.XXXX[\"";
            Map<String , Object>con_map = it.next();
            str += PinyinHelper.getShortPinyin(con_map.get("alias").toString()).toUpperCase()+"\"]= "+"ret."+con_map.get("name4Js")+" //"+(con_map.containsKey("alias")?con_map.get("alias").toString():"")+"\r\n";
            text_rows.add(str);
        }
        reultTextArea.setText("");
        String text="var ret = //调用方法返回数据\r\nto-do//异常处理的实现\r\n";
        for(int i=0 ; i<text_rows.size() ; i++)
            text += text_rows.get(i);
        reultTextArea.setText(text);
    }

    //Dic生成
    public void buildDictionary(java.util.List<java.util.List<String>> rows_list)throws PinyinException {
        java.util.List<String> text_rows = new ArrayList<String>();
        ArrayList<Map<String , Object>>con_list = ExcelFileReader.getExcelContent(rows_list);
        Iterator<Map<String , Object>>it = con_list.iterator();
        while(it.hasNext()){
            String str = "";
            Map<String , Object>con_map = it.next();
            str += "<item key = \""+con_map.get("index").toString().split("\\.")[0]+"\""+" text = \""+(con_map.containsKey("name4Js")?con_map.get("name4Js").toString():"")+"\" />\r\n";
            text_rows.add(str);
        }
        reultTextArea.setText("");
        String text="";
        for(int i=0 ; i<text_rows.size() ; i++)
            text += text_rows.get(i);
        reultTextArea.setText(text);
    }
}
