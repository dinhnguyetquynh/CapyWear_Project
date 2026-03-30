package org.example.clothing_be.dto.general.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiRes <T>{
    private int code;
    private String message;
    private T result;

    public static <T> ApiRes<T> success(int code, T result, String message) {
        return ApiRes.<T>builder()
                .code(code)
                .message(message)
                .result(result)
                .build();
    }
}
