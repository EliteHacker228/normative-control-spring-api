package ru.maeasoftoworks.normativecontrol.api.hashing;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;
import ru.maeasoftoworks.normativecontrol.api.utils.hashing.Sha256;

@Slf4j
public class Sha256Tests {

    @Test
    public void defaultTest() {
        String resultHash = Sha256.getStringSha256("Test string for SHA256");
        Assertions.assertEquals("fe857e15959fce6652084e1a74c99ec416299e7b6eefed6e415f05adc044b821", resultHash);
    }
}
