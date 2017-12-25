package com.github.ddth.cacheadapter.qnd;

import com.github.ddth.cacheadapter.ICompressor;
import com.github.ddth.cacheadapter.utils.compressor.JdkDeflateCompressor;

public class QndCompressorJdkDeflate {

    public static void main(String[] args) throws Exception {
        String orgData = "ddth-cache-adaptor - Nguyễn Bá Thành";
        ICompressor compressor = JdkDeflateCompressor.instance;
        byte[] buff = compressor.compress(orgData.getBytes("UTF-8"));
        String data = new String(compressor.decompress(buff));

        System.out.println(orgData);
        System.out.println(data);
    }

}
