package com.wehud.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * This class represents a game stored in the application.
 *
 * @author Olivier Gonçalves, WeHud, 2017.
 */

public final class Game {

    @SerializedName("name")
    private String mName;

    @SerializedName("followers")
    private List<User> mFollowers;

}
