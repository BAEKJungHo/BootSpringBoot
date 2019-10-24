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
