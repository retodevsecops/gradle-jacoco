package com.consubanco.model.entities.file;
import com.consubanco.model.entities.document.Document;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class File {
    private String name;
    private String url;
    private Document document;
}
