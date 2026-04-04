package backend.training.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TrainingProgramPdfFileResponse {

    private byte[] content;
    private String fileName;
    private String contentType;
}

