package com.whr.analysis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

public class AnalysisInfoTest {
	private static final String NAME = "张三";
	private static final String IDCN = "43050219870524233X";
	private static final String ADDRESS = "湖北省黄冈市蕲春县刘河镇凉亭村八大胡同3栋88号";
//	private static final String ADDRESS = "江西省南昌市东湖区第三街道";
	private static final String TELEPHONE = "13510639646";
	private static final String SPLIT_SYMBOL = " `~!@#$%^&*()_-+=<>?:{}|,.，。《》【】";

	private static int count = 0;

	public static void main(String[] args) {
//		testMain();
		testFormat();
//		test
	}

	private static void testSingle() {
		AnalysisInfoVo splitResult = AddressAnalysis.split("13510639646李四江西省南昌市东湖区第三街道43050219870524233X");
		System.out.println("结果:" + splitResult);
	}
	
	private static void testFormat() {
		AnalysisInfoVo splitResult = AddressAnalysis
				.split("收件人姓名：张三 收件人电话：15800000000 收件人地址：广东省深圳市南山区科技大厦");

		System.out.println("结果:" + splitResult);
	}

	private static void testMain(){
		String[] arrays21 = { TELEPHONE, ADDRESS };// 地址+电话
		String[] arrays22 = { NAME, TELEPHONE };// 姓名+电话
		String[] arrays23 = { NAME, ADDRESS };// 姓名+地址
		String[] arrays24 = { NAME, IDCN };// 姓名+身份证号
		String[] arrays25 = { TELEPHONE, IDCN };// 电话+身份证号
		String[] arrays26 = { ADDRESS, IDCN };// 地址+身份证号

		String[] arrays3no_address = { NAME, TELEPHONE, IDCN };
		String[] arrays3no_tele = { NAME, IDCN, ADDRESS };
		String[] arrays3no_name = { TELEPHONE, IDCN, ADDRESS };
		String[] arrays3no_id = { NAME, TELEPHONE, ADDRESS };

		String[] arrays4 = { NAME, TELEPHONE, IDCN, ADDRESS };
		List<String[]> testCases = new ArrayList<String[]>();
		testCases.add(arrays21);
		testCases.add(arrays22);
		testCases.add(arrays23);
		testCases.add(arrays24);
		testCases.add(arrays25);
		testCases.add(arrays26);
		testCases.add(arrays3no_address);
		testCases.add(arrays3no_tele);
		testCases.add(arrays3no_name);
		testCases.add(arrays3no_id);
		testCases.add(arrays4);
		final int size=testCases.size()-1;
		for (int i=0;i<=size;i++) {
			System.out
			.println("=========================编号="+i+"=============================================================");
			test(testCases.get(i),true);		//TODO 设置是否分割
		}
		System.out.println("count=" + count);
	}
	
	/**
	 * 测试
	 * 
	 * @param testCase
	 * @param isSplit 是否有分隔符
	 */
	private static void test(String[] testCase,boolean isSplit) {
		List<String> case1 = new ArrayList<String>();
		perm(testCase, 0, case1,isSplit);
		for (String s : case1) {
			System.out.println("输入:" + s);
			AnalysisInfoVo splitResult = AddressAnalysis.split(s);
			count++;
			System.out.println("结果:" + splitResult);
		}
	}

	/**
	 * 交换
	 * 
	 * @param arr
	 * @param from
	 * @param to
	 */
	private static void swap(String[] arr, int from, int to) {
		final String tmp = arr[from];
		arr[from] = arr[to];
		arr[to] = tmp;
	}

	/**
	 * 全排列
	 * 
	 * @param arr
	 * @param start
	 * @param result
	 */
	private static void perm(String[] arr, int start, List<String> result,
			boolean isSplit) {
		if (start >= arr.length) {
			StringBuilder sb = new StringBuilder();
			for (String anArr : arr) {
				if (isSplit) {
					sb.append(getRandomString(3,SPLIT_SYMBOL)+anArr + getRandomString(3,SPLIT_SYMBOL));
				} else {
					sb.append(anArr);
				}
			}
			result.add(sb.toString());
		} else {
			for (int i = start; i < arr.length; ++i) {
				swap(arr, start, i);
				perm(arr, start + 1, result, isSplit);
				swap(arr, i, start);
			}
		}
	}

	public static String getRandomString(int count, String str) {
		if (str != null) {
			if (!StringUtils.isBlank(str)) {
				if (count <= 0) {
					return "";
				}
				char[] charArray = str.toCharArray();
				int index;
				char[] newArray = new char[count];
				for (int i = 0; i < count; i++) {
					index = RandomUtils.nextInt(0,charArray.length);
					newArray[i] = str.charAt(index);
				}
				return new String(newArray);
			}
		}
		return null;
	}
}
