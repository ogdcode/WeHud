package com.wehud.network;

/**
 * This class represents a response from the API.
 *
 * @author Olivier Gonçalves, WeHud, 2017
 */

public final class Response {
    private int code;
    private String content;

    private Response() {
    }

    Response(int code, String content) {
        this.code = code;
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
