package com.mindora.service.impl;
import com.mindora.service.MoodService;

import com.mindora.dto.request.MoodLogRequest;
import com.mindora.dto.response.MoodLogResponse;
import com.mindora.entity.MoodLog;
import com.mindora.repository.MoodLogRepository;
import com.mindora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Quản lý nhật ký tâm trạng hàng ngày.
 * logMood() thực hiện upsert: tìm bản ghi theo (userId, ngày) rồi cập nhật nếu đã có, tạo mới nếu chưa.
 * Điều này cho phép user sửa mood trong ngày mà không bị lỗi duplicate key.
 */
@Service
@RequiredArgsConstructor
public class MoodServiceImpl implements MoodService {

    private final MoodLogRepository moodLogRepository;
    private final UserRepository userRepository;

    // Emoji mặc định theo điểm số khi client không gửi emoji tùy chỉnh
    private static final Map<Integer, String> SCORE_EMOJI = Map.of(
        7, "🥰", 6, "😊", 5, "🙂", 4, "😐", 3, "😰", 2, "😔", 1, "😡"
    );

    /** Upsert mood theo ngày: cập nhật nếu đã có bản ghi hôm đó, tạo mới nếu chưa. */
    @Override
    @Transactional
    public MoodLogResponse logMood(UUID userId, MoodLogRequest req) {
        LocalDate date = req.getLogDate() != null ? req.getLogDate() : LocalDate.now();

        // Tìm log của ngày này, nếu không có thì tạo object rỗng
        MoodLog log = moodLogRepository.findByUserIdAndLogDate(userId, date)
                .orElseGet(MoodLog::new);

        log.setUser(userRepository.getReferenceById(userId));
        log.setMoodScore(req.getMoodScore());
        // Dùng emoji từ client nếu có, không thì dùng emoji mặc định theo điểm
        log.setMoodEmoji(req.getMoodEmoji() != null
                ? req.getMoodEmoji()
                : SCORE_EMOJI.getOrDefault(req.getMoodScore(), "😐"));
        log.setNote(req.getNote());
        log.setLogDate(date);
        moodLogRepository.save(log);

        return toResponse(log);
    }

    /** Lấy mood 7 ngày gần nhất (hôm nay - 6 ngày đến hôm nay). */
    @Override
    @Transactional(readOnly = true)
    public List<MoodLogResponse> getWeek(UUID userId) {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(6);
        return getRange(userId, from, to);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MoodLogResponse> getRange(UUID userId, LocalDate from, LocalDate to) {
        return moodLogRepository
                .findByUserIdAndLogDateBetweenOrderByLogDateAsc(userId, from, to)
                .stream().map(this::toResponse).toList();
    }

    private MoodLogResponse toResponse(MoodLog m) {
        return new MoodLogResponse(
                m.getId(), m.getMoodScore(), m.getMoodEmoji(), m.getNote(), m.getLogDate());
    }
}
