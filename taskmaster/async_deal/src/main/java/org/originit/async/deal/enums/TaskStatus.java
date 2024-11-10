package org.originit.async.deal.enums;

/**
 * @author pshao
 */

public enum TaskStatus {
    /**
     * 等待
     */
    PENDING(0x01),
    /**
     * 执行中
     */
    EXECUTING(0x02),
    /**
     * 成功
     */
    SUCCESS(0x04),
    /**
     * 失败
     */
    FAIL(0x08);

    private TaskStatus(int status) {
        this.status = status;
    }
    private int status;

    public int getStatus() {
        return this.status;
    }
}