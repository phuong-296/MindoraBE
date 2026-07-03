-- ============================================================
-- Mindora AI — Seed Data
-- Chạy lại bao nhiêu lần cũng không bị trùng (ON CONFLICT DO NOTHING)
-- Password cho tất cả user mẫu: 123456
-- BCrypt hash: $2a$10$Idolu7UZNw16caH.1jayaucgj/KsJK4OLztdKZfYb1c7i1qSogGGW
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


-- ===================== USER_PREFERENCES =====================
INSERT INTO user_preferences (id, user_id, language, favorite_music_genres, notification_frequency, dark_mode, email_notifications, push_notifications, created_at, updated_at) VALUES
  ('d0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'vi', ARRAY['lo-fi','acoustic'],       'daily',   false, true, true,  now(), now()),
  ('d0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000002', 'vi', ARRAY['classical','piano'],       'weekly',  true,  true, false, now(), now()),
  ('d0000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000003', 'vi', ARRAY['ambient','nature-sounds'], 'daily',   false, true, true,  now(), now()),
  ('d0000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000004', 'vi', NULL,                              'weekly',  false, true, true,  now(), now()),
  ('d0000000-0000-0000-0000-000000000005', 'b0000000-0000-0000-0000-000000000005', 'vi', NULL,                              'daily',   true,  true, true,  now(), now()),
  ('d0000000-0000-0000-0000-000000000006', 'b0000000-0000-0000-0000-000000000006', 'vi', NULL,                              'daily',   false, true, true,  now(), now())
ON CONFLICT (user_id) DO NOTHING;


-- ===================== EXPERTS =====================
INSERT INTO experts (id, user_id, specialization, bio, location, years_experience, rating, is_online, is_verified, created_at, updated_at) VALUES
  ('e0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000004',
   'Tâm lý lâm sàng',
   'Tiến sĩ Tâm lý học lâm sàng với hơn 12 năm kinh nghiệm điều trị trầm cảm, lo âu và rối loạn stress sau sang chấn (PTSD). Tốt nghiệp Đại học Khoa học Xã hội và Nhân văn TP.HCM.',
   'TP. Hồ Chí Minh', 12, 4.8, true, true, now() - interval '40 days', now()),

  ('e0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000005',
   'Tham vấn tâm lý thanh thiếu niên',
   'Thạc sĩ Tâm lý học, chuyên tư vấn cho học sinh, sinh viên về áp lực học tập, quan hệ gia đình và định hướng nghề nghiệp. 8 năm kinh nghiệm tại các trung tâm tư vấn tâm lý.',
   'Hà Nội', 8, 4.6, false, true, now() - interval '35 days', now())
ON CONFLICT (user_id) DO NOTHING;


-- ===================== AI CONVERSATIONS =====================
INSERT INTO ai_conversations (id, user_id, title, is_archived, created_at, updated_at) VALUES
  -- User An: 2 conversations
  ('f0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'Cuộc trò chuyện đầu tiên',          false, now() - interval '28 days', now() - interval '1 day'),
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
  ('30000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 4, 'neutral',  'Ngày bình thường',                    CURRENT_DATE - interval '7 days', now() - interval '7 days'),
  ('30000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001', 3, 'sad',      'Hơi buồn vì trời mưa',               CURRENT_DATE - interval '6 days', now() - interval '6 days'),
  ('30000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000001', 5, 'happy',    'Đi cafe với bạn',                     CURRENT_DATE - interval '5 days', now() - interval '5 days'),
  ('30000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000001', 2, 'sad',      'Bị sếp mắng',                         CURRENT_DATE - interval '4 days', now() - interval '4 days'),
  ('30000000-0000-0000-0000-000000000005', 'b0000000-0000-0000-0000-000000000001', 3, 'anxious',  'Vẫn còn buồn về chuyện hôm qua',     CURRENT_DATE - interval '3 days', now() - interval '3 days'),
  ('30000000-0000-0000-0000-000000000006', 'b0000000-0000-0000-0000-000000000001', 5, 'happy',    'Tập thể dục buổi sáng, khá hơn rồi', CURRENT_DATE - interval '2 days', now() - interval '2 days'),
  ('30000000-0000-0000-0000-000000000007', 'b0000000-0000-0000-0000-000000000001', 6, 'happy',    'Hoàn thành project đúng hạn!',        CURRENT_DATE - interval '1 day',  now() - interval '1 day'),

  -- User Bình: mood logs
  ('30000000-0000-0000-0000-000000000008', 'b0000000-0000-0000-0000-000000000002', 2, 'tired',    'Lại mất ngủ',                         CURRENT_DATE - interval '5 days', now() - interval '5 days'),
  ('30000000-0000-0000-0000-000000000009', 'b0000000-0000-0000-0000-000000000002', 3, 'sad',      'Mệt mỏi',                             CURRENT_DATE - interval '4 days', now() - interval '4 days'),
  ('30000000-0000-0000-0000-000000000010', 'b0000000-0000-0000-0000-000000000002', 5, 'happy',    'Gặp lại bạn cũ!',                     CURRENT_DATE - interval '3 days', now() - interval '3 days'),
  ('30000000-0000-0000-0000-000000000011', 'b0000000-0000-0000-0000-000000000002', 4, 'neutral',  'Ổn hơn',                               CURRENT_DATE - interval '2 days', now() - interval '2 days'),

  -- User Cường: mood logs
  ('30000000-0000-0000-0000-000000000012', 'b0000000-0000-0000-0000-000000000003', 3, 'anxious',  'Lo lắng chờ kết quả thi',             CURRENT_DATE - interval '16 days', now() - interval '16 days'),
  ('30000000-0000-0000-0000-000000000013', 'b0000000-0000-0000-0000-000000000003', 7, 'loved',    'ĐẬUUUUU!!! Hạnh phúc quá!',           CURRENT_DATE - interval '15 days', now() - interval '15 days'),
  ('30000000-0000-0000-0000-000000000014', 'b0000000-0000-0000-0000-000000000003', 6, 'happy',    'Vẫn vui vì kết quả thi',              CURRENT_DATE - interval '14 days', now() - interval '14 days')
ON CONFLICT DO NOTHING;


-- ===================== CONTENT LIBRARY =====================
INSERT INTO content_library (id, title, content_type, description, thumbnail_url, content_url, mood_tag, duration_minutes, is_active, created_by, created_at, updated_at) VALUES
  -- Nhạc thư giãn
  ('40000000-0000-0000-0000-000000000001', 'Mưa rơi bên hiên — Lo-fi chill',        'music',
   'Nhạc lo-fi nhẹ nhàng với tiếng mưa, giúp thư giãn và tập trung.',
   'https://images.unsplash.com/photo-1501426026826-31c667bdf23d', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3',
   'calm', 45, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '50 days', now()),

  ('40000000-0000-0000-0000-000000000002', 'Piano nhẹ nhàng cho giấc ngủ',          'music',
   'Tuyển tập piano acoustic giúp bạn dễ ngủ hơn.',
   'https://images.unsplash.com/photo-1520523839897-bd0b52f945a0', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3',
   'sleep', 60, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '50 days', now()),

  ('40000000-0000-0000-0000-000000000003', 'Tiếng sóng biển thư giãn',              'music',
   'Âm thanh sóng biển tự nhiên, phù hợp để thiền và thư giãn.',
   'https://images.unsplash.com/photo-1507525428034-b723cf961d3e', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3',
   'calm', 30, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '48 days', now()),

  -- Bài viết
  ('40000000-0000-0000-0000-000000000004', '5 cách đối phó với lo âu hiệu quả',    'article',
   'Tìm hiểu 5 phương pháp khoa học đã được chứng minh giúp giảm lo âu: thở sâu, thiền chánh niệm, vận động, viết nhật ký và kết nối xã hội.',
   'https://images.unsplash.com/photo-1506126613408-eca07ce68773', NULL,
   'calm', 8, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '45 days', now()),

  ('40000000-0000-0000-0000-000000000005', 'Hiểu về trầm cảm: Dấu hiệu và cách tìm kiếm giúp đỡ', 'article',
   'Trầm cảm không chỉ là buồn bã. Bài viết giải thích các dấu hiệu nhận biết và khi nào nên tìm đến chuyên gia.',
   'https://images.unsplash.com/photo-1493836512294-502baa1986e2', NULL,
   'sad', 12, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '42 days', now()),

  ('40000000-0000-0000-0000-000000000006', 'Tại sao giấc ngủ quan trọng với sức khỏe tâm thần?', 'article',
   'Ngủ không đủ giấc ảnh hưởng nghiêm trọng đến cảm xúc và khả năng ra quyết định. Tìm hiểu cách cải thiện chất lượng giấc ngủ.',
   'https://images.unsplash.com/photo-1541781774459-bb2af2f05b55', NULL,
   'sleep', 10, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '40 days', now()),

  -- Video
  ('40000000-0000-0000-0000-000000000007', 'Thiền chánh niệm 10 phút cho người mới', 'video',
   'Video hướng dẫn thiền chánh niệm cơ bản, phù hợp cho người chưa từng thiền. Giọng hướng dẫn nhẹ nhàng bằng tiếng Việt.',
   'https://images.unsplash.com/photo-1508672019048-805c876b67e2', 'https://example.com/video/meditation-10min.mp4',
   'calm', 10, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '38 days', now()),

  ('40000000-0000-0000-0000-000000000008', 'Yoga buổi sáng — Nạp năng lượng',      'video',
   'Bài tập yoga 15 phút giúp bạn khởi đầu ngày mới tràn đầy năng lượng và tinh thần tích cực.',
   'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b', 'https://example.com/video/morning-yoga.mp4',
   'energy', 15, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '35 days', now()),

  -- Bài tập
  ('40000000-0000-0000-0000-000000000009', 'Kỹ thuật thở 4-7-8 giảm lo âu',       'exercise',
   'Bài tập thở đơn giản: hít vào 4 giây, giữ hơi 7 giây, thở ra 8 giây. Lặp lại 4 lần. Hiệu quả ngay lập tức khi lo âu.',
   'https://images.unsplash.com/photo-1506126613408-eca07ce68773', NULL,
   'calm', 5, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '50 days', now()),

  ('40000000-0000-0000-0000-000000000010', 'Bài tập gratitude — 3 điều biết ơn',   'exercise',
   'Mỗi tối trước khi ngủ, viết ra 3 điều bạn biết ơn trong ngày. Nghiên cứu cho thấy bài tập này giúp cải thiện hạnh phúc đáng kể sau 2 tuần.',
   'https://images.unsplash.com/photo-1455849318743-b2233052fcff', NULL,
   'happy', 5, true, 'b0000000-0000-0000-0000-000000000006', now() - interval '48 days', now())
ON CONFLICT (id) DO NOTHING;


-- ===================== USER SAVED CONTENT =====================
INSERT INTO user_saved_content (id, user_id, content_id, saved_at, created_at) VALUES
  ('50000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', now() - interval '20 days', now() - interval '20 days'),
  ('50000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000009', now() - interval '18 days', now() - interval '18 days'),
  ('50000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000004', now() - interval '15 days', now() - interval '15 days'),
  ('50000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000002', now() - interval '15 days', now() - interval '15 days'),
  ('50000000-0000-0000-0000-000000000005', 'b0000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000006', now() - interval '12 days', now() - interval '12 days'),
  ('50000000-0000-0000-0000-000000000006', 'b0000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000007', now() - interval '10 days', now() - interval '10 days')
ON CONFLICT (user_id, content_id) DO NOTHING;


-- ===================== MUSIC RECOMMENDATIONS =====================
INSERT INTO music_recommendations (id, user_id, content_id, mood_tag, reason, recommended_at, created_at) VALUES
  ('60000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001',
   'calm', 'Bạn đang có cảm xúc lo lắng, nhạc lo-fi với tiếng mưa sẽ giúp bạn thư giãn hơn.', now() - interval '20 days', now() - interval '20 days'),
  ('60000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000002',
   'sleep', 'Bạn đang gặp vấn đề về giấc ngủ, nhạc piano nhẹ nhàng có thể giúp bạn dễ ngủ hơn.', now() - interval '15 days', now() - interval '15 days'),
  ('60000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000003',
   'calm', 'Dựa trên nhật ký gần đây, bạn cần thư giãn. Tiếng sóng biển sẽ giúp bạn bình tĩnh hơn.', now() - interval '4 days', now() - interval '4 days')
ON CONFLICT (id) DO NOTHING;


-- ===================== NOTIFICATIONS =====================
INSERT INTO notifications (id, user_id, type, title, message, is_read, created_at) VALUES
  ('70000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'mood_reminder',
   'Ghi nhận tâm trạng hôm nay',
   'Bạn chưa ghi nhận tâm trạng hôm nay. Hãy dành 1 phút để check-in cảm xúc nhé! ',
   false, now() - interval '1 hour'),

  ('70000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001', 'system',
   'Chào mừng đến Mindora!',
   'Cảm ơn bạn đã tham gia Mindora. Hãy bắt đầu bằng cách trò chuyện với AI hoặc viết nhật ký cảm xúc nhé!',
   true, now() - interval '30 days'),

  ('70000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000001', 'expert_alert',
   'Kết nối với chuyên gia',
   'Dựa trên phân tích gần đây, chúng tôi khuyên bạn nên nói chuyện với chuyên gia tâm lý. TS. Phạm Thị Dung hiện đang online.',
   false, now() - interval '4 days'),

  ('70000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000002', 'mood_reminder',
   'Ghi nhận tâm trạng hôm nay',
   'Đừng quên ghi lại cảm xúc hôm nay nhé! Theo dõi tâm trạng giúp bạn hiểu bản thân tốt hơn. ',
   false, now() - interval '2 hours'),

  ('70000000-0000-0000-0000-000000000005', 'b0000000-0000-0000-0000-000000000002', 'system',
   'Chào mừng đến Mindora!',
   'Cảm ơn bạn đã tham gia Mindora. Hãy bắt đầu bằng cách trò chuyện với AI hoặc viết nhật ký cảm xúc nhé!',
   true, now() - interval '25 days'),

  ('70000000-0000-0000-0000-000000000006', 'b0000000-0000-0000-0000-000000000003', 'system',
   'Chào mừng đến Mindora!',
   'Cảm ơn bạn đã tham gia Mindora. Hãy bắt đầu bằng cách trò chuyện với AI hoặc viết nhật ký cảm xúc nhé!',
   true, now() - interval '20 days'),

  -- Notifications cho expert
  ('70000000-0000-0000-0000-000000000007', 'b0000000-0000-0000-0000-000000000004', 'expert_alert',
   'Yêu cầu kết nối mới',
   'Nguyễn Văn An đã gửi yêu cầu kết nối với bạn. Hãy xem và phản hồi nhé!',
   false, now() - interval '3 days')
ON CONFLICT (id) DO NOTHING;


-- ===================== MENTAL HEALTH ANALYSES =====================
INSERT INTO mental_health_analyses (id, user_id, source_type, depression_risk_score, risk_level, ai_summary, alert_sent, analyzed_at, created_at) VALUES
  ('80000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'journal', 0.25, 'low',
   'Người dùng có dấu hiệu lo âu nhẹ liên quan đến áp lực học tập. Tuy nhiên, có các hoạt động tích cực như chạy bộ và viết nhật ký. Nguy cơ thấp.',
   false, now() - interval '25 days', now() - interval '25 days'),

  ('80000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001', 'chat', 0.55, 'medium',
   'Người dùng thể hiện cảm xúc tiêu cực rõ rệt: tự ti, cảm giác "vô dụng" sau khi bị phê bình công khai. Cần theo dõi thêm trong 1-2 tuần tới. Đề xuất kết nối chuyên gia nếu tình trạng không cải thiện.',
   true, now() - interval '5 days', now() - interval '5 days'),

  ('80000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000002', 'mood_log', 0.45, 'medium',
   'Mood score trung bình 3.0/7 trong 5 ngày gần nhất, kèm theo mất ngủ kéo dài. Có dấu hiệu cải thiện sau khi kết nối xã hội (gặp bạn cũ). Tiếp tục theo dõi.',
   false, now() - interval '10 days', now() - interval '10 days'),

  ('80000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000003', 'chat', 0.10, 'low',
   'Người dùng thể hiện cảm xúc tích cực, vui vẻ sau khi đạt kết quả thi tốt. Tinh thần ổn định, không có dấu hiệu nguy cơ.',
   false, now() - interval '15 days', now() - interval '15 days')
ON CONFLICT (id) DO NOTHING;


-- ===================== EXPERT CONNECTIONS =====================
INSERT INTO expert_connections (id, user_id, expert_id, status, trigger_reason, notes, requested_at, created_at, updated_at) VALUES
  ('90000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'e0000000-0000-0000-0000-000000000001',
   'pending', 'ai_alert',
   'Hệ thống phát hiện nguy cơ trầm cảm mức trung bình từ phân tích cuộc trò chuyện gần nhất.',
   now() - interval '4 days', now() - interval '4 days', now() - interval '4 days'),

  ('90000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000002', 'e0000000-0000-0000-0000-000000000002',
   'accepted', 'self_request',
   'Người dùng tự yêu cầu kết nối vì gặp vấn đề mất ngủ kéo dài.',
   now() - interval '12 days', now() - interval '12 days', now() - interval '10 days'),

  ('90000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000003', 'e0000000-0000-0000-0000-000000000001',
   'closed', 'self_request',
   'Người dùng yêu cầu tư vấn trước kỳ thi. Đã kết thúc sau khi tình trạng cải thiện.',
   now() - interval '20 days', now() - interval '20 days', now() - interval '14 days')
ON CONFLICT (id) DO NOTHING;
