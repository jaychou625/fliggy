package com.webbeds.fliggy.utils;

import com.alibaba.fastjson.JSONObject;
import com.webbeds.fliggy.entity.DOTW_hotel_info;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
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

    /**
     * excel转实体类
     * @param path
     * @return
     * @throws Exception
     */
    public List<DOTW_hotel_info> excel2Bean(String path) throws Exception {
        InputStream is = new FileInputStream(path);
        XSSFWorkbook excel = new XSSFWorkbook(is);
//        HSSFWorkbook excel = new HSSFWorkbook(is);
        DOTW_hotel_info dotw_hotel_info = null;
        List<DOTW_hotel_info> list = new ArrayList<DOTW_hotel_info>();

        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < excel.getNumberOfSheets(); numSheet++) {
//            HSSFSheet sheet = excel.getSheetAt(numSheet);
            XSSFSheet sheet = excel.getSheetAt(numSheet);
            if (sheet == null)
                continue;
            // 循环行Row
            for (int rowNum = 1; rowNum < sheet.getLastRowNum(); rowNum++) {
//                HSSFRow row = sheet.getRow(rowNum);
                XSSFRow row = sheet.getRow(rowNum);
                if (row == null)
                    continue;
                dotw_hotel_info = new DOTW_hotel_info();
                XSSFCell cell = row.getCell(1);
                if (cell == null)
                    continue;
                dotw_hotel_info.setCountry(cell.getStringCellValue());
                cell = row.getCell(4);
                if (cell == null)
                    continue;
                dotw_hotel_info.setCity(cell.getStringCellValue());
                cell = row.getCell(6);
                if (cell == null)
                    continue;
                dotw_hotel_info.setHotelCode(cell.getRawValue());
                cell = row.getCell(7);
                if (cell == null)
                    continue;
                dotw_hotel_info.setHotelName(cell.getStringCellValue());
                cell = row.getCell(8);
                if (cell == null)
                    continue;
                dotw_hotel_info.setStarRating(cell.getStringCellValue());
                cell = row.getCell(9);
                if (cell == null)
                    continue;
                dotw_hotel_info.setReservationTelephone(cell.getRawValue());
                cell = row.getCell(10);
                if (cell == null)
                    continue;
                dotw_hotel_info.setHotelAddress(cell.getStringCellValue());
                cell = row.getCell(11);
                if (cell == null)
                    continue;
                dotw_hotel_info.setLatitude(cell.getRawValue());
                cell = row.getCell(12);
                if (cell == null)
                    continue;
                dotw_hotel_info.setLongitude(cell.getRawValue());
                cell = row.getCell(13);
                if (cell == null)
                    continue;
                dotw_hotel_info.setChainName(cell.getStringCellValue());
                cell = row.getCell(14);
                if (cell == null)
                    continue;
                dotw_hotel_info.setBrandName(cell.getStringCellValue());
                cell = row.getCell(15);
                if (cell == null)
                    continue;
                dotw_hotel_info.setNew_Property(cell.getStringCellValue());
                list.add(dotw_hotel_info);
            }
        }

        return list;
    }

    public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //得到要下载的文件名
        String fileName = URLDecoder.decode(request.getParameter("diarycontent"),"utf-8");
        //fileName = new String(fileName.getBytes("iso8859-1"),"UTF-8");
        //上传的文件都是保存在D:\fileupload\目录下的子目录当中
        String fileSaveRootPath="C:\\";
//        System.out.println(URLDecoder.decode(request.getParameter("diarycontent"),"utf-8"));
        //通过文件名找出文件的所在目录
        //String path = findFileSavePathByFileName(fileName,fileSaveRootPath);
        //String path = fileSaveRootPath;
        //得到要下载的文件
        File file = new File(fileSaveRootPath + "\\" + fileName);
        //如果文件不存在
        if(!file.exists()){
            request.setAttribute("message", "您要下载的资源已被删除！！");
        }
        //处理文件名
        String realname = fileName.substring(fileName.indexOf("_")+1);
        //设置响应头，控制浏览器下载该文件
        response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(realname, "UTF-8"));
        //读取要下载的文件，保存到文件输入流
        FileInputStream in = new FileInputStream(fileSaveRootPath + "\\" + fileName);
        //创建输出流
        OutputStream out = response.getOutputStream();
        //创建缓冲区
        byte buffer[] = new byte[1024];
        int len = 0;
        //循环将输入流中的内容读取到缓冲区当中
        while((len=in.read(buffer))>0){
            //输出缓冲区的内容到浏览器，实现文件下载
            out.write(buffer, 0, len);
        }
        //关闭文件输入流
        in.close();
        //关闭输出流
        out.close();
    }

}
