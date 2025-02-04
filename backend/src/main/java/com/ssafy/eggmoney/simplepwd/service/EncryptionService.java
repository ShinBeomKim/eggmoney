package com.ssafy.eggmoney.simplepwd.service;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EncryptionService {
    private PublicKey publicKey;
    private PrivateKey privateKey;
//    private final String secretKey;
//
    @PostConstruct
    public void initKeys() throws Exception{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    public PublicKey getPublicKey(){
        return publicKey;
    }

    public PrivateKey getPrivateKey(){
        return privateKey;
    }
//    public EncryptionService(@Value("${secret.aesKey}") String secretKey) {
//        this.secretKey = secretKey;
//    }

    private SecretKey generateKey() throws Exception{
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        return keyGen.generateKey();
    }

    public String encryptImage(byte[] imageBytes) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            //cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//            SecretKeySpec keySpec =new SecretKeySpec(secretKey.getBytes(), "AES");
//            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedImage = cipher.doFinal(imageBytes);
            if (encryptedImage == null || encryptedImage.length == 0) {
                throw new RuntimeException("이미지 암호화 실패: 암호화된 데이터가 유효하지 않습니다.");
            }
            return Base64.getEncoder().encodeToString(encryptedImage);
        } catch (Exception e) {
            throw new Exception("이미지 암호화 중 오류가 발생했습니다.", e);
        }
    }
    public List<Integer> decryptPin(String encryptedPin, HttpSession session) {

        List<Integer> shuffledPinPad = (List<Integer>) session.getAttribute("shuffledPinPad");
        if(shuffledPinPad==null){
            throw new IllegalArgumentException("세션이 만료되었거나 섞인 카드 정보가 없습니다.");
        }

        List<Integer> pressedIndexes = Arrays.stream(encryptedPin.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        List<Integer> decryptedNumbers = new ArrayList<>();
        for(Integer index: pressedIndexes){
            // 인덱스가 유효한 범위 내에 있는지 확인
            if (index >= 0 && index < shuffledPinPad.size()) {
                decryptedNumbers.add(shuffledPinPad.get(index)); // 유효한 인덱스일 때만 처리
            } else {
                throw new IndexOutOfBoundsException("인덱스 " + index + "가 배열의 길이를 벗어났습니다.");
            }
        }
        return decryptedNumbers;
    }
}
// private final SecretKey secretKey;
//
//    public EncryptionService() throws Exception{
//        this.secretKey = generateKey();
//    }
//
//    private SecretKey generateKey() throws Exception{
//        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
//        keyGen.init(128);
//        return keyGen.generateKey();
//    }
//
//    public String encryptImage(byte[] imageBytes) throws Exception {
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//        byte[] encryptedImage = cipher.doFinal(imageBytes);
//        return Base64.getEncoder().encodeToString(encryptedImage);
//    }