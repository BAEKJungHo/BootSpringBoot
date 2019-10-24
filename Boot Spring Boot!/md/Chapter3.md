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