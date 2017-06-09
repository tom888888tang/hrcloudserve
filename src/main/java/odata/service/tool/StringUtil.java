package odata.service.tool;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	public static boolean isEmpty(Object obj) {
		return "".equalsIgnoreCase(parseString(obj));
	}

	/**
	 * 过滤空对象
	 * 
	 * @param obj
	 * @return
	 */
	public static String parseString(Object obj) {
		return parseString(obj, "");
	}

	/**
	 * 过滤空对象
	 * 
	 * @param obj
	 * @return
	 */
	public static String parseString(Object obj, String str) {
		boolean flag = false;
		if (obj == null) {
			flag = true;
		}
		if (flag == false) {
			if ("null".equalsIgnoreCase(obj.toString())
					|| "".equalsIgnoreCase(obj.toString())) {
				flag = true;
			}
		}
		if (flag) {
			return str;
		} else {
			return obj.toString().trim();
		}
	}

	/**
	 * 转换Long类型
	 * 
	 * @param obj
	 * @return
	 */
	public static Long parseLong(Object obj) {
		return parseLong(obj, null);
	}

	/**
	 * 转换Long类型
	 * 
	 * @param obj
	 * @return
	 */
	public static Long parseLong(Object obj, Long value) {
		if (isEmpty(obj)) {
			return value;
		}
		String str = parseString(obj);
		return Long.parseLong(str);
	}

	/**
	 * 转换Integer类型
	 * 
	 * @param obj
	 * @return
	 */
	public static Integer parseInteger(Object obj) {
		return parseInteger(obj, null);
	}

	/**
	 * 转换Integer类型
	 * 
	 * @param obj
	 * @return
	 */
	public static Integer parseInteger(Object obj, Integer value) {
		if (isEmpty(obj)) {
			return value;
		}
		String str = parseString(obj);
		return Integer.valueOf(str);
	}

	/**
	 * 转换Double类型
	 * 
	 * @param obj
	 * @return
	 */
	public static Double parseDouble(Object obj) {
		return parseDouble(obj, null);
	}

	/**
	 * 转换Double类型
	 * 
	 * @param obj
	 * @return
	 */
	public static Double parseDouble(Object obj, Double data) {
		String str = parseString(obj);
		if ("".equalsIgnoreCase(str)) {
			if (isEmpty(data)) {
				return null;
			}
			return data;
		}
		return Double.parseDouble(str);
	}

	/**
	 * 转换Double类型
	 * 
	 * @param obj
	 * @return
	 */
	public static String formatDate(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		if (date == null) {
			return "";
		}
		try {
			return sdf.format(date);
		} catch (Exception ex) {
			return "";
		}
	}

	/**
	 * 转换Date类型
	 * 
	 * @param obj
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(Object obj, String format)
			throws ParseException {
		String str = parseString(obj);
		if ("".equalsIgnoreCase(str)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(str);
	}

	/**
	 * 过滤回车符
	 * 
	 * @param value
	 * @return
	 */
	public static String filterEnter(String value) {
		value = parseString(value);
		value = value.replaceAll("\r", "");
		value = value.replaceAll("\n", "<br/>");
		return value;
	}

	public static boolean checkDate(String date) {
		String eL = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1][0-9])|([2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
		Pattern p = Pattern.compile(eL);
		Matcher m = p.matcher(date);
		boolean b = m.matches();
		return b;
	}

	/**
	 * 过滤掉SQL中包含 1 = 1 部分内容
	 * 
	 * @param sql
	 * @return
	 */
	public static String filterSQL(String sql) {
		Pattern pattern1 = Pattern.compile(
				"[\\s]*(.+)[\\s]*=[\\s]*\\1[\\s]+((and)|(or))[\\s]+",
				Pattern.CASE_INSENSITIVE);
		Pattern pattern2 = Pattern.compile(
				"[\\s]*((and)|(or))[\\s]+(.+)[\\s]*=[\\s]*\\4[\\)]",
				Pattern.CASE_INSENSITIVE);
		Pattern pattern3 = Pattern.compile(
				"[\\s]*((and)|(or))[\\s]+(.+)[\\s]*=[\\s]*\\4[\\s]+",
				Pattern.CASE_INSENSITIVE);
		Pattern pattern4 = Pattern.compile(
				"[\\s]*where[\\s]+(.*)[\\s]*=[\\s]*\\1[\\)]",
				Pattern.CASE_INSENSITIVE);
		Pattern pattern5 = Pattern.compile(
				"[\\s]*where[\\s]+(.*)[\\s]*=[\\s]*\\1[\\s]+",
				Pattern.CASE_INSENSITIVE);

		String str = sql;
		str = " " + str;
		str = str + " ";

		Matcher matcher = pattern1.matcher(str);
		while (matcher.find()) {
			str = matcher.replaceAll(" ");
		}
		matcher = pattern2.matcher(str);
		while (matcher.find()) {
			str = matcher.replaceAll(" ) ");
		}
		matcher = pattern3.matcher(str);
		while (matcher.find()) {
			str = matcher.replaceAll(" ");
		}
		matcher = pattern4.matcher(str);
		while (matcher.find()) {
			str = matcher.replaceAll(" ) ");
		}
		matcher = pattern5.matcher(str);
		while (matcher.find()) {
			str = matcher.replaceAll(" ");
		}
		return str.trim();
	}

	public static boolean checkFloat(String str) {
		boolean b = str.matches("^(([0-9]+\\.[0-9]+)|([0-9]*))$");
		return b;
	}

	public static boolean checkNumber(String str) {
		boolean b = str.matches("[\\d]+");
		return b;
	}

	public static String getRandomNum() {
		String t = String.valueOf(System.currentTimeMillis());
		t = t.substring(t.length() - 5, t.length());
		String rad = "0123456789";
		StringBuffer result = new StringBuffer();
		java.util.Random rand = new java.util.Random();
		int length = 27;
		for (int i = 0; i < length; i++) {
			int randNum = rand.nextInt(10);
			result.append(rad.substring(randNum, randNum + 1));
		}
		return t + result;
	}

	public static String getRandomNumByLength(int length) {
		String rad = "0123456789";
		java.util.Random rand = new java.util.Random();
		StringBuffer result = new StringBuffer();
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				int randNum = rand.nextInt(10);
				result.append(rad.substring(randNum, randNum + 1));
			}
		}
		return result.toString();
	}

	/**
	 * 精确小数位数
	 * 
	 * @param dou
	 *            小数
	 * @param num1
	 *            位数
	 * @param num2
	 *            1.四舍五入 2.舍去小数
	 * @return
	 */
	public static Double exactDouble(Double dou, int num1, int num2) {
		if (isEmpty(dou)) {
			return null;
		}
		int roundingMode = 4;
		BigDecimal bigDec = new BigDecimal(dou);
		bigDec = bigDec.setScale(10, roundingMode);
		dou = bigDec.doubleValue();

		// bigDec = new BigDecimal(dou);
		// if(num2 == 1){
		// roundingMode = 4;
		// }else{
		// roundingMode = 3;
		// }
		// bigDec = bigDec.setScale(num1,3);
		// return bigDec.doubleValue();

		int tmpNum = 1;
		for (int i = 0; i < num1; i++) {
			tmpNum *= 10;
		}
		if (num2 == 1) {
			return StringUtil
					.parseDouble((Math.round(dou * tmpNum) / 1.0D / tmpNum));
		} else if (num2 == 2) {
			return (Math.floor(dou * tmpNum) / tmpNum);
		} else {
			return null;
		}
	}

	/**
	 * 精确小数位数 直接保留多少位小数
	 * */
	public static BigDecimal exactNumber(Double d, int num) {
		return new BigDecimal(d).setScale(num, BigDecimal.ROUND_HALF_UP);
	}

	public static Object viewZero(Object o) {
		if (Double.parseDouble(String.valueOf(o)) == 0) {
			return "0";
		} else {
			return o;
		}
	}

	/**
	 * 根绝长度截取字符串
	 * */
	public static String subStrByLength(String str, int length) {
		if (null == str)
			return "";
		else if (str.length() > length)
			return str.substring(0, length);
		else
			return str;
	}

	public static String fillLeft(String paramString, char paramChar,
			int paramInt) {
		return fillStr(paramString, paramChar, paramInt, true);
	}

	public static String fillRight(String paramString, char paramChar,
			int paramInt) {
		return fillStr(paramString, paramChar, paramInt, false);
	}

	private static String fillStr(String paramString, char paramChar,
			int paramInt, boolean paramBoolean) {
		int i = paramInt - paramString.length();

		if (i <= 0) {
			return paramString;
		}
		StringBuilder localStringBuilder = new StringBuilder(paramString);
		for (; i > 0; --i) {
			if (paramBoolean)
				localStringBuilder.insert(0, paramChar);
			else {
				localStringBuilder.append(paramChar);
			}
		}
		return localStringBuilder.toString();
	}

	public static byte[] hexStringToBytes(String paramString) {
		if ((paramString == null) || (paramString.equals(""))) {
			return null;
		}
		paramString = paramString.toUpperCase();
		int i = paramString.length() / 2;
		char[] arrayOfChar = paramString.toCharArray();
		byte[] arrayOfByte = new byte[i];
		for (int j = 0; j < i; ++j) {
			int k = j * 2;
			arrayOfByte[j] = (byte) (charToByte(arrayOfChar[k]) << 4 | charToByte(arrayOfChar[(k + 1)]));
		}
		return arrayOfByte;
	}

	private static byte charToByte(char paramChar) {
		return (byte) "0123456789ABCDEF".indexOf(paramChar);
	}

	public static String bytesToHexString(byte[] paramArrayOfByte) {
		StringBuilder localStringBuilder = new StringBuilder("");
		if ((paramArrayOfByte == null) || (paramArrayOfByte.length <= 0)) {
			return null;
		}
		for (int i = 0; i < paramArrayOfByte.length; ++i) {
			int j = paramArrayOfByte[i] & 0xFF;
			String str = Integer.toHexString(j);
			if (str.length() < 2) {
				localStringBuilder.append(0);
			}
			localStringBuilder.append(str);
		}
		return localStringBuilder.toString();
	}

	public static String generateRandomString(int paramInt) {
		char[] arrayOfChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

		StringBuffer localStringBuffer = new StringBuffer();
		Random localRandom = new Random();

		for (int i = 0; i < paramInt; ++i) {
			localStringBuffer.append(arrayOfChar[localRandom
					.nextInt(arrayOfChar.length)]);
		}
		return localStringBuffer.toString();
	}
	
	/**
	 * key1=value1&key2=value2格式数据转换为map
	 * 
	 * @param data
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static Map<String, Object> asMapFromString(String data) throws UnsupportedEncodingException {
		Map<String, Object> map = new HashMap<String, Object>();
		String[] kvs = data.split("&");
		for (String kv : kvs) {
			String key = kv.substring(0, kv.indexOf("="));
			String value = kv.substring(kv.indexOf("=") + 1);
			map.put(key, value);
		}
		return map;
	}
	
	 /**
     * 以15位到19位银行卡号校验
     * @param phoneNumber
     * @return 
     */
    public static boolean isBankCardNumber(String telNumber) {
        String regex = "\\d{15,19}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(telNumber.trim());
        return match.matches();
    }
    
    /**
     * 金额验证
     * @param str
     * @return
     */
    public static boolean isMoneyUnit(String str) {
        if ("".equals(str) || "0".equals(str)) {
            return false;
        }

        if ("0".equals(str.substring(0, 1))) {
            return false;
        }
        Matcher match = null;
        if (isNumber(str) == true) {
            Pattern pattern = Pattern.compile("[0-9]*");
            match = pattern.matcher(str.trim());
        } else {
            if (str.trim().indexOf(".") == -1) {
                Pattern pattern = Pattern.compile("^[+]?[0-9]*");
                match = pattern.matcher(str.trim());
            } else {
                Pattern pattern = Pattern.compile("^[+]?[0-9]+(\\.\\d{1,100}){1}quot;");
                match = pattern.matcher(str.trim());
            }
        }
        return match.matches();
    }
    
    public static boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher match = pattern.matcher(str.trim());
        return match.matches();
    }
    
    
    /**
     * 替换四个字节的字符 '\xF0\x9F\x98\x84\xF0\x9F）的解决方案
     * @param content
     * @return
     */
    public static String removeFourChar(String content) {
        byte[] conbyte = content.getBytes();
        for (int i = 0; i < conbyte.length; i++) {
            if ((conbyte[i] & 0xF8) == 0xF0) {
                for (int j = 0; j < 4; j++) {                          
                    conbyte[i+j]=0x30;                     
                }  
                i += 3;
            }
        }
        content = new String(conbyte);
        return content.replaceAll("0000", "");
    }
	
}
