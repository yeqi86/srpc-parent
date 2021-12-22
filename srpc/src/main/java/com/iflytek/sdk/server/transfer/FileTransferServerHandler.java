package com.iflytek.sdk.server.transfer;

import com.google.gson.Gson;
import com.iflytek.sdk.dto.RequestFile;
import com.iflytek.sdk.dto.ResponseFile;
import com.iflytek.sdk.util.PropertiesUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author： liwh
 * @Date: 2016/11/17.
 * @Description：
 */
public class FileTransferServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(FileTransferServerHandler.class);

    private volatile int byteRead;
    private volatile long start = 0;

    /**
     * 文件默认存储地址
     */
    private String file_dir = PropertiesUtil.getString("file_write_path", "d://data");

    private RandomAccessFile randomAccessFile;
    private File file;
    private long fileSize = -1;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Gson gson = new Gson();
        System.out.println(gson.toJson(msg));
        if (msg instanceof RequestFile) {
            RequestFile ef = (RequestFile) msg;
            byte[] bytes = ef.getBytes();
            byteRead = ef.getEndPos();

            String md5 = ef.getFile_md5();//文件MD5值

            if (start == 0) { //只有在文件开始传的时候才进入 这样就减少了对象创建 和可能出现的一些错误
                String path = file_dir + File.separator + md5 + ef.getFile_type();
                file = new File(path);
                fileSize = ef.getFile_size();

                //根据 MD5 和 文件类型 来确定是否存在这样的文件 如果存在就 秒传
                if (file.exists()) {
                    start = file.length() + 1;
                    if (start >= fileSize) {
                        ResponseFile responseFile = new ResponseFile();
                        responseFile.setStart(start);
                        responseFile.setFileMd5(md5);
                        responseFile.setFileUrl(getFilePath());
                        responseFile.setProgress(100);
                        responseFile.setFileEnd(true);
                        ctx.writeAndFlush(responseFile);
                        ctx.close();
                        return;
                    }
                    int progress = (int) ((start * 100) / fileSize);
                    log.info("file exists:" + ef.getFile_name() + "--" + ef.getFile_md5() + "[" + ctx.channel().remoteAddress() + "]");
                    ResponseFile responseFile = new ResponseFile();
                    responseFile.setStart(start);
                    responseFile.setFileMd5(md5);
                    responseFile.setProgress(progress);
                    responseFile.setFileUrl(getFilePath());
                    responseFile.setFileEnd(false);
                    //          SecureModel responseFile = new SecureModel();
                    ctx.writeAndFlush(responseFile);
                    //      ctx.close();

                    //TODO 这里可以做 断点续传 ，读取当前已经存在文件的总长度  和 传输过来的文件总长度对比 如果不一致，则认为本地文件没有传完毕 则续传
                    // 不过这步骤必须做好安全之后来做，否则可能会出现 文件被恶意加入内容
                    return;
                }
            }
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(start);
            randomAccessFile.write(bytes);
            start = start + byteRead;

            if (byteRead > 0 && (start < fileSize && fileSize != -1)) {
                //log.info((start*100)/fileSize+"::::" +fileSize+"::: " +(start*100));
                // ResponseFile responseFile = new ResponseFile(start,md5,(start*100)/fileSize);
                ResponseFile responseFile = new ResponseFile();
                responseFile.setStart(start);
                responseFile.setFileMd5(md5);
                responseFile.setFileEnd(false);
                responseFile.setProgress((int) ((start * 100) / fileSize));
                responseFile.setFileUrl(getFilePath());
                ctx.writeAndFlush(responseFile);
                return;
                //   ctx.close();
            } else {
                log.info("create file success:" + ef.getFile_name() + "--" + ef.getFile_md5() + "[" + ctx.channel().remoteAddress() + "]");
                ResponseFile responseFile = new ResponseFile();
                responseFile.setFileMd5(md5);
                responseFile.setProgress(100);
                responseFile.setStart(start);
                responseFile.setFileEnd(true);
                responseFile.setFileUrl(getFilePath());
                ctx.writeAndFlush(responseFile);
                //    ctx.close();
                randomAccessFile.close();
                file = null;
                fileSize = -1;
                randomAccessFile = null;
                // ctx.close();  //这步让客户端来做
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();

        //当连接断开的时候 关闭未关闭的文件流
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ctx.close();
    }

    /**
     * 获取 文件路径
     *
     * @return
     */
    private String getFilePath() {
        if (file != null) {
            return PropertiesUtil.getString("download_root_path") + "/" + file.getName();
        } else {
            return null;
        }
    }

}
