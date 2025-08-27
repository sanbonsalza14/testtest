package com.my.quiz.config;

import com.my.quiz.entity.Role;
import com.my.quiz.entity.Quiz;
import com.my.quiz.entity.UserEntity;
import com.my.quiz.repository.QuizRepository;
import com.my.quiz.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SeedDataConfig {

    @Bean
    @Transactional
    public CommandLineRunner seedData(UserRepository userRepository,
                                      QuizRepository quizRepository) {
        return args -> {
            // 관리자 계정 1명 보장(이미 있으면 손대지 않음)
            userRepository.findById("root").orElseGet(() -> {
                UserEntity admin = new UserEntity();
                admin.setEmail("root");
                admin.setPassword("admin");
                admin.setNickname("관리자");
                admin.setRole(Role.ADMIN);
                admin.setApproved(true);
                return userRepository.save(admin);
            });

            // 퀴즈: 있으면 업데이트, 없으면 삽입 (중복X)
            String[][] data = new String[][]{
                    {"달팽이도 이빨이 있다.", "O"},
                    {"지구는 태양계에서 세 번째 행성이다.", "O"},
                    {"빛의 속도는 소리의 속도보다 느리다.", "X"},
                    {"대한민국의 국기는 태극기이다.", "O"},
                    {"π(파이)는 유리수이다.", "X"},
                    {"1km는 1000m이다.", "O"},
                    {"1인치는 2.54cm이다.", "O"},
                    {"소금의 화학식은 NaCl이다.", "O"},
                    {"물의 화학식은 H2O이다.", "O"},
                    {"세종대왕은 한글을 창제했다.", "O"},
                    {"고래는 물고기이다.", "X"},
                    {"사과에는 비타민 C가 전혀 없다.", "X"},
                    {"서울은 경기도에 속한 기초자치단체이다.", "X"},
                    {"사람의 심장은 4개의 방(심방·심실)을 가진다.", "O"},
                    {"프랑스의 수도는 파리이다.", "O"},
                    {"태평양은 지구에서 가장 큰 대양이다.", "O"},
                    {"인류의 첫 달 착륙은 1969년에 있었다.", "O"},
                    {"코끼리는 포유류이다.", "O"},
                    {"수소의 원자번호는 1이다.", "O"},
                    {"금의 원소기호는 Au이다.", "O"},
                    {"지구의 자연 위성은 달 하나뿐이다.", "O"},
                    {"비둘기는 포유류이다.", "X"},
                    {"일본의 수도는 도쿄이다.", "O"},
                    {"북극곰은 남극에 산다.", "X"},
                    {"축구 경기에서 한 팀의 필드 플레이어는 10명이다.", "X"},
                    {"야구에서 홈런은 언제나 1점이다.", "X"},
                    {"대한민국 주민등록번호는 숫자 13자리로 구성된다.", "O"},
                    {"피타고라스 정리는 a^2 + b^2 = c^2 이다.", "O"},
                    {"원은 꼭짓점이 3개이다.", "X"},
                    {"서울 지하철 노선은 1호선 하나뿐이다.", "X"},
                    {"HTML은 프로그래밍 언어이다.", "X"},
                    {"자바 바이트코드 파일 확장자는 .class 이다.", "O"},
                    {"대한민국 국가 최상위 도메인은 .kr 이다.", "O"},
                    {"현대 한글의 기본 자모 수는 24자이다.", "O"},
                    {"독수리는 조류이다.", "O"},
                    {"상자는 액체이다.", "X"},
                    {"눈(雪)은 겨울에만 내릴 수 있다.", "X"},
                    {"빛의 속도는 약 3×10^8 m/s 이다.", "O"},
                    {"순수한 물은 1기압에서 0℃에서 얼기 시작한다.", "O"},
                    {"하계 올림픽은 4년마다 열린다.", "O"},
                    {"곰은 초식동물만으로 분류된다.", "X"},
                    {"바닷물은 담수이다.", "X"},
                    {"사람의 뼈는 보통 206개 정도이다.", "O"},
                    {"지구의 적도 둘레는 약 4만 km이다.", "O"},
                    {"대한민국의 화폐 단위는 원(₩)이다.", "O"},
                    {"피자는 이탈리아에서 유래했다.", "O"},
                    {"판다의 주식(主食)은 대나무이다.", "O"},
                    {"바나나는 나무에서 자라는 나무의 일종이다.", "X"},
                    {"우유는 식물에서 만들어진다.", "X"},
                    {"사람은 햇빛을 통해 비타민 D를 합성할 수 있다.", "O"},
                    {"음력과 양력은 동일한 달력 체계이다.", "X"},
                    {"북반구에서 남쪽이 항상 더운 것은 아니다.", "O"},
                    {"태양은 별이다.", "O"},
                    {"은하는 태양계보다 훨씬 크다.", "O"},
                    {"빛이 전혀 없으면 사람의 눈은 색을 구분할 수 없다.", "O"},
                    {"구글은 한국 기업이다.", "X"},
                    {"대한민국의 국화는 무궁화이다.", "O"},
                    {"CPU는 중앙처리장치를 의미한다.", "O"},
                    {"고등어는 민물고기이다.", "X"},
                    {"물은 무색·무취의 액체이다(순수한 경우).", "O"},
                    {"빙하는 전부 북극에만 존재한다.", "X"},
                    {"지구의 자전 방향은 서쪽에서 동쪽이다.", "O"},
                    {"철의 원소기호는 Fe이다.", "O"},
                    {"사하라 사막은 남극에 있다.", "X"}
            };

            List<Quiz> toInsert = new ArrayList<>();
            for (String[] row : data) {
                String content = row[0];
                String answer  = row[1];

                quizRepository.findByContent(content).ifPresentOrElse(
                        q -> {
                            boolean changed = false;
                            if (!q.getAnswer().equalsIgnoreCase(answer)) { q.setAnswer(answer); changed = true; }
                            if (!"관리자".equals(q.getWriter())) { q.setWriter("관리자"); changed = true; }
                            if (changed) quizRepository.save(q);
                        },
                        () -> {
                            Quiz q = new Quiz();
                            q.setContent(content);
                            q.setAnswer(answer);
                            q.setWriter("관리자");
                            toInsert.add(q);
                        }
                );
            }
            if (!toInsert.isEmpty()) quizRepository.saveAll(toInsert);
        };
    }
}
