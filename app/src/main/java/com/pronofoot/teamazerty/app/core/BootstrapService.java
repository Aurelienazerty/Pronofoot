
package com.pronofoot.teamazerty.app.core;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.pronofoot.teamazerty.app.model.Classement;
import com.pronofoot.teamazerty.app.model.Competition;
import com.pronofoot.teamazerty.app.model.CompetitionSelector;
import com.pronofoot.teamazerty.app.model.Grille;
import com.pronofoot.teamazerty.app.model.Resultat;
import com.pronofoot.teamazerty.app.model.StatUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.pronofoot.teamazerty.app.core.Constants.Http.HEADER_PARSE_APP_ID;
import static com.pronofoot.teamazerty.app.core.Constants.Http.HEADER_PARSE_REST_API_KEY;
import static com.pronofoot.teamazerty.app.core.Constants.Http.PARSE_APP_ID;
import static com.pronofoot.teamazerty.app.core.Constants.Http.PARSE_REST_API_KEY;
import static com.pronofoot.teamazerty.app.core.Constants.Http.URL_CLASSEMENT;
import static com.pronofoot.teamazerty.app.core.Constants.Http.URL_GRILLE_PRONO;
import static com.pronofoot.teamazerty.app.core.Constants.Http.URL_GRILLE_RESULTAT;
import static com.pronofoot.teamazerty.app.core.Constants.Http.URL_RESULTAT;
import static com.pronofoot.teamazerty.app.core.Constants.Http.URL_SELECTOR;
import static com.pronofoot.teamazerty.app.core.Constants.Http.URL_STAT_USER;

/**
 * Bootstrap API service
 */
public class BootstrapService {

    private UserAgentProvider userAgentProvider;
    private final String apiKey;
    private final String username;
    private final String password;

    /**
     * GSON instance to use for all request  with date format set up for proper parsing.
     */
    public Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss.S").create();

    /**
     * You can also configure GSON with different naming policies for your API. Maybe your api is Rails
     * api and all json values are lower case with an underscore, like this "first_name" instead of "firstName".
     * You can configure GSON as such below.
     *
     * public static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd").setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();
     *
     */


    /**
     * Read and connect timeout in milliseconds
     */
    private static final int TIMEOUT = 30 * 1000;


    private static class JsonException extends IOException {

        private static final long serialVersionUID = 3774706606129390273L;

        /**
         * Create exception from {@link JsonParseException}
         *
         * @param cause
         */
        public JsonException(JsonParseException cause) {
            super(cause.getMessage());
            initCause(cause);
        }
    }

    private class ListCompetition implements Serializable {
        private static final long serialVersionUID = 271019810008L;
        public List<Competition> competitions = new LinkedList<Competition>();
    }

    /**
     * Create bootstrap service
     *
     * @param username
     * @param password
     */
    public BootstrapService(final String username, final String password) {
        this.username = username;
        this.password = password;
        this.apiKey = null;
        this.GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss.S").create();
    }

    /**
     * Create bootstrap service
     *
     * @param userAgentProvider
     * @param apiKey
     */
    public BootstrapService(final String apiKey, final UserAgentProvider userAgentProvider) {
        this.userAgentProvider = userAgentProvider;
        this.username = null;
        this.password = null;
        this.apiKey = apiKey;
        this.GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss.S").create();
    }

    /**
     * Execute request
     *
     * @param request
     * @return request
     * @throws IOException
     */
    public HttpRequest execute(HttpRequest request) throws IOException {
        if (!configure(request).ok())
            throw new IOException("Unexpected response code: " + request.code());
        return request;
    }

    private HttpRequest configure(final HttpRequest request) {
        request.connectTimeout(TIMEOUT).readTimeout(TIMEOUT);
        request.userAgent(userAgentProvider.get());

        if(isPostOrPut(request))
            request.contentType(Constants.Http.CONTENT_TYPE_JSON); // All PUT & POST requests to Parse.com api must be in JSON - https://www.parse.com/docs/rest#general-requests

        return addCredentialsTo(request);
    }

    private boolean isPostOrPut(HttpRequest request) {
        return request.getConnection().getRequestMethod().equals(HttpRequest.METHOD_POST)
               || request.getConnection().getRequestMethod().equals(HttpRequest.METHOD_PUT);

    }

    private HttpRequest addCredentialsTo(HttpRequest request) {

        // Required params for
        request.header(HEADER_PARSE_REST_API_KEY, PARSE_REST_API_KEY );
        request.header(HEADER_PARSE_APP_ID, PARSE_APP_ID);

        /**
         * NOTE: This may be where you want to add a header for the api token that was saved when you
         * logged in. In the bootstrap sample this is where we are saving the session id as the token.
         * If you actually had received a token you'd take the "apiKey" (aka: token) and add it to the
         * header or form values before you make your requests.
          */

        /**
         * Add the user name and password to the request here if your service needs username or password for each
         * request. You can do this like this:
         * request.basic("myusername", "mypassword");
         */

        return request;
    }

    private <V> V fromJson(HttpRequest request, Class<V> target) throws IOException {
        Reader reader = request.bufferedReader();
        if (GSON == null) {
            GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss.S").create();
        }
        /*BufferedReader reader2 = request.bufferedReader();
        String ligne;
        while ((ligne=reader2.readLine())!=null){
            Log.i("TA lecture : ", ligne);
        }*/
        try {
            return GSON.fromJson(reader, target);
        } catch (JsonParseException e) {
            Log.e("TA", e.getMessage());
            throw new JsonException(e);
        } finally {
            try {
                reader.close();
            } catch (IOException ignored) {
                // Ignored
            }
        }
    }

    /**
     * Récupère le classement
     * @param params
     * @return non-null but possibly empty list of bootstrap
     * @throws IOException
     */
    public Classement getClassement(String url, Map<String, String> params) throws IOException {
        try {
            //Log.i("TA : Appel à ", urlClassement);
            //HttpRequest.
            //HttpRequest.post(URL_CLASSEMENT);
            HttpRequest request = execute(HttpRequest.post(url, (Map)params, true));
            return fromJson(request, Classement.class);
        } catch (HttpRequestException e) {
            throw e.getCause();
        }
    }


    /**
     * Récupère le classement en point
     *
     * @return non-null but possibly empty list of bootstrap
     * @throws IOException
     */
    public List<Resultat> getClassementPoint() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        Classement response = getClassement(URL_CLASSEMENT, params);
        if (response != null && response.resultats != null) {
            return response.resultats;
        }
        return Collections.emptyList();
    }

    /**
     * Récupère le classement en  %
     *
     * @return non-null but possibly empty list of bootstrap
     * @throws IOException
     */
    public List<Resultat> getClassementPourcent() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constants.Param.PART_CLASSEMENT, Constants.Param.CLASSEMENT_POURCENT);
        Classement response = getClassement(URL_CLASSEMENT, params);
        if (response != null && response.resultats != null) {
            return response.resultats;
        }
        return Collections.emptyList();
    }

    /**
     * Récupère le classement
     *
     * @return non-null but possibly empty list of bootstrap
     * @throws IOException
     */
    public Classement getResultatGrille() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        return getClassement(URL_RESULTAT, params);
    }

    /**
     * Récupère une espace_prono
     * @return non-null but possibly empty list of bootstrap
     * @throws IOException
     */
    public Grille getGrille(String user_id, String username, String password, int grille_id, int compet_id, String regId, String version) throws IOException {
        return _getGrille(URL_GRILLE_RESULTAT, user_id, username, password, grille_id, compet_id, regId, version);
    }

    /**
     * Récupère une espace_prono pronosticable pour un utilisateur
     * @param user_id
     * @param username
     * @param password
     * @return Grille
     * @throws IOException
     */
    public Grille getGrilleForUser(String user_id, String username, String password, int grille_id, int compet_id, String regId, String version) throws IOException {
        return _getGrille(URL_GRILLE_PRONO, user_id, username, password, grille_id, compet_id, regId, version);
    }

    /**
     * Récupère une espace_prono pour un utilisateur
     * @param url
     * @param user_id
     * @param username
     * @param password
     * @return Grille
     * @throws IOException
     */
    private Grille _getGrille(String url, String user_id, String username, String password, int grille_id, int compet_id, String regId, String version) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });

        GSON = builder.create();

        try {
            Map<String, String> params = new HashMap<String, String>();
            final String user_lang = Locale.getDefault().getLanguage();
            params.put(Constants.Param.PARAM_USERLANG, user_lang);
            params.put(Constants.Param.PARAM_GRILLE_ID, "" + grille_id);
            params.put(Constants.Param.PARAM_USER_ID, "" + user_id);
            params.put(Constants.Param.PARAM_COMPET_ID, "" + compet_id);
            params.put(Constants.Param.PARAM_USERNAME, "" + username);
            params.put(Constants.Param.PARAM_PASSWORD, "" + password);
            params.put(Constants.Param.PARAM_VERSION, "" + version);
            params.put(Constants.Param.PARAM_GCM_REGID, "" + regId);
            params.put(Constants.Param.PARAM_ANDROID, "" + Build.VERSION.RELEASE);

            //Log.i("TA", "POST : " + params);

            HttpRequest request = execute(HttpRequest.post(url, params, true));

            return fromJson(request, Grille.class);
        } catch (HttpRequestException e) {
            throw e.getCause();
        }
    }

    /**
     * Récupère la listes des compétitions et les grilles correspondantes
     * @param futures rechercher celles non jouées
     * @return Litee de compétition
     * @throws IOException
     */
    public CompetitionSelector getListCompetition(boolean futures, int compet_id) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });

        GSON = builder.create();

        try {
            String url = URL_SELECTOR;
            Map<String, String> params = new HashMap<String, String>();
            final String user_lang = Locale.getDefault().getLanguage();
            params.put(Constants.Param.PARAM_USERLANG, user_lang);
            if (futures) {
                params.put(Constants.Param.PARAM_FUTURE, "true");
            }
            params.put(Constants.Param.PARAM_COMPET_ID, "" + compet_id);
            //Log.i("TA", url);
            HttpRequest request = execute(HttpRequest.post(url, params, true));
            return fromJson(request, CompetitionSelector.class);
        } catch (HttpRequestException e) {
            throw e.getCause();
        }
    }

    /**
     * Récupère les stat d'un user
     * @param user_id id du user
     * @return Statistiques
     * @throws IOException
     */
    public StatUser getStatUser(int user_id) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });

        GSON = builder.create();

        try {
            String url = URL_STAT_USER;
            Map<String, String> params = new HashMap<String, String>();
            final String user_lang = Locale.getDefault().getLanguage();
            params.put(Constants.Param.PARAM_USERLANG, user_lang);
            params.put(Constants.Param.PARAM_USER_ID, user_id + "");
            HttpRequest request = execute(HttpRequest.post(url, params, true));
            return fromJson(request, StatUser.class);
        } catch (HttpRequestException e) {
            throw e.getCause();
        }
    }

}
