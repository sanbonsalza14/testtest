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
            if (!userRepository.existsByRole(Role.ADMIN)) {
                UserEntity admin = new UserEntity();
                admin.setEmail("root");
                admin.setPassword("admin");
                admin.setNickname("관리자");
                admin.setRole(Role.ADMIN);
                admin.setApproved(true);
                userRepository.save(admin);
            }

            if (quizRepository.count() == 0) {
                List<Quiz> list = new ArrayList<>();
                String[][] data = new String[][]{
                        {"달팽이도 이빨이 있다.", "O"},
                        {"지하철 1량(칸)에는 출입문이 모두 8개이다. (양끝 합치면 10개도 O)", "O"},
                        {"세계에서 제일 처음으로 텔레비전 방송을 시작한 나라는 영국이다.", "O"},
                        {"말도 잠잘 때 사람처럼 코를 곤다.", "O"},
                        {"셰익스피어 희곡 ‘햄릿’의 햄릿은 네덜란드 사람이다.", "X"},
                        {"바늘 한 쌍은 모두 22개이다.", "X"},
                        {"북두칠성은 시계의 반대 방향으로 회전한다.", "O"},
                        {"게의 다리는 모두 10개이다.", "O"},
                        {"열대 지방에 자라는 나무에는 나이테가 없다.", "O"},
                        {"늑대는 개과, 호랑이는 고양이과, 닭은 꿩과에 속한다.", "O"},
                        {"화장용 크림의 단맛은 글리세린 성분 때문.", "O"},
                        {"벼룩은 수컷이 암컷보다 크다.", "X"},
                        {"딸기는 장미과 식물이다.", "O"},
                        {"여의도 국회의사당 기둥은 24개다.", "O"},
                        {"1~100 사이 9의 개수는 19개다.", "X"},
                        {"금강산은 계절별로 불리는 이름이 다르다.", "O"},
                        {"병아리도 배꼽이 있다.", "O"},
                        {"전쟁 시 남아 출생률이 높다.", "O"},
                        {"(중복) 전쟁 시 남아 출생률이 높다.", "O"},
                        {"향수는 손등에 바르고 10분 후 향을 확인한다.", "O"},
                        {"세계적으로 가장 많이 발생하는 병은 말라리아다.", "X"},
                        {"세계 최초 신용카드는 아메리칸 익스프레스다.", "X"},
                        {"람바다는 브라질 춤이다.", "O"},
                        {"밀물·썰물은 하루 2번씩 일어난다.", "O"},
                        {"고추 1관은 5kg이다.", "X"},
                        {"조선 호패는 남녀 모두가 소지했다.", "X"},
                        {"인간의 뇌세포는 재생이 안 된다.", "O"},
                        {"감각기관 중 가장 먼저 나빠지는 건 시각이다.", "O"},
                        {"우리나라 최초 대중가요는 ‘희망가’다.", "X"},
                        {"가장 넓은 차선은 광화문 16차선이다.", "O"},
                        {"시내버스 경로석은 6석 이상이어야 한다.", "O"},
                        {"용은 십장생의 하나다.", "X"},
                        {"일간신문은 3종 우편물이다.", "O"},
                        {"물고기도 기침을 한다.", "O"},
                        {"사슴뿔은 매년 빠졌다가 다시 난다.", "O"},
                        {"사람의 땀은 산성이다.", "O"},
                        {"소는 앞다리부터, 말은 뒷다리부터 앉는다.", "O"},
                        {"색소폰은 최초 연주자 이름에서 유래했다.", "O"},
                        {"로댕 ‘생각하는 사람’은 오른손으로 턱을 괜다.", "O"},
                        {"원시인의 가장 큰 적은 공룡이었다.", "X"},
                        {"멀리 들리는 목소리는 여자 목소리다.", "O"},
                        {"어린 닭이 낳은 달걀일수록 크다.", "X"},
                        {"열대어의 입맞춤은 애정 표현이다.", "X"},
                        {"축구공은 흰·검만 사용해야 한다.", "X"},
                        {"마라톤 42.195km는 1회 아테네부터였다.", "X"},
                        {"탱고의 고장은 아르헨티나다.", "O"},
                        {"단오날 청포(창포)물로 머리를 감았다.", "X"},
                        {"고래는 5m 이하 물속에서 잠을 잔다.", "X"},
                        {"우리나라 최초 국립공원은 지리산이다.", "O"},
                        {"최다 출산 기록은 60명 이하다.", "X"}
                };
                for (String[] row : data) {
                    Quiz q = new Quiz();
                    q.setContent(row[0]);
                    q.setAnswer(row[1]);
                    q.setWriter("root");
                    list.add(q);
                }
                quizRepository.saveAll(list);
            }
        };
    }
}
