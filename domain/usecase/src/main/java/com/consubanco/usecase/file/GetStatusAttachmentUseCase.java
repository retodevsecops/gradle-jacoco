package com.consubanco.usecase.file;

import com.consubanco.model.entities.file.constant.AttachmentStatusEnum;
import com.consubanco.model.entities.file.vo.AttachmentStatus;
import com.consubanco.usecase.process.GetProcessByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class GetStatusAttachmentUseCase {

    private final GetProcessByIdUseCase getProcessByIdUseCase;

    public Mono<AttachmentStatus> execute(String processId) {
        return getProcessByIdUseCase.execute(processId)
                .map(e -> getStatus());
    }

    private static AttachmentStatus getStatus() {
        int randomValue = ThreadLocalRandom.current().nextInt(3);
        return switch (randomValue) {
            case 0 -> AttachmentStatus.builder()
                    .status(AttachmentStatusEnum.PENDING)
                    .build();
            case 1 -> AttachmentStatus.builder()
                    .status(AttachmentStatusEnum.FAILED)
                    .invalidAttachments(List.of(AttachmentStatus.InvalidAttachment.builder()
                                    .code("001")
                                    .name("recibo-nomina-0")
                                    .reason("any reason jejejeje")
                            .build()))
                    .build();
            default -> AttachmentStatus.builder()
                    .status(AttachmentStatusEnum.SUCCESS)
                    .build();
        };
    }

}