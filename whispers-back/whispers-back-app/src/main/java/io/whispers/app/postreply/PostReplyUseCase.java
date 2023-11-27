package io.whispers.app.postreply;

import io.whispers.domain.*;

public class PostReplyUseCase {

    private WhisperRepository whisperRepository;

    private UserRepository userRepository;

    public PostReplyUseCase(WhisperRepository whisperRepository,
                            UserRepository userRepository) {
        this.whisperRepository = whisperRepository;
        this.userRepository = userRepository;
    }

    public PostReplyResponse execute(PostReplyRequest request) {
        this.userRepository.createIfNotExists(request.sender());
        Reply reply = this.whisperRepository.createReply(new CreateReplyData(
                request.text(),
                request.sender(),
                request.replyingTo()
        ));
        return PostReplyResponse.from(reply);
    }

}
