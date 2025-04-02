package com.callibrity.ai.tools.web;

import lombok.val;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatClient chatClient;
    private final ChatTools tools;

    public ChatController(ChatClient.Builder builder, ChatTools tools) {
        final var options = OpenAiChatOptions.builder()
                .toolChoice(OpenAiApi.ChatCompletionRequest.ToolChoiceBuilder.AUTO)
                .build();
        this.chatClient = builder
                .defaultOptions(options)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
        this.tools = tools;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
       final var completion = chatClient.prompt()
                .user(request.prompt())
                .tools(tools)
                .call()
                .content();


        return new ChatResponse(completion);
    }

    public record ChatRequest(String prompt) {

    }

    public record ChatResponse(String completion) {

    }
}
