package edu.miage.springboot.services.impl.ai;

import java.time.Duration;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.miage.springboot.services.interfaces.AiMatchingService;
import edu.miage.springboot.utils.ai.AiResponseUtils;
import edu.miage.springboot.web.dtos.ai.MatchingResultDTO;
import edu.miage.springboot.web.dtos.ai.OllamaResponseDTO;

@Service
public class AiMatchingServiceImpl implements AiMatchingService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiMatchingServiceImpl(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://localhost:11434")
                .build();
    }

    @Override
    public MatchingResultDTO matchCvWithJob(String cvText, String jobDescription) {

        String prompt =
                "You are an AI recruitment analyst.\n\n" +
                "JOB OFFER:\n" + jobDescription + "\n\n" +
                "CANDIDATE CV:\n" + cvText + "\n\n" +
                "Return ONLY valid JSON.\n" +
                "Do not add explanations.\n" +
                "Do not use markdown.\n\n" +
                "JSON format:\n" +
                "{\n" +
                "  \"matchingScore\": number,\n" +
                "  \"strengths\": [],\n" +
                "  \"missingSkills\": [],\n" +
                "  \"recommendation\": \"short sentence\"\n" +
                "}";

        Map<String, Object> body = Map.of(
                "model", "llama3",
                "prompt", prompt,
                "stream", false
        );

        // 1️⃣ Appel Ollama (JSON technique)
        String rawResponse = webClient.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(60))
                .block();

        try {
            // 2️⃣ Parser la réponse Ollama
            OllamaResponseDTO ollama =
                    objectMapper.readValue(rawResponse, OllamaResponseDTO.class);

            // 3️⃣ Extraire le JSON métier depuis ollama.response
            String json =
                    AiResponseUtils.extractJson(ollama.getResponse());

            
            return objectMapper.readValue(json, MatchingResultDTO.class);

        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to parse Ollama response.\nRaw response:\n" + rawResponse,
                e
            );
        }
    }
}
