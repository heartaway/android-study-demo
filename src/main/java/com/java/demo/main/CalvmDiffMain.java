package com.java.demo.main;

import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ����Դ��
 * �Ϻ�����ET2&EU13,2.0,adp,adphost,91
 * ������SU18,2.4,adp,adphost,82
 *
 * �ű���NA61,3.2,adp,adphost,110
 * EuSU18,4.0,adp,adphost,137
 * �Ϻ���ET2&EU13,4.4,adp,adphost,151
 * <p>
 * <p>
 * <p>
 * Created by xinyuan on 16/10/10.
 */
public class CalvmDiffMain {


    public static void main(String[] args) throws Exception {
        File file = new File("/Users/xinyuan/Desktop/111.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buf = new byte[1024];
        StringBuffer sb = new StringBuffer();
        while ((fileInputStream.read(buf)) != -1) {
            sb.append(new String(buf));
            buf = new byte[1024];
        }

        String[] lineData = sb.toString().split("\n");
        System.out.println(lineData);
        Map<String/*Ӧ�÷���*/, List<String[]>> map = new HashMap<String, List<String[]>>();
        for (String line : lineData) {
            String[] lineArray = line.split(",");
            String[] newLineArray = new String[6];
            newLineArray[0] = lineArray[0];
            newLineArray[1] = lineArray[1];
            newLineArray[2] = lineArray[2];
            newLineArray[3] = lineArray[3];
            newLineArray[4] = lineArray[4];


            String groupName = lineArray[3];
            String idc = lineArray[0];
            if (idc.contains("����")) {
                if (map.containsKey(groupName + "_����")) {
                    map.get(groupName + "_����").add(newLineArray);
                } else {
                    List<String[]> tmp = new ArrayList<String[]>();
                    tmp.add(newLineArray);
                    map.put(groupName + "_����", tmp);
                }
            } else {
                if (map.containsKey(groupName + "_��")) {
                    map.get(groupName + "_��").add(newLineArray);
                } else {
                    List<String[]> tmp = new ArrayList<String[]>();
                    tmp.add(newLineArray);
                    map.put(groupName + "_��", tmp);
                }
            }
        }


        for (Map.Entry<String, List<String[]>> entry : map.entrySet()) {
            String key = entry.getKey();
            List<String[]> value = entry.getValue();
            //�Ʒ��鴦��
            if (key.contains("_��")) {

                double basicScale = 0;
                double basicScaleCount = 0;
                int index = 0;
                //�ҳ����QPS�±� ,��ȡ���������
//                for (int i = 0; i < value.size(); i++) {
//                    String[] item = value.get(i);
//                    double currentScale = Double.valueOf(item[1]);
//                    int currentScaleCount = Integer.valueOf(item[4]);
//                    double currentQps = currentScale * 10000 / currentScaleCount;
//                    if (currentQps > basicQps) {
//                        basicQps = currentQps;
//                        basicScale = currentScale;
//                        basicScaleCount = currentScaleCount;
//
//                        index = i;
//                    }
//                }

                //�ű���Ϊ��׼
                for (int i = 0; i < value.size(); i++) {
                    String[] item = value.get(i);
                    if (item[0].contains("�ű���")) {
                        basicScale = Double.valueOf(item[1]);
                        basicScaleCount = Integer.valueOf(item[4]);
                        index = i;
                        break;
                    }
                }

                if (basicScale == 0 || basicScaleCount == 0) {
                    continue;
                }

                for (int i = 0; i < value.size(); i++) {
                    if (i == index) {
                        value.get(i)[5] = "0";
                    } else {
                        double currentScale = Double.valueOf(value.get(i)[1]);
                        int currentScaleCount = Integer.valueOf(value.get(i)[4]);
                        int diff = Double.valueOf(basicScaleCount * currentScale / basicScale).intValue() - currentScaleCount;
                        if (diff > 0) {
                            value.get(i)[5] = " + " + diff;
                        } else {
                            value.get(i)[5] = String.valueOf(diff);
                        }
                    }
                }
            }

            //���Ʒ��鴦��
            if (key.contains("_����")) {
                //�ҳ����ڷ���scale�±� ,��ȡ���������
                double basicScale = 10000000;
                int basicScaleCount = 0;
                int index = 0;
                for (int i = 0; i < value.size(); i++) {
                    String[] item = value.get(i);
                    if (item[0].contains("���ڷ���")) {
                        basicScale = Double.valueOf(item[1]);
                        basicScaleCount = Integer.valueOf(item[4]);
                        index = i;
                        break;
                    }
                }

                if (basicScale == 10000000 || basicScaleCount == 0) {
                    continue;
                }

                for (int i = 0; i < value.size(); i++) {
                    if (i == index) {
                        value.get(i)[5] = "0";
                    } else {
                        double currentScale = Double.valueOf(value.get(i)[1]);
                        int currentScaleCount = Integer.valueOf(value.get(i)[4]);
                        int diff = Double.valueOf(basicScaleCount * currentScale / basicScale).intValue() - currentScaleCount;
                        if (diff > 0) {
                            value.get(i)[5] = " + " + diff;
                        } else {
                            value.get(i)[5] = String.valueOf(diff);
                        }
                    }
                }
            }
        }


        Map<String, List<String[]>> resultMap = new HashMap<String, List<String[]>>();
        for (Map.Entry<String, List<String[]>> entry : map.entrySet()) {
            String key = entry.getKey();
            List<String[]> value = entry.getValue();
            String groupName = key.replace("_����", "");
            groupName = groupName.replace("_��", "");
            if (resultMap.containsKey(groupName)) {
                resultMap.get(groupName).addAll(value);
            } else {
                resultMap.put(groupName, value);
            }
        }

        //�����ʽ��Ӧ�����ƣ�Ӧ�÷��飬������SU18(ʵ��)��������SU18(��ֵ) ,�ű���NA61(ʵ��), �ű���NA61(��ֵ), �Ϻ���ET2&EU13(4.4) , ���ڷ���SU18 (4), �Ϻ�����ET2&EU13(2)
        StringBuilder resultBuilder = new StringBuilder();
        for (Map.Entry<String, List<String[]>> entry : resultMap.entrySet()) {
            String key = entry.getKey();//Ӧ�÷���
            List<String[]> value = entry.getValue();//Ӧ�õ�5������

            if (value.size() > 0) {
                resultBuilder.append(value.get(0)[2]);
                resultBuilder.append(",");
                resultBuilder.append(value.get(0)[3]);
            }

            String szC = "";
            String zbC = "";
            String shC = "";
            String szNC = "";
            String shNC = "";

            for (String[] item : value) {
                if (item[0].contains("������")) {
                    szC = "," + item[4] + "," + item[5];
                }

                if (item[0].contains("�ű���")) {
                    zbC = "," + item[4] + "," + item[5];
                }

                if (item[0].contains("�Ϻ���")) {
                    shC = "," + item[4] + "," + item[5];
                }

                if (item[0].contains("���ڷ���")) {
                    szNC = "," + item[4] + "," + item[5];
                }

                if (item[0].contains("�Ϻ�����")) {
                    shNC = "," + item[4] + "," + item[5];
                }
            }

            if (StringUtils.isEmpty(szC)) {
                resultBuilder.append(",,");
            } else {
                resultBuilder.append(szC);
            }

            if (StringUtils.isEmpty(zbC)) {
                resultBuilder.append(",,");
            } else {
                resultBuilder.append(zbC);
            }

            if (StringUtils.isEmpty(shC)) {
                resultBuilder.append(",,");
            } else {
                resultBuilder.append(shC);
            }

            if (StringUtils.isEmpty(szNC)) {
                resultBuilder.append(",,");
            } else {
                resultBuilder.append(szNC);
            }

            if (StringUtils.isEmpty(szC)) {
                resultBuilder.append(",,");
            } else {
                resultBuilder.append(shNC);
            }

            resultBuilder.append("\r\n");
        }


        System.out.println(resultBuilder.toString());
    }
}
