package com.dftaihua.dfth_threeinone.network.requestbody;


import com.dfth.sdk.network.requestBody.BaseRequestBody;

public class UserIconUploadRequestBody extends BaseRequestBody {
    private static final String CONTENT_TYPE = "application/octet-stream";
    public String fileName;
    public Long fileSize;

    public UserIconUploadRequestBody(String fileName,long fileSize) {
        super(null);
        this.fileName = fileName;
        this.fileSize = fileSize;
    }
//    @Override
//    public MediaType contentType() {
//        return MediaType.parse(CONTENT_TYPE);
//    }
//
//    @Override
//    public void writeTo(BufferedSink sink) throws IOException {
//        RandomAccessFile file = new RandomAccessFile(mFile,"rw");
//        byte[] buffer = new byte[1024 * 1024];
//        long length = 0;
//        while (file.length() > length){
//            long s = file.read(buffer);
//            sink.write(buffer,0,(int)s);
//            length += s;
//        }
//        IOUtils.closeSlient(file);
//    }
}