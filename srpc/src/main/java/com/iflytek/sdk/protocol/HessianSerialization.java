package com.iflytek.sdk.protocol;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.google.gson.Gson;
import com.iflytek.sdk.util.Logger;
import io.netty.buffer.ByteBuf;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * <br/>==========================

 * <br/>==========================
 */
public class HessianSerialization implements Serialize{

    @Override
    public void serialize(Object obj, ByteBuf byteBuf) {
        Gson gson = new Gson();
        System.out.println("序列化:"+gson.toJson(obj));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(baos);
        try {
            output.startMessage();
            output.writeObject(obj);
            output.flush();
            output.completeMessage();
            output.getBytesOutputStream().flush();
            output.close();

            byteBuf.writeBytes(baos.toByteArray());
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                Logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object deserialize(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Hessian2Input input = new Hessian2Input(bais);
        try {
            input.startMessage();
            Object object = input.readObject();
            input.completeMessage();
            return object;
        } catch (IOException e) {
            Logger.error(e.getMessage(), e);
        } finally {
            try {
                input.close();
                bais.close();
            } catch (IOException e) {
                Logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    public void serialize(Object obj, ByteBuf bytebuf, int size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
        Hessian2Output output = new Hessian2Output(baos);
        try {
            output.startMessage();
            output.writeObject(obj);
            output.flush();
            output.completeMessage();
            output.getBytesOutputStream().flush();
            output.close();
            bytebuf.writeBytes(baos.toByteArray());
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                Logger.error(e.getMessage(), e);
            }
        }
    }

}
