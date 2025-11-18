package com.example.loginapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * アプリケーションのエントリーポイントクラス。
 * <p>
 * このクラスはSpring Bootアプリケーションの起動クラスであり、
 * {@link SpringBootApplication} アノテーションによって
 * 自動設定・コンポーネントスキャン・設定クラスの登録を行います。
 * </p>
 *
 * <p>
 * また、{@link MapperScan} により MyBatis の Mapper インターフェースを
 * 指定パッケージ（{@code com.example.loginapp.mapper}）から自動的に検出します。
 * </p>
 *
 * <p>
 * アプリケーションを実行する際は、{@link #main(String[])} メソッドをエントリーポイントとして
 * Spring Boot が起動します。
 * </p>
 */
@SpringBootApplication
@MapperScan("com.example.loginapp.domain.repository")
public class LoginappApplication {

	/**
	 * Spring Boot アプリケーションを起動するメインメソッド。
	 *
	 * @param args コマンドライン引数（任意）
	 */
	public static void main(String[] args) {
		SpringApplication.run(LoginappApplication.class, args);
	}

}
