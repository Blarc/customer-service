package com.github.blarc;

import io.quarkus.elytron.security.common.BcryptUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/*
Just a simple test to generate a password.
 */
@Disabled
public class PasswordTest {
    static final String PASSWORD = "qweasd123";

    @Test
    void generatePassword() {
        System.out.println(BcryptUtil.bcryptHash(PASSWORD));
    }
}
