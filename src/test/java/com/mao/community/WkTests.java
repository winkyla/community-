package com.mao.community;

import java.io.IOException;

public class WkTests {


    public static void main(String[] args) {
        String cmd="e:/work/wkhtmltopdf/bin/wkhtmltoimage --quality 75 https://www.nowcoder.com e:/work/data/wk-images/3.png";
        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("OK!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
