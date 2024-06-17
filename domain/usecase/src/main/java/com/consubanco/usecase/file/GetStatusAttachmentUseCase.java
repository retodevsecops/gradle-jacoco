package com.consubanco.usecase.file;

import com.consubanco.model.entities.file.constant.AttachmentStatusEnum;
import com.consubanco.model.entities.file.vo.AttachmentStatus;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetStatusAttachmentUseCase {

    private final GetProcessByIdUseCase getProcessByIdUseCase;

    public Mono<AttachmentStatus> execute(String processId) {
        return getProcessByIdUseCase.execute(processId)
                .map(e -> AttachmentStatus.builder()
                        .status(AttachmentStatusEnum.PENDING)
                        .build());
    }

}