package boot.spring.boot.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HelloController.java
 * HelloController
 * ==============================================
 *
 * @author BJH
 * @history 작성일            작성자     변경내용
 * @history 2019-10-21         BJH      최초작성
 * ==============================================
 */
@RestController
public class HelloController {

    @GetMapping("/greeting")
    public String greeting() {
        return "Hello, Boot Spring Boot !!";
    }

    /**
     * @RestController = @Controller + @ResponseBody
     * 스프링 4.0에 추가된 메타 어노테이션이다.
     * @GetMapping = @RequestMapping(mehtod = RequestMethod.GET)
     * 스프링 4.3에 추가된 메터 어노테이션이다.
     */

}
