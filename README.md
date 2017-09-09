## 医保开发自用工具，利用Excel快速生成 
<li>1.schema文件</li>
<li>2.mapping文件</li>
<li>3.建表sql语句</li>
<li>4.Js入参</li>
<li>5.Js出参</li>
 表名为Excel文件名
 
 玩票的自用小工具,一边使用一边完善
                                                                  
### 1.schema
|1.参数名|2.参数类型|3.字段长度|4.字段说明|5.备注|
|------:|------:|------:|------:|------:|
|不处理|type|length|alias && id|不处理|
                                                                   
### 2.mapping
 |1.参数名|2.参数类型|3.字段长度|4.字段说明|5.备注|
 |------:|------:|------:|------:|------:|
 |不处理|type|length|name|不处理|
 
 ### 3.建表sql语句
 |1.参数名|2.参数类型|3.字段长度|4.字段说明|5.备注|
  |------:|------:|------:|------:|------:|
  |不处理|type|length|name&&remark|不处理|
  
 ### 4.Js入参(json)
 |1.参数名|2.参数类型|3.字段长度|4.字段说明|5.备注|
  |------:|------:|------:|------:|------:|
  |name|不处理|不处理|remark|不处理|
 ### 5.Js出参(json)
  1.参数名|2.参数类型|3.字段长度|4.字段说明|5.备注|
  |------:|------:|------:|------:|------:|
  |name|不处理|不处理|colName&&remark|不处理|
### 待处理
 |问题|预计处理时间|
 |------:|------:|
 |07+ 版本兼容|09-10|
 |重构|视近期工作量决定|
 |模块根据文档结构读取|09-25|
 
 主要功能完成与 2017-08-18 ,后期增加不同模板，兼容性支持
 Tip：重构时将类抽取出来
 
 
 [2017-08-20]Add: 新增加配置文件ExcelFile，在该文件中设置你要取的字段类型的下标[0 , length-1]  
 [2017-08-20]Tip: SQL方法待优化;暂时仅支持 Excel 03~07 格式  
 [2017-09-09]Tip: 工作繁忙，推迟   
  感谢[@Jayer](https://github.com/stuxuhai) 提供[JPinyin](https://github.com/stuxuhai/jpinyin)一个汉字转拼音的Java开源类库
