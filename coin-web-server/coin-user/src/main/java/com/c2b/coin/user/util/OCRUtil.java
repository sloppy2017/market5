package com.c2b.coin.user.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class OCRUtil {
	/**
	 * 转换json字符串
	 *
	 * @param rsContent
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> jsonStringToList(String rsContent)
			throws Exception {// 将json串第一层的key和vale，作为键值对存进map，返回
		JSONObject jsonObject = JSONObject.parseObject(rsContent);// 将String串转为jsonObject
		Map result = new HashMap();
		Iterator iterator = jsonObject.keySet().iterator();// jsonObject.keys()只能获取json的第一层key，然后将key和对应的value封装进map对象
		String key = null;
		String value = null;

		while (iterator.hasNext()) {// 遍历当前json第一层的keys

			key = (String) iterator.next();
			value = jsonObject.getString(key);
			result.put(key, value);// 将key和对应的value存入map

		}
		return result;
	}

	public static List<String> printjson(String jsonString) {
		String message = "";
		String cardsinfo = "";
		String status = "";
		String value = "";
		String type = "";
		String items = "";
		String desc = "";
		String content = "";
		String[] arr;

		Map<String, String> map = new HashMap<String, String>();
		List<String> list = new ArrayList<String>();
		try {
			map = jsonStringToList(jsonString);
			message = map.get("message");// {"status":2,"value":"识别成功"}
			cardsinfo = map.get("cardsinfo");// [{"type":"2","items":[{"desc":"保留","content":""},{"desc":"姓名","content":"王**"},{"desc":"性别","content":"男"},{"desc":"民族","content":"汉"},{"desc":"出生","content":"1981-02-**"},{"desc":"住址","content":"浙江省*************"},{"desc":"公民身份号码","content":"330************"},{"desc":"头像","content":"\/9j\/4AAQS2Q=="}]}]
			cardsinfo = cardsinfo.substring(1, cardsinfo.length() - 1);// 去掉两边的[]，保留引号及里面的值。{"type":"2","items":[{"desc":"保留","content":""},{"desc":"姓名","content":"王**"},{"desc":"性别","content":"男"},{"desc":"民族","content":"汉"},{"desc":"出生","content":"1981-02-21"},{"desc":"住址","content":"浙江省桐庐县凤)镇园林村上喻二组"},{"desc":"公民身份号码","content":"330*************"},{"desc":"头像","content":"\/9j\/4AAQS2Q=="}]}

			map = jsonStringToList(message);
			status = map.get("status");
			value = map.get("value");
			if(!cardsinfo.equals("")){

			map = jsonStringToList(cardsinfo);
			type = map.get("type");
			items = map.get("items");// [{"desc":"保留","content":""},{"desc":"姓名","content":"王**"},{"desc":"性别","content":"男"},{"desc":"民族","content":"汉"},{"desc":"出生","content":"1981-02-21"},{"desc":"住址","content":"浙江省**********"},{"desc":"公民身份号码","content":"330************"},{"desc":"头像","content":"\/9j\/4AAQS2Q=="}]
			items = items.substring(1, items.length() - 1);// 去掉两边的[].{"desc":"保留","content":""},{"desc":"姓名","content":"王元青"},{"desc":"性别","content":"男"},{"desc":"民族","content":"汉"},{"desc":"出生","content":"1981-02-21"},{"desc":"住址","content":"浙江省桐庐县凤)镇园林村上喻二组"},{"desc":"公民身份号码","content":"330**************"},{"desc":"头像","content":"\/9j\/4AAQS2Q=="}
			arr = items.split("},");// 分割为四组字符串，返回一个字符串数组。数组格式:[{"desc":"保留","content":"",
									// {"desc":"姓名","content":"王**",
									// {"desc":"性别","content":"男",
									// {"desc":"民族","content":"汉",
									// {"desc":"出生","content":"1981-02-**",
									// {"desc":"住址","content":"浙江省桐庐县凤)镇园林村上喻二组",
									// {"desc":"公民身份号码","content":"330***********",
									// {"desc":"头像","content":"\/9j\/4AAQS2Q=="}]
			System.out.println("type:" + type);
			for (int i = 0; i < arr.length; i++) {// 遍历这个数组，每个数组都补全，之后每个数组元素都是一个json格式的字符串，调用jsonStringToList方法，将每个key和value存入map
				if (i < arr.length - 1) {
					arr[i] += "}";// 补全每个数组元素
				}
				map = jsonStringToList(arr[i]);
				desc = map.get("desc");
				content = map.get("content");
				list.add(desc + ":" + content);
				// System.out.println(desc + ":" + content);
			}
			 }else if(value.contains("识别失败")){
	            	list.add(value);
	            }
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return list;
	}
	public static List<String> printjsonfordoc(String jsonString) {
		String message = "";
		String cardsinfo = "";
		String status = "";
		String value = "";
		String type = "";
		String rowitems = "";
		String rowInfo="";
		String desc = "";
		String content = "";
		String rowContext="";
		String[] arr;
		String[] arr2;

		Map<String, String> map = new HashMap<String, String>();
		List<String> list = new ArrayList<String>();
		try {
			map = jsonStringToList(jsonString);
			message = map.get("message");// {"status":2,"value":"识别成功"}
			cardsinfo = map.get("cardsinfo");
			cardsinfo = cardsinfo.substring(1, cardsinfo.length() - 1);// 去掉两边的[]，保留引号及里面的值。
			//System.out.println("cardsinfo"+cardsinfo);
			map = jsonStringToList(message);
			status = map.get("status");
			value = map.get("value");
			if(!cardsinfo.equals("")){
			map = jsonStringToList(cardsinfo);
			type = map.get("type");
			System.out.println(type);



			rowitems = map.get("items");//
			//System.out.println("rowitemsitems"+rowitems);
			if(!rowitems.equals("[]")){
			rowitems = rowitems.substring(1, rowitems.length() - 1);// 去掉两边的[].{"desc":"保留","content":""},{"desc":"姓名","content":"王**"},{"desc":"性别","content":"男"},{"desc":"民族","content":"汉"},{"desc":"出生","content":"1981-02-21"},{"desc":"住址","content":"浙江省桐庐县凤)镇园林村上喻二组"},{"desc":"公民身份号码","content":"*****************"},{"desc":"头像","content":"\/9j\/4AAQS2Q=="}

			map = jsonStringToList(rowitems);
			//System.out.println("X"+rowitems);
			arr2= rowitems.split("]},");//rowitems中用]},分割成数组，获得行总数
			//System.out.println("arr2"+arr2.length);
			for(int a = 0; a < arr2.length; a++){
				if (a < arr2.length - 1) {
					arr2[a] += "]}";// 补全每个数组元素

				}
				map = jsonStringToList(arr2[a]);
			//System.out.println("rowitems2"+map.get("rowContext"));

			rowContext=map.get("rowContext");
			rowContext = rowContext.substring(1, rowContext.length() - 1);
			//{"chardesc":"char0","charInfo":[{"content":"239-223-278-262"}],"charValue":{"content":"那"}},{"chardesc":"char1","charInfo":[{"content":"283-223-320-262"}],"charValue":{"content":"少"}},{"chardesc":"char2","charInfo":[{"content":"325-221-363-262"}],"charValue":{"content":"年"}}
			//map = jsonStringToList(rowContext);
			//{"rowdesc":"row0","rowInfo":[{"content":"239-224-1393-262"}],"rowContext":[{"chardesc":"char0","charInfo":[{"content":"239-223-278-262"}],"charValue":{"content":"那"}},{"chardesc":"char1","charInfo":[{"content":"283-223-320-262"}],"charValue":{"content":"少"}},{"chardesc":"char2","charInfo":[{"content":"325-221-363-262"}],"charValue":{"content":"年"}},{"chardesc":"char3","charInfo":[{"content":"368-222-403-262"}],"charValue":{"content":"到"}},{"chardesc":"char4","charInfo":[{"content":"412-224-444-262"}],"charValue":{"content":"了"}},{"chardesc":"char5","charInfo":[{"content":"451-221-489-262"}],"charValue":{"content":"钟"}},{"chardesc":"char6","charInfo":[{"content":"495-222-530-263"}],"charValue":{"content":"楼"}},{"chardesc":"char7","charInfo":[{"content":"535-223-574-261"}],"charValue":{"content":"上"}},{"chardesc":"char8","charInfo":[{"content":"581-247-589-262"}],"charValue":{"content":"，"}},{"chardesc":"char9","charInfo":[{"content":"599-223-637-263"}],"charValue":{"content":"转"}},{"chardesc":"char10","charInfo":[{"content":"644-221-679-262"}],"charValue":{"content":"身"}},{"chardesc":"char11","charInfo":[{"content":"684-221-723-263"}],"charValue":{"content":"拿"}},{"chardesc":"char12","charInfo":[{"content":"729-223-764-262"}],"charValue":{"content":"钟"}},{"chardesc":"char13","charInfo":[{"content":"770-223-808-263"}],"charValue":{"content":"绳"}},{"chardesc":"char14","charInfo":[{"content":"815-223-850-262"}],"charValue":{"content":"的"}},{"chardesc":"char15","charInfo":[{"content":"858-222-892-262"}],"charValue":{"content":"时"}},{"chardesc":"char16","charInfo":[{"content":"899-222-938-263"}],"charValue":{"content":"候"}},{"chardesc":"char17","charInfo":[{"content":"942-247-951-262"}],"charValue":{"content":"，"}},{"chardesc":"char18","charInfo":[{"content":"974-222-1010-262"}],"charValue":{"content":"看"}},{"chardesc":"char19","charInfo":[{"content":"1016-224-1053-262"}],"charValue":{"content":"见"}},{"chardesc":"char20","charInfo":[{"content":"1058-222-1096-263"}],"charValue":{"content":"楼"}},{"chardesc":"char21","charInfo":[{"content":"1100-223-1137-263"}],"charValue":{"content":"梯"}},{"chardesc":"char22","charInfo":[{"content":"1142-224-1181-261"}],"charValue":{"content":"上"}},{"chardesc":"char23","charInfo":[{"content":"1186-223-1223-263"}],"charValue":{"content":"传"}},{"chardesc":"char24","charInfo":[{"content":"1228-221-1266-264"}],"charValue":{"content":"声"}},{"chardesc":"char25","charInfo":[{"content":"1271-226-1307-263"}],"charValue":{"content":"洞"}},{"chardesc":"char26","charInfo":[{"content":"1313-224-1350-263"}],"charValue":{"content":"对"}},{"chardesc":"char27","charInfo":[{"content":"1353-224-1393-263"}],"charValue":{"content":"面"}}]}
			//System.out.println("rowContext"+map.get("charValue"));

			arr = rowContext.split("},");// 分割rowContext中n组字符串，返回一个字符串数组.
			//System.out.println(arr.length);
			//System.out.println("type:" + type);
			for (int i = 0; i < arr.length; i++) {// 遍历这个数组，每个数组都补全，之后每个数组元素都是一个json格式的字符串，调用jsonStringToList方法，将每个key和value存入map
				if (i < arr.length - 1) {
					arr[i] += "}";// 补全每个数组元素{"chardesc":"char0","charInfo":[{"content":"239-223-278-262"}],"charValue":{"content":"那"}}
				}
				map = jsonStringToList(arr[i]);//{charInfo=[{"content":"239-223-278-262"}], chardesc=char0, charValue={"content":"那"}}
				desc = map.get("charValue");//{charInfo=[{"content":"239-223-278-262"}], chardesc=char0, charValue={"content":"那"}}
				map = jsonStringToList(desc);
				desc = map.get("content");
				list.add(desc);
			}
			list.add("\n");
			}
			}else{
				list.add(status+value+rowitems);
			}

			}else if(value.contains("识别失败")){
            	list.add(status+value);
            }
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return list;

	}

	public static Map listToMap(List<String> ocrList){
	  Map map = new HashMap();
	  if(ocrList == null || ocrList.size() == 0){
	    return null;
    }
    for(String str : ocrList){
	    String[] temp = str.split(":");
	    if(temp.length == 2){
        map.put(temp[0],temp[1]);
      }
    }
    return map;
  }
	public static void main(String[] args) {
//		String jsonString = "{\"message\":{\"status\":2,\"value\":\"识别成功\"},"
//				+ "\"cardsinfo\":[" + "{\"type\":\"2\"," + "\"items\":["
//				+ "{\"desc\":\"保留\",\"content\":\"\"},"
//				+ "{\"desc\":\"姓名\",\"content\":\"王**\"},"
//				+ "{\"desc\":\"性别\",\"content\":\"男\"},"
//				+ "{\"desc\":\"民族\",\"content\":\"汉\"},"
//				+ "{\"desc\":\"出生\",\"content\":\"1981-02-**\"},"
//				+ "{\"desc\":\"住址\",\"content\":\"浙江省*******\"},"
//				+ "{\"desc\":\"公民身份号码\",\"content\":\"3*********9\"},"
//				+ "{\"desc\":\"头像\",\"content\":\"/9j/4AAQS2Q==\"}" + "]}"
//				+ "]" + "}";
      String jsonString  = "{\"message\":{\"status\":-1,\"value\":\"识别失败!ReconClientL:-2\"},\"cardsinfo\":[]}";
    List<String> list = printjson(jsonString);
    Map map = listToMap(list);
    System.out.println(map.toString());

	}
}
