package io.whispers.api;

import io.whispers.app.getmostrecentwhispers.GetMostRecentWhispersUseCase;
import io.whispers.app.getmostrecentwhispers.GetMostRecentWhispersResponse;
import io.whispers.app.getmostrecentwhispers.RecentWhisperView;
import io.whispers.app.postwhisper.PostWhisperRequest;
import io.whispers.app.postwhisper.PostWhisperResponse;
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
    PostWhisperResponse post(@RequestBody PostWhisperBody body, @RequestHeader("Authorization") String authorization) {
        PostWhisperUseCase useCase = new PostWhisperUseCase(this.whisperRepository);
        return useCase.execute(new PostWhisperRequest(
                authorization.substring("Bearer ".length()),
                body.text()
        ));
    }

}
