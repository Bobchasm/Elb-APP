package com.neusoft.elm.util;

import javax.swing.text.DateFormatter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CommonUtil {
    public static String getCurrentDate()
    {
        LocalDate now = LocalDate.now();

        // 创建一个DateTimeFormatter来定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 使用formatter将LocalDateTime格式化为字符串
        String formattedTime = now.format(formatter);
        return formattedTime;
    }

}
