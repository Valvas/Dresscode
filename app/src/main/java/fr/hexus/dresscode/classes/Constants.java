package fr.hexus.dresscode.classes;

public final class Constants
{
    public static final String WARDROBE_TABLE_NAME = "wardrobe";
    public static final String WARDROBE_TABLE_COLUMNS_TYPE = "type";
    public static final String WARDROBE_TABLE_COLUMNS_UUID = "uuid";
    public static final String WARDROBE_TABLE_COLUMNS_PATH = "path";
    public static final String WARDROBE_TABLE_COLUMNS_STORED_ON_API = "api";
    public static final String WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_COLOR_ID = "color";
    public static final String WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_ELEMENT_ID = "element";
    public static final String OUTFIT_ELEMENTS_TABLE_COLUMNS_OUTFIT_UUID = "outfit";
    public static final String OUTFIT_ELEMENTS_TABLE_COLUMNS_ELEMENT_UUID = "element";

    public static final String OUTFIT_TABLE_NAME = "outfit";
    public static final String OUTFIT_TABLE_COLUMNS_NAME = "name";
    public static final String OUTFIT_TABLE_COLUMNS_UUID = "uuid";
    public static final String OUTFIT_TABLE_COLUMNS_STORED_ON_API = "api";
    public static final String WARDROBE_ELEMENT_COLORS_TABLE_NAME = "colors";
    public static final String OUTFIT_ELEMENTS_TABLE_NAME = "elements";

    public static final String DRESSCODE_APP_FOLDER = "/dresscode";

    public static final String SHARED_PREFERENCES_FILE_NAME = "customPreferences";

    public static final String API_BASE_URL = "http://dresscode.ddns.net";

    public static final String EMAIL_REGEX_FORMAT = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
    public static final String PASSWORD_REGEX_FORMAT = "(?=.{6,24}$)[a-zA-Z0-9]*";
    public static final String LASTNAME_REGEX_FORMAT = "[a-zA-Zéèàùâêîôûäëïöüñ]{2,}(-)?[a-zA-Zéèàùâêîôûäëïöüñ]+";
    public static final String FIRSTNAME_REGEX_FORMAT = "[a-zA-Zéèàùâêîôûäëïöüñ]{2,}(-)?[a-zA-Zéèàùâêîôûäëïöüñ]+";


    public static final String GET_NEW_TOKEN_JOB_TAG = "GET_NEW_TOKEN_JOB_TAG";

    public static final int NEW_TOKEN_RECURRING_TASK_MIN = 21600;
    public static final int NEW_TOKEN_RECURRING_TASK_MAX = 28800;

    public static final String WARDROBE_OUTFIT_API_TAG_CREATE = "WARDROBE_OUTFIT_API_TAG_CREATE";
    public static final String WARDROBE_OUTFIT_API_TAG_UPDATE = "WARDROBE_OUTFIT_API_TAG_UPDATE";
    public static final String WARDROBE_OUTFIT_API_TAG_DELETE = "WARDROBE_OUTFIT_API_TAG_DELETE";

    public static final String LOG_NETWORK_MANAGER_SENDING_NEW_WARDROBE_ELEMENT = "SENDING_NEW_WARDROBE_ELEMENT";
    public static final String LOG_NETWORK_MANAGER_SENDING_NEW_WARDROBE_OUTFIT = "SENDING_NEW_WARDROBE_OUTFIT";
    public static final String LOG_NETWORK_MANAGER_REMOVING_WARDROBE_OUTFIT = "REMOVING_WARDROBE_OUTFIT";
    public static final String LOG_NETWORK_MANAGER_REMOVING_WARDROBE_ELEMENT = "REMOVING_WARDROBE_ELEMENT";
    public static final String LOG_NETWORK_MANAGER_UPDATING_WARDROBE_OUTFIT = "UPDATING_WARDROBE_OUTFIT";
}
