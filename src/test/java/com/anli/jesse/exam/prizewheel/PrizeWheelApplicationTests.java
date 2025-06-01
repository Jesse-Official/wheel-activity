package com.anli.jesse.exam.prizewheel;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("因為測試內嵌redis會有問題，暫時跳過這個測試")
@SpringBootTest(classes = com.anli.jesse.exam.wheelactivity.PrizeWheelApplication.class)
class PrizeWheelApplicationTests {

    @Test
    void contextLoads() {
    }

}
