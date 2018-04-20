package org.sanchain.client.api.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.hadoop.hbase.util.Bytes;
import org.bouncycastle.crypto.digests.MD5Digest;

/**
 * Created by zara on 17/5/26.
 */
public class Md5Utils {
    public static String md5Sign(String data){
        MD5Digest digest = new MD5Digest();
        byte[] resBuf = new byte[digest.getDigestSize()];
        byte[] challenge = Bytes.toBytes(data);
        digest.update(challenge, 0, challenge.length);
        digest.doFinal(resBuf, 0);
        return Hex.encodeHexString(resBuf);
    }
}
