-- ============================================================
-- Mindora AI — Knowledge Base (RAG)
-- Nguồn: PFA (WHO), CBT, Mindfulness, Crisis Intervention
-- ============================================================

-- Bảng knowledge_document_tags và knowledge_document_keywords
-- được tạo tự động bởi Hibernate @ElementCollection

INSERT INTO knowledge_documents (id, title, content, source, chunk_index, created_at) VALUES

-- ── ANXIETY / LO LẮNG ─────────────────────────────────────────────────────
('d0000000-0000-0000-0000-000000000001',
 'Kỹ thuật hít thở 4-7-8 cho lo lắng cấp tính',
 'Kỹ thuật thở 4-7-8 là phương pháp hiệu quả để giảm lo âu nhanh chóng. Cách thực hiện: Thở ra hoàn toàn qua miệng. Ngậm miệng và hít vào qua mũi trong 4 giây. Nín thở trong 7 giây. Thở ra hoàn toàn qua miệng trong 8 giây. Lặp lại chu kỳ này 4 lần. Kỹ thuật này kích hoạt hệ thần kinh phó giao cảm, giúp cơ thể thoát khỏi trạng thái "chiến hay chạy" và bình tĩnh trở lại trong vòng 2-3 phút.',
 'PFA - WHO', 1, now()),

('d0000000-0000-0000-0000-000000000002',
 'Nhận diện và đặt tên cảm xúc lo lắng',
 'Theo Sơ cứu Tâm lý (PFA), bước đầu tiên khi lo lắng là nhận diện và đặt tên cho cảm xúc đó. Nghiên cứu tâm lý học (Lieberman, 2011) cho thấy chỉ cần gọi tên cảm xúc - "Tôi đang lo lắng" - cũng đã giảm 30% cường độ cảm xúc đó. Hãy hỏi bản thân: Tôi đang lo về điều gì cụ thể? Điều tệ nhất có thể xảy ra là gì? Khả năng điều đó xảy ra là bao nhiêu? Nếu nó xảy ra, tôi có thể làm gì? Quá trình này giúp chuyển lo lắng từ cảm xúc lan toả sang vấn đề cụ thể có thể giải quyết.',
 'PFA - WHO', 2, now()),

('d0000000-0000-0000-0000-000000000003',
 'Kỹ thuật 5-4-3-2-1 Grounding cho lo âu',
 'Kỹ thuật grounding 5-4-3-2-1 giúp đưa tâm trí trở về hiện tại khi đang lo âu hoặc hoảng loạn. Thực hiện: Nhìn xung quanh và đặt tên 5 vật bạn có thể nhìn thấy. Chạm vào 4 vật và cảm nhận kết cấu của chúng. Lắng nghe 3 âm thanh xung quanh. Ngửi và xác định 2 mùi hương. Nếm 1 thứ gì đó hoặc cảm nhận vị trong miệng. Kỹ thuật này hoạt động vì nó buộc não bộ tập trung vào thực tại thay vì lo lắng về tương lai.',
 'CBT - Anxiety Management', 3, now()),

('d0000000-0000-0000-0000-000000000004',
 'Phân biệt lo lắng bình thường và rối loạn lo âu',
 'Lo lắng là phản ứng bình thường của cơ thể với căng thẳng. Tuy nhiên, cần chú ý khi lo lắng: kéo dài hơn 6 tháng, ảnh hưởng đến công việc/học tập/các mối quan hệ, đi kèm triệu chứng thể xác (tim đập nhanh, khó thở, đổ mồ hôi), khó kiểm soát dù đã cố gắng. Rối loạn lo âu tổng quát (GAD) ảnh hưởng 3.6% dân số thế giới (WHO). Điều quan trọng: lo âu có thể điều trị được, với hiệu quả cao qua liệu pháp CBT và/hoặc thuốc. Nếu bạn nhận thấy những dấu hiệu trên, hãy tham khảo ý kiến chuyên gia tâm lý.',
 'WHO Mental Health Guidelines', 4, now()),

-- ── SADNESS / BUỒN BÃ ─────────────────────────────────────────────────────
('d0000000-0000-0000-0000-000000000005',
 'Phân biệt buồn bình thường và trầm cảm',
 'Buồn bã là cảm xúc bình thường, nhưng trầm cảm là bệnh lý cần điều trị. Trầm cảm khác buồn bình thường ở: thời gian (hơn 2 tuần liên tục), cường độ (không cải thiện dù có chuyện vui), phạm vi (mất hứng thú với MỌI hoạt động từng yêu thích), chức năng (ảnh hưởng nghiêm trọng đến ăn, ngủ, làm việc). Theo WHO, 280 triệu người trên thế giới mắc trầm cảm. Trầm cảm KHÔNG phải do yếu đuối hay thiếu ý chí - đây là bệnh lý thực thể liên quan đến hóa học não. Điều trị sớm có hiệu quả cao (60-80% trường hợp cải thiện với điều trị phù hợp).',
 'WHO - Depression Guidelines', 1, now()),

('d0000000-0000-0000-0000-000000000006',
 'Kỹ thuật Behavioral Activation khi buồn',
 'Behavioral Activation (Kích hoạt hành vi) là kỹ thuật CBT hiệu quả nhất cho buồn bã và trầm cảm nhẹ-vừa. Nguyên lý: Khi buồn, chúng ta thường thu mình lại, làm ít đi → cảm thấy tệ hơn → thu mình thêm (vòng luẩn quẩn). Cách phá vỡ: Lên lịch 1 hoạt động nhỏ mang lại cảm giác thành tựu hoặc vui vẻ mỗi ngày. Không cần "có hứng" mới làm - hành động trước, cảm xúc sẽ theo sau. Bắt đầu cực nhỏ: đi bộ 5 phút, gọi 1 người bạn 10 phút, nấu 1 món đơn giản. Ghi lại cảm xúc trước và sau - thường thấy cải thiện 2-3 điểm trên thang 10.',
 'CBT - Behavioral Activation', 2, now()),

('d0000000-0000-0000-0000-000000000007',
 'Viết nhật ký cảm xúc để xử lý nỗi buồn',
 'Viết nhật ký (expressive writing) được James Pennebaker nghiên cứu và chứng minh giảm đáng kể các triệu chứng trầm cảm và lo âu. Hướng dẫn: Viết 15-20 phút mỗi ngày về những điều khiến bạn buồn hoặc khó chịu nhất. Không cần lo về ngữ pháp hay văn phong - cứ viết tự do. Viết về suy nghĩ VÀ cảm xúc, không chỉ sự kiện. Sau 3-4 ngày viết, nhiều người bắt đầu thấy pattern và hiểu rõ hơn về cảm xúc của mình. Đây là lý do ứng dụng Mindora có tính năng nhật ký - để bạn có không gian an toàn để xử lý cảm xúc.',
 'Pennebaker - Expressive Writing Research', 3, now()),

-- ── STRESS / CĂNG THẲNG ───────────────────────────────────────────────────
('d0000000-0000-0000-0000-000000000008',
 'Phân biệt Eustress và Distress',
 'Không phải mọi stress đều có hại. Eustress (stress tích cực) giúp tập trung, tăng hiệu suất, tạo động lực - như cảm giác trước khi thuyết trình hay thi đấu. Distress (stress tiêu cực) xảy ra khi áp lực vượt quá khả năng ứng phó, dẫn đến kiệt sức, lo âu, mất tập trung. Nhận biết distress: cảm thấy overwhelmed, không thể ngừng nghĩ về vấn đề, ngủ không ngon, hay cáu gắt. Điểm mấu chốt: stress không phải vấn đề - NHẬN THỨC về stress mới là vấn đề. Khi ta xem stress là thử thách (challenge) thay vì đe dọa (threat), phản ứng sinh lý của cơ thể thay đổi tích cực.',
 'WHO Stress Management Guidelines', 1, now()),

('d0000000-0000-0000-0000-000000000009',
 'Kỹ thuật Box Breathing giảm stress',
 'Box Breathing (Hít thở hộp vuông) là kỹ thuật được Navy SEALs và vận động viên elite sử dụng để kiểm soát stress trong tình huống áp lực cao. Cách thực hiện: Hít vào chậm 4 giây. Nín thở 4 giây. Thở ra chậm 4 giây. Nín thở 4 giây (hộp vuông hoàn chỉnh). Lặp lại 4-6 chu kỳ. Nghiên cứu (Jerath et al., 2015) cho thấy kỹ thuật này giảm cortisol (hormone stress), tăng hoạt động hệ thần kinh phó giao cảm, cải thiện tập trung và ra quyết định. Có thể thực hiện bất cứ lúc nào - trước cuộc họp, trong khi chờ đợi, hoặc khi cảm thấy overwhelmed.',
 'Stress Management - Military Psychology', 2, now()),

('d0000000-0000-0000-0000-000000000010',
 'Quản lý thời gian và tránh burnout',
 'Burnout là trạng thái kiệt sức hoàn toàn về thể xác, cảm xúc và tinh thần do stress kéo dài. Dấu hiệu sớm: mệt mỏi mãn tính, hiệu suất giảm, trở nên hoài nghi, tách biệt khỏi công việc/học tập. Phòng ngừa burnout theo WHO: Đặt ranh giới rõ ràng giữa công việc và nghỉ ngơi. Lên lịch "không làm việc" và giữ nghiêm. Học nói "không" với các nhiệm vụ không thiết yếu. Ưu tiên ngủ đủ 7-9 tiếng. Duy trì ít nhất 1 hoạt động thuần vui vẻ không liên quan đến năng suất mỗi tuần. Kết nối xã hội thực sự (không qua màn hình) ít nhất 2-3 lần/tuần.',
 'WHO Burnout Guidelines', 3, now()),

-- ── ANGER / TỨC GIẬN ──────────────────────────────────────────────────────
('d0000000-0000-0000-0000-000000000011',
 'Hiểu nguồn gốc của cơn giận',
 'Giận dữ thường là "cảm xúc thứ cấp" - bên dưới giận thường có tổn thương, sợ hãi, thất vọng hoặc cảm giác không được tôn trọng. Theo mô hình Iceberg Emotion: Giận là phần nổi, phần chìm thường là một trong số: cảm thấy bị bất công, sợ mất kiểm soát, tổn thương sâu, thất vọng vì kỳ vọng không được đáp ứng, cảm giác bất lực. Hỏi bản thân: "Bên dưới cơn giận này, mình đang cảm thấy gì thật sự?" Điều này giúp xử lý nguồn gốc thay vì chỉ đối phó với triệu chứng.',
 'CBT - Anger Management', 1, now()),

('d0000000-0000-0000-0000-000000000012',
 'Kỹ thuật STOP để kiểm soát cơn giận',
 'Kỹ thuật STOP giúp ngắt vòng phản ứng tự động khi tức giận: S - Stop (Dừng lại): Không làm gì ngay khi cảm thấy giận bùng lên. T - Take a breath (Hít thở): Thở sâu 3 lần để kích hoạt hệ thần kinh phó giao cảm. O - Observe (Quan sát): Nhận biết cảm xúc và suy nghĩ đang xảy ra mà không phán xét. P - Proceed (Tiếp tục): Sau khi bình tĩnh hơn, quyết định cách phản ứng phù hợp. Khoa học: Cơn giận kích hoạt amygdala (não cảm xúc) và tắt prefrontal cortex (não lý trí). Kỹ thuật STOP cho não lý trí thời gian "trở lại online" sau khoảng 90 giây.',
 'CBT - Anger Management', 2, now()),

-- ── TIRED / MỆT MỎI ──────────────────────────────────────────────────────
('d0000000-0000-0000-0000-000000000013',
 'Phân biệt mệt thể xác và mệt tinh thần',
 'Mệt mỏi thể xác phục hồi sau khi ngủ ngon. Mệt mỏi tinh thần (mental fatigue) phức tạp hơn và không phục hồi chỉ bằng ngủ. Dấu hiệu mệt tinh thần: Sau một đêm ngủ vẫn cảm thấy kiệt sức. Khó tập trung dù đã nghỉ ngơi. Mất động lực với mọi thứ. Quyết định nhỏ cũng cảm thấy nặng nề. Nguyên nhân thường gặp: Overcommitment (nhận quá nhiều việc), thiếu ranh giới, không có thời gian "không làm gì" (idle time), cảm xúc không được xử lý tích tụ theo thời gian. Phục hồi mệt tinh thần cần: đặt ra ranh giới, thực hành self-compassion, và đôi khi cần hỗ trợ chuyên nghiệp.',
 'Psychology Today - Mental Fatigue', 1, now()),

('d0000000-0000-0000-0000-000000000014',
 'Kỹ thuật Restorative Activities phục hồi năng lượng',
 'Không phải mọi "nghỉ ngơi" đều phục hồi năng lượng tâm thần. Passive rest (lướt mạng xã hội, xem TV) thường không phục hồi mental fatigue, thậm chí làm tệ hơn. Restorative Activities (hoạt động phục hồi) theo lý thuyết Attention Restoration (Kaplan): Tiếp xúc thiên nhiên (đi bộ công viên, nhìn cây cối) phục hồi khả năng tập trung hiệu quả nhất. Hoạt động nhẹ nhàng tự nguyện (làm vườn, nấu ăn đơn giản, vẽ). Kết nối xã hội ý nghĩa (không phải xã giao). Thực hành chánh niệm (mindfulness). Mục tiêu: 20-30 phút mỗi ngày cho ít nhất 1 restorative activity.',
 'Kaplan Attention Restoration Theory', 2, now()),

-- ── HAPPY / VUI VẺ ────────────────────────────────────────────────────────
('d0000000-0000-0000-0000-000000000015',
 'Tăng cường cảm xúc tích cực bền vững',
 'Hạnh phúc bền vững không đến từ những sự kiện lớn mà từ những khoảnh khắc nhỏ hàng ngày. Nghiên cứu của Martin Seligman (Positive Psychology) xác định 5 yếu tố PERMA tạo nên well-being: P - Positive Emotions (cảm xúc tích cực), E - Engagement (gắn kết/flow), R - Relationships (mối quan hệ ý nghĩa), M - Meaning (ý nghĩa và mục đích), A - Accomplishment (thành tựu). Thực hành: Mỗi tối viết 3 điều tốt xảy ra hôm nay (Three Good Things - hiệu quả tương đương thuốc chống trầm cảm theo nghiên cứu Seligman 2005). Thực hành lòng biết ơn làm tăng dopamine và serotonin một cách tự nhiên.',
 'Positive Psychology - Seligman', 1, now()),

-- ── CRISIS / KHỦNG HOẢNG ──────────────────────────────────────────────────
('d0000000-0000-0000-0000-000000000016',
 'Nhận biết dấu hiệu khủng hoảng tâm lý',
 'Dấu hiệu cần tìm kiếm giúp đỡ khẩn cấp: Có suy nghĩ về việc tự làm hại bản thân hoặc tự tử. Cảm thấy cuộc sống không còn ý nghĩa hoặc không có lý do để tiếp tục. Nghe hoặc thấy những thứ người khác không nhận ra. Hoàn toàn mất kết nối với thực tại. Không thể chăm sóc bản thân cơ bản (ăn, ngủ, vệ sinh) trong nhiều ngày. NẾU BẠN ĐANG GẶP KHỦNG HOẢNG, HÃY: Gọi ngay đường dây hỗ trợ sức khỏe tâm thần Việt Nam: 1800 599 920 (miễn phí, 24/7). Đến phòng cấp cứu bệnh viện gần nhất. Nói với người thân hoặc bạn bè bạn tin tưởng. Bạn không đơn độc, và luôn có sự giúp đỡ.',
 'PFA - Crisis Intervention WHO', 1, now()),

-- ── NEUTRAL / CHUNG ────────────────────────────────────────────────────────
('d0000000-0000-0000-0000-000000000017',
 'Mindfulness cơ bản — Chánh niệm cho người mới bắt đầu',
 'Mindfulness (Chánh niệm) là khả năng chú ý hoàn toàn vào khoảnh khắc hiện tại mà không phán xét. Hơn 3000 nghiên cứu khoa học xác nhận mindfulness giảm lo âu (58%), trầm cảm (38%), và stress (40%). Bài tập 5 phút cho người mới: Ngồi thoải mái, lưng thẳng nhưng không cứng. Nhắm mắt hoặc nhìn xuống một điểm cố định. Tập trung vào cảm giác của hơi thở - không khí vào mũi, ngực nở ra, bụng phồng lên. Khi tâm trí lang thang (và nó sẽ lang thang), nhẹ nhàng đưa sự chú ý trở lại hơi thở - không tự trách. Bắt đầu 5 phút/ngày, tăng dần.',
 'MBSR - Kabat-Zinn Mindfulness Program', 1, now()),

('d0000000-0000-0000-0000-000000000018',
 'Tự chăm sóc bản thân — Self-Compassion',
 'Self-compassion (Lòng trắc ẩn với bản thân) của Kristin Neff gồm 3 yếu tố: 1. Self-kindness: Đối xử với bản thân như với một người bạn tốt khi thất bại, không tự chỉ trích gay gắt. 2. Common humanity: Nhận ra rằng đau khổ và thất bại là phần chung của trải nghiệm con người - bạn không đơn độc. 3. Mindfulness: Quan sát suy nghĩ và cảm xúc tiêu cực mà không bị cuốn vào chúng. Nghiên cứu cho thấy self-compassion hiệu quả hơn self-esteem (lòng tự trọng truyền thống) trong việc duy trì sức khỏe tâm thần lâu dài. Câu hỏi thực hành: "Nếu một người bạn đang trải qua điều này, mình sẽ nói gì với họ?"',
 'Kristin Neff - Self-Compassion Research', 2, now()),

('d0000000-0000-0000-0000-000000000019',
 'Vai trò của giấc ngủ với sức khỏe tâm thần',
 'Giấc ngủ và sức khỏe tâm thần có mối liên hệ hai chiều: Thiếu ngủ làm tăng lo âu, trầm cảm, và khả năng phản ứng cảm xúc tiêu cực. Rối loạn lo âu và trầm cảm thường gây mất ngủ. Người lớn cần 7-9 tiếng ngủ mỗi đêm (NSF). Hygiene giấc ngủ tốt: Đi ngủ và thức dậy cùng giờ mỗi ngày (kể cả cuối tuần). Tắt màn hình điện tử 1 tiếng trước khi ngủ (blue light ức chế melatonin). Nhiệt độ phòng 18-20°C là lý tưởng. Không dùng caffeine sau 2 giờ chiều. Nếu không ngủ được sau 20 phút, dậy làm gì đó nhẹ nhàng đến khi buồn ngủ.',
 'Sleep Foundation - Mental Health', 1, now()),

('d0000000-0000-0000-0000-000000000020',
 'Xây dựng mạng lưới hỗ trợ xã hội',
 'Mối quan hệ xã hội là yếu tố bảo vệ sức khỏe tâm thần mạnh nhất được biết đến. Nghiên cứu Harvard Grant Study (80 năm theo dõi) kết luận: Chất lượng mối quan hệ - không phải tiền bạc, danh vọng, hay thể chất - là yếu tố dự đoán tốt nhất về hạnh phúc và sức khỏe lâu dài. Cô đơn làm tăng nguy cơ trầm cảm gấp 3 lần và tương đương hút 15 điếu thuốc mỗi ngày về tác hại sức khỏe (Holt-Lunstad, 2015). Xây dựng kết nối: Ưu tiên thời gian face-to-face với người thân. Tham gia nhóm có cùng sở thích. Tình nguyện phục vụ cộng đồng. Đừng chờ cảm xúc tốt mới kết nối - hãy hành động trước.',
 'Harvard Study of Adult Development', 1, now())

ON CONFLICT (id) DO NOTHING;

-- ── Emotion Tags ─────────────────────────────────────────────────────────
INSERT INTO knowledge_document_tags (document_id, tag) VALUES
-- anxious chunks
('d0000000-0000-0000-0000-000000000001', 'anxious'),
('d0000000-0000-0000-0000-000000000001', 'stressed'),
('d0000000-0000-0000-0000-000000000002', 'anxious'),
('d0000000-0000-0000-0000-000000000002', 'stressed'),
('d0000000-0000-0000-0000-000000000003', 'anxious'),
('d0000000-0000-0000-0000-000000000003', 'stressed'),
('d0000000-0000-0000-0000-000000000004', 'anxious'),
-- sad chunks
('d0000000-0000-0000-0000-000000000005', 'sad'),
('d0000000-0000-0000-0000-000000000006', 'sad'),
('d0000000-0000-0000-0000-000000000006', 'tired'),
('d0000000-0000-0000-0000-000000000007', 'sad'),
-- tired/stress chunks
('d0000000-0000-0000-0000-000000000008', 'stressed'),
('d0000000-0000-0000-0000-000000000008', 'tired'),
('d0000000-0000-0000-0000-000000000009', 'stressed'),
('d0000000-0000-0000-0000-000000000009', 'anxious'),
('d0000000-0000-0000-0000-000000000010', 'tired'),
('d0000000-0000-0000-0000-000000000010', 'stressed'),
-- angry chunks
('d0000000-0000-0000-0000-000000000011', 'angry'),
('d0000000-0000-0000-0000-000000000012', 'angry'),
-- tired chunks
('d0000000-0000-0000-0000-000000000013', 'tired'),
('d0000000-0000-0000-0000-000000000013', 'sad'),
('d0000000-0000-0000-0000-000000000014', 'tired'),
('d0000000-0000-0000-0000-000000000014', 'stressed'),
-- happy chunks
('d0000000-0000-0000-0000-000000000015', 'happy'),
-- crisis
('d0000000-0000-0000-0000-000000000016', 'sad'),
('d0000000-0000-0000-0000-000000000016', 'anxious'),
-- neutral/general
('d0000000-0000-0000-0000-000000000017', 'neutral'),
('d0000000-0000-0000-0000-000000000017', 'anxious'),
('d0000000-0000-0000-0000-000000000017', 'stressed'),
('d0000000-0000-0000-0000-000000000018', 'neutral'),
('d0000000-0000-0000-0000-000000000018', 'sad'),
('d0000000-0000-0000-0000-000000000019', 'neutral'),
('d0000000-0000-0000-0000-000000000019', 'tired'),
('d0000000-0000-0000-0000-000000000020', 'neutral'),
('d0000000-0000-0000-0000-000000000020', 'sad')
ON CONFLICT DO NOTHING;

-- ── Keywords ──────────────────────────────────────────────────────────────
INSERT INTO knowledge_document_keywords (document_id, keyword) VALUES
('d0000000-0000-0000-0000-000000000001', 'hít thở'), ('d0000000-0000-0000-0000-000000000001', 'lo lắng'), ('d0000000-0000-0000-0000-000000000001', 'hoảng loạn'),
('d0000000-0000-0000-0000-000000000002', 'lo lắng'), ('d0000000-0000-0000-0000-000000000002', 'cảm xúc'), ('d0000000-0000-0000-0000-000000000002', 'nhận diện'),
('d0000000-0000-0000-0000-000000000003', 'grounding'), ('d0000000-0000-0000-0000-000000000003', 'hiện tại'), ('d0000000-0000-0000-0000-000000000003', 'lo âu'),
('d0000000-0000-0000-0000-000000000004', 'lo âu'), ('d0000000-0000-0000-0000-000000000004', 'rối loạn'), ('d0000000-0000-0000-0000-000000000004', 'chuyên gia'),
('d0000000-0000-0000-0000-000000000005', 'buồn'), ('d0000000-0000-0000-0000-000000000005', 'trầm cảm'), ('d0000000-0000-0000-0000-000000000005', 'chán nản'),
('d0000000-0000-0000-0000-000000000006', 'buồn'), ('d0000000-0000-0000-0000-000000000006', 'động lực'), ('d0000000-0000-0000-0000-000000000006', 'hành động'),
('d0000000-0000-0000-0000-000000000007', 'nhật ký'), ('d0000000-0000-0000-0000-000000000007', 'viết'), ('d0000000-0000-0000-0000-000000000007', 'cảm xúc'),
('d0000000-0000-0000-0000-000000000008', 'stress'), ('d0000000-0000-0000-0000-000000000008', 'căng thẳng'), ('d0000000-0000-0000-0000-000000000008', 'áp lực'),
('d0000000-0000-0000-0000-000000000009', 'hít thở'), ('d0000000-0000-0000-0000-000000000009', 'stress'), ('d0000000-0000-0000-0000-000000000009', 'bình tĩnh'),
('d0000000-0000-0000-0000-000000000010', 'mệt mỏi'), ('d0000000-0000-0000-0000-000000000010', 'burnout'), ('d0000000-0000-0000-0000-000000000010', 'kiệt sức'),
('d0000000-0000-0000-0000-000000000011', 'tức giận'), ('d0000000-0000-0000-0000-000000000011', 'giận'), ('d0000000-0000-0000-0000-000000000011', 'bực bội'),
('d0000000-0000-0000-0000-000000000012', 'tức giận'), ('d0000000-0000-0000-0000-000000000012', 'kiểm soát'), ('d0000000-0000-0000-0000-000000000012', 'bình tĩnh'),
('d0000000-0000-0000-0000-000000000013', 'mệt mỏi'), ('d0000000-0000-0000-0000-000000000013', 'kiệt sức'), ('d0000000-0000-0000-0000-000000000013', 'năng lượng'),
('d0000000-0000-0000-0000-000000000014', 'nghỉ ngơi'), ('d0000000-0000-0000-0000-000000000014', 'phục hồi'), ('d0000000-0000-0000-0000-000000000014', 'năng lượng'),
('d0000000-0000-0000-0000-000000000015', 'hạnh phúc'), ('d0000000-0000-0000-0000-000000000015', 'tích cực'), ('d0000000-0000-0000-0000-000000000015', 'well-being'),
('d0000000-0000-0000-0000-000000000016', 'khủng hoảng'), ('d0000000-0000-0000-0000-000000000016', 'tự tử'), ('d0000000-0000-0000-0000-000000000016', 'cần giúp đỡ'),
('d0000000-0000-0000-0000-000000000017', 'mindfulness'), ('d0000000-0000-0000-0000-000000000017', 'thiền'), ('d0000000-0000-0000-0000-000000000017', 'chánh niệm'),
('d0000000-0000-0000-0000-000000000018', 'bản thân'), ('d0000000-0000-0000-0000-000000000018', 'self-care'), ('d0000000-0000-0000-0000-000000000018', 'yêu thương'),
('d0000000-0000-0000-0000-000000000019', 'ngủ'), ('d0000000-0000-0000-0000-000000000019', 'giấc ngủ'), ('d0000000-0000-0000-0000-000000000019', 'mất ngủ'),
('d0000000-0000-0000-0000-000000000020', 'bạn bè'), ('d0000000-0000-0000-0000-000000000020', 'cô đơn'), ('d0000000-0000-0000-0000-000000000020', 'kết nối')
ON CONFLICT DO NOTHING;
