package com.bsoft.fengld.ybutil;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fengld on 2017/8/17.
 * E-mail : fengld@bsoft.com.cn
 * 支持 2007
 */
public class YbAutoUtil  extends JFrame {
    //下拉选项框
    private static JComboBox fileType = null;
    //文件名
    private static String fileName = null;
    //文件路径文本框
    private static JTextField pathTextField = null;
    //出参文本框
    private static JTextArea reultTextArea = null;
    //
    private static String[] fileTypeEnum = {"1.schema文件", "2.mapping文件", "3.建表sql语句","4.xml文件","5.其他文件"};
    public YbAutoUtil(){
        JFrame utilFrame = new JFrame();
        //窗口大小
        utilFrame.setSize(1000, 600);
//		//窗口位置
        utilFrame.setLocation(200,90);
        //窗口可关闭(程序关闭)
        utilFrame.setDefaultCloseOperation(utilFrame.EXIT_ON_CLOSE);
        utilFrame.setTitle("文本生成器");
        utilFrame.setResizable(false);
        //自由布局方式
        utilFrame.setLayout(null );

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
        pathTextField.setBounds(75, 58,590, 24);

        //选择监听事件
        choosePath.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                FileSystemView fsv = FileSystemView.getFileSystemView();  							//注意了，这里重要的一句
                JFileChooser jfc=new JFileChooser();
                jfc.setCurrentDirectory(fsv.getHomeDirectory());    										//得到桌面路径
                ExcelFileFilter excelFilter = new ExcelFileFilter(); 											//excel过滤器
                jfc.addChoosableFileFilter(excelFilter);
                jfc.setFileFilter(excelFilter);
                jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
                jfc.showDialog(new JLabel(), "选择");
                File file=jfc.getSelectedFile();
                if(file != null){
                    String type[] = file.getName().split(".");
                    if(file.isDirectory()){
                        JOptionPane.showMessageDialog(null, "请勿选择文件夹，请正确选择excel类型文件");
                        return;
                    }
                    fileName = file.getName();
                    pathTextField.setText(file.getAbsolutePath());

                }
            }
        });

        //生成按钮事件
        buildBut.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                java.util.List<java.util.List<String>> rows_list = new ArrayList<java.util.List<String>>();
                try {
                    if(readerExcel(pathTextField.getText(), rows_list)){
                        if("1.schema文件".equals(fileType.getSelectedItem().toString())){
                            buildSchema(rows_list);
                        }else if("2.mapping文件".equals(fileType.getSelectedItem().toString())){
                            buildMapping(rows_list);
                        }else if("3.建表sql语句".equals(fileType.getSelectedItem().toString())){
                            buildSql(rows_list);
                        } else if("4.Js入参".equals(fileType.getSelectedItem().toString())){
                            buildInput(rows_list);
                        }else if("5.Js出参".equals(fileType.getSelectedItem().toString())){
                            buildOutput(rows_list);
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
    public  void buildSchema(java.util.List<java.util.List<String>> rows_list) throws PinyinException {
        java.util.List<String> text_rows = new ArrayList<String>();
        for(int i=0; i<rows_list.size(); i++){			//每一行
            String  row_str ="<item ";
            Map<String , Object> schema = new HashMap<String , Object>();
            for(int j=0; j<rows_list.get(i).size(); j++){			//每一列（每个单元格）
                if(j == 3 && !(rows_list.get(i).get(j) ==null)){  //字段名
                    String colName = rows_list.get(i).get(j);
                   schema.put("id" , " id=\""+ PinyinHelper.getShortPinyin(colName).toUpperCase()+"\"   ");

                 	//说明文字
                    schema.put("alias" , "alias=\""+rows_list.get(i).get(j)+"\"   ");
                }else if(j == 1){		//字段类型
                    String str = "";
                    if(rows_list.get(i).get(j).indexOf("(") != -1){
                        String type = rows_list.get(i).get(j).split("[(]")[0];
                        String length = rows_list.get(i).get(j).split("[(]")[1].split("[)]")[0];
                        if(type.toLowerCase().equals("varchar2")){
                            str= " type=\"string\"   length=\""+length+"\" ";
                        }else if(type.toLowerCase().equals("number")){
                            str= " type=\"long\"   length=\""+length+"\" ";
                        }else{
                            str= " type=\""+type.toLowerCase()+"\"    length=\""+length+"\" ";
                        }
                        schema.put("type" , str);
                    }else{
                        schema.put("type"," type=\""+rows_list.get(i).get(j).toLowerCase()+"\" ");
                    }
                }else if(j == 2){//字段长度
                   schema.put("length"," length=\""+rows_list.get(i).get(j).split("\\.")[0]+"\"   ");
                }
            }
            row_str +=schema.get("id").toString()+schema.get("type").toString()+schema.get("length").toString()+schema.get("alias").toString()+" />";
            text_rows.add(row_str);
        }
        reultTextArea.setText("");
        String text ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"+"" +
                "<entry entityName=\""+fileName.toUpperCase().split("\\.")[0]+"\"  alias=\""+fileName.toUpperCase().split("\\.")[0]+"\">\r\n";
        for(int i=0; i<text_rows.size(); i++){
            text +="	"+text_rows.get(i)+"\r\n";
        }
        text += "</entry>";
        reultTextArea.setText(text);
    }
    //生成mapping文件
    public  void buildMapping(java.util.List<java.util.List<String>> rows_list) throws PinyinException {
        java.util.List<String> text_rows = new ArrayList<String>();
        for(int i=0; i<rows_list.size(); i++){			//每一行
            Map<String , Object> mapping = new HashMap<String,Object>();
            String  row_str ="<property ";
            for(int j=0; j<rows_list.get(i).size(); j++){			//每一列（每个单元格）
                if(j == 3){  //字段名
                    String colName = rows_list.get(i).get(j);
                    mapping.put("name","name="+PinyinHelper.getShortPinyin(colName).toUpperCase()+"\"   ");
                }else if(j == 1){		//字段类型
                        String type = rows_list.get(i).get(j).trim();
//                        System.out.println(type);
                        if(type.toLowerCase().equals("varchar2") || type.toLowerCase().equals("string")){
                            mapping.put("type","type=\"java.lang.String\" ") ;
                        }else if(type.toLowerCase().equals("number")){
                            mapping.put("type","type=\"java.lang.Long\" ") ;
                        }else{
                            mapping.put("type","type=\""+type.toLowerCase()+"\"  ") ;
                        }
                }else if(j ==2){
                    String length = rows_list.get(i).get(j).split("\\.")[0].toString();//整数位
                    mapping.put("length","  length=\"" + length + "\"  ");
                }
            }
            row_str+=mapping.get("name").toString()+mapping.get("type").toString()+mapping.get("length").toString()+"/>";
            text_rows.add(row_str);
        }
        reultTextArea.setText("");
        String text ="<?xml version=\"1.0\"?>\r\n"+
                "<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"\r\n"+
                "\"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd\">\r\n"+
                "<hibernate-mapping>\r\n"+
                "	<class entity-name=\""+fileName.toUpperCase().split("\\.")[0]+"\" table=\""+fileName.toUpperCase().split("\\.")[0]+"\">\r\n";
        for(int i=0; i<text_rows.size(); i++){
            text +="		"+ text_rows.get(i)+"\r\n";
        }
        text+="	</class>\r\n"+
                "</hibernate-mapping>";
        reultTextArea.setText(text);
    }

    public boolean readerExcel(String path, java.util.List<java.util.List<String>> rows_list) throws Exception{
        File file = new File(path);
        if(!file.exists()){
            return false;
        }
        FileInputStream fint = new FileInputStream(file);
        POIFSFileSystem poiFileSystem = new POIFSFileSystem(fint);
        HSSFWorkbook workbook = new HSSFWorkbook(poiFileSystem);
        //获取第一张Sheet表
        HSSFSheet sheet = workbook.getSheetAt(0);

        //我们既可能通过Sheet的名称来访问它，也可以通过下标来访问它。如果通过下标来访问的话，要注意的一点是下标从0开始，就像数组一样。
        //获取第一列数据(字段名)
        /**
         * FileInputStream inp = new FileInputStream("E:\\WEIAN.xls");
         HSSFWorkbook wb = new HSSFWorkbook(inp);
         HSSFSheet sheet = wb.getSheetAt(2); // 获得第三个工作薄(2008工作薄)
         // 填充上面的表格,数据需要从数据库查询
         HSSFRow row5 = sheet.getRow(4); // 获得工作薄的第五行
         HSSFCell cell54 = row5.getCell(3);// 获得第五行的第四个单元格
         cell54.setCellValue("测试纳税人名称");// 给单元格赋值
         //获得总列数
         int coloumNum=sheet.getRow(0).getPhysicalNumberOfCells();
         int rowNum=sheet.getLastRowNum();//获得总行数
         */
        int rowNum=sheet.getLastRowNum();//获得总行数
        //获取总列数
        int columnNum=sheet.getRow(0).getPhysicalNumberOfCells();
        for(int i=0; i<=rowNum; i++){
            java.util.List<String> row_list =new ArrayList<String>();
            HSSFRow row;
            row = sheet.getRow(i);
            for(int j=0; j<columnNum; j++){
                //System.out.println(i+"  "+j);
                row_list.add(row.getCell(j).toString());
            }
            rows_list.add(row_list);
        }
        fint.close();
        return true;
    }

    //生成对应的赋值语句
    public  void buildEvaluate(java.util.List<java.util.List<String>> rows_list) {
        String tableName = "";
        java.util.List<String> text_rows = new ArrayList<String>();
        for(int i=0; i<rows_list.size(); i++){			//每一行
            String  row_str ="";
            for(int j=0; j<rows_list.get(i).size(); j++){			//每一列（每个单元格）
                if(j == 0){  //his业务表名
                    if(i ==0){
                        tableName = rows_list.get(i).get(j);
                    }
                    row_str += rows_list.get(i).get(j)+".put(\"";
                }else if(j == 1){  	//his业务字段
                    row_str += rows_list.get(i).get(j)+"\", ";
                }else if(j == 2){		//下载功能号
                    row_str += "((Map<String, Object>)"+rows_list.get(i).get(j)+".get(\"";
                }else if(j == 3){		//下载功能号中对应节点
                    row_str += rows_list.get(i).get(j)+"\")).get(\"";
                }else if(j == 4){		//下载功能号中对应字段
                    row_str += rows_list.get(i).get(j)+"\"));		";
                }else if(j == 5){		//his字段说明
                    row_str += "//"+rows_list.get(i).get(j);
                }
            }
            text_rows.add(row_str);
        }
        reultTextArea.setText("");
        String text ="Map<String, Object> "+tableName+" = new HashMap<String, Object>();"+"\r\n"+"\r\n";
        for(int i=0; i<text_rows.size(); i++){
            text += text_rows.get(i)+"\r\n";
        }
        //再拼接赋值
        text += "\r\n"+"Map<String, Object> temp = new HashMap<String, Object>();"+"\r\n";
        text += "temp.put(\"scPath\",\""+tableName+"\");"+"\r\n";
        text += "temp.put(\"data\","+tableName+");"+"\r\n";
        text += "result.add(temp);"+"\r\n";
        reultTextArea.setText(text);
    }

    //生成对应的sql建表语句
    public  void buildSql(java.util.List<java.util.List<String>> rows_list) throws PinyinException {
        String tableName = "";
        Map<String,Object> sql = new HashMap<String,Object>();
        java.util.List<String> text_rows = new ArrayList<String>();
        java.util.List<String> com_rows = new ArrayList<String>();
        for(int i=0; i<rows_list.size(); i++){			//每一行
            String  row_str ="";
            String com_str = "";
            for(int j=0; j<rows_list.get(i).size(); j++){			//每一列（每个单元格）
                if(j == 0){  //参数名
                    //暂不处理
                }else if(j == 1){  	//字段类型
                    String type = rows_list.get(i).get(j).trim();
//                        System.out.println(type);
                    if(type.toLowerCase().equals("varchar2") || type.toLowerCase().equals("string")){
                        sql.put("type","VARCHAR2(") ;
                    }else if(type.toLowerCase().equals("number")){
                        sql.put("type","NUMBER(") ;
                    }else{
                        sql.put("type",type.toLowerCase()+"(") ;
                    }
                }else if(j == 2){		//长度
                    sql.put("length",rows_list.get(i).get(j).split("\\.")[0].toString().trim()+")");
                }else if(j == 3){		//字段名
                   sql.put("colName" , PinyinHelper.getShortPinyin(rows_list.get(i).get(j)).toUpperCase());
                   sql.put("colComments" ,rows_list.get(i).get(j));
                }
            }
            row_str += sql.get("colName").toString()+" "+sql.get("type").toString()+sql.get("length").toString();
            if(i<rows_list.size()-1)
                row_str+=",";
            com_str += "comment on column "+fileName.toUpperCase().split("\\.")[0]+"."+sql.get("colName")+"\r\n"+"is '"+sql.get("colComments")+"' ;\r\n";
            text_rows.add(row_str);
            com_rows.add(com_str);
        }
        reultTextArea.setText("");
        String text ="--create table with given colname you'd better add index and key by yourself\r\n--只生成列，主键等自己加\r\n"
                +"create table  "+fileName.toUpperCase().split("\\.")[0]+"\r\n"+"("+"\r\n";
        for(int i=0; i<text_rows.size(); i++){
            text += text_rows.get(i)+"\r\n";
        }
        text+=")\r\n;\r\n--Add comments to the columns \r\n";
        for(int i=0; i<com_rows.size(); i++){
            text += com_rows.get(i)+"\r\n";
        }
        reultTextArea.setText(text);
    }

    //生成对应的Js入参
    public  void buildInput(java.util.List<java.util.List<String>> rows_list) {
        java.util.List<String> text_rows = new ArrayList<String>();
        for(int i=0; i<rows_list.size(); i++){			//每一行
            String  xmlText ="";
            String xmlNode = "";
            for(int j=0; j<rows_list.get(i).size(); j++){			//每一列（每个单元格）
                if(j == 0){  //his业务表名
                    xmlNode = rows_list.get(i).get(j);
                }else if(j == 1){  	//his业务字段
//					xmlText =  rows_list.get(i).get(j);
                }else if(j == 2){		//字段类型
                    if(rows_list.get(i).get(j).indexOf("(") != -1){
                        String type = rows_list.get(i).get(j).split("[(]")[0];
                        String length = rows_list.get(i).get(j).split("[(]")[1].split("[)]")[0];
                        if(type.toLowerCase().equals("varchar2")){
                            xmlText =  rows_list.get(i).get(0);
                        }else if(type.toLowerCase().equals("number")){
                            xmlText =  "123";
                        }
                    }
                }
            }
            text_rows.add("<"+xmlNode+">"+xmlText+"</"+xmlNode+">");
        }
        reultTextArea.setText("");
        String text ="";
        for(int i=0; i<text_rows.size(); i++){
            text += text_rows.get(i)+"\r\n";
        }
        reultTextArea.setText(text);
    }
    //生成对应的指定其他文件
    public  void buildOutput(java.util.List<java.util.List<String>> rows_list) {
        reultTextArea.setText(readerLocalTXT().toUpperCase());
    }

    public String readerLocalTXT() {
        Long now = new Date().getTime();
        // 读取txt内容为字符串
        StringBuffer txtContent = new StringBuffer();
        // 每次读取的byte数
        char[] chs = new char[1024];
        String src = "C:/Users/Administrator/Desktop/new"+now+".txt";
        String str = "";
        String line = "";
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(new File(src));
            isr = new InputStreamReader(fis, "utf-8");
            br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                line +="\r\n";
                str = str + line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }


}
