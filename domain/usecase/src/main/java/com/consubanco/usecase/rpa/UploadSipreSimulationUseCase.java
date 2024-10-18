package com.consubanco.usecase.rpa;

import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.rpa.SipreSimulation;
import com.consubanco.usecase.file.helpers.FileHelper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class UploadSipreSimulationUseCase {

    private final FileHelper fileHelper;

    public Mono<Void> execute(SipreSimulation sipreSimulation) {
        return uploadFiles(sipreSimulation)
                .then();
    }

    private Mono<List<File>> uploadFiles(SipreSimulation sipreSimulation) {
        String directory = FileConstants.rpaDirectory(sipreSimulation.getOfferId());
        return fileHelper.uploadFilesInParallel(sipreSimulation.getFiles(), directory)
                .collectList();
    }

}
