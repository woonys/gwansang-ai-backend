package org.example.gwansangspringaibackend.service;

import org.example.gwansangspringaibackend.fortune.domain.Fortune;
import org.springframework.stereotype.Service;

@Service
public class SlackService {
    public void sendMonitoringMessage(Fortune savedFortune) {
        // 슬랙 메시지 전송
    }

    public void sendErrorMessage(String message, String fortuneAnalysis) {
        // 슬랙 메시지 전송
    }
}
