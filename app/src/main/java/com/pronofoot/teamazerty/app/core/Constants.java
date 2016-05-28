

package com.pronofoot.teamazerty.app.core;

/**
 * Bootstrap constants
 */
public class Constants {

    public static class Auth {
        private Auth() {}

        /**
         * Account type id
         */
        public static final String BOOTSTRAP_ACCOUNT_TYPE = "com.pronofoot.teamazerty.app";

        /**
         * Account name
         */
        public static final String BOOTSTRAP_ACCOUNT_NAME = "pronofoot";

        /**
         * Provider id
         */
        public static final String BOOTSTRAP_PROVIDER_AUTHORITY = "com.pronofoot.teamazerty.app.sync";

        /**
         * Auth token type
         */
        public static final String AUTHTOKEN_TYPE = BOOTSTRAP_ACCOUNT_TYPE;

    }

    public static class Pref {
        private Pref() {}

        public static final String PREF_NAME = "pronofoot_pref";
        public static final String PREF_LOGIN = "pronofoot_login";
        public static final String PREF_PASSWORD = "pronofoot_password";
        public static final String PREF_USER_ID = "pronofoot_user_id";
        public static final String PREF_PUB = "pronofoot_publicite";
    }

    public static class Push {
        private Push() {}

        public static final String GOOGLE_PROJECT_ID = "297833814813";
        public static final String PARAM_MESSAGE = "message";
        public static final String PARAM_TYPE = "type";

        public static final String PARAM_GRILLE_NOM = "gille_nom";
        public static final String PARAM_GRILLE_ID = "grille_id";

        public static final String PARAM_TITRE = "titre";
        public static final String PARAM_URL = "url";

        public static class TypePush {
            private TypePush() {}

            public static final String TYPE_GRILLE = "grille";
            public static final String TYPE_URL = "url";
        }
    }

    public static class Param {
        private Param() {}

        public static final String PARAM_PASSWORD = "password";
        public static final String PARAM_USERNAME = "username";
        public static final String PARAM_USERMAIL = "email";
        public static final String PARAM_USERLANG = "user_lang";
        public static final String PARAM_GCM_REGID = "gcm_regid";
        public static final String PARAM_VERSION = "version";
        public static final String PARAM_ANDROID = "android";
        public static final String PARAM_CONFIRMCREDENTIALS = "confirmCredentials";
        public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";
        public static final String PARAM_GRILLE_ID = "grille_id";
        public static final String PARAM_USER_ID = "user_id";
        public static final String PARAM_COMPET_ID = "compet_id";
        public static final String PARAM_FUTURE = "futures";
        public static final String PARAM_NOTIF = "notif";
        public static final String PARAM_INFO = "info";
        public static final String PARAM_NEWSLETTER = "newsletter";
        public static final String PARAM_HOME_PAGE_IS_RESULT = "home_page_result";
        public static final String PARAM_WANT_PUB = "want_pub";

        public static final String CLASSEMENT_POURCENT = "pourcent";
        public static final String PART_CLASSEMENT = "part";

    }

    public static class Indent {
        private Indent() {}

        public final static String GRILLE_PRONO = "com.pronofoot.teamazerty.espaceprono.grilleprono";
        public final static String GRILLE_RESULTAT = "com.pronofoot.teamazerty.espaceprono.grilleres";
        public final static String COMPET_ID = "com.pronofoot.teamazerty.espaceprono.competition";
        public final static String FIRST_GRILLE_PRONO = "com.pronofoot.teamazerty.espaceprono.firstgrilleprono";
        public final static String LAST_GRILLE_PRONO = "com.pronofoot.teamazerty.espaceprono.lastgrilleprono";
        public final static String FIRST_GRILLE_RESULTAT = "com.pronofoot.teamazerty.espaceprono.firstgrilleres";
        public final static String LAST_GRILLE_RESULTAT = "com.pronofoot.teamazerty.espaceprono.lastgrilleres";
        public final static String PAGE = "com.pronofoot.teamazerty.espaceprono.page";

        public final static String USER_ID = "user_id";
        public final static String USERNAME = "username";

        public final static String LUNCHED = "lunched";
    }

    /**
     * All HTTP is done through a REST style API built for demonstration purposes on Parse.com
     * Thanks to the nice people at Parse for creating such a nice system for us to use for bootstrap!
     */
    public static class Http {
        private Http() {}

        /**
         * Base URL for all requests
         */
        public static final String URL_BASE = "https://www.team-azerty.com/html/prono-foot/mobile";

        /**
         * URL de classement
         */
        public static final String URL_CLASSEMENT = URL_BASE + "/classement.php";

        /**
         * URL de classement
         */
        public static final String URL_RESULTAT = URL_BASE + "/resultat.php";

        /**
         * URL de modifcation du profile
         */
        public static final String URL_PROFIL = URL_BASE + "/profil.php";

        /**
         * Authentication URL
         */
        public static final String URL_AUTH = URL_BASE + "/auth.php";
        public static final String URL_LOUGOUT = URL_BASE + "/logout.php";
        public static final String URL_REGISTER = URL_BASE + "/register.php";

        /**
         * URL des grilles
         */
        public static final String URL_GRILLE_RESULTAT = URL_BASE + "/grille_resultat.php";
        public static final String URL_GRILLE_PRONO = URL_BASE + "/grille_prono.php";
        public static final String URL_DO_PRONO = URL_BASE + "/do_prono.php";

        /**
         * URL du remplisseur de select
         */
        public static final String URL_SELECTOR = URL_BASE + "/selector.php";

        /**
         * URL du remplisseur de select
         */
        public static final String URL_STAT_USER = URL_BASE + "/stat_user.php";

        public static final String PARSE_APP_ID = "zHb2bVia6kgilYRWWdmTiEJooYA17NnkBSUVsr4H";
        public static final String PARSE_REST_API_KEY = "N2kCY1T3t3Jfhf9zpJ5MCURn3b25UpACILhnf5u9";
        public static final String HEADER_PARSE_REST_API_KEY = "X-Parse-REST-API-Key";
        public static final String HEADER_PARSE_APP_ID = "X-Parse-Application-Id";
        public static final String CONTENT_TYPE_JSON = "application/json";

        public static final String FORUM_PRONOFOOT = "https://www.team-azerty.com/sport/application-mobile-pronofoot-t2878.html";
        public static final String PROFIL  = "https://www.team-azerty.com/html/membres/profil.php";
        public static final String REGLEMENT  = "https://www.team-azerty.com/html/prono-foot/reglement.php";
        public static final String BASE_STAT_USER  = "https://www.team-azerty.com/html/prono-foot/pronostiqueur-";
        public static final String END_STAT_USER  = ".html";
    }


    public static class Extra {
        private Extra() {}
        public static final String VIELLE_VERSION = "Vieille version";
        public static final String USER = "user";
        public static final String REASON_REGISTER_FAIL_MAIL = "mail";

    }

    public static class Types {
        private Types() {}

        public static final String CLASSEMENT_POINT = "classement_point";
        public static final String CLASSEMENT_POURCENT = "classement_pourcent";
    }

}


