package com.my.quiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing   //DB 작업을 할 때, 매번 created_at, updated_at 같은 칼럼을 직접 코딩해서 채우는 건 귀찮고 실수하기 쉬워.
//@EnableJpaAuditing을 켜면 JPA가 자동으로 이런 값을 관리해 줘서 코드가 깔끔해지고 실수를 줄일 수 있어.
@SpringBootApplication

public class QuizApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuizApplication.class, args);
	}

}
