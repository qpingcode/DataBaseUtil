package me.qping.utils.database.util;

import lombok.val;
import me.qping.utils.DateUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 参数处理，
 * 如： select * from table where date > ${begin} and date < ${end}
 * 将 sql 中 begin 和 end 替换成配置好的参数
 */
public class ParamsUtil {

    /**
     * 如果val是内置对象的格式，形式为 @{expression}， 将val转换为内置对象
     * 比如:
     *
     * @param val
     * @return
     * @{sysdate} 为当前时间
     * @{sysdate - 1} 为前一天时间, date类型
     * @{sysdate + 1} 为后一天时间, date类型
     * @{sysdate | format yyyy-MM-dd} 为当前时间格式化为 "yyyy-MM-dd" 的字符串
     * @{sysdate + 1 | format yyyy-MM-dd HH:mm:ss} 为后一天时间格式化为 "yyyy-MM-dd HH:mm:ss" 的字符串
     * <p>
     * 注意：@{expression} 必须有配对的括号，括号内空格是没有影响的
     */
    public static Object buildInObjectDetect(String val) {
        if (val == null) return null;
        String p = "@\\{([^}]+)\\s*}";
        Matcher b = Pattern.compile(p).matcher(val);

        StringBuffer sb = new StringBuffer();
        if (b.find()) {
            String key = b.group(1);
            return getBuildInObject(key);
        }
        return val;
    }

    private static Object getBuildInObject(String key) {
        String[] expressions = key.split("\\|");

        Object prev = null;

        // 按管道符 | 分割
        for (int i = 0; i < expressions.length; i++) {
            String expression = expressions[i];

            // modify by qping 2020-12-30 增加对 sysdate - 1/4 格式的支持
            Matcher timeMatch = Pattern.compile("\\s*(sysdate)\\s*([\\+\\-]?)\\s*(\\d+\\/\\d+|\\d*)\\s*").matcher(expression);
//            Matcher timeMatch = Pattern.compile("\\s*(sysdate)\\s*([\\+\\-]?)\\s*(\\d*)\\s*").matcher(expression);
            Matcher formatMatch = Pattern.compile("\\s*(format)\\s*(\\w+[\\S\\s]+\\w+)?\\s*").matcher(expression);


            // 解析日期表达式 sysdate 或者 sysdate - 1
            if (timeMatch.find()) {
                int gc = timeMatch.groupCount();

                String operator = timeMatch.group(2);
                String dayAdd = timeMatch.group(3);
                Date now = new Date();

                if (StringUtils.isNotBlank(operator) && StringUtils.isNotBlank(dayAdd)) {

                    int multiNumber;
                    if (operator.equals("+")) {
                        multiNumber = 1;
                    } else if (operator.equals("-")) {
                        multiNumber = -1;
                    } else {
                        throw new RuntimeException("内置对象解析失败，非法的操作符：" + operator);
                    }
                    now = DateUtil.addTime(now, Calendar.SECOND, secondAgo(dayAdd) * multiNumber);

//                    int dayAddInt = Integer.parseInt(dayAdd);
//                    now = DateUtil.addTime(now, Calendar.DAY_OF_MONTH, dayAddInt * multiNumber);
                }

                prev = now;
                continue;
            }
            // 解析格式化表达式，format yyyy-MM-dd HH:mm:ss.SSS
            else if (formatMatch.find()) {
                if (prev == null || !(prev instanceof Date)) {
                    throw new RuntimeException("内置对象解析失败，format表达式不能单独使用，必须跟在一个日期表达式后面，参考 sysdate - 1 | format yyyy-MM-dd");
                }
                String formatExpression = formatMatch.group(2);

                if (formatExpression == null) {
                    throw new RuntimeException("内置对象解析失败，format表达式必须不能为空，参考：sysdate | format yyyy-MM-dd HH:mm:ss");
                }
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(formatExpression);
                    prev = sdf.format(prev);
                } catch (Exception ex) {
                    throw new RuntimeException("内置对象解析失败，format表达式错误，参考java日期格式化表达式: " + formatExpression);
                }

            } else {
                throw new RuntimeException("内置对象解析失败，错误的表达式： " + expression);
            }
        }

        if (prev == null) {
            throw new RuntimeException("内置对象解析失败，错误的表达式: " + key);
        }

        return prev;
    }

    /**
     * 处理sql中的内置对象，形式为 @{expression}
     * 处理方式是将表达式解析后直接替换字符串
     * 比如 select * from table where date > '@{sysdate|format yyyy-MM-dd}' ----> select * from table where date > '2020-08-01'
     *
     * @param sql
     * @return
     */
    public static String dealBuildInObjects(String sql) {
        StringBuffer sb = new StringBuffer();
        String p = "@\\{([^}]+)\\s*}";
        Matcher b = Pattern.compile(p).matcher(sql);

        while (b.find()) {
            String key = b.group(1);
            Object val = getBuildInObject(key);
            b.appendReplacement(sb, val.toString());
        }
        b.appendTail(sb);
        return sb.toString();
    }


    /**
     * 处理sql中参数，形式为 ${expression}
     * 处理方式：SQL中该参数会被替换成问号 ? ，并且 PreparedStatement 会 setObject
     * 比如：select * from table where date > ${begin}  ----> select * from table where date > ?
     *
     * @param paramsMap
     * @param sql
     * @param sqlParams
     * @return
     */
    public static String dealParamSafe(Map<String, Object> paramsMap, String sql, List<Object> sqlParams, boolean fillWithBlank) {
        String p = "\\$\\{\\s*(\\S+)\\s*\\}";
        Matcher m = Pattern.compile(p).matcher(sql);

        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String full = m.group();
            String key = m.group(1);

            if (fillWithBlank) {
                m.appendReplacement(sb, "''");
            } else {
                if (paramsMap.get(key) == null) {
                    throw new RuntimeException("参数注入失败，参数名：" + full);
                }
                Object val = paramsMap.get(key);
                m.appendReplacement(sb, "?");
                sqlParams.add(val);
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 处理sql中非安全参数，形式为 #{expression}
     * 处理方式：直接使用paramsMap中的匹配到的变量进行字符串替换
     * 比如：select * from table where date > '#{begin}'  ---- paramsMap = {begin:'2020-05-01'} ----> select * from table where date > '2020-05-01'
     *
     * @param paramsMap
     * @param sql
     * @return
     */
    public static String dealParamUnsafe(Map<String, Object> paramsMap, String sql, boolean fillWithBlank) {
        StringBuffer sb = new StringBuffer();
        String p = "#\\{\\s*(\\S+?)\\s*\\}";
        Matcher m2 = Pattern.compile(p).matcher(sql);
        while (m2.find()) {
            String full = m2.group();
            String key = m2.group(1);
            if(paramsMap == null || paramsMap.get(key) == null){
                if(fillWithBlank){
                    m2.appendReplacement(sb, "");
                }else{
                    throw new RuntimeException("参数注入失败，参数名：" + full);
                }
            }else{
                Object val = paramsMap.get(key);
                m2.appendReplacement(sb, val.toString());
            }
        }
        m2.appendTail(sb);
        return sb.toString();
    }

    public static String autoFillParams(String sql) {
        sql = dealBuildInObjects(sql);
        sql = dealParamUnsafe(new HashMap<String, Object>(), sql, true);
        sql = dealParamSafe(new HashMap<String, Object>(), sql, new ArrayList<>(), true);
        return sql;
    }

    public static void main(String[] args) {

        String expression = " sysdate + 1";
        Matcher timeMatch = Pattern.compile("\\s*(sysdate)\\s*([\\+\\-]?)\\s*(\\d+\\/\\d+|\\d*)\\s*").matcher(expression);
        Matcher formatMatch = Pattern.compile("\\s*(format)\\s*(\\w+[\\S\\s]+\\w+)?\\s*").matcher(expression);


        // 解析日期表达式 sysdate 或者 sysdate - 1
        if (timeMatch.find()) {
            int gc = timeMatch.groupCount();

            String operator = timeMatch.group(2);
            String dayAdd = timeMatch.group(3);

            System.out.println(operator + "  " + dayAdd);

            Date now = new Date();
            Date deal = DateUtil.addTime(now, Calendar.SECOND, secondAgo(dayAdd) * (-1));

            System.out.println(DateUtil.dateToString(now, "yyyy-MM-dd HH:mm:ss.SSS"));
            System.out.println(DateUtil.dateToString(deal, "yyyy-MM-dd HH:mm:ss.SSS"));

        }


        Map<String, Object> paramsMap  = new HashMap<>();
        paramsMap.put("begin", "2020-01-01");
        paramsMap.put("end", "2020-01-02");


        List<Object> sqlParams = new ArrayList<>();
        String sql = dealParamUnsafe(paramsMap, "select * from table where time > #{begin}and\ntime<#{end}", false);
        System.out.println(sql);
//        dealParamSafe(paramsMap, "select * from table where time > #{begin} and time < #{end}", sqlParams, false);

    }


    public static final int DAY_MILLISECOND= 24 * 3600 * 1000;
    public static final int DAY_SECOND= 24 * 3600;


    static int secondAgo(String ratio) {
        if (ratio.contains("/")) {
            String[] rat = ratio.split("/");
            // 将表达式转换成多少秒前执行  1/4  --> 1 / 4 * 24 * 3600   -->  1 * 24 * 3600 / 4
            return new BigDecimal(rat[0]).multiply(new BigDecimal(DAY_SECOND)).divide(new BigDecimal(rat[1]), 0, BigDecimal.ROUND_HALF_UP).intValue();
        } else {
            return new BigDecimal(ratio).multiply(new BigDecimal(DAY_SECOND)).intValue();
        }
    }
}
