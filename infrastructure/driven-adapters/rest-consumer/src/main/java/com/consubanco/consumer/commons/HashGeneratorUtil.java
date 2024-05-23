package com.consubanco.consumer.commons;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@UtilityClass
public class HashGeneratorUtil {

    private static final String ALGORITHM = "SHA-256";
    private static final int HASH_SIZE = 64;
    private static final String PAD_STR = "5";
    private static final String FORMAT_HEX = "%032x";
    private static final int SIG_NUM = 1;

    public static String getHashDocument(final byte[] documentContent) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = md.digest(documentContent);
            String hashHex = String.format(FORMAT_HEX, new BigInteger(SIG_NUM, hash));
            return StringUtils.leftPad(hashHex, HASH_SIZE, PAD_STR);
        } catch (NoSuchAlgorithmException exception) {
            throw ExceptionFactory.buildTechnical(exception, null);
        }
    }

}
