package ru.yandex.practicum.interaction.api.exception.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private ReasonError cause;

    private List<StackTraceItem> stackTrace;

    private String httpStatus;

    private String userMessage;

    private String message;

    private List<ReasonError> suppressed;

    private String localizedMessage;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ReasonError {

        private List<StackTraceItem> stackTrace;

        private String message;

        private String localizedMessage;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StackTraceItem {

        private String classLoaderName;

        private String moduleName;

        private String moduleVersion;

        private String methodName;

        private String fileName;

        private Integer lineNumber;

        private String className;

        private Boolean nativeMethod;
    }
}