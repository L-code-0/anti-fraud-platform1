package com.anti.fraud.modules.collaboration.model;

import lombok.Data;

import java.util.UUID;

/**
 * 操作类
 * 表示用户的编辑操作，如插入、删除、替换等
 */
@Data
public class Operation {

    /**
     * 操作类型：INSERT-插入，DELETE-删除，REPLACE-替换
     */
    public enum Type {
        INSERT,
        DELETE,
        REPLACE
    }

    /**
     * 操作ID
     */
    private String id;

    /**
     * 操作类型
     */
    private Type type;

    /**
     * 操作位置（从0开始）
     */
    private int position;

    /**
     * 操作内容
     */
    private String content;

    /**
     * 操作长度（用于删除操作）
     */
    private int length;

    /**
     * 操作版本号
     */
    private int version;

    /**
     * 操作发起者ID
     */
    private Long userId;

    /**
     * 操作发起者名称
     */
    private String userName;

    /**
     * 操作时间戳（毫秒）
     */
    private long timestamp;

    /**
     * 构造函数
     */
    public Operation() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 构造函数
     * @param type 操作类型
     * @param position 操作位置
     * @param content 操作内容
     * @param length 操作长度
     * @param version 操作版本号
     * @param userId 操作发起者ID
     * @param userName 操作发起者名称
     */
    public Operation(Type type, int position, String content, int length, int version, Long userId, String userName) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.position = position;
        this.content = content;
        this.length = length;
        this.version = version;
        this.userId = userId;
        this.userName = userName;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 生成插入操作
     * @param position 插入位置
     * @param content 插入内容
     * @param version 操作版本号
     * @param userId 操作发起者ID
     * @param userName 操作发起者名称
     * @return 插入操作
     */
    public static Operation insert(int position, String content, int version, Long userId, String userName) {
        return new Operation(Type.INSERT, position, content, 0, version, userId, userName);
    }

    /**
     * 生成删除操作
     * @param position 删除位置
     * @param length 删除长度
     * @param version 操作版本号
     * @param userId 操作发起者ID
     * @param userName 操作发起者名称
     * @return 删除操作
     */
    public static Operation delete(int position, int length, int version, Long userId, String userName) {
        return new Operation(Type.DELETE, position, null, length, version, userId, userName);
    }

    /**
     * 生成替换操作
     * @param position 替换位置
     * @param content 替换内容
     * @param length 替换长度
     * @param version 操作版本号
     * @param userId 操作发起者ID
     * @param userName 操作发起者名称
     * @return 替换操作
     */
    public static Operation replace(int position, String content, int length, int version, Long userId, String userName) {
        return new Operation(Type.REPLACE, position, content, length, version, userId, userName);
    }

    @Override
    public String toString() {
        return "Operation{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", position=" + position +
                ", content='" + content + '\'' +
                ", length=" + length +
                ", version=" + version +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
