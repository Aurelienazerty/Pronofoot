package com.pronofoot.teamazerty.app.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by PBVZ9205 on 05/06/2014.
 */
public class StatUser implements Serializable {

    private static final long serialVersionUID = -271019810008L;

    private String username;
    private int user_id;
    private String saison;

    private List<Stat> stats;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getSaison() {
        return saison;
    }

    public void setSaison(String saison) {
        this.saison = saison;
    }

    public List<Stat> getStats() {
        return stats;
    }

    public void setStats(List<Stat> stats) {
        this.stats = stats;
    }
}
