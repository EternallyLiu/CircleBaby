/**
 * LICENSE AND TRADEMARK NOTICES
 * <p/>
 * Except where noted, sample source code written by Motorola Mobility Inc. and
 * provided to you is licensed as described below.
 * <p/>
 * Copyright (c) 2012, Motorola, Inc. All rights reserved except as otherwise
 * explicitly indicated.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p/>
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
 * - Neither the name of Motorola, Inc. nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * <p/>
 * Other source code displayed may be licensed under Apache License, Version 2.
 * <p/>
 * Copyright ¬© 2012, Android Open Source Project. All rights reserved unless
 * otherwise explicitly indicated.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0.
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

// Please refer to the accompanying article at
// http://developer.motorola.com/docs/using_the_advanced_encryption_standard_in_android/

package cn.timeface.circle.baby.support.utils.encode;

// A tutorial guide to using AES encryption in Android
// First we generate a 256 bit secret key; then we use that secret key to AES
// encrypt a plaintext message.
// Finally we decrypt the ciphertext to get our original message back.
// We don't keep a copy of the secret key - we generate the secret key whenever
// it is needed,
// so we must remember all the parameters needed to generate it -
// the salt, the IV, the human-friendly passphrase, all the algorithms and
// parameters to those algorithms.
// Peter van der Linden, April 15 2012

import android.util.Log;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    private final String KEY_GENERATION_ALG = "PBKDF2WithHmacSHA1";

    private final int HASH_ITERATIONS = 10000;
    private final int KEY_LENGTH = 256;

    private final String KEY = "timeface2014__time2014$#@!";

    // private char[] humanPassphrase = { 'P', 'e', 'r', ' ', 'v', 'a', 'l',
    // 'l',
    // 'u', 'm', ' ', 'd', 'u', 'c', 'e', 's', ' ', 'L', 'a', 'b', 'a',
    // 'n', 't' };
    private char[] humanPassphrase = KEY.toCharArray();

    // must save this for next time we want the key
    private byte[] salt = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0xA, 0xB, 0xC, 0xD,
            0xE, 0xF};

    private byte[] store = {-68, 112, 16, -14, -107, 85, -74, -65, 96, -64,
            -55, -27, -83, -29, 26, 14, -79, -111, 125, 90, -32, 35, -27, -117,
            -82, 3, 116, 9, -63, -74, -115, -19};

    private PBEKeySpec myKeyspec = new PBEKeySpec(humanPassphrase, salt,
            HASH_ITERATIONS, KEY_LENGTH);
    private final String CIPHERMODEPADDING = "AES/CBC/PKCS7Padding";

    private SecretKeyFactory keyfactory = null;
    private SecretKey sk = null;
    private byte[] iv = {0xA, 1, 0xB, 5, 4, 0xF, 7, 9, 0x17, 3, 1, 6, 8, 0xC,
            0xD, 91};

    private IvParameterSpec IV;

    public AES() {
        try {
            keyfactory = SecretKeyFactory.getInstance(KEY_GENERATION_ALG);
            // store = getKeyStore();
            sk = new SecretKeySpec(store, CIPHERMODEPADDING);
        } catch (NoSuchAlgorithmException nsae) {
            Log.e("AESdemo",
                    "no key factory support for PBEWITHSHAANDTWOFISH-CBC");
        }

        // This is our secret key. We could just save this to a file instead of
        // regenerating it
        // each time it is needed. But that file cannot be on the device (too
        // insecure). It could
        // be secure if we kept it on a server accessible through https.
        IV = new IvParameterSpec(iv);
    }

    /**
     * 产生密钥，比较耗时
     *
     * @return
     */
    public byte[] getKeyStore() {
        try {
            byte[] keystore = keyfactory.generateSecret(myKeyspec).getEncoded();
            System.out.println(showByteArray(keystore));
            return keystore;
        } catch (InvalidKeySpecException e) {
            Log.e("AESdemo", "invalid key spec for PBEWITHSHAANDTWOFISH-CBC");
        }
        return null;
    }

    public String encrypt(byte[] plaintext) {
        byte[] ciphertext = encrypt(CIPHERMODEPADDING, sk, IV, plaintext);
        String base64_ciphertext = Base64Encoder.encode(ciphertext);
        return base64_ciphertext;
    }

    public String decrypt(String ciphertext_base64) {
        byte[] s = Base64Decoder.decodeToBytes(ciphertext_base64);
        String decrypted = new String(decrypt(CIPHERMODEPADDING, sk, IV, s));
        return decrypted;
    }

    private byte[] encrypt(String cmp, SecretKey sk, IvParameterSpec IV,
                           byte[] msg) {
        try {
            Cipher c = Cipher.getInstance(cmp);
            c.init(Cipher.ENCRYPT_MODE, sk, IV);
            return c.doFinal(msg);
        } catch (NoSuchAlgorithmException nsae) {
            Log.e("AESdemo", "no cipher getinstance support for " + cmp);
        } catch (NoSuchPaddingException nspe) {
            Log.e("AESdemo", "no cipher getinstance support for padding " + cmp);
        } catch (InvalidKeyException e) {
            Log.e("AESdemo", "invalid key exception");
        } catch (InvalidAlgorithmParameterException e) {
            Log.e("AESdemo", "invalid algorithm parameter exception");
        } catch (IllegalBlockSizeException e) {
            Log.e("AESdemo", "illegal block size exception");
        } catch (BadPaddingException e) {
            Log.e("AESdemo", "bad padding exception");
        }
        return null;
    }

    private byte[] decrypt(String cmp, SecretKey sk, IvParameterSpec IV,
                           byte[] ciphertext) {
        try {
            Cipher c = Cipher.getInstance(cmp);
            c.init(Cipher.DECRYPT_MODE, sk, IV);
            return c.doFinal(ciphertext);
        } catch (NoSuchAlgorithmException nsae) {
            Log.e("AESdemo", "no cipher getinstance support for " + cmp);
        } catch (NoSuchPaddingException nspe) {
            Log.e("AESdemo", "no cipher getinstance support for padding " + cmp);
        } catch (InvalidKeyException e) {
            Log.e("AESdemo", "invalid key exception");
        } catch (InvalidAlgorithmParameterException e) {
            Log.e("AESdemo", "invalid algorithm parameter exception");
        } catch (IllegalBlockSizeException e) {
            Log.e("AESdemo", "illegal block size exception");
        } catch (BadPaddingException e) {
            Log.e("AESdemo", "bad padding exception");
            e.printStackTrace();
        }
        return null;
    }

    private String showByteArray(byte[] data) {
        if (null == data) {
            return null;
        }
        StringBuilder sb = new StringBuilder("{");
        for (byte b : data) {
            sb.append(b).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

}