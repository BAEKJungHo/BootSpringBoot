## 스프링 부트 구동 실패

스프링 부트 애플리케이션이 구동 중 실패하면 `FailureAnalyzers`를 통해서 발생한 문제를 출력하고 이 문제를 해결할 수 있는 기회를 제공한다.

예를들어 8080 포트를 사용하고 있는데 다른 애플리케이션을 실행하는경우 아래와 같은 메세지를 볼 수 있다.

Embedded servlet container failed to start. Port 8080 was already in use.

## 유연한(Fluent) 빌더 API

ApplicationContext 계층(hierarchy, 부모/자식 관계의 다중 컨텍스트)을 구성하거나 유연한(Fluent) 형태의 빌더 API를 선호한다면 `SpringApplicationBuilder`를 사용할 수 있다.
SpringApplicationBuilder는 parent와 child 메서드를 포함하고 있어 계층을 구성하거나 여러 메서드를 연이어 호출할 수 있다.

```java
new SpringApplicationBuilder()
    .sources(Parent.class) // 애플리케이션에 추가하려는 구성 클래스아ㅗ 컴포넌트
    .child(Application.class) // 자식 컨텍스트 지정
    .bannerMode(Banner.Mode.OFF) // 배너모드 비활성화
    .run(args); // 실행인자 전달
```

얘는 거의 안쓰임

ApplicationContext 계층을 생성할 때 몇가지 제약이 있는데, 웹 컴포넌트는 하위 컨텍스트 내에 포함되어야 하고 부모와 자식 컨텍스트에는 동일한 Environment가 사용된다.

## CommandLineRunner와 ApplicationRunner

스프링 부트 애플리케이션(@SpringBootApplication이 적용된 클래스)이 실행될 때 SpringBootApplication과 함께 실행 되도록 빈을 정의할 수 있는 인터페이스가 CommandLineRunner와 ApplicationRunner이다. 둘의 차이점은 CommandLineRunner는 애플리케이션 실행인자를 문자열로 받는데 반해, ApplicationRunner는 ApplicationArguments 배열로 받는다는 것이다.

이 인터페이스의 용도는 `애플리케이션이 실행될 때 반드시 한번 실행되어야 하는 기능을 구현`하는데 사용할 수 있다. 하나의 컴포넌트로 인식되기 때문에 애플리케이션 컨텍스트를 공유한다.

### CommandLineRunner 구현 예제

```java
@Slf4j
@Component
public class CmdRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        log.debug("Run CmdRunner");
    }
}
```

String... args는 가변길이 파라미터 (Variable Length Parameter)로 여러개의 문자열을 받을 수 있다. 0 ~ *

run("A", "B", "C"); // 가능
run("A", "B", "C", "D", "E", "F"); // 

### ApplicationRunner

ApplicationRunner가 CommandLineRunner보다 훨씬 많이 쓰인다. CommandLineRunner는 거의 안쓰임

```java
@Slf4j
@Component
public class AppRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.debug("Run AppRunner");
    }
}
```

## @Value

@Value 어노테이션을 사용하면 application.yml, properties에 설정된 속성 값을 주입 받을 수 있다.

name 속성 주입 예 

```java
@Component
public class MyBean {
    @Value("${name}")
    private String name;
}
```

application.yml

```yml
name: Boot Spring Boot
```

## 무작위값 구성

`RandomValuePropertySource`는 무작위 값을 주입하는데 유용하다(테스트 혹은 비밀을 위한 무작위 값) 32 비트 정수(int), 64 비트 정수(long), UUID(Universeally Unique IDentifier) 혹은 문자열

```yml
my:
    secret: ${random.value}
    number: ${random.int}
        less.then.ten: ${random.int(10)}
        in.range: ${random.int[1024, 65536]}
    bignumber: ${random.long}
    uuid: ${random.uuid}
```

위에서 number가 오류났음

```yml
code:
    secret: ${random.value}
    bignumber: ${random.long}
    uuid: ${random.uuid}
```

```java
package boot.baek.learning;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties("code")
public class RandomValuePropertySource {
    private String secret;
    private String bigNumber;
    private String uuid;
}
```

@ConfigurationProperties 어노테이션을 사용하면 yml이나 properties에 있는 속성을 바인딩 시킬 수 있다.

@ConfigurationProperties 괄호 안에는 소문자만 들어갈 수있다. createSecretCode와 같이 카멜케이스 형식의 문자열을 넣으면

prefix must be in canonial form 에러가 난다.

```java
package boot.baek.learning;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("greeting")
public class HelloRestController {

    private final RandomValuePropertySource randomValuePropertySource;

    @GetMapping
    public String greeting() {
        String uuid = randomValuePropertySource.getUuid();
        String bigNumber = randomValuePropertySource.getBigNumber();
        return "Hello ~" + uuid + "!!!" + bigNumber;
    }

}
```

## 애플리케이션 속성 파일

SpringApplication은 다음 위치에 있는 application.yml(properties) 파일을 읽어서 Environment에 속성을 적재한다.

1. 현재 디렉터리 하위에 있는 /config
2. 현재 디렉터리
3. 클래스패스 /config 패키지
4. 최상위 클래스 패스


최상위 클래스 패스는 ${PROJECT_HOME}/src/main/java 혹은 ${PROJECT_HOME}/src/main/resources를 의미한다.

### properties보다 yml이 좋은 이유

properties 파일로 작성할 경우 프로파일에 따라서 구성 파일이 여러 개로 나뉘어야 하는데 반해, yml은 --- 구분자를 통해서 프로파일을 구분지을 수 있어 하나의 애플리케이션 속성문서에서 
프로파일별 적용을 파악할 수 있으며 프로파일에 따라 덮어쓰는 속성들을 파악하기가 용이하다.

```yml
... 생략

---
spring:
  profiles: test
  datasource:
    url: jdbc:log4jdbc:mysql://xxx.xxx.xx.xx:3307/DB명?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
    username: testUser
    password: root
---
spring:
  profiles: learning
  datasource:
    url: jdbc:log4jdbc:mysql://xxx.xxx.xx.xx:3306/DB명?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
    username: learningUser
    password: root
---
```

이런식으로 적용하면 Edit Configuration에서 active propfiles에 profiles 이름을 적어서 원하는 프로파일을 사용할 수 있다.

### 속성내 치환자(placeholder)

application.yml에 정의된 값은 Environment에 존재할 경우 필터링 되기 때문에 앞서 정의한 값을 위처럼 다음처럼 사용할 수 있다.

```yml
app:
    name: boot spring boot
    descr: ${app.name} is Spring Boot Application
```

### 스타터(Starters)를 사용하면 spring-boot-starter를 통해서 자동으로 yml을 제공한다.

### YAML 적재

    스프링 프레임워크는 YAML 문서 적재에 사용하기 위해 두 가지 클래스를 제공한다.

    1. YamlPropertiesFactoryBean : Properties에 YAML을 적재
    2. YamlMap-FactoryBean : Map에 YAML을 적재

### YAML 단점

YAML 파일은 @PropertySource 어노테이션에 적재되지 않는다. @PropertySource에 값을 적재하기 위해서는 properties 파일을 사용해야 한다.

```yml
barxxx:
    ~
barzzz:
```

서드파티 구성
@ConfigurationProperties는 클래스 레벨에서 선언해도 잘 동작하지만, 메서드 레벨에서 @Bean을 선언할 때도 사용할 수 있다. 이 방법은
서드파티 컴포넌트에 속성을 연결하고 외부에서 제어할 때 특히 유용하다.

```java
@Bean
@ConfigurationProperties(prefix = "bar")
public BarComponent barComponent() {

}
```


prefix = "이름" 으로 사용하면 해당 이름을 접두사로 가진 값들이 바인딩 된다.

### 느슨한 연결

스프링 부트는 Environment 속성에서 @ConfigurationProperties 빈으로 연결할 때 몇 가지 느슨한 규칙을 사용하기 때문에 Environment 속성명과 빈 송성명을 정확하게
맞추지 않아도 된다.

아래와 같이 되어있을 경우

```java
@Getter @Setter
@ConfigurationProperties(prefix="person")
public class OwnerProperties {
    private String firstName;
}
```

아래 처럼 사용할 수 있다.

property / name

person.firstName  카멜식 문법

person.first-name 프로퍼티스와 야믈 파일에서 사용하길 권장하는 대시 표기법

person.first_name 프로퍼티스와 야믈 파일에서 대안으로 사용하는 미퉂ㄹ 표기법

PERSON_FIRST_NAME 시스템 환경변수에서 사용할 때 권장

### @ConfigurationProperties 유효성 검사

스프링 부트는 스프링의 `@Validated` 어노테이션을 @ConfigurationProperties 클래스에 선언하여 유효성 검사를 할 수 있다.

클래스패스 상에 JSR-303 구현체가 있다면 클래스 필드에 강제 어노테이션을 추가하는 것은 간단하다.

```java
@Component
@Data
@Validated
@ConfigurationProperties("example")
public class ExampleProperties {
    private boolean enabled;

    @NotNull
    private InetAddress remoteAddress;

    @Valid
    privaet final Security security = new Security();

    @Data
    public static class Security {
        @NotEmpty
        private String username;
        private String password;
        private List<String> roles = new ArrayList<>(Collections.singleton("USER"));
    }
}
```

@Valid는 javax.validation.Valid이고 @Validated는 org.springframework.validation.annotation.Validated입니다

@Validated를 사용한 그룹지정 방법 https://code-examples.net/ko/q/227f614

## @Bean 메서드는 정적(static)으로 선언해야한다.

@Bean 메서드를 정적으로 선언하면 @Configuration 클래스를 인스턴스화 하지 않고도 빈을 만들 수 있다. 이 방법은 초기 인스턴스 생성으로 발생할 수 있는 문제를 피할 수 있다.

## @ConfiguationProperties와 @Valid 비교

feature / @ConfigurationProperties / @Valid

느슨한연결             지원           미지원
메타데이터 지원        지원           미지원
SpEL 평가             미지원          지원

## simpleMappingExceptionResolver 공부하기

## 로깅

스프링 부트는 기본 구성은 그대로 유지하면서 모든 내부 로깅에 `Commons Logging`을 사용한다. Java Util Logging, Log4J2 Logback을 위한 기본 구성을 제공한다.

스타터를 사용한다면 로깅에는 기본적으로 `로그백(Logback)`을 이용한다.

### 파일 출력

스프링 부트는 기본적으로 콘솔에만 로그를 출력하고 로그 파일을 작성하지는 않는다.

만약 로그 파일을 작성하길 원한다면 logging.file 혹은 logging.path 속성을 설정해야 한다.(application.yml에 작성)

logging.* 송성은 다음과 같다.

logging.file / logging.path / 예 / 설명 
미지정           미지정                         콘솔에만 출력됨
파일지정         미지정        app.log          지정된 로그파일을 작성한다. 이름으로 현재 디렉터리에 대한 상대경로나 위치를 지정
미지정           지정 디렉터리  /var/log         지정된 디렉터리에 spring.log로 작성한다. 이름으로 현재 디렉터리나 위치를 지정할 수 있다.


로그 파일은 10MB 단위로 절삭되며 ERRROR, WARN 그리고 INFO 레벨 메세지가 기본적으로 기록된다.