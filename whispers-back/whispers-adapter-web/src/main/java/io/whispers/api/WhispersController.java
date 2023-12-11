package io.whispers.api;

import io.whispers.app.getmostrecentwhispers.*;
import io.whispers.app.postreply.PostReplyRequest;
import io.whispers.app.postreply.PostReplyUseCase;
import io.whispers.app.postwhisper.PostWhisperRequest;
import io.whispers.app.postwhisper.PostWhisperUseCase;
import io.whispers.domain.UserRepository;
import io.whispers.domain.WhisperEventPublisher;
import io.whispers.domain.WhisperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/whispers")
class WhispersController {

    @Autowired
    private WhisperRepository whisperRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WhisperEventPublisher whisperEventPublisher;

    @GetMapping(path = "")
    Collection<RecentWhisperView> get(@RequestParam("topic") Optional<String> topic) {
        GetMostRecentWhispersUseCase useCase = new GetMostRecentWhispersUseCase(this.whisperRepository);
        GetMostRecentWhispersResponse response = useCase.execute(new GetMostRecentWhispersRequest(
                topic.map(MostRecentFilterByTopic::new)
        ));
        return response.whispers();
    }

    @GetMapping(path = "/mine")
    Collection<RecentWhisperView> getMine(@RequestHeader("Authorization") String authorization) {
        String sender = authorization.substring("Bearer ".length());
        GetMostRecentWhispersUseCase useCase = new GetMostRecentWhispersUseCase(this.whisperRepository);
        GetMostRecentWhispersResponse response = useCase.execute(new GetMostRecentWhispersRequest(
                Optional.of(new MostRecentFilterBySender(sender))
        ));
        return response.whispers();
    }

    @PostMapping
    Object post(@RequestBody PostWhisperBody body, @RequestHeader("Authorization") String authorization) {
        String sender = authorization.substring("Bearer ".length());
        if (body.replyingTo().isEmpty()) {
            PostWhisperUseCase useCase = new PostWhisperUseCase(
                    this.whisperRepository,
                    this.userRepository,
                    this.whisperEventPublisher);
            return useCase.execute(new PostWhisperRequest(
                    sender,
                    body.text()
            ));
        } else {
            PostReplyUseCase useCase = new PostReplyUseCase(
                    this.whisperRepository,
                    this.userRepository);
            return useCase.execute(new PostReplyRequest(
                    sender,
                    body.text(),
                    body.replyingTo().get()
            ));
        }
    }

}
