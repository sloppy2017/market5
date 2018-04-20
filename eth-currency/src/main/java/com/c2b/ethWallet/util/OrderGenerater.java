package com.c2b.ethWallet.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.c2b.coin.common.DateUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;


/**
 * @author zhangchunming
 * @describe 订单生成器
 */
public class OrderGenerater {

    private static final String[] CHARARTERS = {"0123456789ABCDEFGHIJ","KLMNOPQRSTUVWXYZ"};
    private static final String CHARNUM = "0123456789";

    public static String generateOrderNo(){
    	StringBuffer sb = new StringBuffer();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        sb.append(sf.format(DateUtil.getCurrentDate())).append(RandomStringUtils.randomNumeric(6));
        return sb.toString();
    }
    /**
     * @describe:订单号规则：日期+6位随机数+邀请码（唯一）
     * @author: zhangchunming
     * @date: 2016年11月1日下午5:47:23
     * @param usercode
     * @return: String
     */
    public static String generateOrderNo(String usercode){
        if("".equals(usercode)||usercode == null){
            return null;
        }
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat sf = new SimpleDateFormat("yyMMddHHmmss");
        sb.append(sf.format(DateUtil.getCurrentDate())).append(RandomStringUtils.randomNumeric(6)).append(usercode);
        return sb.toString();
    }
    /**
     * @describe:生成流水号
     * @author: zhangchunming
     * @date: 2016年11月4日下午2:12:12
     * @param usercode 邀请码 唯一
     * @param acc_no 交易类型 与表acc_def中acc_no对应
     * @return: String
     */
    public static String generateFlowNo(String usercode, String acc_no){
        if(StringUtils.isEmpty(usercode)||StringUtils.isEmpty(acc_no)){
            return null;
        }
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat sf = new SimpleDateFormat("yyMMddHHmmss");
        sb.append(sf.format(DateUtil.getCurrentDate())).append(RandomStringUtils.randomNumeric(6)).append(acc_no).append(usercode);
        return sb.toString();
    }
    /**
     * @describe:消费码规则：日期+邀请码（唯一）+6位随机数
     * @author: lishuo
     * @date: 2016年11月1日下午5:47:23
     * @param usercode
     * @return: String
     */
    public static String generateOrderCode(String usercode){
        if("".equals(usercode)||usercode == null){
            return null;
        }
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat sf = new SimpleDateFormat("yyMMddHHmmss");
        sb.append(sf.format(DateUtil.getCurrentDate())).append(usercode).append(RandomStringUtils.randomNumeric(6));
        return sb.toString();
    }

    /**
     * @describe:生成消费码
     * @author: zhangchunming
     * @date: 2016年12月16日下午12:07:14
     * @param length
     * @return: String
     */
    public static String getCode(int length) {
        StringBuffer StringBuffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            Random random = new Random();
            int index = random.nextInt(CHARNUM.length());
            /*if (i % 4 == 0)
                StringBuffer.append(" " + CHARNUM.charAt(index));
            else*/
            StringBuffer.append(CHARNUM.charAt(index));
        }
        System.out.println(StringBuffer.toString().trim());
        return StringBuffer.toString().trim();
    }

    /**
     * @describe:生成没有重复数字的数
     * @author: zhangchunming
     * @date: 2016年12月17日下午5:48:06
     * @return
     * @return: String
     */
    public static String merchandiseNumber() {
        String number = "";
        // 初始化备选数组
        int[] defaultNums = new int[10];
        for (int i = 0; i < defaultNums.length; i++) {
            defaultNums[i] = i;
        }
        Random random = new Random();
        int[] nums = new int[8];
        // 默认数组中可以选择的部分长度
        int canBeUsed = 10;
        // 填充目标数组
        for (int i = 0; i < nums.length; i++) {
            // 将随机选取的数字存入目标数组
            int index = random.nextInt(canBeUsed);
            nums[i] = defaultNums[index];
            // 将已用过的数字扔到备选数组最后，并减小可选区域
            swap(index, canBeUsed - 1, defaultNums);
            canBeUsed--;
        }
        if (nums.length > 0) {
            for (int i = 0; i < nums.length; i++) {
                number += nums[i];
            }
        }
        return number;
    }
    private static void swap(int i, int j, int[] nums) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
    /**
     * @describe:生成消费码，以当前最新消费码递增
     * @author: zhangchunming
     * @date: 2016年12月16日下午5:04:59
     * @param length
     * @param code 当前最新code
     * @return: String
     */
    public static String getCode(int length, String code, int maxCount) {
        StringBuffer StringBuffer = new StringBuffer();
            if(StringUtils.isEmpty(code)){
                /*for (int i = 0; i < length; i++) {
                    Random random = new Random();
                    int index = random.nextInt(CHARARTERS[0].length());//前20位随机取，后16按递增方式取，以避免重复
                    if(i<4){//前4位随机生成
                        StringBuffer.append(CHARARTERS[0].charAt(index));
                    }else{//后面位，递增生成
                        for(int j = 0;j<CHARARTERS[1].length()-1;j++){
                            StringBuffer.append(CHARARTERS[1].charAt(j));
                            for(int k = 0;j<CHARARTERS[1].length()-1;k++){
                                StringBuffer.append(CHARARTERS[1].charAt(k));
                                for(int m = 0;j<CHARARTERS[1].length()-2;m++){
                                    StringBuffer.append(CHARARTERS[1].charAt(m));
                                    for(int n = 0;j<CHARARTERS[1].length()-1;n++){
                                        StringBuffer.append(CHARARTERS[1].charAt(n));
                                    }
                                }
                            }
                        }
                    }
                }*/
                int count = 0;
                for(int j = 0;j<CHARARTERS[1].length()&&count<maxCount;j++){
                    char a = CHARARTERS[1].charAt(j);
                    for(int k = 0;k<CHARARTERS[1].length()&&count<maxCount;k++){
                        char b = CHARARTERS[1].charAt(k);
                        for(int m = 0;m<CHARARTERS[1].length()&&count<maxCount;m++){
                            char c = CHARARTERS[1].charAt(m);
                            for(int n = 0;n<CHARARTERS[1].length()&&count<maxCount;n++){
                                char d = CHARARTERS[1].charAt(n);

                                java.lang.StringBuffer StringBuffer1 = new StringBuffer(); //前4位
                                StringBuffer1.append(a).append(b).append(c).append(d);
                                Random random = new Random();
                                for(int e = 0;e<4;e++){
                                    int index_1 = random.nextInt(CHARARTERS[0].length());//前20位随机取，后16按递增方式取，以避免重复
                                    StringBuffer1.insert(random.nextInt(StringBuffer1.length()), CHARARTERS[0].charAt(index_1));
                                }
                                /*str = StringBuffer1.toString()+a+b+c+d;
                                int len = str.length();
                                for(int f = 0;f<len;f++){
                                    int index = random.nextInt(str.length());
                                    StringBuffer2.append(str.charAt(index));
                                    str = str.replaceFirst(str.charAt(index)+"", "");
                                }*/
                                StringBuffer.append(StringBuffer1).append(",");
                                ++count;
                            }
                        }
                    }
                }
            }else{
                int index1 = -1,index2 = -1,index3 = -1,index4 = -1;
                for(int a = 0;a<code.length();a++){
                    char  letter = code.charAt(a);
                    if(CHARARTERS[1].indexOf(letter)>=0){
                        if(index1==-1){
                            index1 = CHARARTERS[1].indexOf(letter);
                        }else{
                            if(index2==-1){
                                index2 = CHARARTERS[1].indexOf(letter);
                            }else{
                                if(index3==-1){
                                    index3 = CHARARTERS[1].indexOf(letter);
                                }else{
                                    if(index4==-1){
                                        index4 = CHARARTERS[1].indexOf(letter)+1;
                                    }
                                }
                            }
                        }
                    }
                }
                System.out.println("index1="+index1+",index2="+index2+",index3="+index3+",index4="+index4);
                int count = 0;
                for(int j = index1;j<CHARARTERS[1].length()&&count<maxCount;j++){
                    char a = CHARARTERS[1].charAt(j);
                    for(int k = index2;k<CHARARTERS[1].length()&&count<maxCount;k++){
                        char b = CHARARTERS[1].charAt(k);
                        for(int m = index3;m<CHARARTERS[1].length()&&count<maxCount;m++){
                            char c = CHARARTERS[1].charAt(m);
                            for(int n = index4;n<CHARARTERS[1].length()&&count<maxCount;n++){
//                                StringBuffer.append(a).append(b).append(c).append(CHARARTERS[1].charAt(n)).append(",");
                                char d = CHARARTERS[1].charAt(n);
                                java.lang.StringBuffer StringBuffer1 = new StringBuffer();
                                StringBuffer1.append(a).append(b).append(c).append(d);
                                Random random = new Random();
                                for(int e = 0;e<4;e++){
                                    int index_1 = random.nextInt(CHARARTERS[0].length());//前20位随机取，后16按递增方式取，以避免重复
                                    StringBuffer1.insert(random.nextInt(StringBuffer1.length()), CHARARTERS[0].charAt(index_1));
                                    /*StringBuffer1.append(CHARARTERS[0].charAt(index1));
                                    int index_2 = random.nextInt(StringBuffer1.length());
                                    StringBuffer2.append(CHARARTERS[0].charAt(index_1)).append(StringBuffer1.charAt(index_2));*/
                                }
                                /*str = StringBuffer1.toString()+a+b+c+d;
                                int len = str.length();
                                for(int f = 0;f<len;f++){
                                    int index = random.nextInt(str.length());
                                    StringBuffer2.append(str.charAt(index));
                                    str = str.replaceFirst(str.charAt(index)+"", "");
                                }*/
                                StringBuffer.append(StringBuffer1).append(",");
                                ++count;
                            }
                            index4 = 0;
                        }
                        index3 = 0;
                    }
                    index2 = 0;
                }
            }
            String codeStr = StringBuffer.substring(0,StringBuffer.length()-1).toString().trim();
//            String codeStr = StringBuffer.toString();
            System.out.println("codeStr="+codeStr);
            System.out.println("codeStr.length="+codeStr.split(",").length);
        return codeStr;
    }

    private static int[] BuildRandomSequence4(int low, int high) {
        int x = 0, tmp = 0;
        if (low > high) {
            tmp = low;
            low = high;
            high = tmp;
        }
        int[] array = new int[high - low + 1];
        for (int i = low; i <= high; i++) {
            array[i - low] = i;
        }
        for (int i = array.length - 1; i > 0; i--) {
            Random random = new Random();
            x = random.nextInt(i + 1);
            tmp = array[i];
            array[i] = array[x];
            array[x] = tmp;
        }
        return array;
    }
    public static void main(String[] args) {
        /*StringBuffer sb = new StringBuffer();
        SimpleDateFormat sf = new SimpleDateFormat("yyMMddHHmmss");
        String usercode = null;
        //sb.append(sf.format(new Date())).append(RandomStringUtils.randomNumeric(6)).append(usercode);
        sb.append(sf.format(new Date())).append(usercode).append(RandomStringUtils.randomNumeric(6));
        System.out.println(sb.toString());*/
//        getCode(8,"6KKK632O",5);
        /*String str = "abc";
        str = str.replace(str.charAt(2)+"", "");
        System.out.println(str);*/

//        System.out.println(String.valueOf(BuildRandomSequence4(0, 9)));

//        getCode(8, 1000);
       /* for(int i = 0 ;i<1000; i++){
            System.out.println(merchandiseNumber());
        }*/

        getCode(8) ;
    }
}
