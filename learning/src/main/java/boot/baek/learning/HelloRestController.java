package boot.baek.learning;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("learning")
public class HelloRestController {

    private final RandomValuePropertySource randomValuePropertySource;

    @GetMapping
    public String learning() {
        String uuid = randomValuePropertySource.getUuid();
        String bigNumber = randomValuePropertySource.getBigNumber();
        return "Hello ~" + uuid + "!!!" + bigNumber;
    }

}
