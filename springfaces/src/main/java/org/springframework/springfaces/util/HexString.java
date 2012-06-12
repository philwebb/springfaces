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
package org.springframework.springfaces.util;

import java.io.Serializable;
import java.util.Arrays;

import org.springframework.util.Assert;

/**
 * A hexadecimal (base 16) string that can be {@link HexString#HexString(String) constructed} from a suitable Java
 * String. Use {@link #valueOf(byte[]) valueOf} to create a <tt>HexString</tt> instance from a byte array. Conversion
 * between native java types is also possible using the {@link #toString(byte[])}, {@link #toChars(byte[])} and
 * {@link #toBytes(String)} methods.
 * 
 * @author Phillip Webb
 */
public final class HexString implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

	private final byte[] bytes;

	/**
	 * Create a new {@link HexString} from the specified value.
	 * 
	 * @param hexString the value of the string, for example <tt>"00AABB"</tt>
	 * @throws HexFormatException if the string is not valid hexadecimal
	 * @see #valueOf(byte[])
	 */
	public HexString(String hexString) throws HexFormatException {
		Assert.notNull(hexString, "HexString must not be null");
		this.bytes = toBytes(hexString);
	}

	/**
	 * Private internal constructor
	 * 
	 * @param bytes the bytes of the hex string
	 * @see #valueOf(byte[])
	 */
	private HexString(byte[] bytes) {
		Assert.notNull(bytes, "Bytes must not be null");
		this.bytes = bytes;
	}

	/**
	 * Returns the bytes represented by this hexadecimal string, for example the <tt>HexString</tt> <tt>"0102"</tt>
	 * would return the bytes <tt>{0x01, 0x02}</tt>.
	 * 
	 * @return the bytes represented by this hex string
	 */
	public byte[] getBytes() {
		return this.bytes;
	}

	@Override
	public String toString() {
		return toString(this.bytes);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof HexString) {
			return Arrays.equals(this.bytes, ((HexString) obj).bytes);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.bytes);
	};

	/**
	 * Create a new {@link HexString} instance for the specified bytes.
	 * 
	 * @param bytes the bytes that the string will represent
	 * @return a new {@link HexString} instance
	 */
	public static HexString valueOf(byte[] bytes) {
		return new HexString(bytes);
	}

	/**
	 * Convert the specified bytes into a hexadecimal {@link String}.
	 * 
	 * @param bytes the bytes to convert
	 * @return a hexadecimal {@link String}
	 * @see #toString(byte[], int, int)
	 */
	public static String toString(byte[] bytes) {
		Assert.notNull(bytes, "Bytes must not be null");
		return toString(bytes, 0, bytes.length);
	}

	/**
	 * Convert the specified bytes into a hexadecimal {@link String}.
	 * 
	 * @param bytes the bytes to convert
	 * @param offset the offset in the byte array to start the conversion
	 * @param length the length of the array to consider
	 * @return a hexadecimal {@link String}
	 * @see #toString(byte[])
	 */
	public static String toString(byte[] bytes, int offset, int length) {
		Assert.notNull(bytes, "Bytes must not be null");
		return new String(toChars(bytes, offset, length));
	}

	/**
	 * Convert the specified bytes into a char array containing the hexadecimal value of the bytes.
	 * 
	 * @param bytes the bytes to convert
	 * @return a char array containing the hexadecimal value
	 * @see #toChars(byte[], int, int)
	 */
	public static char[] toChars(byte[] bytes) {
		Assert.notNull(bytes, "Bytes must not be null");
		return toChars(bytes, 0, bytes.length);
	}

	/**
	 * Convert the specified bytes into a char array containing the hexadecimal value of the bytes.
	 * 
	 * @param bytes the bytes to convert
	 * @param offset the offset in the byte array to start the conversion
	 * @param length the length of the array to consider
	 * @return a char array containing the hexadecimal value
	 * @see #toChars(byte[])
	 */
	public static char[] toChars(byte[] bytes, int offset, int length) {
		Assert.notNull(bytes, "Bytes must not be null");
		if (offset < 0 || offset > bytes.length) {
			throw new IndexOutOfBoundsException("Offset is outside of array bounds");
		}
		if (length < 0 || offset + length > bytes.length) {
			throw new IndexOutOfBoundsException("Length is outside of array bounds");
		}
		char[] chars = new char[length * 2];
		for (int i = 0; i < chars.length; i = i + 2) {
			byte b = bytes[(i / 2) + offset];
			chars[i] = HEX_CHARS[b >> 4 & 0xf];
			chars[i + 1] = HEX_CHARS[b & 0xf];
		}
		return chars;
	}

	/**
	 * Converts the specified hexadecimal {@link String} (for example <tt>"00AABB"</tt>) into a byte array.
	 * 
	 * @param hexString the hexadecimal string to convert
	 * @return a converted byte array
	 * @throws HexFormatException if the string is not valid hexadecimal
	 */
	public static byte[] toBytes(String hexString) throws HexFormatException {
		Assert.notNull(hexString, "HexString must not be null");
		if (hexString.length() % 2 != 0) {
			throw new HexFormatException("Hexadecimal strings must contain an even number of characters", hexString);
		}
		byte[] out = new byte[hexString.length() / 2];
		Arrays.fill(out, (byte) 0x00);
		for (int i = 0; i < hexString.length(); i++) {
			int digit = Character.digit(hexString.charAt(i), 16);
			if (digit == -1) {
				throw new HexFormatException("Illegal character '" + hexString.charAt(i)
						+ "' in hexadecimal string at position " + i, hexString, i);
			}
			out[i / 2] = (byte) ((out[i / 2] | digit << (i % 2 == 0 ? 4 : 0)) & 0xFF);
		}
		return out;
	}
}
