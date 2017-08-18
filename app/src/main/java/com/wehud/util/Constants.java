package com.wehud.util;

/**
 * This class stores constants used throughout the application.
 *
 * @author Olivier Gonçalves, 2017
 */

public final class Constants {
    public static final String API_URL = "http://192.168.0.167:3000";

    public static final String API_AUTH = API_URL + "/auth";
    public static final String API_USERS = API_URL + "/users";
    public static final String API_GAMES = API_URL + "/games";
    public static final String API_POSTS = API_URL + "/posts";
    public static final String API_PAGES = API_URL + "/pages";
    public static final String API_EVENTS = API_URL + "/events";
    public static final String API_PLANNINGS = API_URL + "/plannings";

    public static final String API_AUTH_LOGIN = API_AUTH + "/login";
    public static final String API_AUTH_LOGOUT = API_AUTH + "/logout";

    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_ACCEPT = "Accept";

    public static final String PARAM_TOKEN = "token";

    public static final String APPLICATION_JSON = "application/json";

    public static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1OThmMWQ2NTQ5M2E2MjBhYTkxOGJlNDIiLCJpYXQiOjE1MDMwNTgwMjIsImV4cCI6MTUwMzE0NDQyMn0.eRRq4WUSy47Fw7WPIjgF0W5El4-ubVs9IR5nbGuhoFQ";

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String PATCH = "PATCH";

    public static final int HTTP_CREATED = 201;
    public static final int HTTP_NO_CONTENT = 204;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_INTERNAL_SERVER_ERROR = 500;

    public static final int CONNECT_TIMEOUT = 3000;
    public static final int READ_TIMEOUT = 3000;

    public static final String INTENT_POSTS_LIST = "intent_posts_list";
    public static final String INTENT_POSTS_ADD = "intent_posts_add";
    public static final String INTENT_PAGES_ADD = "intent_pages_add";
    public static final String INTENT_PAGES_REMOVE = "intent_pages_remove";
    public static final String INTENT_PAGES_LIST = "intent_pages_list";
    public static final String INTENT_USERS_LIST = "intent_users_list";
    public static final String INTENT_GAMES_LIST = "intent_games_list";
    public static final String INTENT_FOLLOWERS_LIST = "intent_followers_list";

    public static final String EXTRA_API_RESPONSE = "extra_APIResponse";
}
