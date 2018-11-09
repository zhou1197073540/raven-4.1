package com.riddler.usr.des;

import com.yunpian.sdk.util.DesUtil;

import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DesNoViUtil {

    private static AlgorithmParameterSpec iv = null;// 加密算法的参数接口，IvParameterSpec是它的一个实现
    private static SecretKey key = null;
    private final static String SECRET_KEY="41981651";
    private final static String SECRET_IV="64896526";

    public static void main(String[] args) throws Exception {
        DesNoViUtil tools = new DesNoViUtil();
        String data = "zhouchenxi";
        System.out.println("加密:" + tools.encode(data));
        String data1 = tools.encode(data);
        System.out.println("解密:" + tools.decode("C6wq3h4s0CA="));
    }

    public DesNoViUtil() throws Exception {
        byte[] DESkey = SECRET_KEY.getBytes();
        byte[] DESIV = SECRET_IV.getBytes();
        DESKeySpec keySpec = new DESKeySpec(DESkey);// 设置密钥参数
        iv = new IvParameterSpec(DESIV);// 设置向量
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// 获得密钥工厂
        key = keyFactory.generateSecret(keySpec);// 得到密钥对象
    }

    /**
     * 加密
     *
     * @param data 待加密的数据
     * @return 加密后的数据
     * @throws Exception
     */
    public String encode(String data) throws Exception {
        Cipher enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");// 得到加密对象Cipher
        enCipher.init(Cipher.ENCRYPT_MODE, key, iv);// 设置工作模式为加密模式，给出密钥和向量
        byte[] pasByte = enCipher.doFinal(data.getBytes("utf-8"));
        //BASE64Encoder base64Encoder = new BASE64Encoder();
        return Base64.getEncoder().encodeToString(pasByte);
        //return base64Encoder.encode(pasByte);
    }

    /**
     * 解密
     *
     * @param data 解密前的数据
     * @return 解密后的数据
     * @throws Exception
     */
    public String decode(String data) throws Exception {
        Cipher deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        deCipher.init(Cipher.DECRYPT_MODE, key, iv);
        //BASE64Decoder base64Decoder = new BASE64Decoder();
        //byte[] pasByte = deCipher.doFinal(base64Decoder.decodeBuffer(data));
        byte[] pasByte = deCipher.doFinal(Base64.getDecoder().decode(data));
        return new String(pasByte, "UTF-8");
    }
}
