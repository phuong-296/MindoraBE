package com.mindora.service.impl;
import com.mindora.service.JournalService;

import com.mindora.dto.request.JournalRequest;
import com.mindora.dto.response.JournalResponse;
import com.mindora.entity.JournalEntry;
import com.mindora.entity.MoodLog;
import com.mindora.exception.ResourceNotFoundException;
import com.mindora.repository.JournalEntryRepository;
import com.mindora.repository.MoodLogRepository;
import com.mindora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * Quản lý nhật ký cảm xúc cá nhân.
 * Khi tạo/cập nhật nhật ký có moodValue, service tự động đồng bộ MoodLog cùng ngày
 * để biểu đồ mood phản ánh đúng trạng thái từ nhật ký.
 */
@Service
@RequiredArgsConstructor
public class JournalServiceImpl implements JournalService {

    private final JournalEntryRepository journalRepository;
    private final MoodLogRepository moodLogRepository;
    private final UserRepository userRepository;

    // Ánh xạ từ chuỗi mood sang điểm số (1-7) để lưu vào MoodLog
    private static final Map<String, Integer> MOOD_SCORE_MAP = Map.of(
        "loved", 7, "happy", 6, "neutral", 4, "anxious", 3, "sad", 2, "angry", 1
    );
    // Emoji tương ứng với từng mood để hiển thị trên UI
    private static final Map<String, String> MOOD_EMOJI_MAP = Map.of(
        "loved", "🥰", "happy", "😊", "neutral", "😐", "anxious", "😰", "sad", "😔", "angry", "😡"
    );

    @Override
    @Transactional(readOnly = true)
    public Page<JournalResponse> list(UUID userId, Pageable pageable) {
        // Sắp xếp mới nhất lên đầu, hỗ trợ phân trang
        return journalRepository.findByUserIdOrderByEntryDateDesc(userId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public JournalResponse get(UUID userId, UUID id) {
        // findByIdAndUserId đảm bảo user chỉ đọc được nhật ký của mình
        JournalEntry entry = journalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhật ký"));
        return toResponse(entry);
    }

    @Override
    @Transactional
    public JournalResponse create(UUID userId, JournalRequest request) {
        JournalEntry entry = new JournalEntry();
        entry.setUser(userRepository.getReferenceById(userId));
        entry.setTitle(request.getTitle());
        entry.setContent(request.getContent());
        entry.setMoodValue(request.getMoodValue());
        entry.setTags(request.getTags());
        entry.setEntryDate(request.getEntryDate() != null ? request.getEntryDate() : LocalDate.now());
        journalRepository.save(entry);

        // Đồng bộ MoodLog nếu nhật ký có chứa thông tin mood
        if (request.getMoodValue() != null) {
            upsertMoodLog(userId, request.getMoodValue(), entry.getEntryDate());
        }
        return toResponse(entry);
    }

    @Override
    @Transactional
    public JournalResponse update(UUID userId, UUID id, JournalRequest request) {
        JournalEntry entry = journalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhật ký"));

        entry.setTitle(request.getTitle());
        entry.setContent(request.getContent());
        entry.setMoodValue(request.getMoodValue());
        entry.setTags(request.getTags());
        if (request.getEntryDate() != null) entry.setEntryDate(request.getEntryDate());
        journalRepository.save(entry);

        // Cập nhật MoodLog khi mood trong nhật ký thay đổi
        if (request.getMoodValue() != null) {
            upsertMoodLog(userId, request.getMoodValue(), entry.getEntryDate());
        }
        return toResponse(entry);
    }

    @Override
    @Transactional
    public void delete(UUID userId, UUID id) {
        JournalEntry entry = journalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhật ký"));
        journalRepository.delete(entry);
    }

    /** Upsert MoodLog: cập nhật nếu đã có bản ghi ngày đó, tạo mới nếu chưa. */
    private void upsertMoodLog(UUID userId, String moodValue, LocalDate date) {
        MoodLog log = moodLogRepository.findByUserIdAndLogDate(userId, date)
                .orElseGet(MoodLog::new);
        log.setUser(userRepository.getReferenceById(userId));
        log.setMoodScore(MOOD_SCORE_MAP.getOrDefault(moodValue, 4));
        log.setMoodEmoji(MOOD_EMOJI_MAP.getOrDefault(moodValue, "😐"));
        log.setLogDate(date);
        moodLogRepository.save(log);
    }

    private JournalResponse toResponse(JournalEntry e) {
        return new JournalResponse(
                e.getId(), e.getTitle(), e.getContent(), e.getMoodValue(),
                e.getTags(), e.getEntryDate(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
