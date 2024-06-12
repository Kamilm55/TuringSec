package com.turingSecApp.turingSec.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class GlobalConstants {

    //  @Value annotation does not work directly with static fields.
    @Value("${app.root_url}")
    public String ROOT_LINK;


}
