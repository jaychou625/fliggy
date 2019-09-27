package com.webbeds.fliggy;

import com.webbeds.fliggy.utils.Common;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FliggyApplicationTests {

    @Test
    public void contextLoads() {
        Common common = new Common();
        String str = common.subStringSpecial("SANCTUARY BEACH POOL VILLA");
        System.out.println(str);
    }

}
