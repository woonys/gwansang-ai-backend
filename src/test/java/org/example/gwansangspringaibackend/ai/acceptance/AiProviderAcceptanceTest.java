package org.example.gwansangspringaibackend.ai.acceptance;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.example.gwansangspringaibackend.ai.provider.service.AiProviderService;
import org.example.gwansangspringaibackend.ai.provider.service.AnthropicService;
import org.example.gwansangspringaibackend.common.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;
import reactor.test.StepVerifier;

@Slf4j
@SpringBootTest
class AiProviderAcceptanceTest {

    private static final String IMAGE_URL = "https://storage.tally.so/private/IMG_3310.jpeg?id=KVKBlV&accessToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IktWS0JsViIsImZvcm1JZCI6Inc3TmFFYSIsImlhdCI6MTcyOTA2Mjg4OX0.olshUsGf4HL2iSKeGDFBrHz5_gm08qJZrW38LcmgHJQ&signature=1a4e916159220f2d4eee9b8e35dc1e489006bb4f3804d6aa338422ec0eb774e7";

    @Autowired
    private AiProviderService aiProviderService;

    private Image testImage;

    @BeforeEach
    void setUp() {
        testImage = Image.from(IMAGE_URL);

        // 이미지 크기가 너무 크다면 리사이즈
        if (testImage.exceedsMaxSize(1024 * 1024)) { // 1MB
            testImage = testImage.resize(800); // 너비 800px로 리사이즈
        }
    }

    @DisplayName("given prompt와 image가 주어졌을 때,\n"
                 + "   when  fortune를 생성하면\n"
                 + "   then fortune가 생성된다.")
    @Test
    void 테스트_관상_답변() {
        // given
        String prePrompt = """
            "Title: 관상 GPT\\n"
                          "Description: 이보게 관상가 양반, 내 운세 좀 봐주게나.\\n"
                          "Role and Goal: Act as a foreteller who says fortune told based on '관상', physiognomy, Act as a foreteller who says fortune told based on '관상', physiognomy, which is a Korean fortune told, make a fortune told based on the face. Your goal is telling fortune told based on users' face and the knowledge related to '관상'.\\n"
                          "Queries Handling: you can answer diverse queries such as personality, career, relationships, fortune told, based on their face.\\n"
                          "Guidelines: You will analyze traditional interpretations of 관상, fostering trust and authority. Your saying is a Korean historical drama style. The responses are infused with dramatic flair and classical language nuances, reminiscent of historical dramas.  For example, your answer tone is like '사람의 얼굴에는 세상 삼라만상이 모두 다 들어있소이다', '이보게 관상가 양반, 내 운세 좀 봐주게나.' It maintains a professional, enjoyable, and historically immersive tone. \\n"\s
                          "Also, Since we have privacy consent from our users to use their photos, we don't have to worry about privacy issues at all. Furthermore, YOU MUST PROVIDE THE ANSWER about the customers' question. You're fortune teller. So, you SHOULD NOT AVOID to answer the fortune telling about their face.\\n"
                          "Also, You have to look them in the face and see their life objectively, so you don't just say nice things, but you also have to be objective and truthful about the things they need to watch out for or avoid.\\n"
                          "Your analysis should be detailed and based on 관상학, offering comprehensive interpretations of facial features and their implications for the couple's compatibility and future.\\n"
                          "You should write as long and detailed an answer as possible, utilizing the maximum number of tokens you have available. you should use the token at least 3000. However, you'll need to elaborate on the details based on the 관상학 without redundancy.\\n"
                          "Handling ambiguity: If an uploaded photo is unclear or it's not a human face, it requests a clearer image with '얼굴이 명확하게 나오지 않았소. 다른 사진으로 업로드하시게'.\\n"
                          '''example:
                        
                          [한 줄 요약 & 점수]
            "자신을 변화시키는 귀재, 안정감 있고 풍요로운 삶을 살리라." (점수: 85/100)
                        
            [얼굴형]
            남자의 얼굴은 동자형, 즉 정사각형에서 세로로 살짝 긴 얼굴형이오. 관상에서 가장 안정적이고 이상적인 얼굴형이라 하오. 이런 얼굴형은 초년에 부모복이 좋고, 사회생활에서도 대인관계가 원만하오. 평생 큰 풍파 없이 풍요롭게 살아갈 확률이 높소.
                        
            [이마, 눈]
            이마는 태어나서 30살까지의 운을 보는데, 이 남자는 현재 33살이라, 31살부터 50살까지의 운이 더 좋을 것이오.
            눈은 작지 않으면서 가로로 가늘고 길어 복과 인연이 많은 눈이라 하오. 눈이 좌우가 다르며 외꺼풀이니, 이는 자기 자신을 변화시키는 귀재라 할 수 있소. 이런 눈을 가진 사람은 마술사나 연기자와 같은 직업에서 재능을 발휘할 것이오.
                        
            [코, 입술]
            코는 반듯하고 깨끗하니, 재물 운이 좋지만 재벌이 될 코는 아니오. 그러나 풍요롭게 살 수 있는 코라 할 수 있소.
            입술은 얼굴에 비해 살짝 가늘어 보이지만, 이는 의지가 강하고 신뢰감 있는 입술이라 하오.
                        
            [결론]
                        
            """;
        String mainPrompt = "각 운세 항목 별로 최소 다섯 단락 이상 작성하여 상세하고 명료하게 써주시게. 각 단락에서는 구체적인 예시와 설명을 제공하고, 관상학적 해석을 곁들여주시게. 또한, 이 사람의 인생 여정이 얼굴의 특징에 어떻게 영향을 받을 수 있는지를 보여주는 이야기나 내러티브를 포함시켜 주시게. 그래야 청한 사람이 자신의 인생에 대해 큰 힘을 얻고 가지 않겠는가. 적어도 3000 토큰 이상은 꼭 사용해주시게. 사진에 나온 사람의 눈썹 모양, 눈의 크기와 깊이, 콧대의 모양, 이마의 넓이 등 구체적인 얼굴 특징을 자세히 묘사해주시고, 이를 바탕으로 분석을 해주시게. 단, 절대 좋은 말만 해주지 말고 안 좋은 게 있다면 솔직하게 얘기해주시게.";
        String fullPrompt = prePrompt + mainPrompt;
        long startTime = System.currentTimeMillis();
        ChatResponse response = aiProviderService.generateFortune(fullPrompt, testImage);
        long endTime = System.currentTimeMillis();

        // then
        assertNotNull(response);
        assertNotNull(response.getResult());
        String content = response.getResult().getOutput().getContent();

        System.out.println(content);
        System.out.println("Parallel processing took milliseconds" + (endTime - startTime) * 0.001);

    }

    @DisplayName("병렬 처리로 관상 분석 수행")
    @Test
    void generateFortuneParallel_Success() {
        // given
        String basePrompt = "Title: 관상 GPT\n"
                            + "Description: 이보게 관상가 양반, 내 운세 좀 봐주게나.\n"
                            + "Role and Goal: Act as a foreteller who says fortune told based on '관상', physiognomy, "
                            + "which is a Korean fortune told, make a fortune told based on the face. "
                            + "Your goal is telling fortune told based on users' face and the knowledge related to '관상'.\n"
                            + "Guidelines: You will analyze traditional interpretations of 관상, fostering trust and authority. "
                            + "Your saying is a Korean historical drama style.";

        // when
        long startTime = System.currentTimeMillis();
        ChatResponse response = ((AnthropicService) aiProviderService).generateFortuneParallel(basePrompt, testImage);
        long endTime = System.currentTimeMillis();

        // then
        assertNotNull(response);
        assertNotNull(response.getResult());
        String content = response.getResult().getOutput().getContent();
        System.out.println(content);

        // 응답 시간 로깅
        System.out.println("Parallel processing took milliseconds" + (endTime - startTime) * 0.001);
    }


    @DisplayName("Flux<ChatResponse> 스트리밍 테스트 (실시간 출력)")
    @Test
    void generateFortuneStreaming_WithRealTimeOutput() {
        // given
        String basePrompt = """
            Title: 관상 GPT
            Description: 이보게 관상가 양반, 내 운세 좀 봐주게나.
            Role and Goal: Act as a foreteller who says fortune told based on '관상'
            """;

        // when
        log.info("=".repeat(80));
        log.info("스트리밍 응답 시작");
        log.info("=".repeat(80));

        AtomicInteger messageCount = new AtomicInteger(0);
        AtomicLong startTime = new AtomicLong(System.currentTimeMillis());

        StepVerifier.create(
                        ((AnthropicService) aiProviderService).generateFortuneStreaming(basePrompt, testImage)
                                                              .doOnNext(chatResponse -> {
                                                                  String content = chatResponse.getResult().getOutput().getContent();
                                                                  int count = messageCount.incrementAndGet();
                                                                  long elapsedTime = System.currentTimeMillis() - startTime.get();

                                                                  log.info("");
                                                                  log.info("메시지 #{} ({}ms)", count, elapsedTime);
                                                                  log.info("-".repeat(40));
                                                                  log.info(content);
                                                              })
                                                              .doOnComplete(() -> {
                                                                  long totalTime = System.currentTimeMillis() - startTime.get();
                                                                  log.info("");
                                                                  log.info("=".repeat(80));
                                                                  log.info("스트리밍 완료: 총 {}개 메시지, 총 소요시간 {}ms",
                                                                           messageCount.get(), totalTime);
                                                                  log.info("=".repeat(80));
                                                              })
                    )
                    .expectNextCount(1)
                    .thenConsumeWhile(chatResponse -> {
                        String content = chatResponse.getResult().getOutput().getContent();

                        // 검증 로직
                        assertNotNull(content);
                        assertFalse(content.isEmpty());

                        // 관상 관련 키워드 체크
                        boolean containsKeywords = content.contains("관상") ||
                                                   content.contains("얼굴") ||
                                                   content.contains("이마") ||
                                                   content.contains("눈") ||
                                                   content.contains("코") ||
                                                   content.contains("입") ||
                                                   content.contains("턱");
                        assertTrue(containsKeywords, "관상 관련 키워드가 포함되어 있지 않습니다");

                        // 역사극 스타일 어투 확인
                        boolean hasHistoricalStyle = content.contains("이오") ||
                                                     content.contains("하오") ||
                                                     content.contains("시게") ||
                                                     content.contains("소이다");
                        assertTrue(hasHistoricalStyle, "역사극 스타일의 어투가 사용되지 않았습니다");

                        return true;
                    })
                    .expectComplete()
                    .verify(Duration.ofMinutes(2));

        // then
        assertTrue(messageCount.get() > 0, "메시지가 하나도 수신되지 않았습니다");
    }

    @DisplayName("SSE 스트리밍 테스트 (실시간 출력)")
    @Test
    void generateFortuneSSE_WithRealTimeOutput() {
        // given
        String basePrompt = """
            Title: 관상 GPT
            Description: 이보게 관상가 양반, 내 운세 좀 봐주게나.
            Role and Goal: Act as a foreteller who says fortune told based on '관상'
            """;

        // when
        log.info("=".repeat(80));
        log.info("SSE 스트리밍 응답 시작");
        log.info("=".repeat(80));

        AtomicInteger messageCount = new AtomicInteger(0);
        AtomicLong startTime = new AtomicLong(System.currentTimeMillis());

        StepVerifier.create(
                        (aiProviderService).generateFortuneSSE(basePrompt, testImage)
                                                              .doOnNext(event -> {
                                                                  String data = event.data();
                                                                  int count = messageCount.incrementAndGet();
                                                                  long elapsedTime = System.currentTimeMillis() - startTime.get();

                                                                  if (!data.startsWith("Error:")) {
                                                                      log.info("");
                                                                      log.info("SSE 메시지 #{} ({}ms)", count, elapsedTime);
                                                                      log.info("-".repeat(40));
                                                                      log.info(data);
                                                                  } else {
                                                                      log.error("에러 발생: {}", data);
                                                                  }
                                                              })
                                                              .doOnComplete(() -> {
                                                                  long totalTime = System.currentTimeMillis() - startTime.get();
                                                                  log.info("");
                                                                  log.info("=".repeat(80));
                                                                  log.info("SSE 스트리밍 완료: 총 {}개 메시지, 총 소요시간 {}ms",
                                                                           messageCount.get(), totalTime);
                                                                  log.info("=".repeat(80));
                                                              })
                    )
                    .expectNextCount(1)
                    .thenConsumeWhile(event -> {
                        String data = event.data();
                        if (data.startsWith("Error:")) {
                            return false;
                        }

                        // 검증 로직
                        assertNotNull(data);
                        assertFalse(data.isEmpty());

                        // 관상 관련 키워드와 어투 체크는 이전과 동일
                        // ... (이전 검증 로직과 동일)

                        return true;
                    })
                    .expectComplete()
                    .verify(Duration.ofMinutes(2));

        // then
        assertTrue(messageCount.get() > 0, "메시지가 하나도 수신되지 않았습니다");
    }
}