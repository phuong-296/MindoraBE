package com.mindora.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class JournalRequest {

    private String title;

    @NotBlank(message = "Nội dung nhật ký không được để trống")
    private String content;

    private String moodValue;   // loved | happy | neutral | sad | anxious | angry

    private String[] tags;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate entryDate;
}
