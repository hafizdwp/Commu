package dycode.com.commu.cons;

/**
 * Created by Asus on 11/14/2017.
 */

public class Constant {

    public static final String CREDENTIAL_USERNAME = "dycode";
    public static final String CREDENTIAL_PASSWORD = "dycode";
    public static final String GRANT_TYPE = "password";
    public static final String GRANT_TYPE_REFTOKEN = "refresh_token";

    public class URL {
        public static final String BASE = "http://dcnode.southeastasia.cloudapp.azure.com:8035/api/v1/";
        public static final String LOGIN = BASE + "signin";
        public static final String USER = BASE + "user";
        public static final String SIGNUP = BASE + "register";
        public static final String FORPASS = BASE + "forgot";
        public static final String ROOM = BASE + "room";
        public static final String LOGOUT = BASE + "logout";
        public static final String CHANGEPASS = BASE + "password";
        public static final String FRIENDS = BASE + "user/friend";
        public static final String SEARCH = BASE + "user/search";
        public static final String FIND_USER = BASE + "user/find";
        public static final String PUSHNOTIF = BASE + "fcm";
    }

    public class Pref {
        public static final String TOKEN_ACCESS = "token";
        public static final String TOKEN_REFRESH = "refreshToken";

        public static final String BOOL_LOGIN = "login";
        public static final String BOOL_PROFILE= "profile";

        public static final String USER_ID = "userId";
        public static final String USERNAME = "username";
        public static final String FULLNAME = "fullname";
        public static final String EMAIL = "email";
        public static final String PHOTO = "photo";
        public static final String PASSWORD = "password";

        public static final String UPDATE_PROFILE = "updateProfile";
        public static final String MYROOM = "myroom";
    }

    public class Node {
        public static final String CHATROOMS = "chatrooms";
        public static final String MESSAGES = "messages";
        public static final String USERS = "users";
        public static final String FCMTOKENS = "fcmtokens";
    }

    public class Roomtype{
        public static final String PRIVATE = "private";
        public static final String GROUP = "group";
    }
}
