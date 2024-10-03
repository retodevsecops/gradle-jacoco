package com.consubanco.usecase.rpa;

import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.rpa.CartaLibranza;
import com.consubanco.usecase.file.helpers.FileHelper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class UploadCartaLibranzaUseCase {

    private final FileHelper fileHelper;

    public Mono<Void> execute(CartaLibranza cartaLibranza) {
        return uploadFiles(cartaLibranza)
                .then();
    }

    private Mono<List<File>> uploadFiles(CartaLibranza cartaLibranza) {
        String directory = FileConstants.rpaDirectory(cartaLibranza.getOfferId());
        return fileHelper.uploadFilesInParallel(cartaLibranza.getFiles(), directory)
                .collectList();
    }

}
