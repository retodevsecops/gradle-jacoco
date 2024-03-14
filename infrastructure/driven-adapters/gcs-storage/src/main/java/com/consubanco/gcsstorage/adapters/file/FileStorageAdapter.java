package com.consubanco.gcsstorage.adapters.file;

import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.gateways.FileRepository;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileStorageAdapter implements FileRepository {

    private final Storage storage;

    @Override
    public Flux<File> bulkSave(List<File> files) {
        return Flux.fromIterable(files)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(this::save)
                .sequential();
    }

    @Override
    public Mono<File> save(File file) {
        BlobId blobId = BlobId.of(file.getBucketName(), file.getName());
        BlobInfo blob = Blob.newBuilder(blobId).build();
        byte[] fileContent = Base64.getDecoder().decode(file.getContent());
        return Mono.fromCallable(() -> storage.create(blob, fileContent))
                .map(ignored -> file);
    }

}
