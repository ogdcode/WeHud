package com.wehud.util;

/**
 * This class stores constants used throughout the application.
 *
 * @author Olivier Gonçalves, 2017
 */
public final class Constants {
    private static final String API_URL = "https://wehud.herokuapp.com";

    public static final String API_LOGIN = API_URL + "/auth/login";
    public static final String API_LOGOUT = API_URL + "/auth/logout";
    public static final String API_FORGOT_PASSWORD = API_URL + "/auth/password";

    public static final String API_USERS = API_URL + "/users";
    public static final String API_GAMES = API_URL + "/games";
    public static final String API_POSTS = API_URL + "/posts";
    public static final String API_PAGES = API_URL + "/pages";
    public static final String API_EVENTS = API_URL + "/events";
    public static final String API_PLANNINGS = API_URL + "/plannings";

    public static final String API_LIKE = API_POSTS + "/like";
    public static final String API_DISLIKE = API_POSTS + "/dislike";
    public static final String API_MESSAGES = API_POSTS + "/all/messages";

    public static final String API_USERS_USER = API_USERS + "/user";
    public static final String API_USERS_PAGES = API_USERS + "/pages";
    public static final String API_USERS_POSTS = API_USERS + "/posts";
    public static final String API_USERS_PLANNINGS = API_USERS + "/plannings";
    public static final String API_USERS_EVENTS = API_USERS + "/events";
    public static final String API_USER_FOLLOW = API_USERS + "/follow";
    public static final String API_USER_UNFOLLOW = API_USERS + "/unfollow";

    public static final String API_GAME_FOLLOW = API_USERS + "/follow/game";
    public static final String API_GAME_UNFOLLOW = API_USERS + "/unfollow/game";

    public static final String API_PAGE_POSTS = "/posts";

    public static final String API_EVENT_BIND = API_EVENTS + "/bind";
    public static final String API_EVENT_UNBIND = API_EVENTS + "/unbind";
    public static final String API_PLANNING_UNBIND = API_PLANNINGS + "/unbind";

    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_ACCEPT = "Accept";

    public static final String PARAM_TOKEN = "token";

    public static final String APPLICATION_JSON = "application/json";

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String PATCH = "PATCH";

    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_NO_CONTENT = 204;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_INTERNAL_SERVER_ERROR = 500;

    public static final int CONNECT_TIMEOUT = 3000;
    public static final int READ_TIMEOUT = 3000;

    public static final String INTENT_LOGIN = "intent_login";
    public static final String INTENT_LOGOUT = "intent_logout";
    public static final String INTENT_FORGOT_PASSWORD = "intent_forgot_password";
    public static final String INTENT_POSTS_LIST = "intent_posts_list";
    public static final String INTENT_POSTS_ADD = "intent_posts_add";
    public static final String INTENT_POST_LIKE = "intent_post_like";
    public static final String INTENT_POST_DISLIKE = "intent_post_dislike";
    public static final String INTENT_PAGES_ADD = "intent_pages_add";
    public static final String INTENT_PAGES_DELETE = "intent_pages_delete";
    public static final String INTENT_PAGES_LIST = "intent_pages_list";
    public static final String INTENT_GAMES_LIST = "intent_games_list";
    public static final String INTENT_USER_CREATE = "intent_user_create";
    public static final String INTENT_USER_GET = "intent_user_get";
    public static final String INTENT_USER_UPDATE = "intent_user_update";
    public static final String INTENT_USER_DELETE = "intent_user_delete";
    public static final String INTENT_USER_FOLLOW = "intent_user_follow";
    public static final String INTENT_USER_UNFOLLOW = "intent_user_unfollow";
    public static final String INTENT_GAME_GET = "intent_game_get";
    public static final String INTENT_GAME_FOLLOW = "intent_game_follow";
    public static final String INTENT_GAME_UNFOLLOW = "intent_game_unfollow";
    public static final String INTENT_FOLLOWERS_LIST = "intent_followers_list";
    public static final String INTENT_MESSAGES_LIST = "intent_messages_list";
    public static final String INTENT_PLANNINGS_LIST = "intent_plannings_list";
    public static final String INTENT_PLANNINGS_ADD = "intent_plannings_add";
    public static final String INTENT_PLANNINGS_DELETE = "intent_plannings_delete";
    public static final String INTENT_PLANNINGS_UNBIND = "intent_plannings_unbind";
    public static final String INTENT_EVENTS_LIST = "intent_events_list";
    public static final String INTENT_EVENTS_ADD = "intent_events_add";
    public static final String INTENT_EVENTS_DELETE = "intent_events_delete";
    public static final String INTENT_EVENTS_BIND = "intent_events_bind";
    public static final String INTENT_EVENTS_UNBIND = "intent_events_unbind";
    public static final String INTENT_REFRESH_PAGE = "intent_refresh_page";
    public static final String INTENT_REFRESH_POSTS = "intent_refresh_posts";

    public static final String EXTRA_BROADCAST = "extra_broadcast";
    public static final String EXTRA_REFRESH_PAGE = "extra_refresh_page";
    public static final String EXTRA_REFRESH_POSTS = "extra_refresh_posts";

    public static final String PREF_USER_ID = "pref_user_id";
    public static final String PREF_TOKEN = "pref_token";

    static final String CHARACTERS = "0123456789abcdef";

    static final String[] RANKS = {"J", "H", "G", "F", "E", "D", "C", "B", "A", "S"};

    static final String
            TAG_EVENT_LABEL = "Other",
            TAG_LIVE_LABEL = "Live",
            TAG_MEETUP_LABEL = "Meetup",
            TAG_YOUTUBE_LABEL = "YouTube",
            TAG_BIRTHDAY_LABEL = "Birthday";

    static final int
            TAG_EVENT = 0,
            TAG_LIVE = 1,
            TAG_MEETUP = 2,
            TAG_YOUTUBE = 3,
            TAG_BIRTHDAY = 4;

    static final String[] AVATARS = {
            "https://s3.ca-central-1.amazonaws.com/g-zone/images/profile01.png",
            "https://s3.ca-central-1.amazonaws.com/g-zone/images/profile02.png",
            "https://s3.ca-central-1.amazonaws.com/g-zone/images/profile03.png",
            "https://s3.ca-central-1.amazonaws.com/g-zone/images/profile04.png",
    };

    static final String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    static final String LOCAL_PATTERN_DATETIME = "dd/MM/yyyy HH:mm";
    static final String LOCAL_PATTERN_DATE = "dd/MM/yyyy";
    static final String LOCAL_PATTERN_TIME = "HH:mm";
}
