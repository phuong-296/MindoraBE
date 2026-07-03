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
 'Harvard Study of Adult Development', 1, now()),

-- ── PANIC ATTACK / HOẢNG LOẠN CẤP TÍNH ──────────────────────────────────────
('d0000000-0000-0000-0000-000000000021',
 'Kỹ thuật kiểm soát cơn hoảng loạn (Panic Attack)',
 'Cơn hoảng loạn (panic attack) là sự gia tăng đột ngột của nỗi sợ hãi tột cùng kèm theo các triệu chứng thể chất như tim đập nhanh, nghẹn thở, chóng mặt. Cách ứng phó nhanh: 1. Thừa nhận cơn hoảng loạn: Tự nhủ "Đây chỉ là phản ứng sinh lý tạm thời, nó sẽ qua trong 10-15 phút và mình vẫn an toàn". 2. Thở chậm bằng bụng: Hít vào bằng mũi 4 giây, giữ 2 giây, thở ra bằng miệng 6 giây để kích hoạt hệ phó giao cảm. 3. Tập trung vào một điểm cố định trước mặt hoặc ôm lấy cơ thể để tạo cảm giác vững chãi. Tránh cố gắng chạy trốn hay chống cự, hãy thả lỏng và cho phép cơn sóng cảm xúc đi qua.',
 'CBT - Panic Management', 1, now()),

-- ── RELATIONSHIP STRESS / MỐI QUAN HỆ ────────────────────────────────────────
('d0000000-0000-0000-0000-000000000022',
 'Xử lý căng thẳng và giao tiếp trong mối quan hệ',
 'Mâu thuẫn và căng thẳng trong các mối quan hệ (gia đình, tình cảm, bạn bè) thường bắt nguồn từ giao tiếp thiếu hiệu quả. Liệu pháp nhận thức - hành vi (CBT) khuyên dùng kỹ thuật giao tiếp "Tôi" (I-messages) thay vì "Bạn" (You-messages) để tránh gây phòng thủ cho đối phương. Cú pháp: "Tôi cảm thấy [cảm xúc] khi [sự việc xảy ra], vì [lý do], tôi mong muốn [đề xuất cụ thể]". Ví dụ: Thay vì nói "Bạn lúc nào cũng bỏ bê tôi", hãy nói "Tôi cảm thấy tủi thân khi bạn không nhắn tin lúc đi làm về muộn, tôi hy vọng chúng ta có thể nhắn cho nhau một câu để an tâm". Phương pháp này giúp tập trung vào việc giải quyết nhu cầu hơn là đổ lỗi.',
 'CBT - Relationship Psychology', 1, now()),

-- ── ASSERTIVENESS / TỪ CHỐI LÀNH MẠNH ────────────────────────────────────────
('d0000000-0000-0000-0000-000000000023',
 'Kỹ năng từ chối lành mạnh và thiết lập ranh giới',
 'Thiết lập ranh giới cá nhân (boundaries) là nền tảng để bảo vệ sức khỏe tâm thần. Nhiều người rơi vào trạng thái burnout và căng thẳng do không biết cách từ chối (people-pleasing). Các bước thiết lập ranh giới lành mạnh: 1. Nhận diện nhu cầu và giới hạn của bản thân. 2. Nói "Không" một cách lịch sự nhưng dứt khoát, không cần viện cớ quá nhiều hay xin lỗi thái quá. Ví dụ: "Tôi hiểu công việc này quan trọng, nhưng lịch làm việc tuần này của tôi đã kín nên tôi không thể nhận thêm". 3. Chấp nhận sự không thoải mái ban đầu của người khác và tự nhắc nhở bản thân rằng từ chối công việc không có nghĩa là từ chối con người họ.',
 'Assertiveness Training - CBT', 1, now()),

-- ── COGNITIVE REFRAMING / TÁI CẤU TRÚC NHẬN THỨC ──────────────────────────────
('d0000000-0000-0000-0000-000000000024',
 'Kỹ thuật tái cấu trúc nhận thức (Cognitive Reframing)',
 'Tái cấu trúc nhận thức (Cognitive Reframing) là cốt lõi của liệu pháp CBT, giúp thay đổi cách chúng ta nhìn nhận các tình huống tiêu cực để giảm bớt đau khổ. Quy trình thực hiện: 1. Xác định suy nghĩ tự động tiêu cực (ví dụ: "Mình làm hỏng bài thuyết trình này rồi, mình là kẻ thất bại hoàn toàn"). 2. Tìm kiếm bằng chứng khách quan ủng hộ và phản đối suy nghĩ đó. 3. Viết lại một suy nghĩ cân bằng, thực tế hơn (ví dụ: "Bài thuyết trình có một số chỗ chưa tốt, nhưng mình đã hoàn thành và chuẩn bị kỹ lưỡng. Đây là cơ hội để mình rút kinh nghiệm lần sau, nó không quyết định giá trị con người mình"). Kỹ thuật này giúp phá vỡ các bẫy tư duy tiêu cực.',
 'CBT - Cognitive Restructuring', 1, now()),

-- ── PROCRASTINATION / TRÌ HOÃN DO TÂM LÝ ─────────────────────────────────────
('d0000000-0000-0000-0000-000000000025',
 'Vượt qua trì hoãn do tâm lý sợ thất bại',
 'Trì hoãn (procrastination) thường không phải do lười biếng mà là một cơ chế phòng vệ cảm xúc trước nỗi lo sợ thất bại, sự phán xét hoặc áp lực hoàn hảo. Để vượt qua: 1. Áp dụng "quy tắc 5 phút": Cam kết chỉ làm việc đó trong đúng 5 phút. Khi đã bắt đầu, rào cản tâm lý sẽ giảm và bạn dễ tiếp tục hơn. 2. Chia nhỏ mục tiêu cực đại thành các nhiệm vụ siêu nhỏ có thể hoàn thành trong 15-30 phút (kỹ thuật Pomodoro). 3. Thực hành tự trắc ẩn (self-compassion): Tha thứ cho những lần trì hoãn trước đó, vì tự trách móc chỉ làm tăng căng thẳng và dẫn đến trì hoãn tiếp theo. Hãy tập trung vào hành động tiến bước thay vì sự hoàn hảo.',
 'CBT - Procrastination Management', 1, now()),

('d0000000-0000-0000-0000-000000000026',
 'Liệu pháp nhận thức hành vi cho chứng mất ngủ (CBT-I)',
 'CBT-I (Cognitive Behavioral Therapy for Insomnia) là liệu pháp đầu tay điều trị mất ngủ mãn tính mà không dùng thuốc. Kỹ thuật cốt lõi: 1. Kiểm soát kích thích: Chỉ lên giường khi buồn ngủ; không xem điện thoại, đọc sách hoặc làm việc trên giường. Nếu sau 20 phút không ngủ được, hãy rời giường sang phòng khác làm việc nhẹ nhàng. 2. Hạn chế thời gian ngủ: Chỉ nằm trên giường đúng số giờ thực tế bạn có thể ngủ, tránh nằm trằn trọc làm tăng lo âu về giấc ngủ. 3. Vệ sinh giấc ngủ: Tránh caffeine sau 14h, tắt màn hình xanh trước ngủ 1 tiếng. Kỹ thuật này giúp não bộ tái thiết lập mối liên kết mạnh mẽ giữa chiếc giường và giấc ngủ ngon.',
 'CBT-I Sleep Academy', 1, now()),

('d0000000-0000-0000-0000-000000000027',
 'Vượt qua Hội chứng kẻ giả mạo (Impostor Syndrome)',
 'Hội chứng kẻ giả mạo là trạng thái tâm lý nghi ngờ khả năng của bản thân và luôn sợ bị người khác "phát hiện" mình là kẻ lừa dối, dù bạn có đầy đủ năng lực và thành tựu. Cách ứng phó: 1. Ghi nhận và đặt tên cảm xúc: Nhận diện rằng "Đây chỉ là suy nghĩ của hội chứng kẻ giả mạo, không phải sự thật". 2. Ghi chép nhật ký thành tích: Lưu lại những phản hồi tích cực, bằng chứng thực tế về năng lực của mình. 3. Thực hành tự trắc ẩn (self-compassion): Chấp nhận rằng ai cũng có sai sót và việc không biết mọi thứ là bình thường. Chia sẻ với đồng nghiệp đáng tin cậy cũng giúp bạn nhận ra nhiều người cũng có cảm giác giống bạn.',
 'Clinical Psychology - Impostor Syndrome', 1, now()),

('d0000000-0000-0000-0000-000000000028',
 'Kỹ thuật Time-out để kiểm soát cơn giận dữ',
 'Time-out (tạm ngắt) là công cụ kiểm soát cơn giận hiệu quả khi mâu thuẫn giao tiếp leo thang. Khi cảm thấy các dấu hiệu sinh lý của cơn giận (tim đập nhanh, thở gấp, cơ bắp căng thẳng): 1. Chủ động đề xuất tạm dừng: Nói rõ ràng với đối phương bằng giọng bình tĩnh: "Tôi đang cảm thấy nóng giận và không thể thảo luận tiếp một cách khách quan. Tôi cần 20 phút để bình tĩnh lại, sau đó mình sẽ nói tiếp". 2. Rời khỏi không gian tranh chấp: Đi bộ chậm, uống nước mát hoặc hít thở sâu bụng. 3. Không suy nghĩ kích động thêm: Tránh lặp đi lặp lại những suy nghĩ đổ lỗi trong đầu trong thời gian tạm ngắt để prefrontal cortex có đủ thời gian bình tĩnh lại.',
 'Anger Management - CBT', 1, now()),

('d0000000-0000-0000-0000-000000000029',
 'Giải tỏa sự cô đơn và xây dựng kết nối sâu sắc',
 'Cô đơn kéo dài gây hại cho sức khỏe tương đương hút thuốc và làm tăng nguy cơ trầm cảm. Để vượt qua cô đơn: 1. Thay đổi nhận thức: Cô đơn là một tín hiệu cảm xúc nhắc nhở ta cần kết nối xã hội, giống như đói nhắc ta ăn, không phải là một khiếm khuyết của bản thân. 2. Bắt đầu từ những tương tác nhỏ: Chào hỏi người hàng xóm, mỉm cười với nhân viên phục vụ để kích hoạt hormone oxytocin. 3. Tham gia các cộng đồng có cùng sở thích (câu lạc bộ sách, thiện nguyện, thể thao) để tạo cơ hội gặp gỡ tự nhiên. 4. Tập trung vào chất lượng hơn số lượng: Chỉ cần 1-2 mối quan hệ sâu sắc, sẵn sàng lắng nghe và chia sẻ là đủ để bảo vệ bạn khỏi cảm giác cô đơn.',
 'Harvard Loneliness Study', 1, now()),

('d0000000-0000-0000-0000-000000000030',
 'Kỹ thuật thiền quét cơ thể (Body Scan Meditation)',
 'Thiền quét cơ thể (Body Scan) là bài thực hành chánh niệm giúp giải tỏa căng thẳng thể chất và tinh thần cực kỳ hiệu quả, kết nối lại tâm trí và cơ thể. Cách thực hiện: 1. Nằm hoặc ngồi ở tư thế thoải mái nhất, nhắm mắt lại và thở tự nhiên. 2. Di chuyển sự chú ý của bạn chầm chậm qua từng vùng của cơ thể, bắt đầu từ ngón chân, bàn chân, cổ chân, bắp chân... đi dần lên đỉnh đầu. 3. Tại mỗi vùng, hãy cảm nhận các cảm giác sinh lý (nóng, lạnh, căng, đau, hoặc không có cảm giác gì) mà không phán xét. 4. Thở vào vùng đó và tưởng tượng hơi thở giúp giải phóng sự căng thẳng tích tụ. Thực hành 10-15 phút mỗi tối trước khi ngủ giúp cải thiện chất lượng giấc ngủ rõ rệt.',
 'MBSR - Kabat-Zinn Program', 1, now()),

('d0000000-0000-0000-0000-000000000031',
 'Liệu pháp Chấp nhận và Cam kết (ACT) cho lo âu',
 'Liệu pháp ACT (Acceptance and Commitment Therapy) tiếp cận lo âu bằng cách thay đổi mối quan hệ của bạn với lo âu thay vì cố gắng loại bỏ nó. Các nguyên lý chính: 1. Chấp nhận (Acceptance): Cho phép lo âu tồn tại như một cảm xúc tự nhiên của con người, ngưng chiến đấu chống lại nó. Tự nhủ "Tôi đang lo lắng, và cảm giác này không dễ chịu nhưng tôi có thể chịu đựng được". 2. Tách biệt nhận thức (Defusion): Xem suy nghĩ chỉ là ngôn từ, không phải sự thật tuyệt đối. Ví dụ: Thay vì nghĩ "Tôi sẽ thất bại", hãy nói "Tôi đang có suy nghĩ rằng mình sẽ thất bại". 3. Hành động theo giá trị sống (Committed Action): Dù lo âu có hiện diện, bạn vẫn cam kết hành động hướng tới những gì quan trọng nhất với cuộc sống của mình.',
 'ACT - Hayes Acceptance Therapy', 1, now()),

('d0000000-0000-0000-0000-000000000032',
 'Vượt qua nỗi đau buồn và mất mát (Grief & Loss)',
 'Mô hình Kübler-Ross mô tả 5 giai đoạn cảm xúc khi trải qua sự mất mát lớn (người thân qua đời, chia tay, mất việc): 1. Phủ nhận (Denial): Tránh né sự thật. 2. Giận dữ (Anger): Tự hỏi tại sao chuyện này lại xảy ra với mình. 3. Thương lượng (Bargaining): Cố gắng thay đổi thực tại bằng các giả định "nếu như". 4. Trầm cảm (Depression): Đối mặt với nỗi buồn sâu sắc. 5. Chấp nhận (Acceptance): Học cách sống chung với thực tại mới. Lưu ý: Các giai đoạn này không diễn ra theo trình tự tuyến tính mà có thể đảo lộn. Để vượt qua, hãy cho phép bản thân được khóc, được đau buồn và tìm kiếm sự nâng đỡ từ người thân hoặc chuyên gia tâm lý.',
 'Kübler-Ross Grief Model', 1, now()),

('d0000000-0000-0000-0000-000000000033',
 'Quản lý căng thẳng tài chính và lo âu cuộc sống',
 'Căng thẳng tài chính là nguồn cơn phổ biến dẫn đến rối loạn lo âu và trầm cảm. Các bước ứng phó tâm lý: 1. Đối mặt thực tế: Tránh né nhìn vào số dư tài khoản chỉ làm tăng lo âu mơ hồ. Hãy lập một bảng chi tiêu rõ ràng để kiểm soát. 2. Tách biệt giá trị bản thân khỏi tình hình tài chính: Sự giàu có hay nghèo khó tạm thời không quyết định giá trị hay đạo đức con người bạn. 3. Tập trung vào những thứ có thể kiểm soát: Bạn không thể kiểm soát nền kinh tế, nhưng bạn kiểm soát được chi tiêu cá nhân và việc tìm kiếm cơ hội mới. 4. Thực hành self-care giá rẻ: Tập thể dục ngoài trời, đọc sách thư viện, kết nối với bạn bè là những hoạt động miễn phí giúp duy trì năng lượng tinh thần.',
 'Financial Psychology & Wellbeing', 1, now()),

('d0000000-0000-0000-0000-000000000034',
 'Kỹ thuật thư giãn cơ bắp tiến triển (PMR)',
 'Thư giãn cơ bắp tiến triển (Progressive Muscle Relaxation - PMR) được chứng minh giúp làm giảm lo âu và stress bằng cách giải phóng căng thẳng thể lý tích tụ trong cơ. Cách thực hiện: 1. Ngồi hoặc nằm thoải mái. 2. Bắt đầu từ nhóm cơ đầu tiên (ví dụ bàn tay): Gồng chặt cơ tay hết mức trong 5 giây, cảm nhận sự căng tức. 3. Thả lỏng đột ngột hoàn toàn, thở ra và cảm nhận sự thư giãn lan tỏa trong cơ bắp trong 15 giây. 4. Di chuyển tiếp tục qua các nhóm cơ khác: bắp tay, vai, mặt, ngực, bụng, đùi, bàn chân. Kỹ thuật này giúp bạn nhạy bén hơn trong việc nhận biết và chủ động thả lỏng khi cơ thể bắt đầu căng thẳng vì lo âu.',
 'PMR - Jacobson Relaxation Technique', 1, now()),

('d0000000-0000-0000-0000-000000000035',
 'Đối phó với rối loạn trầm cảm theo mùa (SAD)',
 'Trầm cảm theo mùa (Seasonal Affective Disorder - SAD) là một dạng trầm cảm xuất hiện vào một thời điểm nhất định trong năm, phổ biến nhất là mùa đông khi ánh sáng mặt trời giảm đi làm xáo trộn nhịp sinh học và giảm lượng serotonin, melatonin của cơ thể. Cách ứng phó: 1. Liệu pháp ánh sáng: Tiếp xúc với ánh sáng tự nhiên ít nhất 20-30 phút vào buổi sáng hoặc dùng đèn giả lập ánh sáng mặt trời (light therapy box). 2. Kích hoạt hành vi: Lên kế hoạch vận động ngoài trời, đi bộ nhanh. 3. Bổ sung Vitamin D dưới sự hướng dẫn y tế và duy trì kết nối xã hội đều đặn để tránh xu hướng thu mình lại trong những ngày thời tiết u ám.',
 'WHO - Seasonal Depression Guidelines', 1, now()),

('d0000000-0000-0000-0000-000000000036',
 'Chữa lành tổn thương tâm lý quá khứ (Trauma & Resilience)',
 'Chữa lành tổn thương (trauma) từ quá khứ là một hành trình dài đòi hỏi sự kiên nhẫn và tự trắc ẩn. Tổn thương làm thay đổi cách hệ thần kinh phản ứng với các mối đe dọa, khiến bạn dễ rơi vào trạng thái cảnh giác quá mức hoặc tê liệt cảm xúc. Các bước xây dựng khả năng phục hồi (resilience): 1. Tạo sự an toàn: Xây dựng một không gian sống yên bình và thiết lập thói quen sinh hoạt ổn định. 2. Nhận diện các tác nhân kích thích (triggers) gợi lại ký ức cũ. 3. Thực hành tiếp đất (grounding) khi ký ức tràn về để nhắc nhở bản thân rằng bạn đang ở hiện tại và an toàn. 4. Đừng cố gắng tự giải quyết nếu tổn thương quá sâu, hãy tìm sự đồng hành từ nhà trị liệu tâm lý chuyên nghiệp.',
 'Trauma Recovery & Resilience Studies', 1, now()),

('d0000000-0000-0000-0000-000000000037',
 'Cân bằng giữa công việc và cuộc sống (Work-life balance)',
 'Thiếu cân bằng giữa công việc và cuộc sống riêng tư là nguyên nhân hàng đầu dẫn đến kiệt sức nghề nghiệp (burnout). Để thiết lập lại sự cân bằng: 1. Đặt ranh giới công nghệ: Tắt thông báo email và ứng dụng làm việc sau 19h tối. 2. Lập kế hoạch cho thời gian cá nhân: Coi thời gian dành cho gia đình, sở thích, hoặc nghỉ ngơi có giá trị tương đương một cuộc họp quan trọng của đối tác và không tự ý hủy bỏ. 3. Thực hành chánh niệm vi mô: Dành ra 3 phút giữa các ca làm việc để nhắm mắt hít thở sâu, reset lại tâm trí. 4. Nhận ra rằng năng suất làm việc của bạn phụ thuộc trực tiếp vào mức độ hồi phục năng lượng tinh thần sau giờ làm.',
 'Burnout Prevention - WHO', 1, now()),

('d0000000-0000-0000-0000-000000000038',
 'Vượt qua chứng lo âu xã hội (Social Anxiety)',
 'Lo âu xã hội (social anxiety) là nỗi sợ hãi kéo dài và dữ dội về việc bị người khác quan sát, phán xét hoặc làm bẽ mặt trong các tình huống xã hội. Cách ứng phó dựa trên CBT: 1. Nhận diện các bẫy suy nghĩ: Ví dụ bẫy "đọc suy nghĩ người khác" ("Chắc chắn họ đang nghĩ mình trông thật ngớ ngẩn"). 2. Chuyển hướng sự chú ý: Thay vì tập trung cao độ vào những lo lắng nội tâm hay biểu hiện của cơ thể (đổ mồ hôi, run giọng), hãy hướng sự tập trung ra bên ngoài, chú ý lắng nghe lời đối phương nói hoặc quan sát cảnh vật xung quanh. 3. Tiếp xúc dần dần (exposure): Bắt đầu từ những tương tác nhỏ nhất như hỏi đường người lạ, sau đó tăng dần độ khó để não bộ nhận ra tình huống xã hội không nguy hiểm như tưởng tượng.',
 'CBT - Social Anxiety Protocol', 1, now()),

('d0000000-0000-0000-0000-000000000039',
 'Nhận biết và ứng phó mối quan hệ độc hại',
 'Mối quan hệ độc hại (toxic relationship) là mối quan hệ khiến bạn cảm thấy kiệt quệ cảm xúc, bị thao túng, kiểm soát hoặc thiếu tôn trọng kéo dài. Các dấu hiệu nhận biết: 1. Thao túng tâm lý (gaslighting) khiến bạn nghi ngờ trí nhớ và nhận thức của chính mình. 2. Kiểm soát quá mức: Đối phương kiểm soát các mối quan hệ, tài chính hoặc cách ăn mặc của bạn. 3. Giao tiếp đổ lỗi, chỉ trích liên tục. Cách ứng phó: 1. Thừa nhận thực tế mà không tự đổ lỗi cho bản thân. 2. Thiết lập ranh giới cứng: Nói rõ những hành vi bạn không chấp nhận. 3. Xây dựng mạng lưới hỗ trợ xung quanh (bạn bè, gia đình, chuyên gia) và sẵn sàng chấm dứt mối quan hệ để bảo vệ sức khỏe tâm thần của mình.',
 'Relationship Psychology Guidelines', 1, now()),

('d0000000-0000-0000-0000-000000000040',
 'Nuôi dưỡng lòng tự tôn (Self-esteem) lành mạnh',
 'Lòng tự tôn (self-esteem) lành mạnh là sự trân trọng và đánh giá tích cực về giá trị bản thân. Thiếu lòng tự tôn dẫn đến sự tự ti, nhạy cảm quá mức trước lời phê bình và xu hướng làm hài lòng người khác (people-pleasing). Cách nuôi dưỡng: 1. Ngừng so sánh xã hội: Hạn chế lướt mạng xã hội khi đang cảm thấy tồi tệ; so sánh cuộc sống thực tế của bạn với "cuộc sống hoàn hảo đã qua chỉnh sửa" của người khác là không công bằng. 2. Thay đổi tự thoại (self-talk): Thay thế những lời chỉ trích bản thân bằng những lời động viên nhẹ nhàng. 3. Chấp nhận bản thân vô điều kiện: Nhận thức rằng giá trị của bạn là cố định, không phụ thuộc vào thành công hay thất bại tạm thời. 4. Đặt ra và hoàn thành những mục tiêu nhỏ hàng ngày để bồi đắp cảm giác tự chủ.',
 'Self-Esteem Research - CBT', 1, now())

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
('d0000000-0000-0000-0000-000000000020', 'sad'),
('d0000000-0000-0000-0000-000000000021', 'anxious'),
('d0000000-0000-0000-0000-000000000021', 'stressed'),
('d0000000-0000-0000-0000-000000000022', 'stressed'),
('d0000000-0000-0000-0000-000000000022', 'angry'),
('d0000000-0000-0000-0000-000000000022', 'sad'),
('d0000000-0000-0000-0000-000000000022', 'neutral'),
('d0000000-0000-0000-0000-000000000023', 'tired'),
('d0000000-0000-0000-0000-000000000023', 'stressed'),
('d0000000-0000-0000-0000-000000000023', 'neutral'),
('d0000000-0000-0000-0000-000000000024', 'sad'),
('d0000000-0000-0000-0000-000000000024', 'anxious'),
('d0000000-0000-0000-0000-000000000024', 'stressed'),
('d0000000-0000-0000-0000-000000000024', 'neutral'),
('d0000000-0000-0000-0000-000000000025', 'tired'),
('d0000000-0000-0000-0000-000000000025', 'stressed'),
('d0000000-0000-0000-0000-000000000025', 'neutral'),
('d0000000-0000-0000-0000-000000000026', 'tired'),
('d0000000-0000-0000-0000-000000000026', 'stressed'),
('d0000000-0000-0000-0000-000000000026', 'neutral'),
('d0000000-0000-0000-0000-000000000027', 'anxious'),
('d0000000-0000-0000-0000-000000000027', 'sad'),
('d0000000-0000-0000-0000-000000000027', 'neutral'),
('d0000000-0000-0000-0000-000000000028', 'angry'),
('d0000000-0000-0000-0000-000000000028', 'stressed'),
('d0000000-0000-0000-0000-000000000029', 'sad'),
('d0000000-0000-0000-0000-000000000029', 'neutral'),
('d0000000-0000-0000-0000-000000000030', 'tired'),
('d0000000-0000-0000-0000-000000000030', 'stressed'),
('d0000000-0000-0000-0000-000000000030', 'neutral'),
('d0000000-0000-0000-0000-000000000031', 'anxious'),
('d0000000-0000-0000-0000-000000000031', 'stressed'),
('d0000000-0000-0000-0000-000000000032', 'sad'),
('d0000000-0000-0000-0000-000000000032', 'anxious'),
('d0000000-0000-0000-0000-000000000033', 'stressed'),
('d0000000-0000-0000-0000-000000000033', 'anxious'),
('d0000000-0000-0000-0000-000000000033', 'neutral'),
('d0000000-0000-0000-0000-000000000034', 'anxious'),
('d0000000-0000-0000-0000-000000000034', 'stressed'),
('d0000000-0000-0000-0000-000000000034', 'neutral'),
('d0000000-0000-0000-0000-000000000035', 'sad'),
('d0000000-0000-0000-0000-000000000035', 'tired'),
('d0000000-0000-0000-0000-000000000036', 'sad'),
('d0000000-0000-0000-0000-000000000036', 'anxious'),
('d0000000-0000-0000-0000-000000000036', 'stressed'),
('d0000000-0000-0000-0000-000000000037', 'tired'),
('d0000000-0000-0000-0000-000000000037', 'stressed'),
('d0000000-0000-0000-0000-000000000037', 'neutral'),
('d0000000-0000-0000-0000-000000000038', 'anxious'),
('d0000000-0000-0000-0000-000000000038', 'stressed'),
('d0000000-0000-0000-0000-000000000039', 'sad'),
('d0000000-0000-0000-0000-000000000039', 'angry'),
('d0000000-0000-0000-0000-000000000039', 'stressed'),
('d0000000-0000-0000-0000-000000000040', 'sad'),
('d0000000-0000-0000-0000-000000000040', 'neutral')
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
('d0000000-0000-0000-0000-000000000020', 'bạn bè'), ('d0000000-0000-0000-0000-000000000020', 'cô đơn'), ('d0000000-0000-0000-0000-000000000020', 'kết nối'),
('d0000000-0000-0000-0000-000000000021', 'hoảng loạn'), ('d0000000-0000-0000-0000-000000000021', 'panic'), ('d0000000-0000-0000-0000-000000000021', 'cơn hoảng loạn'),
('d0000000-0000-0000-0000-000000000022', 'mối quan hệ'), ('d0000000-0000-0000-0000-000000000022', 'giao tiếp'), ('d0000000-0000-0000-0000-000000000022', 'căng thẳng'),
('d0000000-0000-0000-0000-000000000023', 'ranh giới'), ('d0000000-0000-0000-0000-000000000023', 'từ chối'), ('d0000000-0000-0000-0000-000000000023', 'burnout'),
('d0000000-0000-0000-0000-000000000024', 'suy nghĩ'), ('d0000000-0000-0000-0000-000000000024', 'tư duy'), ('d0000000-0000-0000-0000-000000000024', 'reframing'),
('d0000000-0000-0000-0000-000000000025', 'trì hoãn'), ('d0000000-0000-0000-0000-000000000025', 'pomodoro'), ('d0000000-0000-0000-0000-000000000025', 'sợ thất bại'),
('d0000000-0000-0000-0000-000000000026', 'mất ngủ'), ('d0000000-0000-0000-0000-000000000026', 'insomnia'), ('d0000000-0000-0000-0000-000000000026', 'ngủ'),
('d0000000-0000-0000-0000-000000000027', 'giả mạo'), ('d0000000-0000-0000-0000-000000000027', 'impostor'), ('d0000000-0000-0000-0000-000000000027', 'tự ti'),
('d0000000-0000-0000-0000-000000000028', 'giận'), ('d0000000-0000-0000-0000-000000000028', 'time-out'), ('d0000000-0000-0000-0000-000000000028', 'tức giận'),
('d0000000-0000-0000-0000-000000000029', 'cô đơn'), ('d0000000-0000-0000-0000-000000000029', 'kết nối'), ('d0000000-0000-0000-0000-000000000029', 'bạn bè'),
('d0000000-0000-0000-0000-000000000030', 'quét cơ thể'), ('d0000000-0000-0000-0000-000000000030', 'body scan'), ('d0000000-0000-0000-0000-000000000030', 'thiền'),
('d0000000-0000-0000-0000-000000000031', 'chấp nhận'), ('d0000000-0000-0000-0000-000000000031', 'act'), ('d0000000-0000-0000-0000-000000000031', 'lo âu'),
('d0000000-0000-0000-0000-000000000032', 'đau buồn'), ('d0000000-0000-0000-0000-000000000032', 'grief'), ('d0000000-0000-0000-0000-000000000032', 'mất mát'),
('d0000000-0000-0000-0000-000000000033', 'tài chính'), ('d0000000-0000-0000-0000-000000000033', 'tiền'), ('d0000000-0000-0000-0000-000000000033', 'lo âu'),
('d0000000-0000-0000-0000-000000000034', 'thư giãn cơ'), ('d0000000-0000-0000-0000-000000000034', 'pmr'), ('d0000000-0000-0000-0000-000000000034', 'thả lỏng'),
('d0000000-0000-0000-0000-000000000035', 'theo mùa'), ('d0000000-0000-0000-0000-000000000035', 'sad'), ('d0000000-0000-0000-0000-000000000035', 'mùa đông'),
('d0000000-0000-0000-0000-000000000036', 'tổn thương'), ('d0000000-0000-0000-0000-000000000036', 'trauma'), ('d0000000-0000-0000-0000-000000000036', 'chữa lành'),
('d0000000-0000-0000-0000-000000000037', 'cân bằng'), ('d0000000-0000-0000-0000-000000000037', 'làm việc'), ('d0000000-0000-0000-0000-000000000037', 'burnout'),
('d0000000-0000-0000-0000-000000000038', 'xã hội'), ('d0000000-0000-0000-0000-000000000038', 'lo âu xã hội'), ('d0000000-0000-0000-0000-000000000038', 'giao tiếp'),
('d0000000-0000-0000-0000-000000000039', 'độc hại'), ('d0000000-0000-0000-0000-000000000039', 'toxic'), ('d0000000-0000-0000-0000-000000000039', 'mối quan hệ'),
('d0000000-0000-0000-0000-000000000040', 'tự tôn'), ('d0000000-0000-0000-0000-000000000040', 'giá trị'), ('d0000000-0000-0000-0000-000000000040', 'self-esteem')
ON CONFLICT DO NOTHING;
