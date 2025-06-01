package com.anli.jesse.exam.wheelactivity.interfaces;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.anli.jesse.exam.wheelactivity.application.WheelApplicationService;
import com.anli.jesse.exam.wheelactivity.domain.model.DrawResult;
import com.anli.jesse.exam.wheelactivity.interfaces.request.DrawRequest;
import com.anli.jesse.exam.wheelactivity.interfaces.response.ApiResponse;
import jakarta.validation.Valid;


@RestController
@RequiredArgsConstructor
public class PrizeWheelController {

    private final WheelApplicationService lotteryApplicationService;

    @PostMapping("/api/wheel/play")
    public ResponseEntity<ApiResponse<?>> playWheel(@RequestBody @Valid DrawRequest drawRequest) {
        List<DrawResult> drawResults = lotteryApplicationService.drawPrize(drawRequest.getUserId(), drawRequest.getActivityId(),drawRequest.getDrawTimes());
        return ResponseEntity.ok(ApiResponse.success(Map.of("drawResults", drawResults)));
    }

}
