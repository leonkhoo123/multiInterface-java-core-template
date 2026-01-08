package com.leon.rest_api.service;

import com.leon.rest_api.config.AiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class AiService {

    private static final Logger logger = LoggerFactory.getLogger(AiService.class);
    private static final Pattern THINK_PATTERN = Pattern.compile("<think>.*?</think>", Pattern.DOTALL);

    private final AiProperties aiProperties;
    private final RestTemplate restTemplate;

    public AiService(AiProperties aiProperties) {
        this.aiProperties = aiProperties;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Sends a prompt to the local LM Studio server and returns the model reply as string.
     * If THINKING is True, removes any internal <think>...</think> reasoning blocks.
     */
    public String inquiryLocalLlm(String prompt) {
        logger.info("Sending prompt to LLM: {}", aiProperties.getApiUrl());
        String reply = "";
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", aiProperties.getModelName());
        payload.put("messages", List.of(
                Map.of("role", "system", "content", "You are a helpful assistant."),
                Map.of("role", "user", "content", prompt)
        ));
        payload.put("temperature", aiProperties.getTemp());
        payload.put("max_tokens", aiProperties.getMaxToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(aiProperties.getApiUrl(), request, Map.class);
            
            if (response.getBody() != null && response.getBody().containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    reply = (String) message.get("content");

                    // 🧠 Remove <think> blocks if THINKING is True (Note: Python code said "if THINKING", but logic implies removing it if we don't want it. 
                    // However, the python comment says "If THINKING is False, removes...". But the code says "if THINKING: reply = re.sub...".
                    // Wait, the python code says:
                    // if THINKING:
                    //    reply = re.sub(r"<think>.*?</think>", "", reply, flags=re.DOTALL).strip()
                    // This means if THINKING is true, it removes the block. This seems counter-intuitive based on the variable name, 
                    // but I will follow the python logic: if aiProperties.isThinking() is true, remove the block.
                    // Actually, let's re-read the python comment: "If THINKING is False, removes any internal <think>..."
                    // But the code is: "if THINKING: reply = re.sub..."
                    // This is a contradiction in the python snippet provided. 
                    // Usually "Thinking" models output <think> blocks. If we want to hide them, we should remove them.
                    // If the user wants to see the thinking process, we should keep them.
                    // Let's look at the python code again carefully.
                    // "if THINKING: reply = re.sub..." -> This removes the think block if THINKING is true.
                    // This suggests 'THINKING' variable might mean "Is this a thinking model where we need to clean up output?" 
                    // OR it's a bug in the python script description vs code.
                    // Given the user wants to convert the script, I will follow the CODE logic, not the comment.
                    // Python code: if THINKING -> remove tags.
                    
                    if (aiProperties.isThinking()) {
                        reply = THINK_PATTERN.matcher(reply).replaceAll("").trim();
                    }
                    return reply;
                }
            }
            return "";
        } catch (Exception e) {
            logger.error("Local LLM error", e);
            return "(Local LLM error: " + e.getMessage() + ")";
        }finally {
            logger.info("Ai responded: [{}]",reply);
        }
    }
}
