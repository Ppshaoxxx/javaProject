package org.originit.async.deal.pojo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    void testOrderTime() {
        final long now = System.currentTimeMillis();
        Assertions.assertEquals(now + 1 * 1000, Task.calcOrderTime(now,  4, 1));
        Assertions.assertEquals(now + 2 * 1000, Task.calcOrderTime(now, 4, 2));
        Assertions.assertEquals(now + 4 * 1000, Task.calcOrderTime(now,  4, 3));
        Assertions.assertEquals(now + 4 * 1000, Task.calcOrderTime(now, 4, 4));
        Assertions.assertEquals(now + 4 * 1000, Task.calcOrderTime(now, -4, 1));
        Assertions.assertEquals(now + 4 * 1000, Task.calcOrderTime(now, -4, 2));
        Assertions.assertEquals(now + 4 * 1000, Task.calcOrderTime(now, -4, 3));
        Assertions.assertEquals(now + 4 * 1000, Task.calcOrderTime(now, -4, 4));


    }
}
