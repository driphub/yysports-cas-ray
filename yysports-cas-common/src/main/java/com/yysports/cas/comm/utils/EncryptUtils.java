/**
 * 
 */
package com.yysports.cas.comm.utils;


import com.yysports.cas.comm.dto.UserSessionBean;
import lombok.extern.slf4j.Slf4j;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Objects;

/**
 * @author RAY
 *
 */
@Slf4j
public class EncryptUtils {

	/**
	 * AES加密鑰
	 */
	private static final String ENCRYPT_KEY = "3Q8ZiWKMMhPbZou7apNYsV";
	/**
	 * 密钥算法
	 */
	private static final String ALGORITHM = "AES";
	/**
	 * 加解密算法/工作模式/填充方式
	 */
	private static final String ALGORITHM_STR = "AES/ECB/PKCS5Padding";

	private static final String FACTORY_KEY = "PBKDF2WithHmacSHA256";

	/**
	 * 加密傳入的字串
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String encryptString(String str) throws Exception {
		SecretKeyFactory factory = SecretKeyFactory.getInstance(FACTORY_KEY);

		KeySpec spec = new PBEKeySpec(ENCRYPT_KEY.toCharArray(), ENCRYPT_KEY.getBytes(), 4096,
				256);

		SecretKey aesKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(),
				ALGORITHM);
		Cipher cipher = Cipher.getInstance(ALGORITHM_STR);

		// encrypt the text
		cipher.init(Cipher.ENCRYPT_MODE, aesKey);
		byte[] encrypted = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));

		return Base64.getEncoder().encodeToString(encrypted);
	}

	public static Boolean verifySignature(String input, String signature) {
		try {
			String encrypt = encryptString(input);
			if (encrypt == null) {
				return false;
			}
			return Objects.equals(encrypt, signature);
		} catch (Exception ex) {

			log.error("EncryptUtils.verifySignature cause exception!", ex);
			return false;
		}
	}

	public static void main(String[] args) {
		UserSessionBean bean = new UserSessionBean();
		bean.setUserData("ray.huang");
		bean.setStamp(System.currentTimeMillis());
		bean.setId("YYYY");
		try {

			System.out.println(bean.getSingature());

			System.out.println(verifySignature(bean.getJoingtString(), bean.getSingature()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
