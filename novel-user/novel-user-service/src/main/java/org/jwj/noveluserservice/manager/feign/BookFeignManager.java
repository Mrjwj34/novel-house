package org.jwj.noveluserservice.manager.feign;

import lombok.AllArgsConstructor;
import org.jwj.novelbookapi.feign.BookFeign;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BookFeignManager {
    private final BookFeign bookFeign;
}
