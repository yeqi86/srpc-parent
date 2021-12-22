package com.iflytek.sdk.dto;

import java.io.Serializable;

/**
 * @author： liwh
 * @Date: 2016/11/17.
 * @Description：
 */

public class ResponseFile implements Serializable {

    private static final long serialVersionUID = -3432433811350669751L;

    public ResponseFile(){

    }





    /**
     * 开始 读取点
     */
    private long start;
    /**
     * 文件的 MD5值
     */
    private String fileMd5;
    /**
     * 文件下载地址
     */
    private String fileUrl;
    /**
     * 上传是否结束
     */
    private boolean fileEnd;
    /**
     * 进度
     */
    private int progress ;

//    private File file; //文件
//
//    private String file_name;// 文件名
//
//    private String file_type;  //文件类型


    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public boolean isFileEnd() {
        return fileEnd;
    }

    public void setFileEnd(boolean fileEnd) {
        this.fileEnd = fileEnd;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
