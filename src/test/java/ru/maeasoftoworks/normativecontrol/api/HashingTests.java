package ru.maeasoftoworks.normativecontrol.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.maeasoftoworks.normativecontrol.api.utils.HashingUtils;

public class HashingTests {

    @Test
    public void Sha256Test(){
        Assertions.assertEquals(HashingUtils.sha256("mycoolpassword"), "835d052d985517cb1fa5fc4bf44228f898f7a2233383feaf5cd23ea2fd9b2a3f");
    }
}
