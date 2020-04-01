package com.webbeds.fliggy.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.webbeds.fliggy.entity.DOTW_hotel_info;
import com.webbeds.fliggy.entity.Fliggy_hotel_info;
import com.webbeds.fliggy.entity.Fliggy_interface.City_coordinates;
import com.webbeds.fliggy.entity.Fliggy_roomType_info;
import com.webbeds.fliggy.service.DOTW.Fliggy_hotel_infoService;
import com.webbeds.fliggy.service.DOTW.Fliggy_oversea_cityService;
import com.webbeds.fliggy.service.DOTW.Fliggy_roomTpye_infoService;
import com.webbeds.fliggy.thread.SearchPriceByHidThread;
import com.webbeds.fliggy.thread.SearchPriceThread;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONString;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * 通用工具类
 */
@Slf4j
public class Common {

    @Autowired
    DOTW_interface_util dotw_interface_util;

    @Autowired
    Fliggy_hotel_infoService fliggy_hotel_infoService;

    @Autowired
    Fliggy_interface_util fliggy_interface_util;

    @Autowired
    Fliggy_roomTpye_infoService fliggy_roomTpye_infoService;

    @Autowired
    Fliggy_oversea_cityService fliggy_oversea_cityService;

    //全局变量
    List<JSONObject> havePriceHotel = new ArrayList<>();
    List<JSONObject> noPriceHotel = new ArrayList<>();
    List<String> noPriceHid = new ArrayList<>();
    String account = "";
    String password = "";
    String accountId = "";
    String path = "";
    String version = "";

    //返回房型关键字数组
    public String[] getRoomType() {
        return new String[]{"Suite", "Palace", "CAPUSLE", "Maison", "Exclusive", "City", "Mit", "Quarduple", "Varanda", "Privilege", "Cottage", "Ridge", "Prestige", "Yurt", "Japanese", "Floor", "Oasis", "Apartamento", "Poolside", "Hillside", "Bird", "Vintage", "Sis", "Cottage", "Familiar", "Grand", "Riverside", "Tower", "Grande", "Loft", "View", "Pavilion", "Premier", "Home", "Minimum", "Business", "Room", "Gsl", "Private", "Family", "Renovated", "Doble", "Comfort", "Quad", "Maisonette", "Pavillion", "Residence", "Balcony", "Studio", "Beachfront", "Classic", "Oceanfront", "Front", "Ocean", "Garden", "Advantage", "Chateau", "King", "Dorm", "Bungalow", "Dormitory", "Duplex", "Semi", "SemiDBL ", "DBL", "Villa", "Twin", "Apartment", "Queen", "Double", "Bed", "House", "Single", "Standard", "Deluxe", "Executive", "Triple", "Premium", "Superior", "Club"};
    }

    //首字母转大写
    public String firstCharacterUpper(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    //根据长度分割list Bean
    public List<List<Fliggy_hotel_info>> splitList(List<Fliggy_hotel_info> list, int groupSize) {
        int length = list.size();
        // 计算可以分成多少组
        int num = (length + groupSize - 1) / groupSize; // TODO
        List<List<Fliggy_hotel_info>> newList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = (i + 1) * groupSize < length ? (i + 1) * groupSize : length;
            newList.add(list.subList(fromIndex, toIndex));
        }
        return newList;
    }

    //根据长度分割list String
    public List<List<String>> splitListString(List<String> list, int groupSize) {
        int length = list.size();
        // 计算可以分成多少组
        int num = (length + groupSize - 1) / groupSize; // TODO
        List<List<String>> newList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = (i + 1) * groupSize < length ? (i + 1) * groupSize : length;
            newList.add(list.subList(fromIndex, toIndex));
        }
        return newList;
    }

    //特殊，根据特殊城市名获取城市对应的id
    public Integer getCityIdByCityName(String cityName) {
        Integer cityId = 0;
        switch (cityName) {
            case "NEW TERRITORIES":
                cityId = 810100;
                break;
            case "KOWLOON":
                cityId = 810100;
                break;
            case "HUALIEN":
                cityId = 712600;
                break;
            case "KAOHSIUNG":
                cityId = 710200;
                break;
            case "NEW TAIPEI CITY":
                cityId = 711100;
                break;
            case "TAICHUNG":
                cityId = 710400;
                break;
            case "TAIPEI":
                cityId = 710100;
                break;
            case "HONG KONG":
                cityId = 810100;
                break;
            case "MACAU - MO":
                cityId = 820100;
                break;
            case "SHANGHAI":
                cityId = 310100;
                break;
            case "CHIAYI":
                cityId = 710900;
                break;
            case "HENGCHUN":
                cityId = 712400;
                break;
            case "NANTOU CITY":
                cityId = 710600;
                break;
            case "YILAN - TAIWAN":
                cityId = 711200;
                break;
            case "YILAN":
                cityId = 711200;
                break;
            case "JIAOXI - YILAN":
                cityId = 711200;
                break;
            case "SUAO":
                cityId = 711200;
                break;
            case "TAOYUAN":
                cityId = 711400;
                break;
            case "TAITUNG":
                cityId = 711400;
                break;
            case "TAINAN":
                cityId = 710300;
            case "Singapore":
                cityId = 1000148939;
            default:
                cityId = 0;
                break;
        }
        return cityId;
    }

    /**
     * 将JSON对象转换成excel并输出,2003版本，最多支持65535条单表数据
     */
    public void JSONToExcel(List<JSONObject> list, String fileName) {
        Set<String> keys = null;
        // 创建HSSFWorkbook对象
        HSSFWorkbook wb = new HSSFWorkbook();
        int size = list.size();

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
                if (s != null && jsonObject != null) {
                    if (jsonObject.getString(s) != null) {
                        if (jsonObject.getString(s).length() > 32000) {
                            cell.setCellValue(jsonObject.getString(s).substring(0, 32000));
                        } else {
                            cell.setCellValue(jsonObject.getString(s));
                        }
                    }
                }
            }
            rowNo = 0;

        }

        // 输出Excel文件
        FileOutputStream output = null;
        try {
            output = new FileOutputStream("d://search-report/" + fileName + ".xls");
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
     * 将JSON对象转换成excel并输出,2007版本，最多支持100W条单表数据
     */
    public void JSONToExcel2007(List<JSONObject> list, String fileName) {
        Set<String> keys = null;
        // 创建HSSFWorkbook对象
        XSSFWorkbook wb = new XSSFWorkbook();
        int size = list.size();

        // 创建HSSFSheet对象
        XSSFSheet sheet = wb.createSheet("sheet0");

        String str = null;
        int roleNo = 0;
        int rowNo = 0;
        for (JSONObject jsonObject : list) {
            // 创建HSSFRow对象
            XSSFRow row = sheet.createRow(roleNo++);
            // 创建HSSFCell对象
            if (keys == null) {
                //标题
                keys = jsonObject.keySet();
                for (String s : keys) {
                    XSSFCell cell = row.createCell(rowNo++);
                    cell.setCellValue(s);
                }
                rowNo = 0;
                row = sheet.createRow(roleNo++);
            }

            for (String s : keys) {
                XSSFCell cell = row.createCell(rowNo++);
                if (s != null && jsonObject != null) {
                    if (jsonObject.getString(s) != null) {
                        if (jsonObject.getString(s).length() > 32000) {
                            cell.setCellValue(jsonObject.getString(s).substring(0, 32000));
                        } else {
                            cell.setCellValue(jsonObject.getString(s));
                        }
                    }
                }
            }
            rowNo = 0;

        }

        // 输出Excel文件
        FileOutputStream output = null;
        try {
            output = new FileOutputStream("d://search-report/" + fileName + ".xlsx");
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
     *
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
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
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

    /**
     * excel转list<String>
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<String> excel2String(String path) throws Exception {
        InputStream is = new FileInputStream(path);
        XSSFWorkbook excel = new XSSFWorkbook(is);
//        HSSFWorkbook excel = new HSSFWorkbook(is);
        List<String> list = new ArrayList<>();

        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < excel.getNumberOfSheets(); numSheet++) {
//            HSSFSheet sheet = excel.getSheetAt(numSheet);
            XSSFSheet sheet = excel.getSheetAt(numSheet);
            if (sheet == null)
                continue;
            // 循环行Row
            for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
//                HSSFRow row = sheet.getRow(rowNum);
                XSSFRow row = sheet.getRow(rowNum);
                if (row == null)
                    continue;
                XSSFCell cell = row.getCell(0);
                if (cell == null)
                    continue;
//                String temp = String.valueOf(cell.getNumericCellValue());
                list.add(cell.getCellType() == 0 ? cell.getRawValue() : cell.getStringCellValue());
            }
        }

        return list;
    }

    public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //得到要下载的文件名
        String fileName = URLDecoder.decode(request.getParameter("diarycontent"), "utf-8");
        //fileName = new String(fileName.getBytes("iso8859-1"),"UTF-8");
        //上传的文件都是保存在D:\fileupload\目录下的子目录当中
        String fileSaveRootPath = "D:\\search-report\\";
//        System.out.println(URLDecoder.decode(request.getParameter("diarycontent"),"utf-8"));
        //通过文件名找出文件的所在目录
        //String path = findFileSavePathByFileName(fileName,fileSaveRootPath);
        //String path = fileSaveRootPath;
        //得到要下载的文件
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        File file = new File(fileSaveRootPath + "\\" + fileName + ".xls");
        //如果文件不存在
        if (!file.exists()) {
            request.setAttribute("message", "您要下载的资源已被删除！！");
        }
        //处理文件名
        String realname = fileName.substring(fileName.indexOf("_") + 1) + date + ".xls";
        //设置响应头，控制浏览器下载该文件
        response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(realname, "UTF-8"));
        //读取要下载的文件，保存到文件输入流
        FileInputStream in = new FileInputStream(fileSaveRootPath + "\\" + fileName + ".xls");
        //创建输出流
        OutputStream out = response.getOutputStream();
        //创建缓冲区
        byte buffer[] = new byte[1024];
        int len = 0;
        //循环将输入流中的内容读取到缓冲区当中
        while ((len = in.read(buffer)) > 0) {
            //输出缓冲区的内容到浏览器，实现文件下载
            out.write(buffer, 0, len);
        }
        //关闭文件输入流
        in.close();
        //关闭输出流
        out.close();
    }

    //根据字符串截取合适的长度
    public String subStringSpecial(String totalName) {
        String[] totalNameArr = totalName.split(" ");
        String beforeName = "";
        String[] rootRoomType = getRoomType();
        for (int i = 0; i < rootRoomType.length; i++) {
            int subIndex = totalName.indexOf(rootRoomType[i]) > -1 ? totalName.indexOf(rootRoomType[i]) : totalName.indexOf(rootRoomType[i].toUpperCase());
            if (subIndex == -1) {
                subIndex = totalName.indexOf(rootRoomType[i].toLowerCase());
                if (subIndex != -1) {
                    beforeName = rootRoomType[i];
                    return beforeName;
                }
            } else {
                beforeName = rootRoomType[i];
                return beforeName;
            }
        }
        for (int i = totalNameArr.length - 1; i >= 0; i--) {
            beforeName = "";
            for (int j = 0; j < i + 1; j++) {
                beforeName += totalNameArr[j] + " ";
            }
            beforeName = beforeName.trim();
            if (beforeName.length() <= 25) {
                break;
            } else {
                beforeName = "";
            }
        }

        return beforeName;
    }

    //根据当前时间获取若干天后是几号
    public String dateFormat(int day) {
        //获取当前日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        String endDate = sdf.format(today);//当前日期
        //获取三十天前日期
        Calendar theCa = Calendar.getInstance();
        theCa.setTime(today);
        theCa.add(theCa.DATE, day);//最后一个数字30可改，30天的意思
        Date start = theCa.getTime();
        String startDate = sdf.format(start);//三十天之前日期
        return startDate;
    }

    //更新飞猪城市id方法
    public void updateCityId(List<Fliggy_hotel_info> list) {
        for (Fliggy_hotel_info fliggy_hotel_info : list) {
            if (fliggy_interface_util.coordinates_batch_download(fliggy_hotel_info.getBatch_id()) != null) {
                Integer cityId = Integer.valueOf(fliggy_interface_util.coordinates_batch_download(fliggy_hotel_info.getBatch_id()));
                fliggy_hotel_info.setCity(cityId);
                fliggy_hotel_infoService.updateCity(fliggy_hotel_info);
            } else {
                log.warn("没有找到cityid的国家名：" + fliggy_hotel_info.getCountry() + "酒店名：" + fliggy_hotel_info.getHotel_name());
            }
        }
    }

    //添加酒店入飞猪库方法
    public void add2Fliggy(List<Fliggy_hotel_info> list) {
        for (Fliggy_hotel_info fliggy_hotel_info : list) {
            /**
             * step 1：添加酒店信息（添加成功后，更新对应本地数据库的state信息为1，insertDate为入库时间
             * step 2：酒店添加成功后添加对应酒店的房型信息
             */
            //酒店状态为0才添加酒店。
            String res = null;
            if(fliggy_hotel_info.getState().equals("0")){
                res = fliggy_interface_util.xhotel_add(fliggy_hotel_info);
                if (res != null && res.indexOf("xhotel_add_response") != -1) {
//                    System.out.println("酒店添加成功");
                    fliggy_hotel_info.setInsertDate(new Date());
                    fliggy_hotel_info.setState("1");
                    fliggy_hotel_info.setError_msg("");
                    fliggy_hotel_infoService.updateStateAndDate(fliggy_hotel_info);
                } else {
                    System.out.println("酒店添加失败");
                    String error_msg = res;
                    fliggy_hotel_info.setError_msg(error_msg);
                    fliggy_hotel_info.setState("2");
                    fliggy_hotel_infoService.updateStateAndDate(fliggy_hotel_info);
                }
            }

            if (fliggy_hotel_info.getCity() != 0) {
                //添加酒店对应房型信息入库
                List<Fliggy_roomType_info> roomList = fliggy_roomTpye_infoService.searchRoomByHid(fliggy_hotel_info.getOuter_id());
                if(roomList.size() > 0){
                    System.out.println("酒店_" + fliggy_hotel_info.getHotel_name() + "共有" + roomList.size() + "个房型需要添加");
                }
                for (Fliggy_roomType_info fliggy_roomType_info : roomList) {
                    String resRoom = fliggy_interface_util.xRoomType_add(fliggy_roomType_info);
                    if (resRoom != null && resRoom.indexOf("xhotel_roomtype_add_response") != -1) {
//                            System.out.println("房型添加成功");
                        fliggy_roomType_info.setInsertDate(new Date());
                        fliggy_roomType_info.setState("1");
                        fliggy_roomType_info.setError_msg("");
                        fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
                    } else {
//                            System.out.println("房型添加失败");
                        String error_msg = resRoom;
                        fliggy_roomType_info.setError_msg(error_msg);
                        fliggy_roomType_info.setState("2");
                        fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
                    }
                }
            }
        }
    }

    //有价查询方法(初步查询，当前日期后30，60,90天是否有价)
    public void searchHotelPrice(List<Fliggy_hotel_info> list) {
        List<Fliggy_hotel_info> listNoPrice = new ArrayList<>();
        String fromDate = "";
        String toDate = "";
        JSONObject jsonObject = null;
        int count = 0;
        //第一轮随机搜索当前日期30,60,90天后是否有价
        for (Fliggy_hotel_info fliggy_hotel_info : list) {
            //查询30天是否有价
            fromDate = dateFormat(30);
            toDate = dateFormat(31);
            jsonObject = dotw_interface_util.getPriceInDotwByHotelId(fliggy_hotel_info.getOuter_id(), fromDate, toDate);
            count = Integer.valueOf(jsonObject.getJSONObject("hotels").getString("@count"));
            /**
             * count=0未找到有价信息，>0找到有价信息
             */
            if (count > 0) {
                fliggy_hotel_info.setHave_price("1");
                fliggy_hotel_info.setHave_price_date(new Date());
                fliggy_hotel_infoService.updateHavePrice(fliggy_hotel_info);
                continue;
            } else {
                fromDate = dateFormat(60);
                toDate = dateFormat(61);
                jsonObject = dotw_interface_util.getPriceInDotwByHotelId(fliggy_hotel_info.getOuter_id(), fromDate, toDate);
                count = Integer.valueOf(jsonObject.getJSONObject("hotels").getString("@count"));
                if (count > 0) {
                    fliggy_hotel_info.setHave_price("2");
                    fliggy_hotel_info.setHave_price_date(new Date());
                    fliggy_hotel_infoService.updateHavePrice(fliggy_hotel_info);
                    continue;
                } else {
                    fromDate = dateFormat(90);
                    toDate = dateFormat(91);
                    jsonObject = dotw_interface_util.getPriceInDotwByHotelId(fliggy_hotel_info.getOuter_id(), fromDate, toDate);
                    count = Integer.valueOf(jsonObject.getJSONObject("hotels").getString("@count"));
                    if (count > 0) {
                        fliggy_hotel_info.setHave_price("3");
                        fliggy_hotel_info.setHave_price_date(new Date());
                        fliggy_hotel_infoService.updateHavePrice(fliggy_hotel_info);
                        continue;
                    } else {
                        fliggy_hotel_info.setHave_price("-1");
                        fliggy_hotel_info.setHave_price_date(new Date());
                        fliggy_hotel_infoService.updateHavePrice(fliggy_hotel_info);
                        listNoPrice.add(fliggy_hotel_info);
                    }
                }
            }

        }
//        return listNoPrice;
    }

    //有价查询方法(初步查询，当前日期后30，60,90天是否有价)
    public void searchHotelPriceTemp(List<Fliggy_hotel_info> list, Integer day) {
        List<List<Fliggy_hotel_info>> listThread = splitList(list, 30);
        int count = 0;
        for (int i = 0; i < listThread.size(); i++) {
            searchPriceMethod(listThread.get(i), new ArrayList<>(), day);
        }

    }

    //有价查询方法-second(当前日期后的30天进行每日询价)
    public void searchHotelPriceAgainTemp(List<Fliggy_hotel_info> list) {

    }

    //询价方法，可递归
    public void searchPriceMethod(List<Fliggy_hotel_info> list, List<Fliggy_hotel_info> listHavePrice, Integer day) {
        int count = 0;
        String fromDate = "";
        String toDate = "";
        JSONObject jsonObject = null;
        //查询30天是否有价
        fromDate = dateFormat(day);
        toDate = dateFormat(day + 1);
        jsonObject = dotw_interface_util.getPriceInDotwByHotelIdTemp(list, fromDate, toDate);
        count = Integer.valueOf(jsonObject.getJSONObject("hotels").getString("@count"));
        String state = "-1";
        if (day == 30) {
            state = "1";
        } else if (day == 60) {
            state = "2";
        } else if (day == 90) {
            state = "3";
        } else {
            state = "-1";
        }
        if (count > 0) {
            //如果查询条目=1只有一条，>1则有多条，一条用object多条用array
            if (count == 1) {
                //找到有价酒店并添加入库
                JSONObject jsonObjectTemp = jsonObject.getJSONObject("hotels").getJSONObject("hotel");
                Fliggy_hotel_info fliggy_hotel_info = new Fliggy_hotel_info();
                fliggy_hotel_info.setOuter_id(jsonObjectTemp.getString("@hotelid"));
                fliggy_hotel_info.setHave_price(state);
                fliggy_hotel_info.setHave_price_date(new Date());
                //在集合中删除有价酒店并继续后续询价
                for (int i = 0; i < list.size(); i++) {
                    boolean flag = true;
                    for (int j = 0; j < listHavePrice.size(); j++) {
                        if (list.get(i).getOuter_id().equals(listHavePrice.get(j).getOuter_id())) {
                            flag = false;
                            break;
                        }
                    }
                    if (list.get(i).getOuter_id().equals(jsonObjectTemp.getString("@hotelid")) && flag) {
//                        log.warn("酒店：" + list.get(i).getHotel_name() + "在第" + day + "天有价格");
                        listHavePrice.add(list.get(i));
//                        fliggy_hotel_infoService.updateHavePrice(fliggy_hotel_info);
                    }
                }
            } else {
                JSONArray jsonArray = jsonObject.getJSONObject("hotels").getJSONArray("hotel");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObjectTemp = jsonArray.getJSONObject(i);
                    Fliggy_hotel_info fliggy_hotel_info = new Fliggy_hotel_info();
                    fliggy_hotel_info.setOuter_id(jsonObjectTemp.getString("@hotelid"));
                    fliggy_hotel_info.setHave_price(state);
                    fliggy_hotel_info.setHave_price_date(new Date());
                    //在集合中删除有价酒店并继续后续询价
                    for (int j = 0; j < list.size(); j++) {
                        boolean flag = true;
                        for (int k = 0; k < listHavePrice.size(); k++) {
                            if (list.get(j).getOuter_id().equals(listHavePrice.get(k).getOuter_id())) {
                                flag = false;
                                break;
                            }
                        }
                        if (list.get(j).getOuter_id().equals(jsonObjectTemp.getString("@hotelid")) && flag) {
//                            log.warn("酒店：" + list.get(j).getHotel_name() + "在第" + day + "天有价格");
                            listHavePrice.add(list.get(j));
//                            fliggy_hotel_infoService.updateHavePrice(fliggy_hotel_info);
                        }
                    }
                }
            }
        }

        Integer dayTemp = day + 30;
        //删选出无价酒店列表继续查询60天是否有价,递归查询
        if (dayTemp <= 90) {
            searchPriceMethod(list, listHavePrice, dayTemp);
        } else {
            for (Fliggy_hotel_info fliggy_hotel_infoTemp : list) {
                boolean flag = true;
                for (Fliggy_hotel_info fliggy_hotel_infoTempHavePrice : listHavePrice) {
                    if (fliggy_hotel_infoTempHavePrice.getOuter_id().equals(fliggy_hotel_infoTemp.getOuter_id())) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    fliggy_hotel_infoTemp.setHave_price("-1");
                    fliggy_hotel_infoTemp.setHave_price_date(new Date());
//                   log.warn("酒店：" + fliggy_hotel_infoTemp.getHotel_name() + "在90天内没价格");
//                   fliggy_hotel_infoService.updateHavePrice(fliggy_hotel_infoTemp);
                }
            }
        }
    }


    //有价查询方法-second(当前日期后的30天进行每日询价)
    public void searchHotelPriceAgain(List<Fliggy_hotel_info> list) {
        String fromDate = "";
        String toDate = "";
        JSONObject jsonObject = null;
        int count = 0;
        //第二轮询价进行每天的搜索
        for (Fliggy_hotel_info fliggy_hotel_info : list) {
            for (int i = 1; i <= 30; i++) {
                //查询30天是否有价
                fromDate = dateFormat(i + 10);
                toDate = dateFormat(i + 10 + 1);
                jsonObject = dotw_interface_util.getPriceInDotwByHotelId(fliggy_hotel_info.getOuter_id(), fromDate, toDate);
                count = Integer.valueOf(jsonObject.getJSONObject("hotels").getString("@count"));
                if (count > 0) {
                    fliggy_hotel_info.setHave_price("1");
                    fliggy_hotel_info.setHave_price_date(new Date());
                    fliggy_hotel_infoService.updateHavePrice(fliggy_hotel_info);
                    log.info("找到价格咯,酒店名：" + fliggy_hotel_info.getHotel_name() + "有价日期为：" + fromDate);
                    break;
                }
            }
        }
    }


    //查询插入结果并生成结果集excel
    public void searchAndReport(List<Fliggy_hotel_info> list) {
        List<JSONObject> listHotelInfo = new ArrayList<>();
        List<JSONObject> listRoomInfo = new ArrayList<>();
        for (Fliggy_hotel_info fliggy_hotel_info : list) {
            JSONObject temp = fliggy_interface_util.xHotelSearch(fliggy_hotel_info.getOuter_id());
            if (temp != null && temp.getJSONObject("xhotel_get_response") != null) {
                listHotelInfo.add(temp.getJSONObject("xhotel_get_response").getJSONObject("xhotel"));
                fliggy_hotel_info.setMapping(temp.getJSONObject("xhotel_get_response").getJSONObject("xhotel").getString("data_confirm_str"));
                fliggy_hotel_infoService.updateStateAndDate(fliggy_hotel_info);
            }
            //查询房型
            List<Fliggy_roomType_info> roomList = fliggy_roomTpye_infoService.searchRoomByHid(fliggy_hotel_info.getOuter_id());
            for (Fliggy_roomType_info fliggy_roomType_info : roomList) {
                JSONObject tempRoom = fliggy_interface_util.xRoomSearch(fliggy_roomType_info.getOuter_id());
                if (tempRoom != null && tempRoom.getJSONObject("xhotel_roomtype_get_response") != null) {
                    listRoomInfo.add(tempRoom.getJSONObject("xhotel_roomtype_get_response").getJSONObject("xroomtype"));
                    fliggy_roomType_info.setMapping(tempRoom.getJSONObject("xhotel_roomtype_get_response").getJSONObject("xroomtype").getString("data_confirm_str"));
                    fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
                }
            }
        }
        //最后输出
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        JSONToExcel(listHotelInfo, "hotel" + date);
        JSONToExcel(listRoomInfo, "roomType" + date);
    }

//    //删除飞猪库的房型和酒店（慎用）
//    public void delHotelAndRoom(List<Fliggy_hotel_info> list){
//        /**
//         * step 1：删除房型信息
//         * step 2：删除酒店信息
//         */
//        for (Fliggy_hotel_info fliggy_hotel_info : list){
//            List<Fliggy_roomType_info> roomList = fliggy_roomTpye_infoService.searchRoomByHid(fliggy_hotel_info.getOuter_id());
//            for(Fliggy_roomType_info fliggy_roomType_info : roomList){
//                fliggy_interface_util.xRoomDel(fliggy_roomType_info.getOuter_id());
//                fliggy_roomType_info.setState("0");
//                fliggy_roomType_info.setInsertDate(null);
//                fliggy_roomType_info.setError_msg(null);
//                fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
//            }
////            fliggy_interface_util.xHotelDel(fliggy_hotel_info.getOuter_id());
//            fliggy_hotel_info.setState("0");
//            fliggy_hotel_info.setInsertDate(null);
//            fliggy_hotel_info.setError_msg(null);
//            fliggy_hotel_infoService.updateStateAndDate(fliggy_hotel_info);
//        }
//    }

    /**
     * 更新阿里酒店所在城市的批次号（有些在添加酒店时没加上的给加上批次号）
     */
    public void updateCityBatch(List<Fliggy_hotel_info> list) {
        for (Fliggy_hotel_info fliggy_hotel_info : list) {
            //根据飞猪城市协议接口先上传酒店经纬度信息再下载对应的酒店id;
            City_coordinates city_coordinates = new City_coordinates();
            String countryId = fliggy_oversea_cityService.findCountryIdByName(fliggy_hotel_info.getCountry());
            if (countryId != null) {
                city_coordinates.setCountryId(Long.valueOf(countryId));
            } else {
                if (fliggy_hotel_info.getCountry().equals("TAIWAN") || fliggy_hotel_info.getCountry().equals("MACAU") || fliggy_hotel_info.getCountry().equals("HONG KONG") || fliggy_hotel_info.getCountry().equals("CHINA")) {

                } else {
                    System.out.println(fliggy_hotel_info.getCountry());
                }
                city_coordinates.setCountryId(0L);
            }
            city_coordinates.setLatitude(fliggy_hotel_info.getLatitude());
            city_coordinates.setLongitude(fliggy_hotel_info.getLongitude());
            city_coordinates.setOuterId(fliggy_hotel_info.getOuter_id());
            String batch_id = fliggy_interface_util.city_coordinates_batch_upload(city_coordinates);
            fliggy_hotel_info.setBatch_id(batch_id);
            fliggy_hotel_infoService.updateBatchId(fliggy_hotel_info);
        }
    }

    //删除飞猪库的房型（慎用）
    public void delRoom(List<String> list) {
        /**
         * step 1：删除房型信息
         *
         */
        for (String roomid : list) {
            String msg = fliggy_interface_util.xRoomDel(roomid);
            Fliggy_roomType_info fliggy_roomType_info = new Fliggy_roomType_info();
            fliggy_roomType_info.setState("0");
            fliggy_roomType_info.setInsertDate(null);
            fliggy_roomType_info.setError_msg(null);
            fliggy_roomType_info.setError_msg(msg);
            fliggy_roomType_info.setOuter_id(roomid);
            fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
        }
    }

//    /**
//     * 查询价格处理函数
//     * @param file
//     * @return
//     */
//    public boolean searchPriceByHidOprate(MultipartFile file,String account,String password,String accountId,String version,String fromDate,String toDate){
//        //获得有价无价酒店集合，用于输出
//        havePriceHotel = new ArrayList<>();
//        noPriceHotel = new ArrayList<>();
//        noPriceHid = new ArrayList<>();
//        this.account = account;
//        this.password = password;
//        this.accountId = accountId;
//        this.path = "http://us.DOTWconnect.com/gateway" + version + ".dotw";
//        this.version = version;
//        boolean flag = false;
//        //读取客户端上传的文件
//        long  startTime=System.currentTimeMillis();
//        System.out.println("fileName："+file.getOriginalFilename() );
//        String path="D:/webbeds/temp/"+file.getOriginalFilename();
//
//        File newFile=new File(path);
//        //通过CommonsMultipartFile的方法直接写文件（注意这个时候）
//        try {
//            file.transferTo(newFile);
//            flag = true;
//        } catch (IOException e) {
//            flag = false;
//            e.printStackTrace();
//        }
//        long  endTime=System.currentTimeMillis();
//        System.out.println("采用file.Transto的运行时间："+String.valueOf(endTime-startTime)+"ms");
//
//        try {
//            //根据上传的excel文件获取酒店ID的list
//            List<String> list = excel2String(path);
//            System.out.println("共计" + list.size() + "条数据");
//            //多线程初次询价
//            List<List<String>> listThread = splitListString(list,1000);
//            CountDownLatch latch = new CountDownLatch(listThread.size());
//            //多线程询价，第一次询价
//            for(List<String> listTemp : listThread){
//                SearchPriceByHidThread searchPriceByHidThread = new SearchPriceByHidThread(listTemp,this,"first",latch);
//                Thread t = new Thread(searchPriceByHidThread);
//                t.start();
//            }
//
//            try {
//                latch.await();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            log.info("第一次询价执行询价结束");
//            //第二次询价，逻辑：从当前日期向后询30天
//            List<List<String>> listThreadSecond = splitListString(noPriceHid,1000);
//            CountDownLatch latchSecond = new CountDownLatch(listThreadSecond.size());
//            for(List<String> listTemp : listThreadSecond){
//                SearchPriceByHidThread searchPriceByHidThread = new SearchPriceByHidThread(listTemp,this,"second",latchSecond);
//                Thread t = new Thread(searchPriceByHidThread);
//                t.start();
//            }
//
//            try {
//                latchSecond.await();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            log.info("第二次询价执行询价结束");
//            flag = true;
//        } catch (Exception e) {
//            flag = false;
//            e.printStackTrace();
//        }
//
//        //根据list生成report
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        String date = sdf.format(new Date());
//        JSONToExcel(havePriceHotel,"havePriceHotelAccount--" + account + date);
//        JSONToExcel(noPriceHotel,"noPriceHotelAccount--" + account + date);
//        return flag;
//    }
//
//    //根据Hid有价查询方法(初步查询，当前日期后30，60,90天是否有价)
//    public void searchHotelPriceByHid(List<String> list,Integer day){
//        List<List<String>> listThread = splitListString(list,30);
//        int count = 0;
//        for(int i = 0; i < listThread.size(); i++){
//            firstSearchPriceByHidMethod(listThread.get(i),new ArrayList<>(),day);
//        }
//
//    }
//
//    //根据Hid有价查询方法(第二次查询，当前日期10天后的30个自然日里是否有价)
//    public void searchHotelPriceByHidSecond(List<String> list){
//        List<List<String>> listThread = splitListString(list,30);
//        int count = 0;
//        for(int i = 0; i < listThread.size(); i++){
//            secondSearchPriceByHidMethod(listThread.get(i),new ArrayList<>(),10);
//        }
//
//    }

    //初次询价方法，可递归(根据Hid)
    public void firstSearchPriceByHidMethod(List<String> list, List<String> listHavePrice, Integer day) {
        int count = 0;
        String fromDate = "";
        String toDate = "";
        JSONObject jsonObject = null;
        //查询30天是否有价
        fromDate = dateFormat(day);
        toDate = dateFormat(day + 1);
        jsonObject = dotw_interface_util.getPriceInDotwByHotelIdAndAccount(list, fromDate, toDate, account, password, accountId, path);

        if (version.equals("V3")) {
            count = Integer.valueOf(jsonObject.getJSONObject("hotels").getString("@count"));
            if (count > 0) {
                //如果查询条目=1只有一条，>1则有多条，一条用object多条用array
                if (count == 1) {
                    //找到有价酒店并添加入库
                    JSONObject jsonObjectTemp = jsonObject.getJSONObject("hotels").getJSONObject("hotel");
                    //在集合中删除有价酒店并继续后续询价
                    for (int i = 0; i < list.size(); i++) {
                        boolean flag = true;
                        for (int j = 0; j < listHavePrice.size(); j++) {
                            if (list.get(i).equals(listHavePrice.get(j))) {
                                flag = false;
                                break;
                            }
                        }
                        if (list.get(i).equals(jsonObjectTemp.getString("@hotelid")) && flag) {
                            log.warn("酒店编号：" + list.get(i) + "在第" + day + "天有价格");
                            listHavePrice.add(list.get(i));
                            havePriceHotel.add(jsonObjectTemp);
//                        fliggy_hotel_infoService.updateHavePrice(fliggy_hotel_info);
                        }
                    }
                } else {
                    JSONArray jsonArray = jsonObject.getJSONObject("hotels").getJSONArray("hotel");
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObjectTemp = jsonArray.getJSONObject(i);
                        //在集合中删除有价酒店并继续后续询价
                        for (int j = 0; j < list.size(); j++) {
                            boolean flag = true;
                            for (int k = 0; k < listHavePrice.size(); k++) {
                                if (list.get(j).equals(listHavePrice.get(k))) {
                                    flag = false;
                                    break;
                                }
                            }
                            if (list.get(j).equals(jsonObjectTemp.getString("@hotelid")) && flag) {
                                log.warn("酒店编号：" + list.get(j) + "在第" + day + "天有价格");
                                listHavePrice.add(list.get(j));
                                havePriceHotel.add(jsonObjectTemp);
//                            fliggy_hotel_infoService.updateHavePrice(fliggy_hotel_info);
                            }
                        }
                    }
                }
            }

            Integer dayTemp = day + 30;
            //删选出无价酒店列表继续查询60天是否有价,递归查询
            if (dayTemp <= 90) {
                firstSearchPriceByHidMethod(list, listHavePrice, dayTemp);
            } else {
                for (String fliggy_hotel_infoTemp : list) {
                    boolean flag = true;
                    for (String fliggy_hotel_infoTempHavePrice : listHavePrice) {
                        if (fliggy_hotel_infoTempHavePrice.equals(fliggy_hotel_infoTemp)) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        log.warn("酒店编号：" + fliggy_hotel_infoTemp + "第一轮询价完毕未找到有效价格");
//                    JSONObject jsonObject1 = JSON.parseObject("{\"hotelid\":" + fliggy_hotel_infoTemp + "}");
//                    noPriceHotel.add(jsonObject1);
                        if (!fliggy_hotel_infoTemp.equals("")) {
                            noPriceHid.add(fliggy_hotel_infoTemp);
                        }
//                   fliggy_hotel_infoService.updateHavePrice(fliggy_hotel_infoTemp);
                    }
                }
            }
        }
    }

    //第二次询价方法，可递归(根据Hid)
    public void secondSearchPriceByHidMethod(List<String> list, List<String> listHavePrice, Integer day) {
        int count = 0;
        String fromDate = "";
        String toDate = "";
        JSONObject jsonObject = null;
        //查询30天是否有价
        fromDate = dateFormat(day);
        toDate = dateFormat(day + 1);
        jsonObject = dotw_interface_util.getPriceInDotwByHotelIdAndAccount(list, fromDate, toDate, account, password, accountId, path);
        if (version.equals("V3")) {
            count = Integer.valueOf(jsonObject.getJSONObject("hotels").getString("@count"));
            if (count > 0) {
                //如果查询条目=1只有一条，>1则有多条，一条用object多条用array
                if (count == 1) {
                    //找到有价酒店并添加入库
                    JSONObject jsonObjectTemp = jsonObject.getJSONObject("hotels").getJSONObject("hotel");
                    //在集合中删除有价酒店并继续后续询价
                    for (int i = 0; i < list.size(); i++) {
                        boolean flag = true;
                        for (int j = 0; j < listHavePrice.size(); j++) {
                            if (list.get(i).equals(listHavePrice.get(j))) {
                                flag = false;
                                break;
                            }
                        }
                        if (list.get(i).equals(jsonObjectTemp.getString("@hotelid")) && flag) {
                            log.warn("酒店编号：" + list.get(i) + "在第" + day + "天有价格");
                            listHavePrice.add(list.get(i));
                            havePriceHotel.add(jsonObjectTemp);
//                        fliggy_hotel_infoService.updateHavePrice(fliggy_hotel_info);
                        }
                    }
                } else {
                    JSONArray jsonArray = jsonObject.getJSONObject("hotels").getJSONArray("hotel");
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObjectTemp = jsonArray.getJSONObject(i);
                        //在集合中删除有价酒店并继续后续询价
                        for (int j = 0; j < list.size(); j++) {
                            boolean flag = true;
                            for (int k = 0; k < listHavePrice.size(); k++) {
                                if (list.get(j).equals(listHavePrice.get(k))) {
                                    flag = false;
                                    break;
                                }
                            }
                            if (list.get(j).equals(jsonObjectTemp.getString("@hotelid")) && flag) {
                                log.warn("酒店编号：" + list.get(j) + "在第" + day + "天有价格");
                                listHavePrice.add(list.get(j));
                                havePriceHotel.add(jsonObjectTemp);
//                            fliggy_hotel_infoService.updateHavePrice(fliggy_hotel_info);
                            }
                        }
                    }
                }
            }

            Integer dayTemp = day + 1;
            //删选出无价酒店列表继续查询60天是否有价,递归查询
            if (dayTemp <= 40) {
                secondSearchPriceByHidMethod(list, listHavePrice, dayTemp);
            } else {
                for (String fliggy_hotel_infoTemp : list) {
                    boolean flag = true;
                    for (String fliggy_hotel_infoTempHavePrice : listHavePrice) {
                        if (fliggy_hotel_infoTempHavePrice.equals(fliggy_hotel_infoTemp)) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        log.warn("酒店编号：" + fliggy_hotel_infoTemp + "在90天内未找到有效价格");
//                    noPriceHotel.add("酒店编号：" + fliggy_hotel_infoTemp + "在90天内没价格。");
                        JSONObject jsonObject1 = JSON.parseObject("{\"hotelid\":" + fliggy_hotel_infoTemp + "}");
                        noPriceHotel.add(jsonObject1);
//                    noPriceHid.add(fliggy_hotel_infoTemp);
//                   fliggy_hotel_infoService.updateHavePrice(fliggy_hotel_infoTemp);
                    }
                }
            }
        }
    }

    //添加酒店入飞猪库方法
    public void add2FliggyRoom(List<Fliggy_hotel_info> list) {
        for (Fliggy_hotel_info fliggy_hotel_info : list) {
            /**
             * step 1：添加酒店信息（添加成功后，更新对应本地数据库的state信息为1，insertDate为入库时间）
             * step 2：酒店添加成功后添加对应酒店的房型信息
             */
            if (fliggy_hotel_info.getCity() != 0) {
                //添加酒店对应房型信息入库
                List<Fliggy_roomType_info> roomList = fliggy_roomTpye_infoService.searchRoomByHid(fliggy_hotel_info.getOuter_id());
                for (Fliggy_roomType_info fliggy_roomType_info : roomList) {
                    String resRoom = fliggy_interface_util.xRoomType_add(fliggy_roomType_info);
                    if (resRoom != null && resRoom.indexOf("xhotel_roomtype_add_response") != -1) {
//                            System.out.println("房型添加成功");
                        fliggy_roomType_info.setInsertDate(new Date());
                        fliggy_roomType_info.setState("1");
                        fliggy_roomType_info.setError_msg("");
                        fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
                    } else {
//                            System.out.println("房型添加失败");
                        String error_msg = resRoom;
                        fliggy_roomType_info.setError_msg(error_msg);
                        fliggy_roomType_info.setState("2");
                        fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
                    }
                }
            }
        }
    }


}
