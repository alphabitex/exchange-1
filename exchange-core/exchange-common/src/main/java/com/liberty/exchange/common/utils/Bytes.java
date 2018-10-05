package com.liberty.exchange.common.utils;

/**
 * Bytes Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/8/29
 * Time: 20:08
 * Description: Bytes
 */
class Bytes {

    static String substring(String src, int start_idx, int end_idx){
        byte[] b = src.getBytes();
        StringBuilder tgt = new StringBuilder();
        for(int i=start_idx; i<=end_idx; i++){
            tgt.append((char) b[i]);
        }
        return tgt.toString();
    }
}
