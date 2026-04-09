package com.anti.fraud.common.enums;

import lombok.Getter;

@Getter
public enum ContentType {
    ARTICLE(1, "文章"),
    VIDEO(2, "视频"),
    CASE(3, "案例");

    private final Integer code;
    private final String name;

    ContentType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}

