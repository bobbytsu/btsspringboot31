package com.juaracoding.pcmspringboot31.security;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.AESLightEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;

public class Crypto {

    private static final String defaultKey = "1f4abb472d43bedfe14be294f808aa3d56c89511be570a0a7fefef1bcffa578d";

//    public static String getKey(){
//        return defaultKey;
//    }
    public static String performEncrypt(String keyText, String plainText) {
        try{
            byte[] key = Hex.decode(keyText.getBytes());
            byte[] ptBytes = plainText.getBytes();
            BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESLightEngine()));
            cipher.init(true, new KeyParameter(key));
            byte[] rv = new byte[cipher.getOutputSize(ptBytes.length)];
            int oLen = cipher.processBytes(ptBytes, 0, ptBytes.length, rv, 0);
            cipher.doFinal(rv, oLen);
            return new String(Hex.encode(rv));
        } catch(Exception e) {
            return "Error";
        }
    }

    public static String performEncrypt(String cryptoText) {
        return performEncrypt(defaultKey, cryptoText);
    }

    public static String performDecrypt(String keyText, String cryptoText) {
        try {
            byte[] key = Hex.decode(keyText.getBytes());
            byte[] cipherText = Hex.decode(cryptoText.getBytes());
            BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESLightEngine()));
            cipher.init(false, new KeyParameter(key));
            byte[] rv = new byte[cipher.getOutputSize(cipherText.length)];
            int oLen = cipher.processBytes(cipherText, 0, cipherText.length, rv, 0);
            cipher.doFinal(rv, oLen);
            return new String(rv).trim();
        } catch(Exception e) {
            return "Error";
        }
    }

    public static String performDecrypt(String cryptoText) {
        return performDecrypt(defaultKey, cryptoText);
    }

    public static void main(String[] args) {
        String strToEncrypt = "jdbc:sqlserver://host.docker.internal;databaseName=BEB31;schema=projectz;trustServerCertificate=true";//put text to encrypt in here
        System.out.println("Encryption Result : "+performEncrypt(strToEncrypt));
//        e84b3efac0261a03fff0c5182c725421fca147c61a2530b148ed6cbcabf727df4c99eb129046ef3ec6d7d3eafdfb47413abcc8cd85318145d99908aafe7f4b0db594c3a51aa6675b6af17f83ba3b3d59bace672d05bd6d91a2ac30bf56bab411
//        e84b3efac0261a03fff0c5182c725421fca147c61a2530b148ed6cbcabf727df4c99eb129046ef3ec6d7d3eafdfb47413abcc8cd85318145d99908aafe7f4b0db594c3a51aa6675b6af17f83ba3b3d59bace672d05bd6d91a2ac30bf56bab411
//        316c88341c7e9dcabeec24b37951436f9627a8c27db32c966bd388124006c0004d1a2fec8a916a31db5adb5d1d46bcfcfbe1958d21e0057296aebe64bc43fb95428abfa97d30a4ad0dc4a029c5bdbd7b82ea3531d3c39ce7b288e0dea6afeddc
//        jdbc:sqlserver://java-be-1:3377;databaseName=BEB24;schema=dbproject;trustServerCertificate=true
        String strToDecrypt = "e84b3efac0261a03fff0c5182c725421821c9ad8b5394f3f50cb52a5da2d5a6c803e90abf2a9a233a8a0ec9585263d093ec9c6d32473258e41931ed207245cf04accb672202b6f0530be373599d87ea5a2a693af73b4b70e94930365e551ee82ff484f28977b9ef1dc0a15ec18cf2ebd";//put text to decrypt in here
        System.out.println("Decryption Result : "+performDecrypt(strToDecrypt));
    }
}