package com.dftaihua.dfth_threeinone.network.requestbody;

/*    *****      *     *        
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/6/13 10:45
*/

import com.dfth.sdk.Others.Utils.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class UserIconFileUploadRequestBody extends RequestBody {
    private static final String CONTENT_TYPE = "application/octet-stream";
    protected File mFile;

    public UserIconFileUploadRequestBody(File file) {
        mFile = file;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse(CONTENT_TYPE);
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        RandomAccessFile file = new RandomAccessFile(mFile,"rw");
        byte[] buffer = new byte[1024 * 1024];
        long length = 0;
        while (file.length() > length){
            long s = file.read(buffer);
            sink.write(buffer,0,(int)s);
            length += s;
        }
        IOUtils.closeSlient(file);
    }
}
