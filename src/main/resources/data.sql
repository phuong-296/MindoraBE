-- ============================================================
-- Mindora AI — Seed Data
-- Chạy lại bao nhiêu lần cũng không bị trùng (ON CONFLICT DO NOTHING)
-- ============================================================

-- ===================== ROLES =====================
INSERT INTO roles (id, name, description, created_at) VALUES
  ('a0000000-0000-0000-0000-000000000001', 'USER',   'Người dùng thông thường', now()),
  ('a0000000-0000-0000-0000-000000000002', 'EXPERT', 'Chuyên gia tâm lý',       now()),
  ('a0000000-0000-0000-0000-000000000003', 'ADMIN',  'Quản trị viên',           now())
ON CONFLICT (name) DO NOTHING;


-- ===================== USERS =====================
-- 5 users: 3 regular, 2 experts (cũng có role USER)
INSERT INTO users (id, full_name, email, password, avatar_url, is_active, created_at, updated_at) VALUES
  ('b0000000-0000-0000-0000-000000000001', 'Nguyễn Văn An',     'an@gmail.com',       '$2a$10$Idolu7UZNw16caH.1jayaucgj/KsJK4OLztdKZfYb1c7i1qSogGGW', NULL, true, now() - interval '30 days', now()),
  ('b0000000-0000-0000-0000-000000000002', 'Trần Thị Bình',     'binh@gmail.com',     '$2a$10$Idolu7UZNw16caH.1jayaucgj/KsJK4OLztdKZfYb1c7i1qSogGGW', NULL, true, now() - interval '25 days', now()),
  ('b0000000-0000-0000-0000-000000000003', 'Lê Minh Cường',     'cuong@gmail.com',    '$2a$10$Idolu7UZNw16caH.1jayaucgj/KsJK4OLztdKZfYb1c7i1qSogGGW', NULL, true, now() - interval '20 days', now()),
  ('b0000000-0000-0000-0000-000000000004', 'TS. Phạm Thị Dung', 'dung@expert.com',    '$2a$10$Idolu7UZNw16caH.1jayaucgj/KsJK4OLztdKZfYb1c7i1qSogGGW', NULL, true, now() - interval '40 days', now()),
  ('b0000000-0000-0000-0000-000000000005', 'ThS. Hoàng Văn Em',  'em@expert.com',      '$2a$10$Idolu7UZNw16caH.1jayaucgj/KsJK4OLztdKZfYb1c7i1qSogGGW', NULL, true, now() - interval '35 days', now()),
  ('b0000000-0000-0000-0000-000000000006', 'Admin Mindora',      'admin@mindora.com',  '$2a$10$Idolu7UZNw16caH.1jayaucgj/KsJK4OLztdKZfYb1c7i1qSogGGW', NULL, true, now() - interval '60 days', now())
ON CONFLICT (email) DO NOTHING;


-- ===================== USER_ROLES =====================
INSERT INTO user_roles (id, user_id, role_id, assigned_at, created_at) VALUES
  -- Regular users → role USER
  ('c0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', now(), now()),
  ('c0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001', now(), now()),
  ('c0000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000001', now(), now()),
  -- Expert users → role USER + EXPERT
  ('c0000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000001', now(), now()),
  ('c0000000-0000-0000-0000-000000000005', 'b0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000002', now(), now()),
  ('c0000000-0000-0000-0000-000000000006', 'b0000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000001', now(), now()),
  ('c0000000-0000-0000-0000-000000000007', 'b0000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000002', now(), now()),
  -- Admin → role USER + ADMIN
  ('c0000000-0000-0000-0000-000000000008', 'b0000000-0000-0000-0000-000000000006', 'a0000000-0000-0000-0000-000000000001', now(), now()),
  ('c0000000-0000-0000-0000-000000000009', 'b0000000-0000-0000-0000-000000000006', 'a0000000-0000-0000-0000-000000000003', now(), now())
ON CONFLICT (user_id, role_id) DO NOTHING;


-- ===================== AI CONVERSATIONS =====================
INSERT INTO ai_conversations (id, user_id, title, is_archived, created_at, updated_at) VALUES
                                                                                           -- User An: 2 conversations
                                                                                           ('f0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'Lo lắng về kỳ thi', false, now() - interval '28 days', now() - interval '28 days'),
                                                                                           ('f0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001', 'Tâm sự về công việc',               false, now() - interval '5 days',  now()),
                                                                                           -- User Bình: 1 conversation
                                                                                           ('f0000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000002', 'Cuộc trò chuyện đầu tiên',          false, now() - interval '20 days', now() - interval '3 days'),
                                                                                           -- User Cường: 1 conversation
                                                                                           ('f0000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000003', 'Cuộc trò chuyện đầu tiên',          false, now() - interval '15 days', now() - interval '2 days')
ON CONFLICT (id) DO NOTHING;


-- ===================== MESSAGES =====================
INSERT INTO messages (id, conversation_id, role, content, detected_emotion, sentiment_score, created_at) VALUES
  -- Conversation 1 (An): chào hỏi + lo âu
  ('10000000-0000-0000-0000-000000000001', 'f0000000-0000-0000-0000-000000000001', 'USER',
   'Xin chào, mình cảm thấy hơi lo lắng gần đây. Mình không biết phải làm sao.',
   'anxious', 0.35, now() - interval '28 days'),
  ('10000000-0000-0000-0000-000000000002', 'f0000000-0000-0000-0000-000000000001', 'AI',
   'Chào bạn! Cảm ơn bạn đã chia sẻ. Lo lắng là một cảm xúc bình thường và mình ở đây để lắng nghe bạn. Bạn có thể kể thêm về những gì khiến bạn lo lắng không?',
   NULL, NULL, now() - interval '28 days' + interval '10 seconds'),
  ('10000000-0000-0000-0000-000000000003', 'f0000000-0000-0000-0000-000000000001', 'USER',
   'Mình sắp thi cuối kỳ mà chưa ôn được gì nhiều. Mình sợ sẽ trượt và bố mẹ buồn.',
   'anxious', 0.30, now() - interval '28 days' + interval '2 minutes'),
  ('10000000-0000-0000-0000-000000000004', 'f0000000-0000-0000-0000-000000000001', 'AI',
   'Mình hiểu, áp lực thi cử thực sự rất nặng nề. Đây là một vài gợi ý có thể giúp bạn:\n\n1. **Lập kế hoạch ôn thi** — chia nhỏ từng môn, mỗi ngày một chút\n2. **Nghỉ ngơi đủ giấc** — não bộ cần ngủ để ghi nhớ tốt\n3. **Thở sâu 4-7-8** — hít vào 4 giây, giữ 7 giây, thở ra 8 giây khi lo lắng\n\nBạn muốn thử phương pháp nào trước?',
   NULL, NULL, now() - interval '28 days' + interval '2 minutes 15 seconds'),

  -- Conversation 2 (An): công việc
  ('10000000-0000-0000-0000-000000000005', 'f0000000-0000-0000-0000-000000000002', 'USER',
   'Hôm nay đi làm bị sếp mắng trước mặt mọi người, mình cảm thấy rất tệ.',
   'sad', 0.20, now() - interval '5 days'),
  ('10000000-0000-0000-0000-000000000006', 'f0000000-0000-0000-0000-000000000002', 'AI',
   'Mình rất tiếc khi nghe điều đó. Bị phê bình công khai có thể khiến bạn cảm thấy tổn thương và xấu hổ — đó là phản ứng hoàn toàn bình thường. Bạn có muốn chia sẻ thêm về những gì đã xảy ra không?',
   NULL, NULL, now() - interval '5 days' + interval '8 seconds'),
  ('10000000-0000-0000-0000-000000000007', 'f0000000-0000-0000-0000-000000000002', 'USER',
   'Mình chỉ chậm deadline 1 ngày thôi mà bị nói nặng lời lắm. Mình thấy mình vô dụng.',
   'sad', 0.15, now() - interval '5 days' + interval '3 minutes'),
  ('10000000-0000-0000-0000-000000000008', 'f0000000-0000-0000-0000-000000000002', 'AI',
   'Mình muốn bạn biết rằng: bạn KHÔNG vô dụng. Trễ deadline 1 ngày không định nghĩa giá trị của bạn. Mỗi người đều có lúc mắc sai lầm.\n\nHãy thử tách biệt: hành vi (trễ deadline) khác với con người bạn (có giá trị). Bạn thấy sao nếu tối nay viết nhật ký về cảm xúc hôm nay? Viết ra giúp giải tỏa rất nhiều đấy.',
   NULL, NULL, now() - interval '5 days' + interval '3 minutes 20 seconds'),

  -- Conversation 3 (Bình): mất ngủ
  ('10000000-0000-0000-0000-000000000009', 'f0000000-0000-0000-0000-000000000003', 'USER',
   'Mấy tuần nay mình bị mất ngủ, cứ nằm xuống là suy nghĩ lung tung.',
   'tired', 0.40, now() - interval '20 days'),
  ('10000000-0000-0000-0000-000000000010', 'f0000000-0000-0000-0000-000000000003', 'AI',
   'Mất ngủ kéo dài rất ảnh hưởng đến sức khỏe tinh thần. Bạn thường suy nghĩ về điều gì khi nằm xuống? Và tình trạng này kéo dài bao lâu rồi?',
   NULL, NULL, now() - interval '20 days' + interval '12 seconds'),

  -- Conversation 4 (Cường): tích cực
  ('10000000-0000-0000-0000-000000000011', 'f0000000-0000-0000-0000-000000000004', 'USER',
   'Hôm nay mình vui lắm vì đậu kỳ thi! Cảm ơn Mindora đã giúp mình vượt qua giai đoạn khó khăn.',
   'happy', 0.90, now() - interval '15 days'),
  ('10000000-0000-0000-0000-000000000012', 'f0000000-0000-0000-0000-000000000004', 'AI',
   'Chúc mừng bạn!  Mình rất vui khi biết bạn đã vượt qua. Thành công này là nhờ nỗ lực của chính bạn đấy. Hãy tiếp tục giữ thói quen chăm sóc bản thân nhé!',
   NULL, NULL, now() - interval '15 days' + interval '5 seconds')
ON CONFLICT (id) DO NOTHING;


-- ===================== JOURNAL ENTRIES =====================
INSERT INTO journal_entries (id, user_id, title, content, mood_value, tags, entry_date, created_at, updated_at) VALUES
  ('20000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001',
   'Ngày đầu tiên viết nhật ký',
   'Hôm nay mình bắt đầu viết nhật ký. Cảm giác hơi lạ nhưng mà cũng thú vị. Mình hy vọng việc này sẽ giúp mình hiểu bản thân hơn. Buổi sáng mình đi chạy bộ 30 phút, cảm thấy thoải mái hơn nhiều.',
   'happy', ARRAY['nhật-ký','bắt-đầu','chạy-bộ'],
   CURRENT_DATE - interval '25 days', now() - interval '25 days', now() - interval '25 days'),

  ('20000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001',
   'Áp lực thi cử',
   'Kỳ thi cuối kỳ sắp đến mà mình chưa ôn được bao nhiêu. Cảm giác lo lắng, tim đập nhanh mỗi khi nghĩ về nó. Mình đã thử phương pháp thở 4-7-8 mà Mindora gợi ý, có đỡ hơn một chút.',
   'anxious', ARRAY['thi-cử','lo-lắng','thở-sâu'],
   CURRENT_DATE - interval '20 days', now() - interval '20 days', now() - interval '20 days'),

  ('20000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000001',
   'Bị sếp mắng',
   'Ngày tồi tệ nhất từ đầu tháng. Sếp mắng mình trước mặt cả team chỉ vì trễ deadline 1 ngày. Mình cảm thấy rất xấu hổ và tự ti. Tối về mình không muốn ăn gì cả.',
   'sad', ARRAY['công-việc','stress','tự-ti'],
   CURRENT_DATE - interval '5 days', now() - interval '5 days', now() - interval '5 days'),

  ('20000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000002',
   'Suy nghĩ lúc mất ngủ',
   'Đêm nay lại không ngủ được. Mình cứ nghĩ về tương lai, không biết mình đang đi đúng hướng không. Bạn bè ai cũng có việc làm tốt, còn mình thì vẫn loay hoay. Cảm giác cô đơn lắm.',
   'sad', ARRAY['mất-ngủ','tương-lai','cô-đơn'],
   CURRENT_DATE - interval '18 days', now() - interval '18 days', now() - interval '18 days'),

  ('20000000-0000-0000-0000-000000000005', 'b0000000-0000-0000-0000-000000000002',
   'Hẹn gặp bạn cũ',
   'Hôm nay gặp lại bạn đại học sau 2 năm. Nói chuyện xong thấy vui hẳn lên. Hoá ra ai cũng có khó khăn riêng, mình không cô đơn như mình nghĩ.',
   'happy', ARRAY['bạn-bè','vui','kết-nối'],
   CURRENT_DATE - interval '10 days', now() - interval '10 days', now() - interval '10 days'),

  ('20000000-0000-0000-0000-000000000006', 'b0000000-0000-0000-0000-000000000003',
   'Đậu kỳ thi rồi!',
   'ĐẬUUUU! Mình không tin nổi luôn. Điểm cao hơn mong đợi nữa. Cảm ơn mình đã không bỏ cuộc. Tối nay sẽ ăn mừng với gia đình!',
   'loved', ARRAY['thi-cử','thành-công','hạnh-phúc'],
   CURRENT_DATE - interval '15 days', now() - interval '15 days', now() - interval '15 days')
ON CONFLICT (id) DO NOTHING;


-- ===================== MOOD LOGS =====================
-- User An: mood logs cho 7 ngày gần nhất
INSERT INTO mood_logs (id, user_id, mood_score, mood_emoji, note, log_date, created_at) VALUES
  ('30000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 4, '😐', 'Ngày bình thường',                    CURRENT_DATE - interval '7 days', now() - interval '7 days'),
  ('30000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001', 3, '😔', 'Hơi buồn vì trời mưa',               CURRENT_DATE - interval '6 days', now() - interval '6 days'),
  ('30000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000001', 5, '🙂', 'Đi cafe với bạn',                     CURRENT_DATE - interval '5 days', now() - interval '5 days'),
  ('30000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000001', 2, '😔', 'Bị sếp mắng',                         CURRENT_DATE - interval '4 days', now() - interval '4 days'),
  ('30000000-0000-0000-0000-000000000005', 'b0000000-0000-0000-0000-000000000001', 3, '😰', 'Vẫn còn buồn về chuyện hôm qua',     CURRENT_DATE - interval '3 days', now() - interval '3 days'),
  ('30000000-0000-0000-0000-000000000006', 'b0000000-0000-0000-0000-000000000001', 5, '🙂', 'Tập thể dục buổi sáng, khá hơn rồi', CURRENT_DATE - interval '2 days', now() - interval '2 days'),
  ('30000000-0000-0000-0000-000000000007', 'b0000000-0000-0000-0000-000000000001', 6, '😊', 'Hoàn thành project đúng hạn!',        CURRENT_DATE - interval '1 day',  now() - interval '1 day'),

  -- User Bình: mood logs
  ('30000000-0000-0000-0000-000000000008', 'b0000000-0000-0000-0000-000000000002', 2, '😪', 'Lại mất ngủ',                         CURRENT_DATE - interval '5 days', now() - interval '5 days'),
  ('30000000-0000-0000-0000-000000000009', 'b0000000-0000-0000-0000-000000000002', 3, '😔', 'Mệt mỏi',                             CURRENT_DATE - interval '4 days', now() - interval '4 days'),
  ('30000000-0000-0000-0000-000000000010', 'b0000000-0000-0000-0000-000000000002', 5, '🙂', 'Gặp lại bạn cũ!',                     CURRENT_DATE - interval '3 days', now() - interval '3 days'),
  ('30000000-0000-0000-0000-000000000011', 'b0000000-0000-0000-0000-000000000002', 4, '😐', 'Ổn hơn',                              CURRENT_DATE - interval '2 days', now() - interval '2 days'),

  -- User Cường: mood logs
  ('30000000-0000-0000-0000-000000000012', 'b0000000-0000-0000-0000-000000000003', 3, '😰', 'Lo lắng chờ kết quả thi',             CURRENT_DATE - interval '16 days', now() - interval '16 days'),
  ('30000000-0000-0000-0000-000000000013', 'b0000000-0000-0000-0000-000000000003', 7, '🥰', 'ĐẬUUUUU!!! Hạnh phúc quá!',           CURRENT_DATE - interval '15 days', now() - interval '15 days'),
  ('30000000-0000-0000-0000-000000000014', 'b0000000-0000-0000-0000-000000000003', 6, '😊', 'Vẫn vui vì kết quả thi',              CURRENT_DATE - interval '14 days', now() - interval '14 days')
ON CONFLICT DO NOTHING;


-- ===================== CONTENT LIBRARY =====================
-- content_url: file audio royalty-free (SoundHelix) — phát FULL cho mọi người, không cần đăng nhập.
-- spotify_url: link bài hát thật trên Spotify (tùy chọn) — FE hiển thị nút "Nghe trên Spotify";
--              chỉ phát full khi người nghe tự đăng nhập Spotify trên trình duyệt của họ.
-- youtube_id: video ID của MV/audio chính thức trên YouTube — nhúng iframe, phát bài hát THẬT
--             (có lời) miễn phí, không cần đăng nhập (đã xác minh qua YouTube oEmbed API).
INSERT INTO content_library (id, title, content_type, description, thumbnail_url, content_url, spotify_url, youtube_id, mood_tag, duration_minutes, is_active, created_by, created_at, updated_at) VALUES
  -- Nhạc Việt hot hit giới trẻ
  ('40000000-0000-0000-0000-000000000001', 'Đôi Cánh — Chillies',                   'music',
   'Indie-pop nhẹ nhàng, giai điệu bay bổng của Chillies — giúp thư thái và tập trung.',
   'https://images.unsplash.com/photo-1501426026826-31c667bdf23d?w=400', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3', 'https://open.spotify.com/track/4RVUWunypHM4h54V9yIC9D', NULL,
   'calm', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '50 days', now()),

  ('40000000-0000-0000-0000-000000000002', 'Nếu Điều Đó Xảy Ra — Hoàng Dũng',       'music',
   'Ballad R&B êm dịu của Hoàng Dũng, giai điệu chậm rãi dễ đưa bạn vào giấc ngủ.',
   'https://images.unsplash.com/photo-1520523839897-bd0b52f945a0?w=400', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3', 'https://open.spotify.com/track/7lmsyQicOfzzk3U0rfhMds', NULL,
   'sleep', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '50 days', now()),

  ('40000000-0000-0000-0000-000000000003', 'Vùng Ký Ức — Chillies',                 'music',
   'Giai điệu indie chill sâu lắng, phù hợp để thiền và thư giãn cùng những hoài niệm.',
   'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=400', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3', 'https://open.spotify.com/track/3H6nzsPNMttEvUl3ai3xN8', 'T0sHaz4H9MQ',
   'calm', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '48 days', now()),

  -- Bài viết
  ('40000000-0000-0000-0000-000000000004', '5 cách đối phó với lo âu hiệu quả',    'article',
   'Tìm hiểu 5 phương pháp khoa học đã được chứng minh giúp giảm lo âu: thở sâu, thiền chánh niệm, vận động, viết nhật ký và kết nối xã hội.',
   'https://images.unsplash.com/photo-1506126613408-eca07ce68773', NULL, NULL, NULL,
   'calm', 8, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '45 days', now()),

  ('40000000-0000-0000-0000-000000000005', 'Hiểu về trầm cảm: Dấu hiệu và cách tìm kiếm giúp đỡ', 'article',
   'Trầm cảm không chỉ là buồn bã. Bài viết giải thích các dấu hiệu nhận biết và khi nào nên tìm đến chuyên gia.',
   'https://images.unsplash.com/photo-1493836512294-502baa1986e2', NULL, NULL, NULL,
   'sad', 12, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '42 days', now()),

  ('40000000-0000-0000-0000-000000000006', 'Tại sao giấc ngủ quan trọng với sức khỏe tâm thần?', 'article',
   'Ngủ không đủ giấc ảnh hưởng nghiêm trọng đến cảm xúc và khả năng ra quyết định. Tìm hiểu cách cải thiện chất lượng giấc ngủ.',
   'https://images.unsplash.com/photo-1541781774459-bb2af2f05b55', NULL, NULL, NULL,
   'sleep', 10, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '40 days', now()),

  -- Video
  ('40000000-0000-0000-0000-000000000007', 'Thiền chánh niệm 10 phút cho người mới', 'video',
   'Video hướng dẫn thiền chánh niệm cơ bản, phù hợp cho người chưa từng thiền. Giọng hướng dẫn nhẹ nhàng bằng tiếng Việt.',
   'https://images.unsplash.com/photo-1508672019048-805c876b67e2', 'https://example.com/video/meditation-10min.mp4', NULL, NULL,
   'calm', 10, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '38 days', now()),

  ('40000000-0000-0000-0000-000000000008', 'Yoga buổi sáng — Nạp năng lượng',      'video',
   'Bài tập yoga 15 phút giúp bạn khởi đầu ngày mới tràn đầy năng lượng và tinh thần tích cực.',
   'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b', 'https://example.com/video/morning-yoga.mp4', NULL, NULL,
   'energy', 15, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '35 days', now()),

  -- Bài tập
  ('40000000-0000-0000-0000-000000000009', 'Kỹ thuật thở 4-7-8 giảm lo âu',       'exercise',
   'Bài tập thở đơn giản: hít vào 4 giây, giữ hơi 7 giây, thở ra 8 giây. Lặp lại 4 lần. Hiệu quả ngay lập tức khi lo âu.',
   'https://images.unsplash.com/photo-1506126613408-eca07ce68773', NULL, NULL, NULL,
   'calm', 5, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '50 days', now()),

  ('40000000-0000-0000-0000-000000000010', 'Bài tập gratitude — 3 điều biết ơn',   'exercise',
   'Mỗi tối trước khi ngủ, viết ra 3 điều bạn biết ơn trong ngày. Nghiên cứu cho thấy bài tập này giúp cải thiện hạnh phúc đáng kể sau 2 tuần.',
   'https://images.unsplash.com/photo-1455849318743-b2233052fcff', NULL, NULL, NULL,
   'happy', 5, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '48 days', now())
ON CONFLICT (id) DO UPDATE SET
  title = EXCLUDED.title,
  content_type = EXCLUDED.content_type,
  description = EXCLUDED.description,
  thumbnail_url = EXCLUDED.thumbnail_url,
  content_url = EXCLUDED.content_url,
  spotify_url = EXCLUDED.spotify_url,
  youtube_id = EXCLUDED.youtube_id,
  mood_tag = EXCLUDED.mood_tag,
  duration_minutes = EXCLUDED.duration_minutes,
  is_active = EXCLUDED.is_active,
  updated_at = now();


-- ============================================================
-- NHẠC VIỆT HOT HIT GIỚI TRẺ (cập nhật 07/2026)
-- content_url: file audio royalty-free (SoundHelix) — phát FULL cho mọi người, không cần đăng nhập.
-- spotify_url: link bài hát THẬT trên Spotify (đã xác minh qua Spotify oEmbed API) —
--              đây là các ca khúc có bản quyền thương mại nên chỉ Spotify (đăng nhập)
--              mới được phép phát nguyên bản hợp pháp; content_url chỉ là nhạc nền demo.
-- youtube_id: video ID MV/audio chính thức trên YouTube (đã xác minh qua YouTube oEmbed API) —
--             nhúng iframe để nghe/xem bài hát THẬT (có lời), miễn phí, không cần đăng nhập.
-- mood_tag: calm | sleep | happy | sad | energy
-- ============================================================

INSERT INTO content_library (id, title, content_type, description, thumbnail_url, content_url, spotify_url, youtube_id, mood_tag, duration_minutes, is_active, created_by, created_at, updated_at) VALUES

  -- ======= HAPPY - Vui vẻ / Tích cực (5 bài) =======
  ('40000000-0000-0000-0000-000000000011',
   'Come My Way — Sơn Tùng M-TP, Tyga',
   'music',
   'Bản song ca R&B pha Afrobeats giữa Sơn Tùng M-TP và rapper Mỹ Tyga, giai điệu cuốn hút mang tinh thần tự hào Việt Nam ra thế giới.',
   'https://images.unsplash.com/photo-1470252649378-9c29740c9fa8?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3', 'https://open.spotify.com/track/7326q9PcJDcKuAFcrSkN0t', 'slTDF0RB_nE',
   'happy', 3, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '46 days', now()),

  ('40000000-0000-0000-0000-000000000012',
   'Vạn Sự Như Ý — Trúc Nhân',
   'music',
   'Ca khúc Tết rộn ràng của Trúc Nhân, giai điệu tươi vui gửi gắm lời chúc bình an và may mắn.',
   'https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3', 'https://open.spotify.com/track/1HIGvBEkqODXGfmfK3eJgc', 'hjYOanJelUs',
   'happy', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '44 days', now()),

  ('40000000-0000-0000-0000-000000000013',
   'Không Thử Sao Biết — Wren Evans, itsnk',
   'music',
   'Bản pop điện tử sôi động từ album "Nổ" của Wren Evans, tiết tấu bắt tai truyền cảm hứng dám thử dám làm.',
   'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3', 'https://open.spotify.com/track/0PCmfXVEN74USLLa0P5qOa', 'avUs1Uagm3Y',
   'happy', 3, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '43 days', now()),

  ('40000000-0000-0000-0000-000000000014',
   'Bắc Bling (Bắc Ninh) — Hoà Minzy, NSƯT Xuân Hinh, Tuấn Cry',
   'music',
   'Bản hit lớn nhất năm 2025 của Hoà Minzy, kết hợp dân ca quan họ với nhịp điệu hiện đại đầy tự hào.',
   'https://images.unsplash.com/photo-1448375240586-882707db888b?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3', 'https://open.spotify.com/track/4L2xuczFpbb7cjXebOBNkt', 'CL13X-8o4h0',
   'happy', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '42 days', now()),

  ('40000000-0000-0000-0000-000000000015',
   'Bài Này Chill Phết — Đen, MIN',
   'music',
   'Giai điệu chill vui tươi của Đen Vâu và MIN, đúng như tên gọi — nghe là thấy nhẹ lòng.',
   'https://images.unsplash.com/photo-1499364615650-ec38552f4f34?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3', 'https://open.spotify.com/track/2nR51wakN5K3AJENqGaNg9', 'ddaEtFOsFeM',
   'happy', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '27 days', now()),

  -- ======= ENERGY - Tăng năng lượng / Vận động (6 bài) =======
  ('40000000-0000-0000-0000-000000000016',
   '144 — Wren Evans, itsnk',
   'music',
   'Track điện tử sôi động trong album "Nổ", nhịp bass mạnh mẽ tiếp thêm năng lượng vận động.',
   'https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3', 'https://open.spotify.com/track/5DrsJFu6xuC7ZwFgUKExq5', 'wKp6IMoVwSI',
   'energy', 3, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '41 days', now()),

  ('40000000-0000-0000-0000-000000000017',
   'Không Thể Say — HIEUTHUHAI',
   'music',
   'Bản rap-pop cuốn hút với flow dứt khoát của HIEUTHUHAI, thích hợp khi cần bùng nổ năng lượng.',
   'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3', 'https://open.spotify.com/track/1K0HQ30Wc11okzlcnFA7Ub', 'i0nd3NPJ4MI',
   'energy', 3, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '40 days', now()),

  ('40000000-0000-0000-0000-000000000018',
   'Đánh Đổi — Obito, Shiki, RPT MCK',
   'music',
   'Track rap Việt cá tính, nhịp điệu mạnh mẽ truyền động lực vượt qua thử thách.',
   'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-11.mp3', 'https://open.spotify.com/track/1UQNU5O9VttUgO16pxUnjw', 'vPz8ftK_4bk',
   'energy', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '39 days', now()),

  ('40000000-0000-0000-0000-000000000019',
   'Xa Xôi — RPT MCK, Obito',
   'music',
   'Sự kết hợp giữa hai rapper hàng đầu Vpop, giai điệu cuốn theo từng nhịp trống dồn dập.',
   'https://images.unsplash.com/photo-1533294455009-a77b7557d2d1?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-12.mp3', 'https://open.spotify.com/track/3SJBCyImil9sNCH8lcCnqO', 'NSkimwwZBRs',
   'energy', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '38 days', now()),

  ('40000000-0000-0000-0000-000000000020',
   'Bắc Bling Remix — Zhuro, Hoà Minzy, Xuân Hinh, Tuấn Cry, Masew',
   'music',
   'Bản remix sôi động của hit "Bắc Bling", tempo nhanh phù hợp để vận động và nhảy múa.',
   'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-13.mp3', 'https://open.spotify.com/track/4YydR1FDzHAYsQ8a3TNTBK', '_gntgclEYP8',
   'energy', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '37 days', now()),

  ('40000000-0000-0000-0000-000000000021',
   'Sai Không Thể Sửa (Remix) — Thiên Tú, Oanh Tạ',
   'music',
   'Bản remix dance-pop từ bản gốc của Thiên Tú kết hợp Oanh Tạ, nhịp điệu bùng nổ cho các buổi tập luyện.',
   'https://images.unsplash.com/photo-1455849318743-b2233052fcff?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-14.mp3', 'https://open.spotify.com/track/0ZyJWdljNLNtkNNXK0R6bG', 'Jp4SaOSA460',
   'energy', 3, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '36 days', now()),

  -- ======= SAD - Đồng hành khi buồn (5 bài) =======
  ('40000000-0000-0000-0000-000000000022',
   'Kẻ Say Tình 2 — Quốc Thiên',
   'music',
   'Bản ballad tình cảm được yêu thích năm 2026, giai điệu day dứt về những cuộc tình dang dở.',
   'https://images.unsplash.com/photo-1493836512294-502baa1986e2?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-15.mp3', 'https://open.spotify.com/track/0DjFGDEvgTUMnJ7pdHKbcS', 'uUQG8sk7cLs',
   'sad', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '35 days', now()),

  ('40000000-0000-0000-0000-000000000023',
   'Người Còn Thương Em Không — Tóc Tiên',
   'music',
   'Ca khúc mới của Tóc Tiên, giai điệu buồn man mác về tình yêu không còn vẹn nguyên.',
   'https://images.unsplash.com/photo-1519162808019-7de1683fa2ad?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-16.mp3', 'https://open.spotify.com/track/5r6Rae4kq7CikBuoTaj6RF', 'tMOQ_lII7Ao',
   'sad', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '34 days', now()),

  ('40000000-0000-0000-0000-000000000024',
   'Sai Không Thể Sửa — Thiên Tú',
   'music',
   'Bản ballad được giới trẻ yêu thích, ca từ chân thành về những sai lầm không thể sửa trong tình yêu.',
   'https://images.unsplash.com/photo-1501426026826-31c667bdf23d?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3', 'https://open.spotify.com/track/7Fdl6lnoQLuLZm5hYNeRYF', 'kgPONkoRMPM',
   'sad', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '33 days', now()),

  ('40000000-0000-0000-0000-000000000025',
   'Đông Kiếm Em — Vũ.',
   'music',
   'Ca khúc indie quen thuộc của Vũ., giọng hát mộc mạc gợi nỗi nhớ trong những ngày đông.',
   'https://images.unsplash.com/photo-1534274988757-a28bf1a57c17?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3', 'https://open.spotify.com/track/72JmsjVP3HeNfi4dzJDZWa', 'iFWhVUOObc4',
   'sad', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '32 days', now()),

  ('40000000-0000-0000-0000-000000000026',
   'Đợi — Vũ.',
   'music',
   'Giai điệu chậm rãi, day dứt của Vũ. về sự chờ đợi trong tình yêu.',
   'https://images.unsplash.com/photo-1541781774459-bb2af2f05b55?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3', 'https://open.spotify.com/track/7J5s7XuUbRSmHSa6I89s8Z', 'V6pLnQdGA_c',
   'sad', 3, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '31 days', now()),

  -- ======= CALM - Thư giãn / Tập trung (2 bài) =======
  ('40000000-0000-0000-0000-000000000027',
   'Đường Chân Trời — Chillies',
   'music',
   'Indie-pop nhẹ nhàng của Chillies, giai điệu mở rộng như đường chân trời giúp tâm trí thư thái.',
   'https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3', 'https://open.spotify.com/track/0glDqpRJ9WyPHI1AXk0GcD', 'HTSqRkVpL9E',
   'calm', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '30 days', now()),

  ('40000000-0000-0000-0000-000000000028',
   'Từ Đầu — Chillies',
   'music',
   'Bản chill sâu lắng, phù hợp để tĩnh tâm và nhìn lại mọi thứ từ đầu.',
   'https://images.unsplash.com/photo-1508672019048-805c876b67e2?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3', 'https://open.spotify.com/track/7AAnw2xzxKplhRa0nCpM9i', '0lqvSEdZ3oU',
   'calm', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '29 days', now()),

  -- ======= SLEEP - Giúp ngủ ngon (3 bài) =======
  ('40000000-0000-0000-0000-000000000029',
   'Đại Lộ Mặt Trời — Chillies',
   'music',
   'Giai điệu ấm áp, tiết tấu chậm rãi của Chillies giúp thư giãn trước giờ ngủ.',
   'https://images.unsplash.com/photo-1446071103084-c257b5f70672?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3', 'https://open.spotify.com/track/0RRaMSF6sPNAvMTntUiv8m', 'bJvSlUTeGXc',
   'sleep', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '28 days', now()),

  ('40000000-0000-0000-0000-000000000030',
   'Đôi Mươi — Hoàng Dũng',
   'music',
   'Ballad nhẹ nhàng của Hoàng Dũng về tuổi trẻ, giai điệu êm ái dễ đưa vào giấc ngủ.',
   'https://images.unsplash.com/photo-1510915361894-db8b60106cb1?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3', 'https://open.spotify.com/track/2VfRYjSepfrGMDeGOMVNRz', 'WgYQhO5D9uk',
   'sleep', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '27 days', now()),

  ('40000000-0000-0000-0000-000000000031',
   'Thích Em Hơi Nhiều — Wren Evans',
   'music',
   'Acoustic pop dịu dàng của Wren Evans, giai điệu ngọt ngào tạo cảm giác bình yên trước giờ ngủ.',
   'https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=400',
   'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3', 'https://open.spotify.com/track/25KGfgferpx6hNR0dRNnFP', 'faSVTByG0LQ',
   'sleep', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '26 days', now()),

  -- =====================================================================
  -- PODCAST — Kênh "Vì Sao Thế Nhỉ!" (@visaothenhipodcast)
  -- YouTube IDs lấy trực tiếp từ embed code do người dùng cung cấp.
  -- =====================================================================

  -- 1. Dành Cho Những Ngày Cậu Chẳng Biết Đi Đâu Về Đâu
  ('40000000-0000-0000-0000-000000000032',
   'VSTN — Dành Cho Những Ngày Cậu Chẳng Biết Đi Đâu Về Đâu',
   'podcast',
   'Khi bạn cảm thấy lạc lõng, không biết mình đang đi về đâu — đây là tập podcast để ngồi lại, thở và lắng nghe chính mình. Chill, nhẹ nhàng, chữa lành.',
   'https://images.unsplash.com/photo-1478737270239-2f02b77fc618?w=400',
   NULL, NULL, 'NAOBo3I96yc',
   'calm', 35, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '25 days', now()),

  -- 2. Điều gì ẩn sau những đứa trẻ mang hình hài người lớn?
  ('40000000-0000-0000-0000-000000000033',
   'VSTN — Điều Gì Ẩn Sau Những Đứa Trẻ Mang Hình Hài Người Lớn?',
   'podcast',
   'Bên trong những người lớn vẫn còn một đứa trẻ đang sợ hãi, đang cần được yêu thương. Podcast tâm lý chữa lành về inner child và hành trình trở về với chính mình.',
   'https://images.unsplash.com/photo-1590602847861-f357a9332bbc?w=400',
   NULL, NULL, 'akwTcqoRJ0E',
   'sad', 40, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '24 days', now()),

  -- 3. Trầm cảm không nhìn thấy được bằng mắt thường
  ('40000000-0000-0000-0000-000000000034',
   'VSTN — Trầm Cảm Không Nhìn Thấy Được Bằng Mắt Thường',
   'podcast',
   'Không phải lúc nào người trầm cảm cũng khóc. Đôi khi họ cười rất tươi. Podcast chia sẻ chân thật về sức khỏe tâm thần — để hiểu hơn, thương hơn.',
   'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400',
   NULL, NULL, 'mj-G48KOo2Q',
   'sad', 38, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '23 days', now()),

  -- 4. Có điều gì ẩn nấp sau những nụ cười?
  ('40000000-0000-0000-0000-000000000035',
   'VSTN — Có Điều Gì Ẩn Nấp Sau Những Nụ Cười?',
   'podcast',
   'Nụ cười đôi khi là lớp giáp che đi những nỗi đau không nói được. Podcast nhẹ nhàng, đồng cảm — dành cho những ai đang cười ngoài mặt nhưng khóc bên trong.',
   'https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=400',
   NULL, NULL, 'o9QDdJ6VlRc',
   'sad', 36, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '22 days', now()),

  -- 5. Nếu cả đời không rực rỡ thì sao?
  ('40000000-0000-0000-0000-000000000036',
   'VSTN — Nếu Cả Đời Không Rực Rỡ Thì Sao?',
   'podcast',
   'Không phải ai cũng sinh ra để nổi bật. Và đó hoàn toàn ổn. Podcast về việc sống một cuộc đời bình thường nhưng ý nghĩa — không cần áp lực phải tỏa sáng.',
   'https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=400',
   NULL, NULL, '46Vyd2nMgPQ',
   'calm', 33, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '21 days', now()),

  -- 6. Radio #31 — Cảm ơn vì đã hiện diện trong cuộc đời này
  ('40000000-0000-0000-0000-000000000037',
   'VSTN Radio #31 — Cảm Ơn Vì Đã Hiện Diện Trong Cuộc Đời Này',
   'podcast',
   'Một lá thư không gửi — gửi đến tất cả những ai đang hiện diện, dù mệt mỏi dù vẫn cố gắng. Cảm ơn bạn đã ở lại. Radio nhẹ nhàng, chữa lành.',
   'https://images.unsplash.com/photo-1518199266791-5375a83190b7?w=400',
   NULL, NULL, 'dGtqcBDRe1U',
   'calm', 25, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '20 days', now()),

  -- 7. Có con người sống mà như qua đời
  ('40000000-0000-0000-0000-000000000038',
   'VSTN — Có Con Người Sống Mà Như Qua Đời',
   'podcast',
   'Podcast về trạng thái tồn tại nhưng không thực sự sống — khi cơ thể vẫn đây nhưng tâm hồn đã xa rời. Nhẹ nhàng, chữa lành, và rất đúng với nhiều người.',
   'https://images.unsplash.com/photo-1501139083538-0139583c060f?w=400',
   NULL, NULL, '4qy8dM3lLqc',
   'sad', 37, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '19 days', now()),

  -- 8. Tôi sống dưới những ngày rực rỡ, cả những đêm tối mịt mù
  ('40000000-0000-0000-0000-000000000039',
   'VSTN — Tôi Sống Dưới Những Ngày Rực Rỡ, Cả Những Đêm Tối Mịt Mù',
   'podcast',
   'Cuộc sống không chỉ có ánh sáng — podcast về hành trình chấp nhận cả những ngày tối tăm, mệt mỏi, và tìm ra ý nghĩa ngay trong những khoảnh khắc khó nhất.',
   'https://images.unsplash.com/photo-1543269865-cbf427effbad?w=400',
   NULL, NULL, 'na24wAMYdnI',
   'sad', 42, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '18 days', now()),

  -- 9. Rồi ai sẽ ở lại, cạnh đám trẻ ở đại dương đen
  ('40000000-0000-0000-0000-000000000040',
   'VSTN — Rồi Ai Sẽ Ở Lại, Cạnh Đám Trẻ Ở Đại Dương Đen',
   'podcast',
   'Dành cho những người trẻ đang lạc lõng giữa cuộc đời — cảm giác trôi dạt giữa đại dương tối, không biết ai sẽ ở lại. Podcast đồng cảm, chữa lành sâu sắc.',
   'https://images.unsplash.com/photo-1455642305367-68834a1da7ab?w=400',
   NULL, NULL, 'cfFs4EjoFxY',
   'sad', 39, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '17 days', now()),

  -- 10. Radio #10 — Có một người không có ước mơ
  ('40000000-0000-0000-0000-000000000041',
   'VSTN Radio #10 — Có Một Người Không Có Ước Mơ',
   'podcast',
   'Lá thư không gửi #8 — câu chuyện về người không có ước mơ, và điều đó có nghĩa là gì. Podcast nhẹ nhàng, thấm thía, khiến bạn nhìn lại chính mình.',
   'https://images.unsplash.com/photo-1528715471579-d1bcf0ba5e83?w=400',
   NULL, NULL, 'aLqAb3td0j8',
   'calm', 22, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '16 days', now()),

  -- 11. Hài độc thoại — Em Phà ửe Đâu Thế
  ('40000000-0000-0000-0000-000000000042',
   'Hài Độc Thoại — Em Phà ửe Đâu Thế | Phương Nam Saigon Tếu',
   'podcast',
   'Tiết mục hài độc thoại hài hước, dí dỏm từ không gian biểu diễn Sài Gòn — xem vào những lúc cần cười xả stress, quay đầu, rồi tiếp tục!',
   'https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=400',
   NULL, NULL, 'YC9G5Z26rjs',
   'happy', 15, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '15 days', now()),

  -- 12. Hài độc thoại — Quyền Anh Khó Đấy
  ('40000000-0000-0000-0000-000000000043',
   'Hài Độc Thoại — Quyền Anh Khó Đấy | Phương Nam Saigon Tếu',
   'podcast',
   'Kịt hợp bóng đá và bạch đầu quân — tất nhiên là theo cách hài nhất có thể! Tiết mục cười ra nước mắt của Phương Nam Saigon Tếu.',
   'https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=400',
   NULL, NULL, 'GyvvdI_8IvA',
   'happy', 12, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '14 days', now())

ON CONFLICT (id) DO UPDATE SET
  title = EXCLUDED.title,
  content_type = EXCLUDED.content_type,
  description = EXCLUDED.description,
  thumbnail_url = EXCLUDED.thumbnail_url,
  content_url = EXCLUDED.content_url,
  spotify_url = EXCLUDED.spotify_url,
  youtube_id = EXCLUDED.youtube_id,
  mood_tag = EXCLUDED.mood_tag,
  duration_minutes = EXCLUDED.duration_minutes,
  is_active = EXCLUDED.is_active,
  updated_at = now();


-- ============================================================
-- SAD — 10 bài "đồng hành khi buồn/cô đơn" theo yêu cầu người dùng
-- (Cô Đơn Trên Sofa, Chuyện Rằng, Hạt Mưa Vương Vấn, Công Chúa Bong Bóng,
--  Khởi My, Có Công Mài Sắt, Bích Phương, Phương Mỹ Chi...)
-- youtube_id đã xác minh tồn tại + nhúng được qua YouTube oEmbed API.
-- Chỉ dùng YouTube (không Spotify/content_url) — đúng kiến trúc hiện tại.
-- ============================================================

INSERT INTO content_library (id, title, content_type, description, thumbnail_url, content_url, spotify_url, youtube_id, mood_tag, duration_minutes, is_active, created_by, created_at, updated_at) VALUES

  ('40000000-0000-0000-0000-000000000042',
   'Cô Đơn Trên Sofa — Hồ Ngọc Hà',
   'music',
   'Bản hit gắn liền với Hồ Ngọc Hà, giai điệu day dứt về cảm giác cô đơn giữa một mối quan hệ đã nhạt phai.',
   'https://images.unsplash.com/photo-1518199266791-5375a83190b7?w=400',
   NULL, NULL, 'UXqxTZ67Jio',
   'sad', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '15 days', now()),

  ('40000000-0000-0000-0000-000000000043',
   'Chuyện Rằng — Thịnh Suy',
   'music',
   'Ca khúc indie-ballad chậm rãi của Thịnh Suy, lời hát như một câu chuyện kể lại nỗi buồn đã qua.',
   'https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=400',
   NULL, NULL, 'akgNYX8i9Xs',
   'sad', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '14 days', now()),

  ('40000000-0000-0000-0000-000000000044',
   'Hạt Mưa Vương Vấn — Phan Duy Anh',
   'music',
   'Giai điệu trữ tình nhẹ nhàng, ca từ vương vấn như những hạt mưa gợi nhớ một người đã xa.',
   'https://images.unsplash.com/photo-1519692933481-e162a57d6721?w=400',
   NULL, NULL, 'GRLMnGcTRHs',
   'sad', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '13 days', now()),

  ('40000000-0000-0000-0000-000000000045',
   'Công Chúa Bong Bóng — Bảo Thy, 2B',
   'music',
   'Câu chuyện cổ tích buồn về nàng công chúa bong bóng yêu mưa — giai điệu ngọt ngào nhưng man mác tiếc nuối.',
   'https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=400',
   NULL, NULL, 'Ucgzxh0maF8',
   'sad', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '12 days', now()),

  ('40000000-0000-0000-0000-000000000046',
   'Có Công Mài "Sắc" — Ngô Lan Hương',
   'music',
   'Chơi chữ từ câu tục ngữ quen thuộc, ca khúc kể về sự kiên trì mài giũa một tình yêu đơn phương đầy tổn thương.',
   'https://images.unsplash.com/photo-1494438639946-1ebd1d20bf85?w=400',
   NULL, NULL, 'LetDNcvopbg',
   'sad', 3, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '11 days', now()),

  ('40000000-0000-0000-0000-000000000047',
   'Thì Ra Mình Rất Cô Đơn — Khởi My',
   'music',
   'Khởi My trải lòng về khoảnh khắc nhận ra sự cô đơn của chính mình — giọng hát tha thiết, dễ đồng cảm.',
   'https://images.unsplash.com/photo-1522441815192-d9f04eb0615c?w=400',
   NULL, NULL, 'Q6AGbp_FafA',
   'sad', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '10 days', now()),

  ('40000000-0000-0000-0000-000000000048',
   'Từ Ngày Anh Xa — Khởi My, Kaisoul',
   'music',
   'Ballad nhẹ nhàng của Khởi My về khoảng trống để lại sau một cuộc chia tay.',
   'https://images.unsplash.com/photo-1487956382158-bb926046304a?w=400',
   NULL, NULL, 'CtfxdtADLyQ',
   'sad', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '9 days', now()),

  ('40000000-0000-0000-0000-000000000049',
   'Thương — Bích Phương',
   'music',
   'Bích Phương thể hiện nỗi thương cảm sâu lắng, khác hẳn hình ảnh sôi động thường thấy — một mặt buồn hiếm hoi.',
   'https://images.unsplash.com/photo-1465101162946-4377e57745c3?w=400',
   NULL, NULL, '-yW8lZHMp8E',
   'sad', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '8 days', now()),

  ('40000000-0000-0000-0000-000000000050',
   'Gửi Anh Xa Nhớ — Bích Phương',
   'music',
   'Lá thư âm nhạc gửi đến người thương ở xa, giai điệu day dứt nỗi nhớ của Bích Phương.',
   'https://images.unsplash.com/photo-1502781252888-9143ba7f074e?w=400',
   NULL, NULL, 'sbfks7HdRoE',
   'sad', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '7 days', now()),

  ('40000000-0000-0000-0000-000000000051',
   'Nỗi Buồn Gác Trọ — Phương Mỹ Chi',
   'music',
   'Phương Mỹ Chi làm mới ca khúc bolero kinh điển bằng bản phối Lofi, giữ nguyên nỗi buồn xưa cũ mà vẫn gần gũi với người trẻ.',
   'https://images.unsplash.com/photo-1483086431886-3590a88317fe?w=400',
   NULL, NULL, 'aoq96tI7SCg',
   'sad', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '6 days', now())

ON CONFLICT (id) DO UPDATE SET
  title = EXCLUDED.title,
  content_type = EXCLUDED.content_type,
  description = EXCLUDED.description,
  thumbnail_url = EXCLUDED.thumbnail_url,
  content_url = EXCLUDED.content_url,
  spotify_url = EXCLUDED.spotify_url,
  youtube_id = EXCLUDED.youtube_id,
  mood_tag = EXCLUDED.mood_tag,
  duration_minutes = EXCLUDED.duration_minutes,
  is_active = EXCLUDED.is_active,
  updated_at = now();


-- ============================================================
-- Bổ sung đủ 10 bài/tâm trạng cho 4 mood còn lại (happy, calm, sleep, energy)
-- youtube_id đã xác minh tồn tại + nhúng được qua YouTube oEmbed API.
-- ============================================================

INSERT INTO content_library (id, title, content_type, description, thumbnail_url, content_url, spotify_url, youtube_id, mood_tag, duration_minutes, is_active, created_by, created_at, updated_at) VALUES

  -- ======= HAPPY - thêm 5 bài (đủ 10) =======
  ('40000000-0000-0000-0000-000000000052',
   'See Tình — Hoàng Thùy Linh',
   'music',
   'Bản hit tạo trend toàn cầu của Hoàng Thùy Linh, giai điệu bắt tai, tinh thần vui tươi lan tỏa.',
   'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=400',
   NULL, NULL, 'gJHSDZfJrRY',
   'happy', 3, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '5 days', now()),

  ('40000000-0000-0000-0000-000000000053',
   'Waiting For You — MONO',
   'music',
   'Bản pop-ballad tươi sáng đánh dấu sự trở lại của MONO, giai điệu ấm áp đầy hy vọng.',
   'https://images.unsplash.com/photo-1465146344425-f00d5f5c8f07?w=400',
   NULL, NULL, 'okz5RIZRT0U',
   'happy', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '5 days', now()),

  ('40000000-0000-0000-0000-000000000054',
   'Chạy Ngay Đi — Sơn Tùng M-TP',
   'music',
   'Bản hit sôi động của Sơn Tùng M-TP với thông điệp tích cực về việc chạy trốn những điều tiêu cực.',
   'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=400',
   NULL, NULL, '32sYGCOYJUM',
   'happy', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '5 days', now()),

  ('40000000-0000-0000-0000-000000000055',
   'Có Chắc Yêu Là Đây — Sơn Tùng M-TP',
   'music',
   'Giai điệu vui nhộn, phối khí bắt tai, mang năng lượng tích cực và tự tin.',
   'https://images.unsplash.com/photo-1524594152303-9fd13543fe6e?w=400',
   NULL, NULL, '6t-MjBazs3o',
   'happy', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '4 days', now()),

  ('40000000-0000-0000-0000-000000000056',
   'Ghen — Khắc Hưng, Min, Erik',
   'music',
   'Bản hit đình đám từng gây sốt khắp mạng xã hội, giai điệu vui nhộn dễ khiến người nghe mỉm cười.',
   'https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3?w=400',
   NULL, NULL, 'oCuFT_OF2SI',
   'happy', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '4 days', now()),

  -- ======= CALM - thêm 7 bài (đủ 10) =======
  ('40000000-0000-0000-0000-000000000057',
   'HongKong1 — Nguyễn Trọng Tài, San Ji, Double X',
   'music',
   'Giai điệu lo-fi chill nhẹ nhàng, phù hợp để thư giãn hoặc tập trung làm việc, học tập.',
   'https://images.unsplash.com/photo-1519681393784-d120267933ba?w=400',
   NULL, NULL, 't7tZFq29lis',
   'calm', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '4 days', now()),

  ('40000000-0000-0000-0000-000000000058',
   'Đưa Anh Về — Phan Mạnh Quỳnh',
   'music',
   'Ballad nhẹ nhàng, giọng hát ấm áp của Phan Mạnh Quỳnh mang lại cảm giác bình yên, gần gũi.',
   'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=400',
   NULL, NULL, 'Ijoe0qQBH2U',
   'calm', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '3 days', now()),

  ('40000000-0000-0000-0000-000000000059',
   'Bước Qua Nhau — Vũ.',
   'music',
   'Indie-pop chill đặc trưng của Vũ., giai điệu nhẹ nhàng như một lời tự sự bình thản.',
   'https://images.unsplash.com/photo-1445964047600-cf19fb26f43e?w=400',
   NULL, NULL, 'ixdSsW5n2rI',
   'calm', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '3 days', now()),

  ('40000000-0000-0000-0000-000000000060',
   'Lạ Lùng — Vũ.',
   'music',
   'Chất giọng mộc mạc, tiết tấu chậm rãi của Vũ. dễ đưa tâm trí vào trạng thái thư thái.',
   'https://images.unsplash.com/photo-1500462918059-b1a0cb512f1d?w=400',
   NULL, NULL, 'Yhq8Qb9UzDs',
   'calm', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '3 days', now()),

  ('40000000-0000-0000-0000-000000000061',
   'Rằng Em Mãi Ở Bên — Bích Phương',
   'music',
   'Giai điệu dịu dàng, lời hát ấm áp về sự đồng hành — nhẹ nhàng và an yên.',
   'https://images.unsplash.com/photo-1490750967868-88aa4486c946?w=400',
   NULL, NULL, 'RurxOHMxxPM',
   'calm', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '2 days', now()),

  ('40000000-0000-0000-0000-000000000062',
   'Cho Tôi Lang Thang — Ngọt, Đen',
   'music',
   'Giai điệu indie phóng khoáng, khuyến khích sống chậm lại và tận hưởng hiện tại.',
   'https://images.unsplash.com/photo-1502920917128-1aa500764cbd?w=400',
   NULL, NULL, 'gUr4qp6YGLs',
   'calm', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '2 days', now()),

  ('40000000-0000-0000-0000-000000000063',
   'Sau Này Hãy Gặp Lại Nhau Khi Hoa Nở — Nguyên Hà',
   'music',
   'Giọng hát trong trẻo của Nguyên Hà cùng giai điệu dịu dàng, gợi cảm giác an yên và hy vọng.',
   'https://images.unsplash.com/photo-1490730141103-6cac27aaab94?w=400',
   NULL, NULL, 'xB2qsCnqAXA',
   'calm', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '2 days', now()),

  -- ======= SLEEP - thêm 7 bài (đủ 10) =======
  ('40000000-0000-0000-0000-000000000064',
   'Cơn Mưa Ngang Qua — Sơn Tùng M-TP',
   'music',
   'Bản ballad kinh điển với giai điệu chậm rãi, êm dịu như một cơn mưa nhẹ trước giờ ngủ.',
   'https://images.unsplash.com/photo-1428592953211-077101b2021b?w=400',
   NULL, NULL, 'JQwLF3fsGY0',
   'sleep', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '2 days', now()),

  ('40000000-0000-0000-0000-000000000065',
   'Nắng Ấm Xa Dần — Sơn Tùng M-TP',
   'music',
   'Giai điệu R&B chậm rãi, giọng hát nhẹ nhàng dễ đưa tâm trí vào trạng thái thư giãn sâu.',
   'https://images.unsplash.com/photo-1470252649378-9c29740c9fa8?w=400',
   NULL, NULL, '488ceQWoGGw',
   'sleep', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '1 days', now()),

  ('40000000-0000-0000-0000-000000000066',
   'Buông Đôi Tay Nhau Ra — Sơn Tùng M-TP',
   'music',
   'Ballad chậm rãi, tiết tấu êm ái phù hợp để thư giãn và dễ đi vào giấc ngủ.',
   'https://images.unsplash.com/photo-1470813740244-df37b8c1edcb?w=400',
   NULL, NULL, 'LCyo565N_5w',
   'sleep', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '1 days', now()),

  ('40000000-0000-0000-0000-000000000067',
   'Dừng Lại Thôi — Bùi Anh Tuấn',
   'music',
   'Chất giọng trầm ấm đặc trưng của Bùi Anh Tuấn, giai điệu nhẹ nhàng xoa dịu tâm trí trước giờ ngủ.',
   'https://images.unsplash.com/photo-1445712133517-346b0ba4e5db?w=400',
   NULL, NULL, 'CLom7fJSzmc',
   'sleep', 4, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '1 days', now()),

  ('40000000-0000-0000-0000-000000000068',
   'Chia Tay — Bùi Anh Tuấn',
   'music',
   'Ballad sâu lắng, tiết tấu chậm, phù hợp làm nhạc nền thư giãn buổi tối.',
   'https://images.unsplash.com/photo-1472552944129-b035e9ea3744?w=400',
   NULL, NULL, 'OdE8pYLJh1c',
   'sleep', 4, true, 'b0000000-0000-0000-0000-000000000006', now(), now()),

  ('40000000-0000-0000-0000-000000000069',
   'Rời Bỏ — Hòa Minzy',
   'music',
   'Giọng hát nội lực nhưng tiết chế, giai điệu chậm rãi mang lại cảm giác lắng đọng, dễ chịu.',
   'https://images.unsplash.com/photo-1419242902214-272b3f66ee7a?w=400',
   NULL, NULL, 'zQwKxVCR1y8',
   'sleep', 4, true, 'b0000000-0000-0000-0000-000000000006', now(), now()),

  ('40000000-0000-0000-0000-000000000070',
   'Xin Em — Bùi Anh Tuấn',
   'music',
   'Bản ballad nhẹ nhàng khép lại một ngày dài, giai điệu êm ái ru vào giấc ngủ.',
   'https://images.unsplash.com/photo-1455642305367-68834a1da7ab?w=400',
   NULL, NULL, '5kWI2_LXCh0',
   'sleep', 4, true, 'b0000000-0000-0000-0000-000000000006', now(), now()),

  -- ======= ENERGY - thêm 4 bài (đủ 10) =======
  ('40000000-0000-0000-0000-000000000071',
   'Bên Trên Tầng Lầu — Tăng Duy Tân',
   'music',
   'Giai điệu bắt tai, tiết tấu dồn dập tạo cảm hứng vận động, thường được dùng làm nhạc nền tập luyện.',
   'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=400',
   NULL, NULL, 'LaxkmhiECfM',
   'energy', 3, true, 'b0000000-0000-0000-0000-000000000006', now(), now()),

  ('40000000-0000-0000-0000-000000000072',
   'Có Ai Thương Em Như Anh — Tóc Tiên, Touliver',
   'music',
   'Track dance-pop sôi động của Tóc Tiên và Touliver, năng lượng bùng nổ suốt cả bài.',
   'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400',
   NULL, NULL, 'neCmEbI2VWg',
   'energy', 4, true, 'b0000000-0000-0000-0000-000000000006', now(), now()),

  ('40000000-0000-0000-0000-000000000073',
   'Đi Về Nhà — Đen, JustaTee',
   'music',
   'Bản rap-pop tích cực về hành trình trở về, tiết tấu vui tươi tiếp thêm động lực.',
   'https://images.unsplash.com/photo-1533294455009-a77b7557d2d1?w=400',
   NULL, NULL, 'vTJdVE_gjI0',
   'energy', 4, true, 'b0000000-0000-0000-0000-000000000006', now(), now()),

  ('40000000-0000-0000-0000-000000000074',
   'Chị Ngả Em Nâng — Bích Phương',
   'music',
   'Giai điệu dance sôi động, vui nhộn của Bích Phương, tiếp thêm năng lượng và tiếng cười.',
   'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=400',
   NULL, NULL, 'csgwEmTti_o',
   'energy', 3, true, 'b0000000-0000-0000-0000-000000000006', now(), now())

ON CONFLICT (id) DO UPDATE SET
  title = EXCLUDED.title,
  content_type = EXCLUDED.content_type,
  description = EXCLUDED.description,
  thumbnail_url = EXCLUDED.thumbnail_url,
  content_url = EXCLUDED.content_url,
  spotify_url = EXCLUDED.spotify_url,
  youtube_id = EXCLUDED.youtube_id,
  mood_tag = EXCLUDED.mood_tag,
  duration_minutes = EXCLUDED.duration_minutes,
  is_active = EXCLUDED.is_active,
  updated_at = now();


-- ============================================================
-- PODCAST — thêm 3 video thư giãn/ngủ ngon theo yêu cầu người dùng
-- (mood_tag = 'sleep' — hiển thị ở tab Podcast, filter "Ngủ ngon")
-- youtube_id lấy trực tiếp từ embed code người dùng cung cấp, đã xác minh qua oEmbed.
-- ============================================================

INSERT INTO content_library (id, title, content_type, description, thumbnail_url, content_url, spotify_url, youtube_id, mood_tag, duration_minutes, is_active, created_by, created_at, updated_at) VALUES

  ('40000000-0000-0000-0000-000000000075',
   'Bình Yên Thư Giãn Nhẹ Nhàng — Thiền (Monoman)',
   'podcast',
   'Bản nhạc thiền nhẹ nhàng, giúp tâm trí lắng dịu và dễ chìm vào giấc ngủ sau một ngày dài.',
   'https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=400',
   NULL, NULL, 'FjHGZj2IjBk',
   'sleep', 30, true, 'b0000000-0000-0000-0000-000000000006', now(), now()),

  ('40000000-0000-0000-0000-000000000076',
   'Nhạc Piano Thư Giãn — Nhạc Để Ngủ, Âm Thanh Nước Chảy',
   'podcast',
   'Tiếng piano hoà cùng âm thanh nước chảy, tạo không gian yên tĩnh lý tưởng để thư giãn và ngủ ngon.',
   'https://images.unsplash.com/photo-1520523839897-bd0b52f945a0?w=400',
   NULL, NULL, 'fuXfT4Rv_WM',
   'sleep', 60, true, 'b0000000-0000-0000-0000-000000000006', now(), now()),

  ('40000000-0000-0000-0000-000000000077',
   'Nhạc Thư Giãn Dễ Ngủ — Xả Stress, Ngủ Sâu (Vào Những Ngày Mưa)',
   'podcast',
   'Âm thanh mưa rơi hoà cùng giai điệu nhẹ nhàng, giúp xả stress và đưa vào giấc ngủ sâu.',
   'https://images.unsplash.com/photo-1519692933481-e162a57d6721?w=400',
   NULL, NULL, 'aLuShQwNYRE',
   'sleep', 60, true, 'b0000000-0000-0000-0000-000000000006', now(), now())

ON CONFLICT (id) DO UPDATE SET
  title = EXCLUDED.title,
  content_type = EXCLUDED.content_type,
  description = EXCLUDED.description,
  thumbnail_url = EXCLUDED.thumbnail_url,
  content_url = EXCLUDED.content_url,
  spotify_url = EXCLUDED.spotify_url,
  youtube_id = EXCLUDED.youtube_id,
  mood_tag = EXCLUDED.mood_tag,
  duration_minutes = EXCLUDED.duration_minutes,
  is_active = EXCLUDED.is_active,
  updated_at = now();


-- ============================================================
-- PODCAST — thêm 2 video hài độc thoại theo yêu cầu người dùng
-- (mood_tag = 'happy' — hiển thị ở tab Podcast, filter "Hài hước")
-- youtube_id lấy trực tiếp từ embed code người dùng cung cấp, đã xác minh qua oEmbed.
-- ============================================================

INSERT INTO content_library (id, title, content_type, description, thumbnail_url, content_url, spotify_url, youtube_id, mood_tag, duration_minutes, is_active, created_by, created_at, updated_at) VALUES

  ('40000000-0000-0000-0000-000000000078',
   'Hài Độc Thoại — Em Phà Ở Đâu Thế (Phương Nam, Saigon Tếu)',
   'podcast',
   'Tiết mục hài độc thoại dí dỏm của Phương Nam tại Saigon Tếu, giúp bạn thư giãn và bật cười sảng khoái.',
   'https://images.unsplash.com/photo-1527224857830-43a7acc85260?w=400',
   NULL, NULL, 'YC9G5Z26rjs',
   'happy', 15, true, 'b0000000-0000-0000-0000-000000000006', now(), now()),

  ('40000000-0000-0000-0000-000000000079',
   'Hài Độc Thoại — Quyền Anh Khó Đấy (Phương Nam, Saigon Tếu)',
   'podcast',
   'Tiết mục hài độc thoại hài hước của Phương Nam tại Saigon Tếu, mang lại tiếng cười nhẹ nhàng sau ngày dài.',
   'https://images.unsplash.com/photo-1543269865-cbf427effbad?w=400',
   NULL, NULL, 'GyvvdI_8IvA',
   'happy', 15, true, 'b0000000-0000-0000-0000-000000000006', now(), now())

ON CONFLICT (id) DO UPDATE SET
  title = EXCLUDED.title,
  content_type = EXCLUDED.content_type,
  description = EXCLUDED.description,
  thumbnail_url = EXCLUDED.thumbnail_url,
  content_url = EXCLUDED.content_url,
  spotify_url = EXCLUDED.spotify_url,
  youtube_id = EXCLUDED.youtube_id,
  mood_tag = EXCLUDED.mood_tag,
  duration_minutes = EXCLUDED.duration_minutes,
  is_active = EXCLUDED.is_active,
  updated_at = now();
