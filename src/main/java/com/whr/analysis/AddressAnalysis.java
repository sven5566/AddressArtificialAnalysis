package com.whr.analysis;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressAnalysis {
    public static final String SUCCESS="1";
    public static final String FAIL="0";
    //电话号码正则
    private static  String TEL_REG = "1\\d{10}";
    //身份证正则
    private static String   ID_REG="[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]";

    public static void main(String[] args) {
        String testValue="13823583931湖北省黄冈市蕲春县刘河镇凉亭村八大胡同3栋88号李永春42112619890123441x";
        System.out.println(testValue);
        AnalysisInfoVo ret = split(testValue);
        System.out.println(ret);
    }

    public static AnalysisInfoVo split(String value){
        AnalysisInfoVo firstRet= firSplit(value);
        if(firstRet!=null){
            return firstRet;
        }
        AnalysisInfoVo ret = splitWithFormat(value);
        splitWithoutFormat(value,ret);
        return ret;
    }

    /**
     * 第一步
     * @param str
     * @return
     */
    private static AnalysisInfoVo firSplit(String str){
        AnalysisInfoVo 	infoVo=new AnalysisInfoVo();
        String name = "", tel = "", addr = "",idcn ="";
        str = str.replaceAll(" ", "").replaceAll("：", ":").replaceAll("/", "").replaceAll(",", "").replaceAll("，", "").trim();
        if (str.split("收件人").length < 3) {
            return null;
        }
        String[] strArr = str.split("收件人");
        for (String s : strArr) {
            if (StringUtils.isNotBlank(s)) {
                if (s.startsWith("姓名:")) {
                    name = s.split(":")[1];
                } else if (s.startsWith("电话:")) {
                    tel = s.split(":")[1];
                } else if (s.startsWith("地址:")) {
                    addr = s.split(":")[1];
                } else if(s.startsWith("身份证:"))
                {
                    idcn = s.split(":")[1];
                }
            }
        }
        infoVo.setStatus(SUCCESS);
        infoVo.setAddress(addr);
        infoVo.setName(name);
        infoVo.setTelPhone(tel);
        infoVo.setIdcn(idcn);
        return infoVo;

    }

    /**
     * 不带分隔符的解析
     * @param value
     * @return
     */
    private static AnalysisInfoVo splitWithoutFormat(String value,AnalysisInfoVo ret){
        if(ret==null){
            ret = new AnalysisInfoVo();
        }

        //先找身份证
        Pattern idPattern= Pattern.compile(ID_REG);
        Matcher idMatcher=idPattern.matcher(value);
        if(idMatcher.find()){
            final String idStr = idMatcher.group();
            ret.setIdcn(idStr);
            value=value.replace(idStr,"@");
        }

        //电话号码
        Pattern telPattern= Pattern.compile(TEL_REG);
        Matcher telMatcher  = telPattern.matcher(value);
        if(telMatcher.find()){
            final String telStr=telMatcher.group();
            ret.setTelPhone(telStr);
            value=value.replace(telStr,"@");
        }

        //上面是数字中文间隔的情况，所以调用splitWithFormat
        ret=splitWithFormat(value,ret);
        if(StringUtils.isNotEmpty(ret.getName())){//说明解析成功
            return ret;
        }

        //剩下的是地址+姓名&姓名加+地址的情况
        value=value.replace("@","");
        Segment segment = HanLP.newSegment().enableNameRecognize(true).
                enablePlaceRecognize(true).
                enableNumberQuantifierRecognize(false).
                enableOrganizationRecognize(false).
                enableTranslatedNameRecognize(false)
                ;
        List<Term> termList = segment.seg(value);
        for(int i=0;i<termList.size();i++){
            Term term=termList.get(i);
            System.out.println(term);
            boolean isFirstName=(i==0&&Nature.q.equals(term.nature));//比如“张”
            if(Nature.nr.equals(term.nature)||isFirstName){//人名
                final int tempIndex=value.indexOf(term.word);
                if(tempIndex>5){//姓名的首字母的位置在后面，表明是地址+姓名模式
                    final String tmpName=value.substring(tempIndex);
                    ret.setName(tmpName);
                    value=value.replace(tmpName,"");
                }else{//姓名+地址模式
                    final String states="北京市天津市河北省山西省内蒙古自治区辽宁省吉林省黑龙江省上海市江苏省浙江省安徽省福建省江西省山东省河南省湖北省湖南省广东省广西壮族自治区海南省重庆市四川省贵州省云南省西藏自治区陕西省甘肃省青海省宁夏回族自治区新疆维吾尔自治区台湾省香港特别行政区澳门特别行政区";
                    for(Term t :termList){
                        System.out.println(t);
                        if(Nature.ns.equals(t.nature)&&states.contains(t.word)){//是地名且是省份
                            final int statesIndex = value.indexOf(t.word);
                            if(statesIndex>=0){
                                final String tmpName=value.substring(tempIndex,statesIndex);
                                ret.setName(tmpName);
                                value=value.replace(tmpName,"");
                            }
                        }else {
                            ret.setAddress(value);
                        }
                    }
                }
            }
        }
        ret.setAddress(value);
        return ret;
    }

    /**
     * 带分隔符形式的解析
     * @param value
     * @return
     */
    private static AnalysisInfoVo splitWithFormat(String value){
        return splitWithFormat(value,null);
    }

    private static AnalysisInfoVo splitWithFormat(String value,AnalysisInfoVo result){
        String [] ret=split2array(value);
        if(result==null){
            result=new AnalysisInfoVo();
        }
//        if(ret.length<4){
//            return result;
//        }
        for (String s :ret) {
            //顺序：身份证->电话->人名->地址
            if(s.matches(TEL_REG)&&StringUtils.isEmpty(result.getTelPhone())){
                result.setTelPhone(s);
            }else if(s.matches(ID_REG)&&StringUtils.isEmpty(result.getIdcn())){
                result.setIdcn(s);
            }else if(isChinesName(s)&&StringUtils.isEmpty(result.getName())){
                result.setName(s);
            }else{
                result.setAddress(s);
            }
        }
        return result;
    }

    /**
     * 是否是中文姓名
     * @param value
     * @return
     */
    private static boolean isChinesName(String value) {
        return value.length() < 5;
    }

    /**
     * 分割
     * @param value
     * @return
     */
    private static String[] split2array(String value){
        //用来拆分的特殊字符串的正则
        String FOMAT_REG = "[ `~!@#$%^&*()_\\-+=<>?:\"{}|,.，。《》【】]+";
        return value.split(FOMAT_REG);
    }
}
