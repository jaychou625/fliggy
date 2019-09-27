package com.webbeds.fliggy.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.sf.json.xml.XMLSerializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;

/**
 * dotw接口工具
 */
public class DOTW_interface_util {

    private String path = "http://us.DOTWconnect.com/gatewayV3.dotw";
    private String account = "AlitripXML";
    private String password = "CD84D683CC5612C69EFE115C80D0B7DC";
    private String accountId = "1494305";

    /**
     * 根据hotelid调用dotw接口获取酒店和房型信息
     * @param listTemp
     * @return
     */
    public JSONObject getHotelInfoInDotwByHotelId(List<String> listTemp){
        //API开发接口URL
//        String path = "http://us.DOTWconnect.com/gatewayV3.dotw";
        JSONObject result = null;

        String xml = "";

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='UTF-8'?>");
        sb.append("<customer>\n" +
                "    <username>" + account + "</username>\n" +
                "    <password>" + password + "</password>\n" +
                "    <id>" + accountId + "</id>\n" +
                "    <source>1</source>\n" +
                "    <product>hotel</product>\n" +
                "     <request command=\"searchhotels\">  \n" +
                "        <bookingDetails>  \n" +
                "            <fromDate>2019-05-28</fromDate>  \n" +
                "            <toDate>2019-05-29</toDate>  \n" +
                "            <currency>520</currency>  \n" +
                "            <rooms no=\"1\">  \n" +
                "                <room runno=\"0\">  \n" +
                "                    <adultsCode>1</adultsCode>  \n" +
                "                    <children no=\"0\"></children>  \n" +
                "                    <rateBasis>-1</rateBasis>  \n" +
                "                </room>  \n" +
                "            </rooms>  \n" +
                "        </bookingDetails>  \n" +
                "        <return>  \n" +
                "            <getRooms>true</getRooms>  \n" +
                "            <filters xmlns:a=\"http://us.dotwconnect.com/xsd/atomicCondition\" xmlns:c=\"http://us.dotwconnect.com/xsd/complexCondition\">\n" +
//                "              <city>" + cityId + "</city>\n" +
                "              <noPrice>true</noPrice>\n" +
                "              <c:condition>\n" +
                "                <a:condition>\n" +
                "                <fieldName>hotelId</fieldName>\n" +
                "                  <fieldTest>in</fieldTest>\n" +
                "                  <fieldValues>\n");
        for(String str : listTemp){
            sb.append("<fieldValue>" + str + "</fieldValue>\n" );
        }

        sb.append("                  </fieldValues>\n" +
                "                </a:condition>\n" +
                "              </c:condition>\n" +
                "            </filters>  \n" +
                "            <fields>\n" +
                "            \t<field>hotelName</field>  \n" +
                "                <field>chain</field>  \n" +
                "                <field>cityName</field>  \n" +
                "                <field>cityCode</field>  \n" +
                "                <field>stateName</field>  \n" +
                "                <field>stateCode</field>  \n" +
                "                <field>countryName</field>  \n" +
                "                <field>countryCode</field>  \n" +
                "                <field>regionName</field>  \n" +
                "                <field>regionCode</field> \n" +
                "                <field>preferred</field>  \n" +
                "                <field>builtYear</field>  \n" +
                "                <field>renovationYear</field>  \n" +
                "                <field>floors</field>  \n" +
                "                <field>noOfRooms</field>  \n" +
                "                <field>preferred</field>  \n" +
                "                <field>luxury</field>  \n" +
                "                <field>fullAddress</field>  \n" +
                "                <field>description1</field>  \n" +
                "                <field>description2</field>  \n" +
                "                <field>address</field>  \n" +
                "                <field>zipCode</field>  \n" +
                "                <field>location</field>  \n" +
                "                <field>locationId</field>  \n" +
                "                <field>location1</field>  \n" +
                "                <field>location2</field>  \n" +
                "                <field>location3</field>  \n" +
                "                <field>attraction</field>  \n" +
                "                <field>amenitie</field>  \n" +
                "                <field>leisure</field>  \n" +
                "                <field>business</field>  \n" +
                "                <field>transportation</field>  \n" +
                "                <field>hotelPhone</field>  \n" +
                "                <field>hotelCheckIn</field>  \n" +
                "                <field>hotelCheckOut</field>  \n" +
                "                <field>minAge</field>  \n" +
                "                <field>rating</field>  \n" +
                "                <field>images</field>  \n" +
                "                <field>fireSafety</field>  \n" +
                "                <field>hotelPreference</field>  \n" +
                "                <field>direct</field>  \n" +
                "                <field>geoPoint</field>  \n" +
                "                <field>leftToSell</field>  \n" +
                "                <field>chain</field>  \n" +
                "                <field>lastUpdated</field>  \n" +
                "                <field>priority</field>  \n" +
                "                <roomField>name</roomField>  \n" +
                "                <roomField>roomInfo</roomField>  \n" +
                "                <roomField>roomAmenities</roomField>  \n" +
                "                <roomField>twin</roomField>  \n" +
                "            </fields>  \n" +
                "        </return>  \n" +
                "    </request>\n" +
                "</customer>");
        xml = sb.toString();
        try {
            URL url = new URL(path);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("Pragma", "no-cache");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Content-Type", "application/xml");

            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            String xmlInfo = xml;
            out.write(new String(xmlInfo.getBytes("UTF-8")));
            out.flush();
            out.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(con
                    .getInputStream()));
            String line = "";
            String xmlString = "";
            for (line = br.readLine(); line != null; line = br.readLine()) {
                xmlString += line;
            }
            XMLSerializer xmlSerializer = new XMLSerializer();
            String resutStr = xmlSerializer.read(xmlString).toString();
            result = JSON.parseObject(resutStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

    public JSONObject getPriceInDotwByHotelId(String hid, String fromDate,String toDate){
        //API开发接口URL
//        String path = "http://us.DOTWconnect.com/gatewayV3.dotw";
        JSONObject result = null;

        String xml = "";

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='UTF-8'?>");
        sb.append("<customer>\n" +
                "    <username>" + account + "</username>\n" +
                "    <password>" + password + "</password>\n" +
                "    <id>" + accountId + "</id>\n" +
                "  <source>1</source>\n" +
                "  <product>hotel</product>\n" +
                "  <request defaultnamespace='true' command='searchhotels'>\n" +
                "    <bookingDetails>\n" +
                "      <fromDate>" + fromDate + "</fromDate>\n" +
                "      <toDate>" + toDate + "</toDate>\n" +
                "      <currency>520</currency>\n" +
                "      <rooms no='1'>\n" +
                "        <room runno='0'>\n" +
                "          <adultsCode>2</adultsCode>\n" +
                "          <children no='0'></children>\n" +
                "          <rateBasis>-1</rateBasis>\n" +
                "          <passengerNationality>168</passengerNationality>\n" +
                "          <passengerCountryOfResidence>168</passengerCountryOfResidence>\n" +
                "        </room>\n" +
                "      </rooms>\n" +
                "    </bookingDetails>\n" +
                "    <return>\n" +
                "      <filters xmlns:a='http://us.dotwconnect.com/xsd/atomicCondition' xmlns:c='http://us.dotwconnect.com/xsd/complexCondition'>\n" +
                "        <!--<city>22594</city>-->\n" +
                "        <!--Country>171</Country-->\n" +
                "        <!--rateTypes>  \n" +
                "            <rateType>3</rateType>\n" +
                "        </rateTypes--> \n" +
                "        <c:condition>\n" +
                "          <a:condition>\n" +
                "            <fieldName>onRequest</fieldName>\n" +
                "            <fieldTest>equals</fieldTest>\n" +
                "            <fieldValues>\n" +
                "              <fieldValue>1</fieldValue>\n" +
                "            </fieldValues>\n" +
                "          </a:condition>\n" +
                "          <operator>AND</operator>\n" +
                "          <a:condition>\n" +
                "            <fieldName>hotelId</fieldName>\n" +
                "            <fieldTest>in</fieldTest>\n" +
                "            <fieldValues>\n" +
                "\t\t\t\t<fieldValue>" + hid + "</fieldValue>\n" +
                "            </fieldValues>\n" +
                "          </a:condition>\n" +
                "        </c:condition>\n" +
                "      </filters>\n" +
                "    </return>\n" +
                "  </request>\n" +
                "</customer>");
        xml = sb.toString();
        try {
            URL url = new URL(path);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("Pragma", "no-cache");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Content-Type", "application/xml");

            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            String xmlInfo = xml;
            out.write(new String(xmlInfo.getBytes("UTF-8")));
            out.flush();
            out.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(con
                    .getInputStream()));
            String line = "";
            String xmlString = "";
            for (line = br.readLine(); line != null; line = br.readLine()) {
                xmlString += line;
            }
            XMLSerializer xmlSerializer = new XMLSerializer();
            String resutStr = xmlSerializer.read(xmlString).toString();
            result = JSON.parseObject(resutStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }}


