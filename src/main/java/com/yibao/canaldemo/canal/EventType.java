package com.yibao.canaldemo.canal;

import lombok.Getter;

/**
 * CanalEntry 151 line->EventType
 *
 * @author houxiurong
 * @date 2019-07-26
 */
public enum EventType {
    /**
     * EventType
     */
    INSERT(1, "INSERT"),
    UPDATE(2, "UPDATE"),
    DELETE(3, "DELETE"),
    CREATE(4, "CREATE"),
    ALTER(5, "ALTER"),
    ERASE(6, "ERASE"),
    QUERY(7, "QUERY"),
    TRUNCATE(8, "TRUNCATE"),
    RENAME(9, "RENAME"),
    CINDEX(10, "CINDEX"),
    DINDEX(11, "DINDEX"),
    ;

    @Getter
    private Integer code;

    @Getter
    private String type;

    EventType(Integer code, String type) {
        this.code = code;
        this.type = type;
    }

    public static EventType valueOfType(String type) {
        for (EventType eventType : EventType.values()) {
            if (eventType.type.equalsIgnoreCase(type)) {
                return eventType;
            }
        }
        return null;
    }

    public static EventType valueOfCode(Integer code) {
        for (EventType eventType : EventType.values()) {
            if (eventType.code.equals(code)) {
                return eventType;
            }
        }
        return null;
    }
}