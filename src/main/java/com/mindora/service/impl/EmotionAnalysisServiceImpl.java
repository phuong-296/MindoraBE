package com.mindora.service.impl;
import com.mindora.service.EmotionAnalysisService;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Phân tích cảm xúc đơn giản dựa trên từ khoá tiếng Việt (rule-based).
 * Có thể thay bằng model NLP thật về sau mà không đổi interface.
 */
@Service
public class EmotionAnalysisServiceImpl implements EmotionAnalysisService {

    // Thu tu uu tien: crisis (uu tien cao nhat) → anxious → sad → angry → tired → happy
    // LinkedHashMap dam bao thu tu duyet, tranh Map.of() random order
    private static final Map<String, List<String>> EMOTION_KEYWORDS;
    static {
        EMOTION_KEYWORDS = new LinkedHashMap<>();

        // CRISIS - phai check TRUOC tat ca emotion khac. Khi match -> trigger hotline response.
        EMOTION_KEYWORDS.put("crisis", List.of(
            // Tự tử / tự sát trực tiếp
            "tự tử", "tự sát", "muốn chết", "muốn tự tử", "muốn tự sát",
            "không muốn sống", "không còn muốn sống", "chán sống", "không muốn tồn tại",
            "chả muốn tồn tại", "chẳng muốn tồn tại", "không muốn tồn tại nữa",
            // Kết thúc / biến mất
            "kết thúc cuộc đời", "kết thúc tất cả", "chấm dứt tất cả", "muốn biến mất",
            "muốn ra đi mãi mãi", "ra đi mãi mãi", "không còn ở đây nữa",
            "sẽ không còn ở đây", "muốn chấm dứt",
            // Tự làm hại
            "tự làm hại", "tự hại bản thân", "cắt tay", "tự cắt", "làm hại bản thân",
            // ASCII (không dấu) — quan trọng vì user hay gõ không dấu
            "tu tu", "tu sat", "muon chet", "muon tu tu", "muon tu sat",
            "khong muon song", "khong con muon song", "chan song",
            "khong muon ton tai", "cha muon ton tai", "chang muon ton tai",
            "ket thuc cuoc doi", "ket thuc tat ca", "cham dut tat ca",
            "muon bien mat", "muon ra di mai mai", "ra di mai mai",
            "tu lam hai", "tu hai ban than", "cat tay", "lam hai ban than",
            // Tiếng Anh
            "suicide", "kill myself", "end my life", "self-harm", "want to die",
            "don't want to live", "do not want to live", "want to disappear forever",
            "no reason to live", "can't go on"
        ));

        EMOTION_KEYWORDS.put("anxious", List.of(
            "lo lắng", "căng thẳng", "áp lực", "hoảng loạn", "sợ hãi", "lo âu", "stress",
            "lo quá", "hồi hộp", "bồn chồn", "mất ngủ", "không ngủ được", "tim đập nhanh",
            "run sợ", "sợ", "hoang mang", "bất an", "bối rối", "không biết phải làm gì",
            "ngại", "rối loạn", "nặng nề", "chịu không nổi", "quá tải", "không chịu được",
            "lo ngại", "lo xa", "thấp thỏm", "nơm nớp", "khó thở", "tức ngực",
            "lo lang",  "cang thang",  "ap luc",  "hoang loan",  "so hai",   "lo au",
            "lo qua",  "hoi hop",  "bon chon",  "mat ngu",  "khong ngu duoc",  "tim dap nhanh",
            "run so",  "hoang mang",  "bat an",  "boi roi",  "nang ne",  "qua tai",
            "ngai",  "roi loan",  "kho tho",  "tuc nguc",
            "panic", "anxiety", "nervous", "worried", "stressed", "overwhelmed", "anxious"
        ));
        EMOTION_KEYWORDS.put("sad", List.of(
            "buồn", "khóc", "cô đơn", "thất vọng", "chán nản", "tuyệt vọng", "đau lòng",
            "trống rỗng", "cô độc", "tủi thân", "đau khổ", "u sầu", "chia tay",
            "nhớ nhà", "nhớ ai", "thiếu", "cô quạnh", "hụt hẫng", "nản",
            "chán", "chẳng muốn", "không muốn làm gì", "mất động lực", "vô nghĩa",
            "không có ý nghĩa", "không ai hiểu", "không được quan tâm", "bị bỏ rơi",
            "mất mát", "nhớ", "tiếc", "ân hận", "hối hận",
            "buon",  "khoc",  "co don",  "that vong",  "chan nan",  "tuyet vong",  "dau long",
            "trong rong",  "co doc",  "tui than",  "dau kho",  "u sau",  "chia tay",
            "nho nha",  "hut hang",  "nan",  "chan",  "mat dong luc",  "vo nghia",
            "tiec",  "an han",  "hoi han",  "co quanh",
            "sad", "depressed", "lonely", "hopeless", "crying", "miss", "grief", "lost"
        ));
        EMOTION_KEYWORDS.put("angry", List.of(
            "tức giận", "bực bội", "khó chịu", "ghét", "tức", "bực", "giận dữ",
            "giận", "cáu", "phẫn nộ", "điên tiết", "bực mình", "ức chế",
            "không công bằng", "vô lý", "bất công", "chán ghét", "không thể chịu",
            "tức lên", "muốn la hét", "muốn đập", "bực tức",
            "tuc gian",  "buc boi",  "kho chiu",  "ghet",  "tuc",  "buc",  "gian du",
            "gian",  "cau",  "phan no",  "dien tiet",  "buc minh",  "uc che",
            "khong cong bang",  "vo ly",  "bat cong",  "buc tuc",
            "angry", "mad", "furious", "frustrated", "annoyed", "irritated", "rage"
        ));
        EMOTION_KEYWORDS.put("tired", List.of(
            "mệt mỏi", "kiệt sức", "buồn ngủ", "mệt", "kiệt", "uể oải", "đuối",
            "kiệt quệ", "mất sức", "hết pin", "hết năng lượng", "không còn sức",
            "lười", "ngại dậy", "muốn nằm", "muốn ngủ", "không muốn làm gì",
            "làm việc quá nhiều", "làm nhiều quá", "quá mệt", "燃え尽き症候群",
            "chẳng buồn", "không thiết", "thờ ơ", "vô cảm",
            "met moi",  "kiet suc",  "buon ngu",  "met",  "kiet",  "ue oai",  "duoi",
            "kiet que",  "mat suc",  "het pin",  "het nang luong",  "luoi",
            "qua met",  "chang buon",  "khong thiet",  "tho o",  "vo cam",
            "tired", "exhausted", "fatigue", "burnout", "drained", "sleepy", "worn out"
        ));
        EMOTION_KEYWORDS.put("happy", List.of(
            "vui vẻ", "hạnh phúc", "tuyệt vời", "phấn khởi", "vui", "sung sướng",
            "hào hứng", "phấn chấn", "thăng chức", "thành công", "may mắn",
            "tốt quá", "hay quá", "thích", "yêu", "thú vị", "tuyệt", "đỉnh",
            "vui lắm", "hạnh phúc lắm", "cảm thấy tốt", "ổn lắm", "ngon lành",
            "được rồi", "xong rồi", "đạt", "chiến thắng", "giỏi quá",
            "vui ve",  "hanh phuc",  "tuyet voi",  "phan khoi",  "sung suong",
            "hao hung",  "phan chan",  "thang chuc",  "thanh cong",  "may man",
            "tot qua",  "hay qua",  "thich",  "yeu",  "thu vi",  "dinh",
            "happy", "joyful", "excited", "great", "amazing", "wonderful", "love", "awesome"
        ));
    }

    private static final Map<String, BigDecimal> SENTIMENT_SCORE = Map.of(
        "happy",   new BigDecimal("0.85"),
        "tired",   new BigDecimal("0.30"),
        "sad",     new BigDecimal("0.20"),
        "anxious", new BigDecimal("0.25"),
        "angry",   new BigDecimal("0.10"),
        "crisis",  new BigDecimal("0.05"),
        "neutral", new BigDecimal("0.50")
    );

    @Override
    public String detectEmotion(String text) {
        if (text == null) return "neutral";
        String lower = text.toLowerCase();

        // Crisis luôn giữ priority tuyệt đối — an toàn là trên hết, không cạnh tranh theo độ dài.
        if (EMOTION_KEYWORDS.get("crisis").stream().anyMatch(lower::contains)) {
            return "crisis";
        }

        // Các emotion còn lại: chọn theo keyword khớp DÀI NHẤT (cụ thể nhất) thay vì theo
        // thứ tự category cố định. Trước đây duyệt category theo thứ tự (sad trước tired)
        // khiến câu "buồn ngủ" luôn bị match nhầm thành "sad" qua keyword ngắn "buồn",
        // dù "buồn ngủ" đã được khai báo rõ trong "tired". Ưu tiên keyword dài hơn giúp
        // các cụm từ cụ thể/dài (vd "buồn ngủ") thắng các từ khoá ngắn/chung chung hơn.
        String bestEmotion = "neutral";
        int bestLength = -1;
        for (Map.Entry<String, List<String>> entry : EMOTION_KEYWORDS.entrySet()) {
            if ("crisis".equals(entry.getKey())) continue;
            for (String keyword : entry.getValue()) {
                if (keyword.length() > bestLength && lower.contains(keyword)) {
                    bestLength = keyword.length();
                    bestEmotion = entry.getKey();
                }
            }
        }
        return bestEmotion;
    }

    @Override
    public BigDecimal getSentimentScore(String emotion) {
        return SENTIMENT_SCORE.getOrDefault(emotion, new BigDecimal("0.50"));
    }
}
