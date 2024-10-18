package com.consubanco.usecase.file.helpers;

import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileConstants;
import com.consubanco.model.entities.file.gateway.FileRepository;
import com.consubanco.model.entities.file.message.FileMessage;
import com.consubanco.model.entities.file.util.FileFactoryUtil;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.monoBusiness;
import static com.consubanco.model.entities.file.message.FileBusinessMessage.FILES_NOT_FOUND;

@RequiredArgsConstructor
public class FileHelper {

    private final FileRepository fileRepository;

    public Mono<List<File>> filesByOfferWithoutUrls(String offerId) {
        return Mono.just(offerId)
                .map(FileConstants::offerDirectory)
                .flatMapMany(fileRepository::listByFolderWithoutUrls)
                .collectList()
                .switchIfEmpty(monoBusiness(FILES_NOT_FOUND, FileMessage.offerFilesNotFound(offerId)));
    }

    public Mono<File> save(File file) {
        return fileRepository.save(file);
    }

    public Flux<File> uploadFilesInParallel(List<FileUploadVO> fileUploadVOList, String directory) {
        return Flux.fromIterable(fileUploadVOList)
                .map(fileUploadVO ->  FileFactoryUtil.buildFromFileUploadVO(fileUploadVO, directory))
                .flatMap(fileRepository::save)
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .sequential();
    }

    public Mono<Void> delete(File file) {
        return fileRepository.delete(file);
    }

    public Mono<File> findByName(String name) {
        return fileRepository.getByNameWithoutSignedUrl(name)
                .switchIfEmpty(monoBusiness(FILES_NOT_FOUND, FileMessage.notFoundByName(name)));
    }

}
