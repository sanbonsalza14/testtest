package com.my.quiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class QuizApplication {

	public static void main(String[] args) {
		// ✅ 서버/로컬 환경이 달라도 createdAt/updatedAt 포맷 흔들리지 않도록 고정
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		SpringApplication.run(QuizApplication.class, args);
	}

	/** ✅ 감사 타임스탬프 공급자 (테스트에서 Clock 주입으로 시간 고정 가능) */
	@Bean
	public DateTimeProvider auditingDateTimeProvider(Clock clock) {
		return () -> Optional.of(LocalDateTime.now(clock));
	}

	/** ✅ 시스템 공용 시계: KST 기준 */
	@Bean
	public Clock systemClock() {
		return Clock.system(ZoneId.of("Asia/Seoul"));
	}
}
