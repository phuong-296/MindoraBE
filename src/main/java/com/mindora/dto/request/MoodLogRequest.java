package com.mindora.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MoodLogRequest {

    @NotNull(message = "Điểm tâm trạng không được để trống")
    @Min(value = 1, message = "Điểm tâm trạng từ 1 đến 7")
    @Max(value = 7, message = "Điểm tâm trạng từ 1 đến 7")
    private Integer moodScore;   // 1 (angry) -> 7 (loved)

    private String moodEmoji;

    private String note;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate logDate;
}
