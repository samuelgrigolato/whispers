package io.whispers.app.postreply;

import io.whispers.domain.*;

public class PostReplyUseCase {

    private WhisperRepository whisperRepository;

    public PostReplyUseCase(WhisperRepository whisperRepository) {
        this.whisperRepository = whisperRepository;
    }

    public PostReplyResponse execute(PostReplyRequest request) {
        Reply reply = this.whisperRepository.createReply(new CreateReplyData(
                request.text(),
                request.sender(),
                request.replyingTo()
        ));
        return PostReplyResponse.from(reply);
    }

}
