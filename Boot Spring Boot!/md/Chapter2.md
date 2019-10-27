## 스프링 부트 장점

- `의존성 관리`가 쉽다
- 의존성 라이브러리를 바로 사용할 수 있도록 `자동구성(auto-config)`을 권장한다.
- 스프링 프레임워크, 내장 컨테이너, 개발자도구 사용

> 스프링 부트는 출시할 때마다 지원하는 `의존성 라이브러리 버전`을 정리한 목록을 제공

https://goo.gl/H1VShv

## 그레이들의 장점

https://seonhyungjo.github.io/Maven-VS-Gradle/

https://www.holaxprogramming.com/2017/07/04/devops-gradle-is-faster-than-maven/

https://okky.tistory.com/179

## 인텔리제이에서 그레이들 사용하기

springInitializr 클릭 후 groupId와 ArtifactId를 적고난 다음 화면에서 maven과 gradle을 선택할 수 있는 창이 나온다.

## 그레이들로 jar 파일 만들기

jar파일을 만들기 위해서는 

build.gradle 첫 줄에 아래 코드를 입력해야함

```java
buildscript {
    ext {
        springBootVersion = '2.0.0.RELEASE'
    }
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
    }
}
```

## 스타터

스타터는 애플리케이션이 이용할 수 있는 `의존성을 라이브러리 집합체`이다. 

예를들어 Srping Data JPA를 사용하고 싶으면 build.gradle 혹은 pom.xml에 spring-starter-data-jpa 의존성을 추가하면 DataSource와 스프링에 대한 구성을 하지 않아도 바로 사용할 수 있다.

## 부모 스프링부트 스타터 상속

spring-boot-starter-parent를 상속하면 된다.

```xml
<parent>
    <groupId>org.springframewor.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.0.RELEASE</version>
</parent>
```

여기서 스프링 부트와 관련된 버전을 선언해서 사용할 수 있다. 부모 스프링부트 스타터를 상속받으면 의존성 관리의 이점을 얻을 수 있다.

사용안하고도 가능한데 `scope=import`를 이용하면된다.

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <!-- 스프링 부트로부터 의존성관리 불러오기 -->
            <groupId>org.springframework.boot</groupId>
            <artifactid>spring-boot-dependencies</artifactId>
            <version>2.0.0.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

spring-boot-starter-parent는 매우 보수적인 자바 호환성을 선택했다 (JAVA 6)

자바 버전을 바꾸고 싶으면 아래 코드를 추가하면된다.

```xml
<properties>
    <java.version>1.8</java.version>
</properties>
```

## 코드 구조

스프링 부트는 특별히 어떤 계층구조로 작성하라고 강요하지 않는다. 도움되는 몇가지 가이드가 있으며 이외에는 `팀과 조직의 관례(컨벤션 규칙, Convention Rules)`을 따르면된다.

애플리케이션 메인 클래스는 최상위 패키지(boot.spring.boot)에 위치하는 것을 권장한다.

### 가장 권장하는 구조

- boot 
    - spring
        - boot
            - BootSpringBootApplication.java
            - module
                - Customer
                    - domain
                        - Customer.java
                    - repository
                        - CustomerRepository.java
                    - service
                        - CustomerService.java
                        - CustomerServiceImpl.java
                    - web
                        - CustomerController.java
                - Article
                    - domain
                    - repository
                    - service
                    - web

### @SpringBootApplication

@SpringBootApplication이 선언된 메인 클래스 위치를 임의로 변경하면, 수정(변경)해야할 작업도 많아진다.

위 구조에서 BootSpringBootApplication.java를 app 패키지 아래 위치하게 되면

- boot 
    - spring
        - boot
            - app
                - BootSpringBootApplication.java
            - domain
                - Customer.java
            - repository
                - CustomerRepository.java
            - service
                - CustomerService.java
                - CustomerServiceImpl.java
            - web
                - CustomerController.java

```java
@SpringBootApplication(scanBasePacakages = {"boot.spring.boot.domain", "boot.spring.boot.service", "boot.spring.boot.web"})
@EnableJpaRepositories("boot.spring.boot.domain")
@EntityScan("boot.spring.boot.domain")
public class BootSpringBootApplication {
    public static void main(String][] args) {
        SpringApplication.run(BootSpringBootApplication.class, args);
    }
}
```

위처럼 변경해야 한다. 위 처럼 변경해주지 않으면 컨트롤러 등의 빈(Bean)을 탐색하지 못해 예외가 발생한다. `NoSuchBeanDefinitionException`

### 패키지 구조

https://dzone.com/articles/package-structure

https://www.slipp.net/questions/36

http://www.javapractices.com/topic/TopicAction.do?Id=205

## auto-configuration

자동구성 기능을 활성화 하려면 @Configuration이 선언된 클래스에 @EnableAutoConfiguration 혹은 @SpringBootApplication을 추가하면 된다.

## 스프링 MVC 컨트롤러 작성을 위한 14가지 팁

https://dzone.com/articles/14-tips-for-writing-spring-mvc-controller <-- 유용한글 되게 많음


## @ComponentScan

@ComponentScan

@ComponentScan(basePackages = "boot.spring")

@ComponentScan을 사용하면 다른 속성을 정의하지 않아도된다. 모든 애플리케이션 컴포넌트(@Component, @Service, @Repository, @Controller)가 스프링 빈으로 자동등록된다.

`스프링은 여러 DI중에서 생성자 주입 DI를 강조한다.`

생성자 주입 예제

```java
@Service
public class DatabasesAccountService implements AccountService {
    
    private final RiskAccessor riskAccessor;

    @Autowired
    public DatabasesAccountService(RiskAccessor riskAccessor) {
        this.riskAccessor = riskAccessor;
    }
}
```

위 코드에서 생성자 위에 `@Autowired` 어노테이션을 붙였는데 없어도 의존성 주입이 가능하다

어떠한 빈에 생성자가 오직 하나만 있고, 그 생성자에 파라미터 타입에 빈으로 등록 가능한 존재라면, 이 빈(Bean)은 @Autowired 어노테이션이 없더라도 주입을 해준다.

riskAccessor 필드를 final로 함으로써 빈이 생성된 이후에 변경되지 않는다는것을 명시하는 것이다.

위 코드에서 생성자를 없애고 빈으로 등록하는 방법이 있는데 롬복에서 지원하는 @RequiredArgsConstructor 어노테이션을 사용하면 된다.

```java 
@RequiredArgsConstructor
public class DatabasesAccountService implements AccountService {
    
    private final RiskAccessor riskAccessor;

}
```

## @SpringBootApplication

@SpringBootApplication을 사용하면 @ComponentScan, @EnableAutoConfiguration, @Configuration 을 함께 사용한것과 같다.

스프링 부트 1.4 버전부터 @Configuartion 대신 @SpringBootConfiguration로 대체되었다. @SpringBootConfiguration도 내부적으로 @Configuration 어노테이션을 사용하고 있다.

```java
package org.springframework.boot;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Configuration;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
public @interface SpringBootConfiguration {
}
```

## 패키징된 Application 실행하기

스프링 부트 메이븐 혹은 그레이들 플러그인을 사용하고 있다면 실행가능한 jar를 생성하여 `java -jar`명령으로 애플리케이션을 실행할 수 있다.

> java -jar build/lib/boot-spring-boot-2.0.0.RELEASE.jar

## spring-boot-devtools 사용하기

인텔리제이는 저장하면 빌드하는 매크로를 만들어 사용해야한다. 이클립스는 자동

[IntelliJ에서 Spring Devtools 활용하기](https://sbcoba.tistory.com/36)
