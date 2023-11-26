package io.whispers.api;

import io.whispers.app.getmostrecentwhispers.GetMostRecentWhispersUseCase;
import io.whispers.app.getmostrecentwhispers.GetMostRecentWhispersResponse;
import io.whispers.app.getmostrecentwhispers.RecentWhisperView;
import io.whispers.domain.WhisperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
