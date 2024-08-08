package org.chad.cloudvault.common.constant;

public class FileConstant {
    public static final String TMP_PATH = "/tmp/";

    public static final String PAN_PATH = "/pan/";


    public static final Integer UPLOAD_FAILED = 0;
    public static final Integer UPLOAD_SUCCESS = 1;
    public static final Integer UPLOAD_CONTINUE = 2;

    public static final Byte FILE_FOLDER_TYPE_DIR = 1;
    public static final Byte FILE_FOLDER_TYPE_FILE = 0;

    public static final Byte FILE_DEL_FLAG_NORMAL = 0;
    public static final Byte FILE_DEL_FLAG_DELETED = 1;
    public static final Byte FILE_DEL_FLAG_RECOVERY = 2;
}
