package com.consubanco.model.entities.rpa;

import com.consubanco.model.entities.file.vo.FileUploadVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SipreSimulation {
    private String applicationId;
    private String channel;
    private String offerId;
    private String customerBp;
    private String folioBusiness;
    private List<FileUploadVO> files;
}
