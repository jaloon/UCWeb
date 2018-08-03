package com.tipray.constant;

/**
 * KeyStore(密钥和证书的存储设施)类型
 *
 * @author chenlong
 * @version 1.0 2018-06-19
 */
public class KeyStoreTypeConst {
    /**
     * The proprietary keystore implementation provided by the SunJCE provider.
     */
    public static final String JCEKS = "jceks";
    /**
     * The proprietary keystore implementation provided by the SUN provider.
     */
    public static final String JKS = "jks";
    /**
     * A domain keystore is a collection of keystores presented as a single logical keystore.
     * It is specified by configuration data whose syntax is described in DomainLoadStoreParameter.
     */
    public static final String DKS = "dks";
    /**
     * A keystore backed by a PKCS #11 token.
     */
    public static final String PKCS11 = "pkcs11";
    /**
     * The transfer syntax for personal identity information as defined in PKCS #12.
     */
    public static final String PKCS12 = "pkcs12";
}
