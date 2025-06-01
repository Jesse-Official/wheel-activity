package com.anli.jesse.exam.wheelactivity.interfaces.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class DrawRequest {
    /* 活動ID */
    @NotNull(message = "活動ID不能為空")
    private Integer activityId;
    /* 用戶ID: 為了簡化程式碼所以從request傳入 */
    @NotNull(message = "用戶ID不能為空")
    private Integer userId;
    /* 抽獎次數: 預設抽獎次數為1次 */
    @Min(value = 1, message = "抽獎次數必須大於等於 1")
    private Integer drawTimes = 1;
}
