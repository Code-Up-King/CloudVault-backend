package org.chad.cloudvault.utils;

import java.util.HashMap;
import java.util.Map;

public class FileUtils {
    private static final Map<String, Byte> fileTypeMap = new HashMap<>();

    static {
        // 视频类型
        fileTypeMap.put("mp4", (byte) 1);
        fileTypeMap.put("avi", (byte) 1);
        fileTypeMap.put("mov", (byte) 1);
        fileTypeMap.put("wmv", (byte) 1);
        // 音频类型
        fileTypeMap.put("mp3", (byte) 2);
        fileTypeMap.put("wav", (byte) 2);
        fileTypeMap.put("aac", (byte) 2);
        // 图片类型
        fileTypeMap.put("jpg", (byte) 3);
        fileTypeMap.put("jpeg", (byte) 3);
        fileTypeMap.put("png", (byte) 3);
        fileTypeMap.put("gif", (byte) 3);
        // PDF类型
        fileTypeMap.put("pdf", (byte) 4);
        // Word文档
        fileTypeMap.put("doc", (byte) 5);
        fileTypeMap.put("docx", (byte) 5);
        // Excel表格
        fileTypeMap.put("xls", (byte) 6);
        fileTypeMap.put("xlsx", (byte) 6);
        // 文本文件
        fileTypeMap.put("txt", (byte) 7);
        // 代码文件
        fileTypeMap.put("java", (byte) 8);
        fileTypeMap.put("py", (byte) 8);
        fileTypeMap.put("js", (byte) 8);
        fileTypeMap.put("cpp", (byte) 8);
        // 压缩文件
        fileTypeMap.put("zip", (byte) 9);
        fileTypeMap.put("rar", (byte) 9);
        fileTypeMap.put("7z", (byte) 9);
    }

    public static Byte getFileType(String filename) {
        // 获取文件扩展名
        String extension = "";
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            extension = filename.substring(i + 1).toLowerCase();
        }

        // 根据扩展名返回文件类型
        return fileTypeMap.getOrDefault(extension, (byte) 10); // 默认为'其他'
    }

    //1:视频 2:音频  3:图片 4:文档 5:其他
    private static final Map<String, Byte> fileCategoryMap = new HashMap<>();

    static {
        // 视频类型
        fileCategoryMap.put("mp4", (byte) 1);
        fileCategoryMap.put("avi", (byte) 1);
        fileCategoryMap.put("mov", (byte) 1);
        fileCategoryMap.put("wmv", (byte) 1);
        // 音频类型
        fileCategoryMap.put("mp3", (byte) 2);
        fileCategoryMap.put("wav", (byte) 2);
        fileCategoryMap.put("aac", (byte) 2);
        // 图片类型
        fileCategoryMap.put("jpg", (byte) 3);
        fileCategoryMap.put("jpeg", (byte) 3);
        fileCategoryMap.put("png", (byte) 3);
        fileCategoryMap.put("gif", (byte) 3);
        // 文档类型
        fileCategoryMap.put("pdf", (byte) 4);
        fileCategoryMap.put("doc", (byte) 4);
        fileCategoryMap.put("docx", (byte) 4);
        fileCategoryMap.put("xls", (byte) 4);
        fileCategoryMap.put("xlsx", (byte) 4);
        fileCategoryMap.put("txt", (byte) 4);
        fileCategoryMap.put("java", (byte) 4);
        fileCategoryMap.put("py", (byte) 4);
        fileCategoryMap.put("js", (byte) 4);
        fileCategoryMap.put("cpp", (byte) 4);
        // 压缩文件等其他类型
        fileCategoryMap.put("zip", (byte) 5);
        fileCategoryMap.put("rar", (byte) 5);
        fileCategoryMap.put("7z", (byte) 5);
        // 添加未明确列出的扩展名到其他类别
        // 可以继续添加更多类型
    }

    public static Byte getFileCategory(String filename) {
        // 获取文件扩展名
        String extension = "";
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            extension = filename.substring(i + 1).toLowerCase();
        }

        // 根据扩展名返回文件类型
        return fileCategoryMap.getOrDefault(extension, (byte) 5); // 默认为'其他'
    }
}
