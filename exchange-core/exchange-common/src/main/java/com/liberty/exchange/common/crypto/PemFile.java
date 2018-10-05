package com.liberty.exchange.common.crypto;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 5:01
 * Description: PemFile
 */
class PemFile {
    private PemObject pemObject;

    PemFile(InputStream filename) throws IOException {
        try (PemReader pemReader = new PemReader(new InputStreamReader(filename))) {
            this.pemObject = pemReader.readPemObject();
        }
    }

    PemObject getPemObject() {
        return pemObject;
    }
}
