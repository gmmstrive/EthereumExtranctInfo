package com.gikee.ethereum.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Configuration
public class DateTransform {

    // UTC format
    @Value("${formatDate.formatUTC}")
    private String FormatUTC;

    // CST format
    @Value("${formatDate.formatCST}")
    private String FormatCST;

    /**
     * 通过时间戳转换为指定格式 UTC 时间
     *
     * @param timeStamp
     * @return
     */
    public String getTimeStampToUTC(String timeStamp) {
        if (timeStamp != null) {
            return LocalDateTime.parse(Instant.ofEpochSecond(Long.valueOf(timeStamp)).toString(), DateTimeFormatter.ofPattern(FormatUTC))
                    .format(DateTimeFormatter.ofPattern(FormatCST));
        } else {
            return "";
        }
    }

    public String getUTCToTimeStamp(String timeStamp) {
        if (timeStamp != null) {
            LocalDateTime parse = LocalDateTime.parse(timeStamp, DateTimeFormatter.ofPattern(FormatCST));
            return String.valueOf(LocalDateTime.from(parse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000);
        } else {
            return "";
        }
    }

    /**
     * 获取当前时间 时间戳
     *
     * @return
     */
    public Long getTimeStamp() {
        return LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
    }

}
