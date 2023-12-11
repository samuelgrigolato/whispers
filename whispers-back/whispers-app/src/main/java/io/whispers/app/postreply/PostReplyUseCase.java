package io.whispers.app.postreply;

import io.whispers.domain.model.Reply;
import io.whispers.domain.repository.WhisperRepository;

public class PostReplyUseCase {

    private WhisperRepository whisperRepository;

    public PostReplyUseCase(WhisperRepository whisperRepository) {
        this.whisperRepository = whisperRepository;
    }

    public PostReplyResponse execute(PostReplyRequest request) {
        Reply reply = this.whisperRepository.createReply(
                request.replyingTo(),
                request.reply()
        );
        return PostReplyResponse.from(reply);
    }

}
