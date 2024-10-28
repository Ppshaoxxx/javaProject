package com.zdf.flowsvr.data;

import lombok.Data;

import java.util.List;

@Data
public class UserTaskListRequest {
    String user_id;
    List<Integer> statusList;
}
