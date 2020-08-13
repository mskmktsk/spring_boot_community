package com.osozos.community.util;

import org.jose4j.json.JsonUtil;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

import java.security.PrivateKey;
import java.util.UUID;

public class JWTUtil {

    private static final String KEY_ID = "b2ab1103c09d47179c6fa0c777535799";
    private static final String PUBLIC_KEY = "{\"kty\":\"RSA\",\"kid\":\"b2ab1103c09d47179c6fa0c777535799\",\"n\":\"p98YTFQX22zRNdf1iwPp9ushNDSxQ2u12eg7OIkk5CY-YYVwDE_7EpPjjbOr_Yne_jdY6xQaCJpa5FTKJ8PKlHZHxMU8v86cxGqZpubjSnPr3Hu8VuFz9zkDBMzIGymUFHQPK4fcDruSjIB_RPDZZ8PhKLxfHXIM_SESLgHmXDDi_bBveL-f2OXz9cr-FTvB0-weNLSN9P3cZIa2frr0FBmcTN939PUq3wqIwDoEJT_HXLLbre2m_MRENB9ue5hZ9Yt5uxYbmOo5mWykxgcPaDFIK9Nwd0JoxgLnkDcpE-tR9DyMpJtheyJvwCtH5niJNXEPom-PtA0pgdWxlVi-Fw\",\"e\":\"AQAB\"}";
    private static final String PRIVATE_KEY = "{\"kty\":\"RSA\",\"kid\":\"b2ab1103c09d47179c6fa0c777535799\",\"n\":\"p98YTFQX22zRNdf1iwPp9ushNDSxQ2u12eg7OIkk5CY-YYVwDE_7EpPjjbOr_Yne_jdY6xQaCJpa5FTKJ8PKlHZHxMU8v86cxGqZpubjSnPr3Hu8VuFz9zkDBMzIGymUFHQPK4fcDruSjIB_RPDZZ8PhKLxfHXIM_SESLgHmXDDi_bBveL-f2OXz9cr-FTvB0-weNLSN9P3cZIa2frr0FBmcTN939PUq3wqIwDoEJT_HXLLbre2m_MRENB9ue5hZ9Yt5uxYbmOo5mWykxgcPaDFIK9Nwd0JoxgLnkDcpE-tR9DyMpJtheyJvwCtH5niJNXEPom-PtA0pgdWxlVi-Fw\",\"e\":\"AQAB\",\"d\":\"UAc4IzM3yGCBACU7JpGwGQmQ4713AEg6Am8GxHYFA0sEFCOz_fqnG_0qzyPni1AN4a1rE26a1p0XVaDiWsCpM8uLfU_2HQ6Xh7-2Dm4lzxmiSJMof1atnR8txD0H7IMO2A1YKg1zqAZd3-NmSOx1CFXGYlAjG_uCG66HZy16r1nL5u3OKS8ln2eIQKOAeutaWtonMUP2aIYWgMgGk7swR-eol4DPZqGiwXDuT_tBbzu_GNnBsiRcxKHZoSKWkL_z9Gb7H-qHjTYyNZFOHCqy5ZONbuupo_5GMTOZj85AB5P9wnrsNWPG3x5EnbItxCbEMGiUcxl34g9I4P3RSzWhkQ\",\"p\":\"6Sg_RoVM3BUuYN6kw4w5cXNhTciAUQg1HVoViJitHUsJWxtrhSXe8nLegh8jJ6Gg-s73O6TO1Faq6PQTQwhVE6WlB2WOwzNu48BcEOlnlLvs3jq0oqcjzGTYPXeZlJvoogQHWndpK1Yi5eQQSV4xukO8sGYBnpjPpwfgLsBKQ6k\",\"q\":\"uFFu_OSOvXNkQzDhRsxD8cDGUUJVEK60H0UwBNuTeOAF6wzQo08IgkoSYZb5NFkZ3AsVWfJGh-mziabpQhyTchB0sgrBLG4vifZveRjM0K5tPl8Sly3mEmZQaD0tkDnNf5D1Ag9OHhiytx1YsKfJJRnU6TCp7dUufaoTYinIC78\",\"dp\":\"TnFFAJPebPkPVnXxlvy5Ysr_xKydRyXswMhLEF7Vd1KEfkt2XCCGpqkw5TRWnoHJaDhzg3SKRXQ3IhLj3ByUivyizh9D7baDK0rPbr0oJSkoowe7ODHlYGHZQqzXVeEyXjfexXgBQRxEt3WIaTawoksh03MuPk6JJlgG-5OkKKk\",\"dq\":\"hqI9Hw7YUPw7zXSj6TIFo047zllUh4YFXh4MsE-vjgidag8HS3LMdVcjzBMaXymCYR_bbcMxz89ebHr2QWtcLGJmyzJd3lVx9iWDUYJTAU-XzoplqvZE60W_EBU39fSy7a51uRZy6jsCOA2AnjFBFQ-kXN1GEsq4VeRomjiIvkM\",\"qi\":\"i1_-YDDE5GB-UYNqGR0uauBO6C2feS0oQMJ1-XWvtVd2D74YQjrta335McS__3AJkqYJ0anQwP9mF0ajhVDIwozY-KDZmGAEooqma4QFwbQj4AlJnS9B8A7f_RRkg8IZx7ziinvkO6R_QkCKz3inZC4dBCs3ecL7JJtE6n3O_k8\"}";
    private static final int ACCESS_EXPIRATION_SECOND = 60 * 5;

    /**
     * 获取对应 username 的 token
     * @param username 用户名
     * @return token
     */
    public static String createToken(String username) {
        JwtClaims claims = new JwtClaims();
        claims.setAudience("aud");
        claims.setSubject("sub");
        claims.setClaim("username", username);
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();
        // 到期时间
        NumericDate date = NumericDate.now();
        date.addSeconds(ACCESS_EXPIRATION_SECOND);
        claims.setExpirationTime(date);
        claims.setNotBeforeMinutesInThePast(1);

        // jws
        JsonWebSignature jws = new JsonWebSignature();
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
        jws.setKeyIdHeaderValue(KEY_ID);
        jws.setPayload(claims.toJson());

        try {
            PrivateKey privateKey = new RsaJsonWebKey(JsonUtil.parseJson(PRIVATE_KEY)).getPrivateKey();
            jws.setKey(privateKey);

            // token
            String token = jws.getCompactSerialization();
            return token;
        } catch (JoseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * jws 校验 token
     * @param token 唯一身份令牌
     * @return 用户名
     */
    public static Boolean verifyToken(String username, String token) {
        try {
            JwtConsumer consumer = new JwtConsumerBuilder()
                    .setRequireExpirationTime()
                    .setMaxFutureValidityInMinutes(300)
                    .setAllowedClockSkewInSeconds(30)
                    .setRequireSubject()
                    .setExpectedAudience("aud")
                    .setVerificationKey(new RsaJsonWebKey(JsonUtil.parseJson(PUBLIC_KEY)).getPublicKey())
                    .build();
            JwtClaims claims = consumer.processToClaims(token);
            if (claims != null) {
                return username == (String) claims.getClaimValue("username");
            }
        } catch (JoseException e) {
            e.printStackTrace();
        } catch (InvalidJwtException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * jws 校验 token
     * @param token 唯一身份令牌
     * @return 用户名
     */
    public static String getUsername(String token) {
        try {
            JwtConsumer consumer = new JwtConsumerBuilder()
                    .setSkipAllValidators()
                    .setDisableRequireSignature()
                    .setSkipSignatureVerification()
                    .build();
            JwtClaims claims = consumer.processToClaims(token);
            if (claims != null) {
                String username = (String) claims.getClaimValue("username");
                return username;
            }
        } catch (InvalidJwtException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取 KeyId, publicKey, privateKey
     */
    public static void createKeyPiar() {
        String keyId = UUID.randomUUID().toString().replaceAll("-", "");
        RsaJsonWebKey jwk = null;
        try {
            jwk = RsaJwkGenerator.generateJwk(2048);
        } catch (JoseException e) {
            e.printStackTrace();
        }
        jwk.setKeyId(keyId);
        String publicKey = jwk.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY);
        String privateKey = jwk.toJson(JsonWebKey.OutputControlLevel.INCLUDE_PRIVATE);

        System.out.println(keyId);
        System.out.println(publicKey);
        System.out.println(privateKey);
    }

    public static void main(String[] args) {
        createKeyPiar();
    }
}
