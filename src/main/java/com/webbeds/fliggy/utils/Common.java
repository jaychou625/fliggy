package com.webbeds.fliggy.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;
import java.util.List;
import java.util.Set;

/**
 * 通用工具类
 */
public class Common {
    //首字母转大写
    public String firstCharacterUpper(String str){
        return str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
    }

    //特殊，根据特殊城市名获取城市对应的id
    public Integer getCityIdByCityName(String cityName){
        Integer cityId = 0;
        switch (cityName){
            case "HUALIEN" :
                cityId = 712600;
                break;
            case "KAOHSIUNG" :
                cityId = 710200;
                break;
            case "NEW TAIPEI CITY" :
                cityId = 711100;
                break;
            case "TAICHUNG" :
                cityId = 710400;
                break;
            case "TAIPEI" :
                cityId = 710100;
                break;
            case "HONG KONG" :
                cityId = 810100;
                break;
            case "MACAU - MO" :
                cityId = 820100;
                break;
            case "SHANGHAI" :
                cityId = 310100;
                break;
            default:
                cityId = 0;
                break;
        }
        return cityId;
    }

    //将JSON对象转换成excel并输出
    public void JSONToExcel(List<JSONObject> list,String fileName){
        Set<String> keys = null;
        // 创建HSSFWorkbook对象
        HSSFWorkbook wb = new HSSFWorkbook();
        // 创建HSSFSheet对象
        HSSFSheet sheet = wb.createSheet("sheet0");

        String str = null;
        int roleNo = 0;
        int rowNo = 0;
        for (JSONObject jsonObject : list) {
            // 创建HSSFRow对象
            HSSFRow row = sheet.createRow(roleNo++);
            // 创建HSSFCell对象
            if (keys == null) {
                //标题
                keys = jsonObject.keySet();
                for (String s : keys) {
                    HSSFCell cell = row.createCell(rowNo++);
                    cell.setCellValue(s);
                }
                rowNo = 0;
                row = sheet.createRow(roleNo++);
            }

            for (String s : keys) {
                HSSFCell cell = row.createCell(rowNo++);
                cell.setCellValue(jsonObject.getString(s));
            }
            rowNo = 0;

        }

        // 输出Excel文件
        FileOutputStream output = null;
        try {
            output = new FileOutputStream("c://" + fileName + ".xls");
            wb.write(output);
            wb.close();
            output.flush();
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
