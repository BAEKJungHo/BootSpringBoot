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

