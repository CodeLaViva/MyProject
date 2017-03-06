package com.lhz.haichong.udp_demo;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/27 0027.
 */
public class Char_Split {
    private String ssid = "MyPlace";
    private String pass = "LetMeIn";
    private static final int C = 593; // 协议常数C
    private static final int L = 28;  //协议常数L
    private List<String> data_ssid = new ArrayList<>();
    private int[] sum;


    public Char_Split(String ssid, String pass) {

        this.ssid = ssid;
        this.pass = pass;
    }

    public byte[] construct() {
        split(ssid);
        My_construct(ssid, "ssid", 1);
        split(pass);
        My_construct(pass, "pass", 1);
        split(ssid);
        My_construct(ssid, "ssid", 2);
        split(pass);
        My_construct(pass, "pass", 2);

        String str2 = to_String(data_ssid);
        //System.out.println(data_ssid.toString());
        byte[] my_byte = str2.getBytes();
        for (byte aMy_byte : my_byte) {
            System.out.print(aMy_byte + " ");
        }
        return my_byte;
    }

    private void split(String data) {
        int[] h1 = new int[data.length()]; //存储高位
        int[] l1 = new int[data.length()]; //存储低位
        int[] hi = new int[data.length() * 2 + 1]; //存储与或后的数据
        sum = new int[data.length() * 2];  //存储求和数据
        hi[0] = 0;
        for (int i = 0; i < data.length(); i++) {
            int c = (int) data.charAt(i);
            h1[i] = c / 16; //获取高位
            l1[i] = c % 16; //获取低位

            hi[(i + 1) * 2 - 1] = h1[i] ^ ((i + 1) * 2 - 1);
            if (i == data.length()) {
                break;
            } else {
                hi[(i + 1) * 2] = l1[i] ^ ((i + 1) * 2);
            }
        }

        for (int j = 0; j < data.length() * 2; j++) {
            if (j % 2 == 0) {
                sum[j] = hi[j] * 16 + h1[j / 2];
            } else {
                sum[j] = hi[j] * 16 + l1[j / 2];
            }
            //System.out.println(sum[j]);
        }
    }

    /**
     *
     * @param data 传递需要进行构造的数据
     * @param tag  构造数据的指标，确定是构造ssid还是pass
     * @param count 构造计数，确定是第几次构造
     */

    public void My_construct(String data, String tag, int count) {
        switch (count) {
            case 1:
                data_ssid.add("3");
                if ("ssid".equals(tag)) {
                    data_ssid.add("1099");
                } else if ("pass".equals(tag)) {
                    data_ssid.add("1199");
                }
                data_ssid.add("3");
                data_ssid.add(L + data.length() + "");

                for (int i = 0; i < data.length() * 2; i++) {
                    data_ssid.add("3");
                    data_ssid.add(sum[i] + C + "");
                }
                break;
            case 2:
                data_ssid.add("23");
                if ("ssid".equals(tag)) {
                    data_ssid.add("1099");
                } else if ("pass".equals(tag)) {
                    data_ssid.add("1199");
                }
                data_ssid.add("23");
                data_ssid.add(L + data.length() + "");
                for (int i = 0; i < data.length() * 2; i++) {
                    data_ssid.add("23");
                    data_ssid.add(sum[i] + C + "");
                }
                break;
            default:
                break;
        }
    }

    private String to_String(List<String> arr) {
        if (arr == null)
            return "null";
        if (arr.size() == 0)
            return " ";

        StringBuilder buf = new StringBuilder();
        buf.append(arr.get(0));

        for (int i = 1; i < arr.size(); i++) {
            buf.append(" ");
            buf.append(arr.get(i));
        }

        return buf.toString();
    }
}
