package com.tipray.util;

import com.tipray.constant.KeyStoreTypeConst;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 自定义证书信任管理器
 *
 * @author chenlong
 * @version 1.0 2018-07-18
 */
public class MyX509TrustManager implements X509TrustManager {

    /*
     * The default PKIX X509TrustManager9.  Decisions are delegated
     * to it, and a fall back to the logic in this class is performed
     * if the default X509TrustManager does not trust it.
     */
    X509TrustManager pkixTrustManager;

    MyX509TrustManager() throws Exception {
        // create a "default" JSSE X509TrustManager.

        KeyStore keyStore = KeyStore.getInstance(KeyStoreTypeConst.PKCS12);
        keyStore.load(this.getClass().getClassLoader().getResourceAsStream("pltone.pk12"), "".toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        TrustManager[] tms = tmf.getTrustManagers();

        /*
         * Iterate over the returned trust managers, looking
         * for an instance of X509TrustManager.  If found,
         * use that as the default trust manager.
         */
        for (int i = 0; i < tms.length; i++) {
            if (tms[i] instanceof X509TrustManager) {
                pkixTrustManager = (X509TrustManager) tms[i];
                return;
            }
        }

        /*
         * Find some other way to initialize, or else the
         * constructor fails.
         */
        throw new Exception("Couldn't initialize");
    }

    /*
     * Delegate to the default trust manager.
     */
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            pkixTrustManager.checkClientTrusted(chain, authType);
        } catch (CertificateException excep) {
            // do any special handling here, or rethrow exception.
        }
    }

    /*
     * Delegate to the default trust manager.
     */
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            pkixTrustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException excep) {
            /*
             * Possibly pop up a dialog box asking whether to trust the
             * cert chain.
             */
        }
    }

    /*
     * Merely pass this through.
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return pkixTrustManager.getAcceptedIssuers();
    }
}
