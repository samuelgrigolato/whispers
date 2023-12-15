package io.whispers.api;

import io.whispers.app.getmostrecentwhispers.*;
import io.whispers.app.postreply.PostReplyRequest;
import io.whispers.app.postreply.PostReplyUseCase;
import io.whispers.app.postwhisper.PostWhisperRequest;
import io.whispers.app.postwhisper.PostWhisperUseCase;
import io.whispers.domain.model.UnsavedReply;
import io.whispers.domain.model.UnsavedWhisper;
import io.whispers.domain.model.User;
import io.whispers.domain.repository.WhisperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/whispers")
class WhispersController {

    @Autowired
    private WhisperRepository whisperRepository;

    @GetMapping(path = "")
    Collection<RecentWhisperOutput> get(@RequestParam("topic") Optional<String> topic) {
        GetMostRecentWhispersUseCase useCase = new GetMostRecentWhispersUseCase(this.whisperRepository);
        GetMostRecentWhispersResponse response = useCase.execute(new GetMostRecentWhispersRequest(
                topic.<MostRecentFilter>map(MostRecentFilterByTopic::new).orElse(new MostRecentFilterAll())
        ));
        return response.whispers();
    }

    @GetMapping(path = "/mine")
    Collection<RecentWhisperOutput> getMine(@RequestHeader("Authorization") String authorization) {
        String sender = authorization.substring("Bearer ".length());
        GetMostRecentWhispersUseCase useCase = new GetMostRecentWhispersUseCase(this.whisperRepository);
        GetMostRecentWhispersResponse response = useCase.execute(new GetMostRecentWhispersRequest(
                new MostRecentFilterBySender(sender)
        ));
        return response.whispers();
    }

    @PostMapping
    Object post(@RequestBody PostWhisperBody body, @RequestHeader("Authorization") String authorization) {
        String sender = authorization.substring("Bearer ".length());
        if (body.replyingTo().isEmpty()) {
            PostWhisperUseCase useCase = new PostWhisperUseCase(
                    this.whisperRepository);
            return useCase.execute(new PostWhisperRequest(new UnsavedWhisper(
                    new User(sender),
                    body.text()
            )));
        } else {
            PostReplyUseCase useCase = new PostReplyUseCase(
                    this.whisperRepository);
            return useCase.execute(new PostReplyRequest(
                    new UnsavedReply(new User(sender), body.text()),
                    body.replyingTo().get()
            ));
        }
    }

}
