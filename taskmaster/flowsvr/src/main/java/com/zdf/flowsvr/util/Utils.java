package com.zdf.flowsvr.util;


public class Utils {
    /**
     * 获得任务Id
     * @return
     */
    public static String getTaskId() {
        return SnowFlake.nextId() + "";
    }

    public static boolean isStrNull(String s) {
        return "".equals(s);
    }








}
