/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.springfaces.mvc.render;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;

import org.springframework.springfaces.util.HexString;
import org.springframework.util.Assert;

/**
 * {@link FacesViewStateHandler} that stores {@link ViewArtifact} data as an encrypted hidden HTML field. A unique
 * encryption key is generated for each HTTP session.
 * @author Phillip Webb
 */
public class ClientFacesViewStateHandler implements FacesViewStateHandler {

	private static final String ID = "org.springframework.springfaces.id";

	private static final String SECRET_KEY = ClientFacesViewStateHandler.class.getName() + ".SECRET_KEY";

	public void write(FacesContext facesContext, ViewArtifact viewState) throws IOException {
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
		ResponseWriter writer = facesContext.getResponseWriter();
		String value = encrypt(request, viewState.toString());
		writeHiddenInput(writer, ID, value);
	}

	private void writeHiddenInput(ResponseWriter writer, String id, String value) throws IOException {
		writer.write("<input type=\"hidden\" name=\"");
		writer.write(id);
		writer.write("\" id=\"");
		writer.write(id);
		writer.write("\" value=\"");
		writer.write(value);
		writer.write("\"\\>");
	}

	public ViewArtifact read(HttpServletRequest request) throws IOException {
		String id = request.getParameter(ID);
		if (id == null) {
			return null;
		}
		id = decrypt(request, id);
		return new ViewArtifact(id);
	}

	private String encrypt(HttpServletRequest request, String value) {
		try {
			byte[] bytes = value.getBytes();
			SecretKey secretKey = getSecretKey(request);
			byte[] initializationVector = createRandomInitializationVector();
			Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(initializationVector));
			byte[] mac = getMac(secretKey).doFinal(bytes);
			return HexString.toString(initializationVector) + HexString.toString(cipher.update(mac))
					+ HexString.toString(cipher.doFinal(bytes));
		} catch (Exception e) {
			throw new IllegalStateException("Unable to encrypt input value", e);
		}
	}

	private byte[] createRandomInitializationVector() {
		SecureRandom rand = new SecureRandom();
		byte[] iv = new byte[16];
		rand.nextBytes(iv);
		return iv;
	}

	private String decrypt(HttpServletRequest request, String value) {
		try {
			byte[] bytes = HexString.toBytes(value);
			SecretKey secretKey = getSecretKey(request);
			Cipher cipher = getCipher(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(bytes, 0, 16));
			byte[] decrypted = cipher.doFinal(bytes, 16, bytes.length - 16);
			decrypted = removeAndVerifyMac(secretKey, decrypted);
			return new String(decrypted);
		} catch (Exception e) {
			throw new IllegalStateException("Unable to decrypt input value", e);
		}
	}

	private byte[] removeAndVerifyMac(SecretKey secretKey, byte[] bytes) throws Exception {
		Mac mac = getMac(secretKey);
		mac.update(bytes, 32, bytes.length - 32);
		byte[] expected = mac.doFinal();
		for (int i = 0; i < expected.length; i++) {
			Assert.state(bytes[i] == expected[i], "MAC does not match");
		}
		byte[] rtn = new byte[bytes.length - expected.length];
		System.arraycopy(bytes, 32, rtn, 0, rtn.length);
		return rtn;
	}

	private SecretKey getSecretKey(HttpServletRequest request) throws Exception {
		SecretKey secretKey = (SecretKey) request.getSession().getAttribute(SECRET_KEY);
		if (secretKey == null) {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128);
			secretKey = keyGenerator.generateKey();
			request.getSession().setAttribute(SECRET_KEY, secretKey);
		}
		return secretKey;
	}

	private Cipher getCipher(int mode, SecretKey secretKey, AlgorithmParameterSpec parameters) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
		cipher.init(mode, secretKeySpec, parameters);
		return cipher;
	}

	private Mac getMac(SecretKey secretKey) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(secretKey);
		return mac;
	}
}
