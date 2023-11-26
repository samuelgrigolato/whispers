package io.whispers.api;

import io.whispers.app.getmostrecentwhispers.GetMostRecentWhispersUseCase;
import io.whispers.app.getmostrecentwhispers.GetMostRecentWhispersResponse;
import io.whispers.app.getmostrecentwhispers.RecentWhisperView;
import io.whispers.app.postreply.PostReplyRequest;
import io.whispers.app.postreply.PostReplyUseCase;
import io.whispers.app.postwhisper.PostWhisperRequest;
import io.whispers.app.postwhisper.PostWhisperUseCase;
import io.whispers.domain.WhisperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/whispers")
class WhispersController {

    @Autowired
    private WhisperRepository whisperRepository;

    @GetMapping
    Collection<RecentWhisperView> get() {
        GetMostRecentWhispersUseCase useCase = new GetMostRecentWhispersUseCase(this.whisperRepository);
        GetMostRecentWhispersResponse response = useCase.execute();
        return response.whispers();
    }

    @PostMapping
    Object post(@RequestBody PostWhisperBody body, @RequestHeader("Authorization") String authorization) {
        String sender = authorization.substring("Bearer ".length());
        if (body.replyingTo().isEmpty()) {
            PostWhisperUseCase useCase = new PostWhisperUseCase(this.whisperRepository);
            return useCase.execute(new PostWhisperRequest(
                    sender,
                    body.text()
            ));
        } else {
            PostReplyUseCase useCase = new PostReplyUseCase(this.whisperRepository);
            return useCase.execute(new PostReplyRequest(
                    sender,
                    body.text(),
                    body.replyingTo().get()
            ));
        }
    }

}
