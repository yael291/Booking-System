package yael.project.myApi.main.dto;

public record ApiErrorResponse(
        int errorCode,
        String description) {

}
