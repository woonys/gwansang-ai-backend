package org.example.gwansangspringaibackend.ai.provider.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.example.gwansangspringaibackend.common.Image;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnthropicService implements AiProviderService {
    private final AnthropicChatModel chatModel;
    private final ExecutorService executorService = Executors.newFixedThreadPool(7);

    @Override
    public ChatResponse generateFortune(String prompt, Image image) {
        List<Message> messages = new ArrayList<>();

        if (image != null) {
            String base64Image = image.toBase64();
            ByteArrayResource imageResource = new ByteArrayResource(
                Base64.getDecoder().decode(base64Image)
            );

            // contentType을 Image 객체에서 가져와 사용
            MimeType mimeType = MimeType.valueOf(image.getContentType());
            messages.add(new UserMessage(prompt,
                                         List.of(new Media(mimeType, imageResource))));
        } else {
            messages.add(new UserMessage(prompt));
        }

        return chatModel.call(new Prompt(messages));
    }

    public ChatResponse generateFortuneParallel(String basePrompt, Image image) {
        // 각 섹션별 프롬프트 준비
        Map<String, String> sectionPrompts = getPrompts();

        try {
            // 각 섹션별로 CompletableFuture 생성
            Map<String, CompletableFuture<String>> futures = new HashMap<>();

            for (Map.Entry<String, String> entry : sectionPrompts.entrySet()) {
                futures.put(entry.getKey(), CompletableFuture.supplyAsync(
                    () -> generateSectionResponse(basePrompt + "\n" + entry.getValue(), image),
                    executorService
                ));
            }

            // 모든 Future 완료 대기
            CompletableFuture.allOf(futures.values().toArray(new CompletableFuture[0])).join();

            // 결과 조합
            StringBuilder finalResponse = new StringBuilder();
            finalResponse.append(futures.get("summary").get()).append("\n");
            finalResponse.append(futures.get("face").get()).append("\n");
            finalResponse.append(futures.get("forehead").get()).append("\n");
            finalResponse.append(futures.get("nose").get()).append("\n");
            finalResponse.append(futures.get("conclusion").get());

            // ChatResponse 형식으로 변환
            return createChatResponse(finalResponse.toString());

        } catch (Exception e) {
            log.error("Error generating parallel fortune", e);
            throw new RuntimeException("Failed to generate fortune", e);
        }
    }

    private String generateSectionResponse(String prompt, Image image) {
        List<Message> messages = new ArrayList<>();
        if (image != null) {
            String base64Image = image.toBase64();
            ByteArrayResource imageResource = new ByteArrayResource(
                Base64.getDecoder().decode(base64Image)
            );
            MimeType mimeType = MimeType.valueOf(image.getContentType());
            messages.add(new UserMessage(prompt,
                                         List.of(new Media(mimeType, imageResource))));
        } else {
            messages.add(new UserMessage(prompt));
        }

        ChatResponse response = chatModel.call(new Prompt(messages));
        return response.getResult().getOutput().getContent();
    }

    private ChatResponse createChatResponse(String content) {
        AssistantMessage assistantMessage = new AssistantMessage(content);
        Generation generation = new Generation(assistantMessage);
        return new ChatResponse(List.of(generation));

    }

    @Override
    public Flux<ChatResponse> generateFortuneStreaming(String basePrompt, Image image) {
        Map<String, String> sectionPrompts = Map.of(
            "summary", "다음 이미지를 보고 한 줄 요약과 100점 만점의 점수를 매겨주시오...",
            "face", "다음 이미지를 보고 얼굴형을 분석해주시오...",
            "forehead", "다음 이미지를 보고 이마, 눈썹, 눈을 분석해주시오...",
            "nose", "다음 이미지를 보고 코, 입, 턱을 분석해주시오...",
            "conclusion", "앞선 분석을 바탕으로 종합 평가를 해주시오..."
        );

        List<Flux<ChatResponse>> sectionFluxes = new ArrayList<>();

        for (Map.Entry<String, String> entry : sectionPrompts.entrySet()) {
            List<Message> messages = new ArrayList<>();
            if (image != null) {
                String base64Image = image.toBase64();
                ByteArrayResource imageResource = new ByteArrayResource(
                    Base64.getDecoder().decode(base64Image)
                );
                MimeType mimeType = MimeType.valueOf(image.getContentType());
                messages.add(new UserMessage(basePrompt + "\n" + entry.getValue(),
                                             List.of(new Media(mimeType, imageResource))));
            } else {
                messages.add(new UserMessage(basePrompt + "\n" + entry.getValue()));
            }

            Flux<ChatResponse> sectionFlux = chatModel.stream(new Prompt(messages))
                                                      .filter(response -> response != null &&
                                                                          response.getResult() != null &&
                                                                          response.getResult().getOutput() != null &&
                                                                          response.getResult().getOutput().getContent() != null &&
                                                                          !response.getResult().getOutput().getContent().isEmpty())
                                                      .subscribeOn(Schedulers.boundedElastic());

            sectionFluxes.add(sectionFlux);
        }

        return Flux.merge(sectionFluxes);
    }

    @Override
    public Flux<ServerSentEvent<String>> generateFortuneSSE(String basePrompt, Image image) {
        return generateFortuneStreaming(basePrompt, image)
            .filter(response -> response != null &&
                                response.getResult() != null &&
                                response.getResult().getOutput() != null)
            .map(chatResponse -> {
                String content = chatResponse.getResult().getOutput().getContent();
                if (content == null || content.isEmpty()) {
                    return ServerSentEvent.<String>builder()
                                          .data("스트리밍 응답이 비어있습니다.")
                                          .build();
                }
                return ServerSentEvent.<String>builder()
                                      .data(content)
                                      .build();
            })
            .onErrorResume(error -> {
                log.error("Error generating streaming fortune", error);
                return Flux.just(ServerSentEvent.<String>builder()
                                                .data("Error: " + error.getMessage())
                                                .build());
            });
    }

    @Override
    public boolean supportsImageAnalysis() {
        return true;
    }

    @Override
    public String getProviderName() {
        return "ANTHROPIC";
    }

    private static Map<String, String> getPrompts() {
        return Map.of(
            "summary", """
                다음 이미지를 보고 한 줄 요약과 100점 만점의 점수를 매겨주시오. 
                형식은 [한 줄 요약 & 점수] 로 작성해주시오.
                        
                예시:
                [한 줄 요약 & 점수]
                "지혜롭고 인정 많은 얼굴, 사람들과의 인연이 풍성한 복상이라 하겠소." (점수: 88/100)
                        
                답변 시 주의사항:
                - 그 사람의 가장 두드러진 특징을 한 문장으로 요약
                - 관상학적 해석과 미래의 가능성을 함께 언급
                - 점수는 외모가 아닌 관상학적 길흉화복을 기준으로 책정
                """,

            "face", """
                다음 이미지를 보고 얼굴형을 분석해주시오. 
                형식은 [얼굴형] 으로 작성해주시오.
                        
                예시:
                [얼굴형]
                이 사람의 얼굴은 명품 도자기와 같은 계란형이오. 이마는 넓고 둥글며, 아래로 내려올수록 부드럽게 좁아지는 형상이라 하겠소. 이런 얼굴형은 천수를 누리는 장수상이며, 사회생활에서도 귀인의 도움을 많이 받는 상이오. 다만 너무 부드러운 턱선은 때로는 우유부단한 성격을 나타내니, 중요한 순간에는 과감한 결단이 필요할 것이오.
                        
                답변 시 주의사항:
                - 얼굴의 전체적인 형태를 구체적으로 묘사
                - 각 부분의 균형과 조화에 대한 설명
                - 해당 얼굴형이 의미하는 운명과 성격 특성 분석
                - 주의해야 할 점도 반드시 언급
                """,

            "forehead", """
                다음 이미지를 보고 이마, 눈썹, 눈을 분석해주시오. 
                형식은 [이마, 눈썹, 눈] 으로 작성해주시오.
                        
                예시:
                [이마, 눈썹, 눈]
                이마는 옥돌처럼 맑고 광택이 나며, 넓이와 높이가 절묘한 균형을 이루고 있소. 이는 30세 이후 크게 발전할 상이며, 학식과 지혜가 뛰어난 이마라 하겠소.
                눈썹은 농묵으로 그린 듯 짙고 굵으며, 눈썹산의 기세가 힘차게 뻗어있소. 이는 포부가 크고 추진력이 강한 상이니, 큰일을 도모하기에 적합하오. 그러나 너무 강한 기세는 때로 아랫사람들을 압도할 수 있으니 중용을 지키도록 하시오.
                눈은 마치 맑은 호수와 같이 깊고 평화로우며, 눈동자의 광채가 특히 빼어나오. 이는 총명하고 지혜로우며 사리판단이 정확한 상이오. 눈꼬리가 살짝 올라간 것은 낙천적이고 친화력 있는 성격을 나타내니, 대인관계운이 매우 좋을 것이오.
                        
                답변 시 주의사항:
                - 각 부위별 구체적인 형태와 특징 설명
                - 관상학적 의미와 그에 따른 운명 해석
                - 장단점을 균형있게 서술
                - 각 부위가 서로 어떻게 조화를 이루는지 분석
                """,

            "nose", """
                다음 이미지를 보고 코, 입, 턱을 분석해주시오. 
                형식은 [코, 입, 턱] 으로 작성해주시오.
                        
                예시:
                [코, 입, 턱]
                코는 마치 옥으로 깎아 만든 듯 반듯하고 단정하며, 콧대가 높고 콧망울이 적당히 도톰하오. 이는 귀인의 도움을 받아 40대 이후 크게 번창할 상이며, 특히 재물복과 관록운이 뛰어난 코라 하겠소.
                입술은 붉은 대추알처럼 선명하고 윤기가 나며, 입꼬리가 은은하게 올라가 있소. 이는 말재주가 뛰어나고 인덕이 두터운 상이니, 어떤 자리에서도 사람들의 마음을 사로잡을 수 있는 입이오.
                턱은 옥구슬처럼 둥글고 부드러우면서도 적당한 힘이 실려있소. 이는 의지가 강하고 끈기 있는 상이며, 노년운도 매우 좋은 턱이오. 다만 턱 끝이 너무 뾰족한 것은 경계해야 하니, 때로는 고집을 꺾을 줄도 알아야 하오.
                        
                답변 시 주의사항:
                - 각 부위의 생김새를 세밀하게 묘사
                - 재물운, 관록운, 부부운 등 구체적인 운세 분석
                - 각 부위가 나타내는 성격적 특징 설명
                - 주의점과 개선방향 제시
                """,

            "conclusion", """
                앞선 분석을 바탕으로 종합 평가를 해주시오. 
                형식은 [종합 평가] 로 작성해주시오.
                        
                예시:
                [종합 평가]
                자네의 관상을 종합적으로 살펴보니, 참으로 귀한 상을 타고났소. 이마의 지혜, 눈의 총명함, 코의 귀품이 삼재를 이루니 하늘이 내린 복을 타고난 상이라 하겠소.
                        
                특히 40대 이후의 운세가 매우 빼어나니, 젊어서 쌓은 노력이 이때에 이르러 큰 결실을 맺을 것이오. 재물운과 관록운이 좋고, 특히 사람들과의 인연이 두터워 귀인의 도움을 많이 받을 것이오.
                        
                다만 자네의 관상에서 보이는 몇 가지 경계할 점도 있소. 눈썹의 기세가 강한 것은 야망이 큰 상이지만, 때로는 이로 인해 주변 사람들과 마찰이 생길 수 있으니 중용의 도를 지키도록 하시오. 또한 턱의 뾰족함은 고집이 셀 수 있음을 경계하는 것이니, 때로는 유연한 자세로 상황에 대처하는 지혜도 필요할 것이오.
                        
                앞으로의 인생에서 특히 주의해야 할 시기는 35세에서 37세 사이이오. 이때는 이마와 코의 운세가 충돌하는 시기이니, 특히 사업이나 투자에 있어 신중을 기해야 하오.
                        
                마지막으로 한 가지 조언을 드리자면, 자네의 얼굴에서 보이는 지혜와 인덕을 잘 활용하시오. 사람과의 인연을 소중히 하고, 배움을 게을리하지 않으며, 겸손한 자세를 잃지 않는다면, 50대 이후에는 더없이 영화로운 복을 누리게 될 것이오.
                        
                답변 시 주의사항:
                - 전체적인 관상의 특징을 종합적으로 분석
                - 구체적인 시기별 운세 제시
                - 주의해야 할 점과 개선방향 상세히 설명
                - 앞으로의 인생에 대한 조언과 격려 포함
                - 최소 다섯 단락 이상으로 상세하게 작성
                """
        );
    }
}