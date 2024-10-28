package com.zdf.flowsvr.enums;


/**
 * 任务状态
 */
public enum TaskStatus {
    PENDING(0x01),
    EXECUTING(0x02),
    SUCCESS(0x04),
    FAIL(0x08);

    private TaskStatus(int status) {
        this.status = status;
    }
    private int status;

    public int getStatus() {
        return this.status;
    }
}
