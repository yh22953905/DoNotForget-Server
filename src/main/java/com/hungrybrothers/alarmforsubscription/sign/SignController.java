package com.hungrybrothers.alarmforsubscription.sign;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hungrybrothers.alarmforsubscription.common.Const;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping(Const.API_SIGN)
@RequiredArgsConstructor
public class SignController {
    private final SignService signService;

    @PostMapping("/in/openid")
    public ResponseEntity signInWithOpenID(@RequestBody SignDto signDto) throws JsonProcessingException {
        return ResponseEntity.ok(signService.authenticationOpenId(signDto));
    }
}
